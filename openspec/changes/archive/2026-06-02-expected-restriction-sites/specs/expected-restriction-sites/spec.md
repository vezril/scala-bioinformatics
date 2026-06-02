## ADDED Requirements

### Requirement: Expected restriction sites problem validation

The system SHALL provide a validated `ExpectedRestrictionSitesProblem` domain type holding a motif (`DnaString` of even length at most 10), a string length `n` with `1 ≤ n ≤ 1,000,000`, and an array of GC-content fractions represented by a `Vector[Probability]` of size at most 20. It MUST be constructed only through a smart constructor `from(motif, length, gcContents)` returning `Either[ExpectedRestrictionSitesProblemError, ExpectedRestrictionSitesProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts inputs within bounds

- **WHEN** `ExpectedRestrictionSitesProblem.from` is called with motif `AG`, length `10`, and GC-contents `[0.25, 0.5, 0.75]`
- **THEN** it returns a `Right` whose `motif`, `length`, and `gcContents` match the inputs

#### Scenario: Accepts the upper bounds

- **WHEN** `ExpectedRestrictionSitesProblem.from` is called with a 10 bp motif, length `1000000`, and 20 GC-contents
- **THEN** it returns a `Right`

#### Scenario: Accepts an empty motif

- **WHEN** `ExpectedRestrictionSitesProblem.from` is called with an empty motif and otherwise valid inputs
- **THEN** it returns a `Right`

#### Scenario: Rejects a motif longer than 10 bp

- **WHEN** `ExpectedRestrictionSitesProblem.from` is called with a 12 bp motif
- **THEN** it returns `Left(MotifTooLong(12, 10))`

#### Scenario: Rejects an odd-length motif

- **WHEN** `ExpectedRestrictionSitesProblem.from` is called with the 3 bp motif `AGT`
- **THEN** it returns `Left(OddMotifLength(3))`

#### Scenario: Rejects a non-positive length

- **WHEN** `ExpectedRestrictionSitesProblem.from` is called with length `0`
- **THEN** it returns `Left(NonPositiveLength(0))`

#### Scenario: Rejects a length above the maximum

- **WHEN** `ExpectedRestrictionSitesProblem.from` is called with length `1000001`
- **THEN** it returns `Left(LengthTooLarge(1000001, 1000000))`

#### Scenario: Rejects too many GC-contents

- **WHEN** `ExpectedRestrictionSitesProblem.from` is called with 21 GC-contents
- **THEN** it returns `Left(TooManyGcContents(21, 20))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `ExpectedRestrictionSitesProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Expected restriction sites result rendering

The system SHALL provide an `ExpectedRestrictionSites` result type holding the per-GC-content expected counts as a `Vector[Double]` and exposing a `format: String` that renders each count to three decimal places, separated by single spaces. The empty result MUST render as the empty string.

#### Scenario: Exposes the expected counts

- **WHEN** an `ExpectedRestrictionSites` result is constructed from a vector of counts
- **THEN** its `expectations` field returns exactly that vector

#### Scenario: Formats counts space-separated to three decimals

- **WHEN** `format` is called on a result holding `[0.421875, 0.5625]`
- **THEN** it returns `"0.422 0.563"`

#### Scenario: Empty result renders as the empty string

- **WHEN** `format` is called on a result holding no counts
- **THEN** it returns `""`

### Requirement: Expected restriction sites count computation

The system SHALL provide an algorithm that, given an `ExpectedRestrictionSitesProblem`, returns one expected count per GC-content. For a motif of length `L`, string length `n`, and GC-content `x`, the expected count MUST be `max(0, n - L + 1) · p`, where `p = ∏ⱼ P(sⱼ | x)` with per-symbol probabilities `P(G) = P(C) = x/2` and `P(A) = P(T) = (1 - x)/2`. The result MUST preserve the order of the input GC-contents.

#### Scenario: Computes the canonical Rosalind EVAL sample

- **WHEN** the algorithm is run with length `10`, motif `AG`, and GC-contents `[0.25, 0.5, 0.75]`
- **THEN** the expected counts are within `0.001` of `[0.422, 0.563, 0.422]`

#### Scenario: Yields zero when the motif is longer than the string

- **WHEN** the algorithm is run with length `1`, motif `AG`, and GC-content `[0.5]`
- **THEN** the expected count is `0.0`

#### Scenario: Scales the single-position probability by the position count

- **WHEN** the algorithm is run with length `10`, motif `AT`, and GC-content `[0.5]`
- **THEN** the expected count is within `0.001` of `0.5625`

#### Scenario: Yields zero when a motif symbol is impossible under the GC-content

- **WHEN** the algorithm is run with length `10`, motif `GG`, and GC-content `[0.0]`
- **THEN** the expected count is `0.0`

#### Scenario: An empty GC-content array yields an empty result

- **WHEN** the algorithm is run with an empty GC-content array
- **THEN** the result holds no expected counts
