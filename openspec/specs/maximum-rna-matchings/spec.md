# maximum-rna-matchings Specification

## Purpose

Counts the total number of maximum matchings of basepair edges (`A`-`U` or
`C`-`G`) in the bonding graph of an RNA string (Rosalind spec — MMCH, "Maximum
Matchings and RNA Secondary Structures"). Provides a validated
`MaximumMatchingProblem` input bundle (length cap only — no balance
constraint) carrying the precomputed `#A`, `#U`, `#C`, `#G` symbol counts, a
`MaximumMatchings` `BigInt` result type, and the `MaximumMatching.count`
algorithm that multiplies the falling factorials of the larger over the smaller
count for each complementary pair.

## Requirements

### Requirement: Maximum Matching Problem domain type

The system SHALL provide a validated `MaximumMatchingProblem` domain type in `bio.domain.nucleic` wrapping an `RnaString` (length ≤ 100) together with its precomputed `#A`, `#U`, `#C`, `#G` symbol counts. It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(rna: RnaString): Either[MaximumMatchingProblemError, MaximumMatchingProblem]`. No base-count balance is required (the unbalanced case is valid). Character validity is enforced upstream by `RnaString`.

#### Scenario: Accepts an RNA string within the length bound
- **WHEN** `MaximumMatchingProblem.from` is called with an `RnaString` of length ≤ 100
- **THEN** it returns a `Right` holding a `MaximumMatchingProblem` with the four symbol counts

#### Scenario: Accepts an unbalanced RNA string
- **WHEN** `MaximumMatchingProblem.from` is called with `AUU` (`#A ≠ #U`)
- **THEN** it returns a `Right` holding a `MaximumMatchingProblem`

#### Scenario: Accepts the empty RNA string
- **WHEN** `MaximumMatchingProblem.from` is called with an empty `RnaString`
- **THEN** it returns a `Right` holding a `MaximumMatchingProblem` with all counts zero

#### Scenario: Rejects an RNA string longer than the bound
- **WHEN** `MaximumMatchingProblem.from` is called with an `RnaString` of length 101
- **THEN** it returns a `Left` holding `MaximumMatchingProblemError.ExceedsMaxLength(101, 100)`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.nucleic.MaximumMatchingProblem(rna, 0, 0, 0, 0)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `MaximumMatchingProblem`
- **THEN** the code does not compile

### Requirement: Maximum Matching Problem error ADT

The system SHALL provide a `MaximumMatchingProblemError` sealed ADT in `bio.domain.nucleic` enumerating the validation failures for `MaximumMatchingProblem`. It SHALL include an `ExceedsMaxLength(length: Int, max: Int)` case carrying the offending length and the maximum allowed length.

#### Scenario: Reports the offending and maximum lengths
- **WHEN** an RNA string of length 150 is rejected for exceeding the bound
- **THEN** the error is `MaximumMatchingProblemError.ExceedsMaxLength(150, 100)`

### Requirement: Maximum Matchings result type

The system SHALL provide a `MaximumMatchings` result type in `bio.domain.nucleic` holding the exact total count of maximum matchings as a `BigInt`, and exposing a `format: String` that renders the count as its decimal string.

#### Scenario: Formats the count as a decimal string
- **WHEN** a `MaximumMatchings` holding the count `6` is formatted
- **THEN** `format` returns `"6"`

### Requirement: Maximum matching count algorithm

The system SHALL provide a `MaximumMatching` algorithm in `bio.algorithms.nucleic` with a pure, total method `count(problem: MaximumMatchingProblem): MaximumMatchings`. It SHALL return `P(max(a,u), min(a,u)) · P(max(c,g), min(c,g))`, where `a, u, c, g` are the symbol counts and `P(hi, lo) = hi · (hi-1) · … · (hi-lo+1)` is the falling factorial (with `P(hi, 0) = 1`), computed exactly in `BigInt` arithmetic.

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `count` is run on the RNA string `AUGCUUC`
- **THEN** the result's count is `6`

#### Scenario: Empty string has exactly one (empty) matching
- **WHEN** `count` is run on the empty RNA string
- **THEN** the result's count is `1`

#### Scenario: An unbalanced string uses the falling factorial
- **WHEN** `count` is run on `AUU` (`a=1, u=2`, no C or G)
- **THEN** the result's count is `2`

#### Scenario: A balanced string reduces to the perfect-matching count
- **WHEN** `count` is run on `AAUU` (`a=2, u=2`)
- **THEN** the result's count is `2` (equal to `2!`)

#### Scenario: A string with no possible pairs has exactly one matching
- **WHEN** `count` is run on `AAA` (no U, C, or G to pair with)
- **THEN** the result's count is `1`
