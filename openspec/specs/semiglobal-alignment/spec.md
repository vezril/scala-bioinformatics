## Purpose

Validates a pair of DNA strings and computes their optimal semiglobal alignment — an alignment of all of `s` against all of `t` in which gaps appearing as prefixes or suffixes of either string do not contribute to the score (match `+1`, substitution `-1`, gap `-1`) — returning the optimal score and the two aligned strings, with an IO runner over the SMGB dataset.

## Requirements

### Requirement: Validated semiglobal-alignment problem

The system SHALL provide a `SemiglobalAlignmentProblem` domain type that wraps two DNA
strings `s` and `t` and is constructible only through a smart constructor returning
`Either[SemiglobalAlignmentProblemError, SemiglobalAlignmentProblem]`. Validation SHALL
be first-failure-wins in this order: `s` longer than 10000, then `t` longer than 10000.
Empty strings SHALL be accepted. The type SHALL NOT expose a public `apply` or `copy`
that bypasses validation.

#### Scenario: Accepts the canonical sample strings

- **WHEN** `SemiglobalAlignmentProblem.from` is called with `s = CAGCACTTGGATTCTCGG` and `t = CAGCGTGG`
- **THEN** it returns a `Right` whose `s` and `t` preserve the two input strings

#### Scenario: Accepts empty strings

- **WHEN** `SemiglobalAlignmentProblem.from` is called with two empty strings
- **THEN** it returns a `Right`

#### Scenario: Rejects an over-long s

- **WHEN** `SemiglobalAlignmentProblem.from` is called with an `s` of length 10001 and a valid `t`
- **THEN** it returns `Left(STooLong(10001, 10000))`

#### Scenario: Rejects an over-long t

- **WHEN** `SemiglobalAlignmentProblem.from` is called with a valid `s` and a `t` of length 10001
- **THEN** it returns `Left(TTooLong(10001, 10000))`

#### Scenario: Reports the first failure when both are over-long

- **WHEN** `SemiglobalAlignmentProblem.from` is called with an `s` of length 10001 and a `t` of length 10001
- **THEN** it returns `Left(STooLong(10001, 10000))` ahead of the `t` check

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `SemiglobalAlignmentProblem(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Compute the optimal semiglobal alignment

The system SHALL provide a pure, total `SemiglobalAlignment.align` function that takes a
`SemiglobalAlignmentProblem` and returns a `SemiglobalAlignment` carrying the optimal
semiglobal-alignment score and one optimal pair of augmented strings. A semiglobal
alignment aligns all of `s` against all of `t` such that gaps appearing as a prefix or
suffix of either string do not contribute to the score, scoring a match as `+1`, a
substitution as `-1`, and each scored gap as `-1`. The score SHALL be computed with
integer arithmetic. The augmented `s` (with gap symbols removed) SHALL equal `s`, and
the augmented `t` (with gap symbols removed) SHALL equal `t`. The function SHALL perform
no I/O.

#### Scenario: Computes the score for the canonical sample

- **WHEN** `align` is called with `s = CAGCACTTGGATTCTCGG` and `t = CAGCGTGG`
- **THEN** the returned `score` is `4`

#### Scenario: Produces a valid semiglobal alignment for the canonical sample

- **WHEN** `align` is called with the canonical sample
- **THEN** `augmentedS` and `augmentedT` have equal length, no column has a gap in both strings, removing gaps from `augmentedS` yields `s`, removing gaps from `augmentedT` yields `t`, and scoring the alignment with free leading/trailing gaps (match `+1`, substitution `-1`, internal gap `-1`) equals `4`

#### Scenario: Identical strings align fully

- **WHEN** `align` is called with `s = t = GATTACA`
- **THEN** the returned `score` is `7`, and both augmented strings equal `GATTACA`

#### Scenario: A contained string aligns with free end gaps

- **WHEN** `align` is called with `s = ACGTACGT` and `t = GTAC`
- **THEN** the returned `score` is `4`, removing gaps from `augmentedS` yields `ACGTACGT`, and removing gaps from `augmentedT` yields `GTAC`

### Requirement: Render the semiglobal alignment

The system SHALL provide a `SemiglobalAlignment.format` method that renders the result
as the score on the first line, the augmented `s` on the second line, and the augmented
`t` on the third line.

#### Scenario: Formats score and aligned strings on separate lines

- **WHEN** `format` is called on a `SemiglobalAlignment` with `score = 4`, `augmentedS = CAGCA-CTTGGATTCTCGG`, and `augmentedT = ---CAGCGTGG--------`
- **THEN** it returns exactly `4\nCAGCA-CTTGGATTCTCGG\n---CAGCGTGG--------`

### Requirement: Read and solve the SMGB dataset

The system SHALL provide a `SMGBProb` runner that reads two FASTA-formatted DNA strings
from `smgb_data.txt`, validates them into a `SemiglobalAlignmentProblem`, computes the
semiglobal alignment, and prints the formatted result through the `IO` monad. Invalid
or insufficient input SHALL produce a printed error message rather than an exception.

#### Scenario: Prints the score and alignment for the dataset

- **WHEN** `SMGBProb.solve()` runs against the canonical dataset
- **THEN** it prints a first line of `4` followed by a valid semiglobal alignment of the two strings

#### Scenario: Prints an error for invalid input

- **WHEN** `SMGBProb.solve()` runs against a dataset that cannot be parsed into two valid DNA strings
- **THEN** it prints a descriptive error message and does not throw
