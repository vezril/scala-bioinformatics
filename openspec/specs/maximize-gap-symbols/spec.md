# Maximizing the Gap Symbols of an Optimal Alignment

## Purpose

This capability computes the maximum number of gap symbols that can appear in any maximum-score alignment of two DNA strings under scoring with `m>0, d<0, g<0` (Rosalind MGAP). Because the answer is parameter-independent, it is derived from the identity `|s| + |t| − 2·LCS(s,t)`, where `LCS` is the longest-common-subsequence length.

## Requirements

### Requirement: Maximum-gap problem validation

The system SHALL provide a validated `MaxGapProblem` domain type wrapping two `DnaString`s, each of length at most 5000 bp. It MUST be constructed only through a smart constructor `from(s, t)` returning `Either[MaxGapProblemError, MaxGapProblem]`, applying first-failure-wins validation (checking `s` then `t`), and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts two DNA strings within the length limit

- **WHEN** `MaxGapProblem.from` is called with `AACGTA` and `ACACCTA`
- **THEN** it returns a `Right` whose `s` and `t` match the inputs

#### Scenario: Rejects a first sequence longer than 5000 bp

- **WHEN** `MaxGapProblem.from` is called with a 5001 bp `s` and a short `t`
- **THEN** it returns `Left(SequenceTooLong(5001, 5000))`

#### Scenario: Rejects a second sequence longer than 5000 bp

- **WHEN** `MaxGapProblem.from` is called with a short `s` and a 5001 bp `t`
- **THEN** it returns `Left(SequenceTooLong(5001, 5000))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `MaxGapProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Maximum-gap result rendering

The system SHALL provide a `MaxGapSymbols` result type holding the maximum gap-symbol count as an `Int` and exposing a `format: String` that renders the count.

#### Scenario: Exposes the count

- **WHEN** a `MaxGapSymbols` result is constructed with count `3`
- **THEN** its `count` field returns `3`

#### Scenario: Formats the count

- **WHEN** `format` is called on a result with count `3`
- **THEN** it returns `"3"`

### Requirement: Maximum gap-symbol computation

The system SHALL provide an algorithm that, given a `MaxGapProblem`, returns the maximum number of gap symbols appearing in any maximum-score alignment of `s` and `t` (for scoring with `m>0, d<0, g<0`), equal to `|s| + |t| − 2·LCS(s,t)` where `LCS` is the longest-common-subsequence length.

#### Scenario: Computes the canonical Rosalind MGAP sample

- **WHEN** the algorithm is run with `AACGTA` and `ACACCTA`
- **THEN** the maximum gap count is `3`

#### Scenario: Identical strings need no gaps

- **WHEN** the algorithm is run with `ACGT` and `ACGT`
- **THEN** the maximum gap count is `0`

#### Scenario: Fully disjoint strings are all gaps

- **WHEN** the algorithm is run with `AAAA` and `CCCC`
- **THEN** the maximum gap count is `8`

#### Scenario: An empty sequence gives a gap per symbol of the other

- **WHEN** the algorithm is run with `ACGT` and the empty string
- **THEN** the maximum gap count is `4`
