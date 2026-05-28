## ADDED Requirements

### Requirement: Validated NoncrossingMatchingProblem input bundle

The system SHALL provide a validated domain type `bio.domain.nucleic.NoncrossingMatchingProblem` constructed only through a smart constructor `NoncrossingMatchingProblem.from(rna: RnaString): Either[NoncrossingMatchingProblemError, NoncrossingMatchingProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `rna.value.length <= 300`, else `ExceedsMaxLength(length, max)`.
2. The A and U counts are equal, else `UnpairedAU(aCount, uCount)`.
3. The C and G counts are equal, else `UnpairedCG(cCount, gCount)`.

The empty RNA string MUST be accepted (it produces `auCount = 0`, `cgCount = 0`, and the algorithm returns `1` — the empty matching). The constructed value MUST expose `rna: RnaString`, `auCount: Int` (= the common value of `#A` and `#U`), and `cgCount: Int` (= the common value of `#C` and `#G`). The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind CAT sample
- **WHEN** `NoncrossingMatchingProblem.from` is called with `RnaString.from("AUAU").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.auCount == 2` and `problem.cgCount == 0`

#### Scenario: Accepts the empty RNA string
- **WHEN** `NoncrossingMatchingProblem.from` is called with `RnaString.from("").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.auCount == 0` and `problem.cgCount == 0`

#### Scenario: Accepts the maximum-length 300-character input
- **WHEN** `NoncrossingMatchingProblem.from` is called with `RnaString.from("AU" * 150).toOption.get`
- **THEN** it returns `Right(problem)` where `problem.rna.value.length == 300` and `problem.auCount == 150` and `problem.cgCount == 0`

#### Scenario: Rejects a 301-character input as ExceedsMaxLength(301, 300)
- **WHEN** `NoncrossingMatchingProblem.from` is called with an RNA string of length 301 that balances A/U and C/G
- **THEN** it returns `Left(NoncrossingMatchingProblemError.ExceedsMaxLength(301, 300))`

#### Scenario: Rejects an unbalanced AU string as UnpairedAU(2, 1)
- **WHEN** `NoncrossingMatchingProblem.from` is called with `RnaString.from("AAU").toOption.get`
- **THEN** it returns `Left(NoncrossingMatchingProblemError.UnpairedAU(2, 1))`

#### Scenario: Rejects an unbalanced CG string as UnpairedCG(2, 1)
- **WHEN** `NoncrossingMatchingProblem.from` is called with `RnaString.from("CCG").toOption.get`
- **THEN** it returns `Left(NoncrossingMatchingProblemError.UnpairedCG(2, 1))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `NoncrossingMatchingProblem(rna, 0, 0)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(auCount = 0)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: NoncrossingMatching.count returns the noncrossing-matching count mod 1 000 000

The system SHALL provide an algorithm object `bio.algorithms.nucleic.NoncrossingMatching` with a method `count(problem: NoncrossingMatchingProblem): Int` that returns the total number of noncrossing perfect matchings of basepair edges (`A`-`U` or `C`-`G` only) in the bonding graph of `problem.rna`, modulo `1 000 000`.

The implementation MUST use a bottom-up interval dynamic-programming table `dp(i)(j)` where `j - i + 1` is even, base-cased at `dp(i)(i - 1) = 1` (empty interval) and `dp(i)(j) = 0` for odd-length intervals. The transition MUST be:

```
dp(i)(j) = Σ_{k}  dp(i+1)(k-1) · dp(k+1)(j)  (mod 1 000 000)
```

summed over every `k` in `i+1..j` such that `s(k)` is the complementary basepair partner of `s(i)` (`A↔U` or `C↔G`) and `k - i` is odd. All intermediate multiplications MUST be performed in `Long` to avoid `Int` overflow before the modulus is applied.

#### Scenario: Canonical Rosalind CAT sample `AUAU`
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping `AUAU`
- **THEN** it returns `2`

#### Scenario: Empty RNA string returns 1 (the empty matching)
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping the empty RNA string
- **THEN** it returns `1`

#### Scenario: Single AU pair returns 1
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping `AU`
- **THEN** it returns `1`

#### Scenario: Single CG pair returns 1
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping `CG`
- **THEN** it returns `1`

#### Scenario: `AUAUAU` returns the Catalan number C(3) = 5
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping `AUAUAU`
- **THEN** it returns `5`

#### Scenario: Fully-nested `AAAAUUUU` admits only one noncrossing matching
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping `AAAAUUUU`
- **THEN** it returns `1`

#### Scenario: Mixed-alphabet `AUCG` returns 1
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping `AUCG`
- **THEN** it returns `1`

#### Scenario: CG-only `CGCG` returns 2
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping `CGCG`
- **THEN** it returns `2`

#### Scenario: Modulo wrap on `"AU" * 14` returns `C(14) mod 1 000 000 = 674440`
- **WHEN** `NoncrossingMatching.count` is called with the problem wrapping `"AU" * 14`
- **THEN** it returns `674440`
