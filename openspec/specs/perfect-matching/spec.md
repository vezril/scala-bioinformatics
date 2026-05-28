# perfect-matching Specification

## Purpose

Counts the perfect matchings of basepair edges in the bonding graph of a
balanced RNA string (Rosalind spec 34 — PMCH, "Perfect Matchings and RNA
Secondary Structures"). Provides a validated `PerfectMatchingProblem` input
bundle (length cap, balanced AU and CG checks) and the `PerfectMatching.count`
closed-form `(#A)! · (#C)!` algorithm returning a `BigInt`.

## Requirements

### Requirement: Validated PerfectMatchingProblem input bundle

The system SHALL provide a validated domain type `bio.domain.nucleic.PerfectMatchingProblem` constructed only through a smart constructor `PerfectMatchingProblem.from(rna: RnaString): Either[PerfectMatchingProblemError, PerfectMatchingProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `rna.value.length <= 80`, else `ExceedsMaxLength(length, max)`.
2. The A and U counts are equal, else `UnpairedAU(aCount, uCount)`.
3. The C and G counts are equal, else `UnpairedCG(cCount, gCount)`.

The empty RNA string MUST be accepted (it produces `auCount = 0`, `cgCount = 0`). The constructed value MUST expose `rna: RnaString`, `auCount: Int` (= the common value of `#A` and `#U`), and `cgCount: Int` (= the common value of `#C` and `#G`). The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind PMCH sample
- **WHEN** `PerfectMatchingProblem.from` is called with `RnaString.from("AGCUAGUCAU").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.auCount == 3` and `problem.cgCount == 2`

#### Scenario: Accepts the empty RNA string
- **WHEN** `PerfectMatchingProblem.from` is called with `RnaString.from("").toOption.get`
- **THEN** it returns `Right(problem)` where `problem.auCount == 0` and `problem.cgCount == 0`

#### Scenario: Accepts the maximum-length 80-character input
- **WHEN** `PerfectMatchingProblem.from` is called with `RnaString.from("AU" * 40).toOption.get`
- **THEN** it returns `Right(problem)` where `problem.rna.value.length == 80` and `problem.auCount == 40` and `problem.cgCount == 0`

#### Scenario: Rejects an 81-character input as ExceedsMaxLength(81, 80)
- **WHEN** `PerfectMatchingProblem.from` is called with an RNA string of length 81 that balances A/U and C/G
- **THEN** it returns `Left(PerfectMatchingProblemError.ExceedsMaxLength(81, 80))`

#### Scenario: Rejects an unbalanced AU string as UnpairedAU(2, 1)
- **WHEN** `PerfectMatchingProblem.from` is called with `RnaString.from("AAU").toOption.get`
- **THEN** it returns `Left(PerfectMatchingProblemError.UnpairedAU(2, 1))`

#### Scenario: Rejects an unbalanced CG string as UnpairedCG(2, 1)
- **WHEN** `PerfectMatchingProblem.from` is called with `RnaString.from("CCG").toOption.get`
- **THEN** it returns `Left(PerfectMatchingProblemError.UnpairedCG(2, 1))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `PerfectMatchingProblem(rna, 0, 0)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(auCount = 0)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: PerfectMatching.count returns the basepair-matching count

The system SHALL provide an algorithm object `bio.algorithms.nucleic.PerfectMatching` with a method `count(problem: PerfectMatchingProblem): BigInt` that returns the total number of perfect matchings of basepair edges in the bonding graph of `problem.rna`.

The implementation MUST compute `factorial(problem.auCount) * factorial(problem.cgCount)` and return the result as a `BigInt`. The return type MUST be `BigInt` (because the worst case `40!` exceeds the `Long` range).

#### Scenario: Canonical Rosalind PMCH sample
- **WHEN** `PerfectMatching.count` is called with the problem wrapping `AGCUAGUCAU`
- **THEN** it returns `BigInt(12)`

#### Scenario: Empty input returns 1 (the empty matching)
- **WHEN** `PerfectMatching.count` is called with the problem wrapping the empty RNA string
- **THEN** it returns `BigInt(1)`

#### Scenario: Single AU pair returns 1
- **WHEN** `PerfectMatching.count` is called with the problem wrapping `AU`
- **THEN** it returns `BigInt(1)`

#### Scenario: Single CG pair returns 1
- **WHEN** `PerfectMatching.count` is called with the problem wrapping `CG`
- **THEN** it returns `BigInt(1)`

#### Scenario: Two AU pairs returns 2! = 2
- **WHEN** `PerfectMatching.count` is called with the problem wrapping `AAUU`
- **THEN** it returns `BigInt(2)`

#### Scenario: Two CG pairs returns 2! = 2
- **WHEN** `PerfectMatching.count` is called with the problem wrapping `CCGG`
- **THEN** it returns `BigInt(2)`

#### Scenario: 40 AU pairs returns 40!
- **WHEN** `PerfectMatching.count` is called with the problem wrapping `"AU" * 40`
- **THEN** it returns `BigInt("815915283247897734345611269596115894272000000000")`
