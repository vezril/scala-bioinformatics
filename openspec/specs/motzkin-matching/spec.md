# motzkin-matching Specification

## Purpose

Counts the noncrossing matchings (not necessarily perfect) of basepair edges
(`A`-`U` or `C`-`G`) in the bonding graph of an RNA string, modulo
`1 000 000` (Rosalind spec 36 — MOTZ, "Motzkin Numbers and RNA Secondary
Structures"). Provides a validated `MotzkinMatchingProblem` input bundle
(length cap only — no balance constraint) and the `MotzkinMatching.count`
`O(n³)` interval dynamic-programming algorithm.

## Requirements

### Requirement: Validated MotzkinMatchingProblem input bundle

The system SHALL provide a validated domain type `bio.domain.nucleic.MotzkinMatchingProblem` constructed only through a smart constructor `MotzkinMatchingProblem.from(rna: RnaString): Either[MotzkinMatchingProblemError, MotzkinMatchingProblem]`. The smart constructor MUST enforce only the length cap `rna.value.length <= 300` (returning `ExceedsMaxLength(length, max)` on failure). Unbalanced AU/CG counts and odd-length strings MUST be accepted — partial noncrossing matchings always exist (at minimum, the empty matching), so the Rosalind input contract does not require balance.

The empty RNA string MUST be accepted. The constructed value MUST expose `rna: RnaString`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind MOTZ sample
- **WHEN** `MotzkinMatchingProblem.from` is called with `RnaString.from("AUAU").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.rna.value == "AUAU"`

#### Scenario: Accepts the empty RNA string
- **WHEN** `MotzkinMatchingProblem.from` is called with `RnaString.from("").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.rna.value == ""`

#### Scenario: Accepts the maximum-length 300-character input
- **WHEN** `MotzkinMatchingProblem.from` is called with `RnaString.from("A" * 300).toOption.get`
- **THEN** it returns `Right(problem)` where `problem.rna.value.length == 300`

#### Scenario: Accepts a single-character input (CAT would compute 1 here too, but this confirms acceptance)
- **WHEN** `MotzkinMatchingProblem.from` is called with `RnaString.from("A").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.rna.value == "A"`

#### Scenario: Accepts an unbalanced AU string (CAT would reject)
- **WHEN** `MotzkinMatchingProblem.from` is called with `RnaString.from("AAU").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.rna.value == "AAU"`

#### Scenario: Accepts an odd-length string
- **WHEN** `MotzkinMatchingProblem.from` is called with `RnaString.from("AUC").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.rna.value == "AUC"`

#### Scenario: Rejects a 301-character input as ExceedsMaxLength(301, 300)
- **WHEN** `MotzkinMatchingProblem.from` is called with `RnaString.from("A" * 301).toOption.get`
- **THEN** it returns `Left(MotzkinMatchingProblemError.ExceedsMaxLength(301, 300))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `MotzkinMatchingProblem(rna)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(rna = otherRna)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: MotzkinMatching.count returns the noncrossing-matching count mod 1 000 000

The system SHALL provide an algorithm object `bio.algorithms.nucleic.MotzkinMatching` with a method `count(problem: MotzkinMatchingProblem): Int` that returns the total number of noncrossing matchings (not necessarily perfect) of basepair edges (`A`-`U` or `C`-`G` only) in the bonding graph of `problem.rna`, modulo `1 000 000`.

The implementation MUST use a bottom-up interval dynamic-programming table `M(i)(j)` for `i ≤ j`, with the sentinel `M(i)(i - 1) = 1` (empty interval) accessed via a helper that returns `1` whenever `j < i`. The transition MUST be:

```
M(i)(j) = M(i+1)(j)                                       // position i unbonded
        + Σ_{k ∈ {i+1, ..., j}, s(k) pairs s(i)}
              M(i+1)(k-1) · M(k+1)(j)                    // position i bonded to k
        (mod 1 000 000)
```

`k` ranges over **every** index in `i+1..j` (no parity constraint — partial sub-matchings exist on intervals of any length). All intermediate multiplications MUST be performed in `Long` to avoid `Int` overflow before the modulus is applied.

#### Scenario: Canonical Rosalind MOTZ sample `AUAU`
- **WHEN** `MotzkinMatching.count` is called with the problem wrapping `AUAU`
- **THEN** it returns `7`

#### Scenario: Empty RNA string returns 1 (the empty matching)
- **WHEN** `MotzkinMatching.count` is called with the problem wrapping the empty RNA string
- **THEN** it returns `1`

#### Scenario: Single character returns 1 (only the empty matching)
- **WHEN** `MotzkinMatching.count` is called with the problem wrapping `A`
- **THEN** it returns `1`

#### Scenario: Single AU pair returns 2 (empty matching + one bond)
- **WHEN** `MotzkinMatching.count` is called with the problem wrapping `AU`
- **THEN** it returns `2`

#### Scenario: All-A string returns 1 (no possible bonds, only the empty matching)
- **WHEN** `MotzkinMatching.count` is called with the problem wrapping `AAAA`
- **THEN** it returns `1`

#### Scenario: Three A's with one U returns 4 (3 individual A-U bonds + empty)
- **WHEN** `MotzkinMatching.count` is called with the problem wrapping `AAAU`
- **THEN** it returns `4`

#### Scenario: Mixed alphabet `AUCG` returns 4
- **WHEN** `MotzkinMatching.count` is called with the problem wrapping `AUCG`
- **THEN** it returns `4`

#### Scenario: CG-only `CGCG` returns 7 (same structure as `AUAU`)
- **WHEN** `MotzkinMatching.count` is called with the problem wrapping `CGCG`
- **THEN** it returns `7`
