## ADDED Requirements

### Requirement: Random motif problem validation

The system SHALL provide a validated `RandomMotifProblem` domain type holding a motif (`DnaString` of length at most 10 bp), a trial count `N` with `1 ≤ N ≤ 100000`, and a GC-content fraction represented by a `Probability` (already validated to `[0,1]`). It MUST be constructed only through a smart constructor `from(motif, trials, gcContent)` returning `Either[RandomMotifProblemError, RandomMotifProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a motif and trial count within bounds

- **WHEN** `RandomMotifProblem.from` is called with the motif `ATAGCCGA`, trials `90000`, and GC-content `0.6`
- **THEN** it returns a `Right` whose `motif`, `trials`, and `gcContent` match the inputs

#### Scenario: Accepts the upper bounds

- **WHEN** `RandomMotifProblem.from` is called with a 10 bp motif and trials `100000`
- **THEN** it returns a `Right`

#### Scenario: Accepts an empty motif

- **WHEN** `RandomMotifProblem.from` is called with an empty motif and a valid trial count
- **THEN** it returns a `Right`

#### Scenario: Rejects a motif longer than 10 bp

- **WHEN** `RandomMotifProblem.from` is called with an 11 bp motif
- **THEN** it returns `Left(MotifTooLong(11, 10))`

#### Scenario: Rejects a non-positive trial count

- **WHEN** `RandomMotifProblem.from` is called with trials `0`
- **THEN** it returns `Left(NonPositiveTrials(0))`

#### Scenario: Rejects a trial count above the maximum

- **WHEN** `RandomMotifProblem.from` is called with trials `100001`
- **THEN** it returns `Left(TooManyTrials(100001, 100000))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `RandomMotifProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Random motif match result rendering

The system SHALL provide a `RandomMotifMatch` result type holding the computed probability as a `Double` and exposing a `format: String` that renders the probability rounded to three decimal places.

#### Scenario: Exposes the probability

- **WHEN** a `RandomMotifMatch` result is constructed with probability `0.25`
- **THEN** its `probability` field returns `0.25`

#### Scenario: Formats to three decimal places

- **WHEN** `format` is called on a result with probability `0.5`
- **THEN** it returns `"0.500"`

#### Scenario: Rounds to three decimal places

- **WHEN** `format` is called on a result with probability `0.1234`
- **THEN** it returns `"0.123"`

### Requirement: Matching random motifs probability computation

The system SHALL provide an algorithm that, given a `RandomMotifProblem`, computes the probability that at least one of `N` random DNA strings (each of length equal to the motif, generated with GC-content `x`) equals the motif. The per-symbol probability MUST be `x/2` for `G` and `C` and `(1 - x)/2` for `A` and `T`; the single-string match probability is the product `p` of the per-symbol probabilities; and the result MUST be `1 - (1 - p)^N`.

#### Scenario: Computes the canonical Rosalind RSTR sample

- **WHEN** the algorithm is run with trials `90000`, GC-content `0.6`, and motif `ATAGCCGA`
- **THEN** the result probability is within `0.001` of `0.689`

#### Scenario: A single trial returns the single-string match probability

- **WHEN** the algorithm is run with trials `1`, GC-content `0.5`, and motif `G`
- **THEN** the result probability is `0.25`

#### Scenario: Uniform GC-content weights every symbol equally

- **WHEN** the algorithm is run with trials `1`, GC-content `0.5`, and motif `AT`
- **THEN** the result probability is `0.0625`

#### Scenario: A symbol impossible under the GC-content makes a match impossible

- **WHEN** the algorithm is run with GC-content `0.0` and a motif containing `G` (with any positive trial count)
- **THEN** the result probability is `0.0`

#### Scenario: An empty motif matches with certainty

- **WHEN** the algorithm is run with an empty motif (with any positive trial count and GC-content)
- **THEN** the result probability is `1.0`
