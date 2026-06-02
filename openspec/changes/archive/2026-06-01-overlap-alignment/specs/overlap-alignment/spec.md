## ADDED Requirements

### Requirement: Validated overlap-alignment problem

The system SHALL provide an `OverlapAlignmentProblem` domain type that wraps two DNA
strings `s` and `t` and is constructible only through a smart constructor returning
`Either[OverlapAlignmentProblemError, OverlapAlignmentProblem]`. Validation SHALL be
first-failure-wins in this order: `s` longer than 10000, then `t` longer than 10000.
Empty strings SHALL be accepted. The type SHALL NOT expose a public `apply` or `copy`
that bypasses validation.

#### Scenario: Accepts the canonical sample strings

- **WHEN** `OverlapAlignmentProblem.from` is called with `s = CTAAGGGATTCCGGTAATTAGACAG` and `t = ATAGACCATATGTCAGTGACTGTGTAA`
- **THEN** it returns a `Right` whose `s` and `t` preserve the two input strings

#### Scenario: Accepts empty strings

- **WHEN** `OverlapAlignmentProblem.from` is called with two empty strings
- **THEN** it returns a `Right`

#### Scenario: Rejects an over-long s

- **WHEN** `OverlapAlignmentProblem.from` is called with an `s` of length 10001 and a valid `t`
- **THEN** it returns `Left(STooLong(10001, 10000))`

#### Scenario: Rejects an over-long t

- **WHEN** `OverlapAlignmentProblem.from` is called with a valid `s` and a `t` of length 10001
- **THEN** it returns `Left(TTooLong(10001, 10000))`

#### Scenario: Reports the first failure when both are over-long

- **WHEN** `OverlapAlignmentProblem.from` is called with an `s` of length 10001 and a `t` of length 10001
- **THEN** it returns `Left(STooLong(10001, 10000))` ahead of the `t` check

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `OverlapAlignmentProblem(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Compute the optimal overlap alignment

The system SHALL provide a pure, total `OverlapAlignment.align` function that takes an
`OverlapAlignmentProblem` and returns an `OverlapAlignment` carrying the optimal
overlap-alignment score and one optimal pair of augmented strings. An overlap
alignment aligns a suffix of `s` against a prefix of `t`, scoring a match as `+1`, a
substitution as `-2`, and each gap as `-2`. The score SHALL be computed with integer
arithmetic and SHALL never be negative. The aligned suffix of `s` (with gap symbols
removed) SHALL be a suffix of `s`, and the aligned prefix of `t` (with gap symbols
removed) SHALL be a prefix of `t`. The function SHALL perform no I/O.

#### Scenario: Computes the score for the canonical sample

- **WHEN** `align` is called with `s = CTAAGGGATTCCGGTAATTAGACAG` and `t = ATAGACCATATGTCAGTGACTGTGTAA`
- **THEN** the returned `score` is `1`

#### Scenario: Produces a valid overlap alignment for the canonical sample

- **WHEN** `align` is called with the canonical sample
- **THEN** `augmentedS` and `augmentedT` have equal length, no column has a gap in both strings, removing gaps from `augmentedS` yields a suffix of `s`, removing gaps from `augmentedT` yields a prefix of `t`, and scoring the alignment (match `+1`, substitution `-2`, gap `-2`) equals `1`

#### Scenario: Identical strings align fully

- **WHEN** `align` is called with `s = t = GATTACA`
- **THEN** the returned `score` is `7`, and the augmented strings both equal `GATTACA`

#### Scenario: Disjoint alphabets yield an empty overlap

- **WHEN** `align` is called with `s = AAAA` and `t = TTTT`
- **THEN** the returned `score` is `0`, and both augmented strings are empty

### Requirement: Render the overlap alignment

The system SHALL provide an `OverlapAlignment.format` method that renders the result
as the score on the first line, the augmented `s` on the second line, and the
augmented `t` on the third line.

#### Scenario: Formats score and aligned strings on separate lines

- **WHEN** `format` is called on an `OverlapAlignment` with `score = 1`, `augmentedS = ATTAGAC-AG`, and `augmentedT = AT-AGACCAT`
- **THEN** it returns exactly `1\nATTAGAC-AG\nAT-AGACCAT`

### Requirement: Read and solve the OAP dataset

The system SHALL provide an `OAPProb` runner that reads two FASTA-formatted DNA
strings from `oap_data.txt`, validates them into an `OverlapAlignmentProblem`,
computes the overlap alignment, and prints the formatted result through the `IO`
monad. Invalid or insufficient input SHALL produce a printed error message rather
than an exception.

#### Scenario: Prints the score and alignment for the dataset

- **WHEN** `OAPProb.solve()` runs against the canonical dataset
- **THEN** it prints a first line of `1` followed by a valid overlap alignment of the two strings

#### Scenario: Prints an error for invalid input

- **WHEN** `OAPProb.solve()` runs against a dataset that cannot be parsed into two valid DNA strings
- **THEN** it prints a descriptive error message and does not throw
