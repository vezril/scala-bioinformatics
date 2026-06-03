## ADDED Requirements

### Requirement: Supersequence problem validation

The system SHALL provide a validated `SupersequenceProblem` domain type wrapping two `DnaString`s, each of length at most 1000 bp. It MUST be constructed only through a smart constructor `from(s, t)` returning `Either[SupersequenceProblemError, SupersequenceProblem]`, applying first-failure-wins validation (checking `s` then `t`), and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts two DNA strings within the length limit

- **WHEN** `SupersequenceProblem.from` is called with `ATCTGAT` and `TGCATA`
- **THEN** it returns a `Right` whose `s` and `t` match the inputs

#### Scenario: Rejects a first sequence longer than 1000 bp

- **WHEN** `SupersequenceProblem.from` is called with a 1001 bp `s` and a short `t`
- **THEN** it returns `Left(SequenceTooLong(1001, 1000))`

#### Scenario: Rejects a second sequence longer than 1000 bp

- **WHEN** `SupersequenceProblem.from` is called with a short `s` and a 1001 bp `t`
- **THEN** it returns `Left(SequenceTooLong(1001, 1000))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `SupersequenceProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Supersequence result rendering

The system SHALL provide a `Supersequence` result type holding the shortest common supersequence string and exposing a `format: String` that returns it verbatim.

#### Scenario: Exposes the supersequence

- **WHEN** a `Supersequence` result is constructed with `ATGCATGAT`
- **THEN** its `value` field returns `ATGCATGAT`

#### Scenario: Formats the supersequence verbatim

- **WHEN** `format` is called on a result holding `ATGCATGAT`
- **THEN** it returns `"ATGCATGAT"`

### Requirement: Shortest common supersequence computation

The system SHALL provide an algorithm that, given a `SupersequenceProblem`, returns a shortest common supersequence of `s` and `t` — a shortest string containing both as subsequences, of length `|s| + |t| − LCS(s,t)`.

#### Scenario: Computes a shortest common supersequence of the canonical sample

- **WHEN** the algorithm is run with `ATCTGAT` and `TGCATA`
- **THEN** the result has length 9 and contains both `ATCTGAT` and `TGCATA` as subsequences

#### Scenario: The supersequence of a string and the empty string is the string

- **WHEN** the algorithm is run with `ACGT` and the empty string
- **THEN** the result is `ACGT`

#### Scenario: The supersequence of identical strings is the string itself

- **WHEN** the algorithm is run with `ACGT` and `ACGT`
- **THEN** the result is `ACGT`

#### Scenario: Disjoint strings have no shared characters to merge

- **WHEN** the algorithm is run with `AA` and `CC`
- **THEN** the result has length 4 and contains both `AA` and `CC` as subsequences
