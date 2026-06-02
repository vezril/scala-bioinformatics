## ADDED Requirements

### Requirement: Maximal repeat problem validation

The system SHALL provide a validated `MaximalRepeatProblem` domain type wrapping a `DnaString` (length at most 1000 bp) and a minimum repeat length (at least 1). It MUST be constructed only through a smart constructor `from(dna, minLength)` returning `Either[MaximalRepeatProblemError, MaximalRepeatProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a DNA string and minimum length within bounds

- **WHEN** `MaximalRepeatProblem.from` is called with a 25 bp `DnaString` and minimum length `20`
- **THEN** it returns a `Right` whose `dna` and `minLength` match the inputs

#### Scenario: Rejects a DNA string longer than 1000 bp

- **WHEN** `MaximalRepeatProblem.from` is called with a 1001 bp `DnaString` and minimum length `20`
- **THEN** it returns `Left(SequenceTooLong(1001, 1000))`

#### Scenario: Rejects a non-positive minimum length

- **WHEN** `MaximalRepeatProblem.from` is called with a valid `DnaString` and minimum length `0`
- **THEN** it returns `Left(NonPositiveMinLength(0))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `MaximalRepeatProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Maximal repeats result rendering

The system SHALL provide a `MaximalRepeats` result type holding the maximal-repeat substrings as a `Vector[String]` and exposing a `format: String` rendering one repeat per line. The empty result MUST render as the empty string.

#### Scenario: Exposes the repeats

- **WHEN** a `MaximalRepeats` result is constructed from a vector of repeat strings
- **THEN** its `repeats` field returns exactly that vector

#### Scenario: Formats one repeat per line

- **WHEN** `format` is called on a result holding `AG` and `TAG`
- **THEN** it returns `"AG\nTAG"`

#### Scenario: Empty result renders as the empty string

- **WHEN** `format` is called on a result holding no repeats
- **THEN** it returns `""`

### Requirement: Maximal repeat identification

The system SHALL provide an algorithm that, given a `MaximalRepeatProblem`, returns every maximal repeat of the DNA string whose length is at least `minLength`. A maximal repeat is the path-label of a left-diverse internal node of the suffix tree of `s$`: an internal node (right-maximal) whose subtree leaves are preceded by at least two distinct characters (left-diverse), where a start-of-string occurrence counts as a distinct preceding character.

#### Scenario: Identifies the canonical Rosalind MREP sample (minimum length 20)

- **WHEN** the algorithm is run on `TAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTATTATATAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTAT` with minimum length `20`
- **THEN** the maximal repeats are exactly `TAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTAT` and `ATGGGTCCAGAGTTTTGTAATTT`

#### Scenario: Finds short maximal repeats with a small minimum length

- **WHEN** the algorithm is run on `TAGTTAGCGAGA` with minimum length `2`
- **THEN** the maximal repeats include both `AG` and `TAG`

#### Scenario: Returns nothing when no substring repeats

- **WHEN** the algorithm is run on `ACGT` with minimum length `1`
- **THEN** the result contains no maximal repeats

#### Scenario: Excludes maximal repeats shorter than the minimum length

- **WHEN** the algorithm is run on `TAGTTAGCGAGA` with minimum length `20`
- **THEN** the result contains no maximal repeats
