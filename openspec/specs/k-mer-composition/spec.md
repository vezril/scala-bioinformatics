## Purpose

Counts every length-k DNA word in a sequence and reports the counts in lexicographic
k-mer order, per the Rosalind "k-Mer Composition" (KMER) problem. Provides the validated
`KmerCompositionProblem` input bundle with its `KmerCompositionProblemError` ADT and the
`KmerComposition` result that renders the per-k-mer counts space-separated.

## Requirements

### Requirement: K-mer-composition input errors are represented as a dedicated ADT

The system SHALL represent the ways a k-mer-composition `k` parameter can be invalid
as a sealed `KmerCompositionProblemError` ADT with the cases `NonPositiveK(k)` and
`KExceedsMaximum(k, max)`, where `k` is the requested word length and `max` is the
relevant Rosalind cap. DNA-string validity is handled upstream by `DnaString` and is
not re-encoded by this ADT.

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `KmerCompositionProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `NonPositiveK` carrying the requested
  length and `KExceedsMaximum` carrying the requested length and the maximum

#### Scenario: K-exceeds-maximum error carries the value and maximum

- **WHEN** a `KExceedsMaximum` is produced for `k = 11`
- **THEN** it carries the value 11 and the maximum 10

### Requirement: K-mer-composition problem is a validated, invariant-bearing bundle

The system SHALL provide a `KmerCompositionProblem` pairing a validated `DnaString`
with a word length `k`, constructed only through a smart constructor `from(dna, k)`
that returns `Either[KmerCompositionProblemError, KmerCompositionProblem]`. The
constructor SHALL reject a non-positive `k` and a `k` greater than 10, reporting the
first failure encountered in the order non-positive-k → k-exceeds-maximum. The type
SHALL NOT expose a public `apply` or `copy` that bypasses validation.

#### Scenario: Valid DNA string and length yield a problem

- **WHEN** `from` is given a valid `DnaString` and `k = 4`
- **THEN** it returns a `Right` containing the `KmerCompositionProblem` carrying that
  DNA string and length

#### Scenario: A non-positive length is rejected

- **WHEN** `from` is given `k = 0`
- **THEN** it returns `Left(NonPositiveK(0))`

#### Scenario: A length over the maximum is rejected

- **WHEN** `from` is given `k = 11`
- **THEN** it returns `Left(KExceedsMaximum(11, 10))`

#### Scenario: First failure wins

- **WHEN** an input is invalid in more than one way
- **THEN** the error reflects the earliest failure in the validation order
  (non-positive-k → k-exceeds-maximum)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on
  `KmerCompositionProblem`
- **THEN** the code does not compile

### Requirement: The k-mer composition is counted in lexicographic k-mer order

The system SHALL compute, from a `KmerCompositionProblem`, a `KmerComposition`
holding one count per possible length-`k` DNA word, where the counts appear in the
lexicographic order of those words under the DNA alphabet order `A < C < G < T`. The
count at each position SHALL be the number of times that k-mer occurs as a
contiguous substring of the DNA string (overlapping occurrences counted). The result
SHALL contain exactly `4 ^ k` counts whose sum equals `max(0, length − k + 1)`.
`KmerComposition` SHALL render its counts space-separated via `format`.

#### Scenario: Canonical 4-mer sample is composed

- **WHEN** computing the 4-mer composition of the canonical Rosalind sample DNA
  string
- **THEN** the result is the 256 counts in lexicographic 4-mer order matching the
  Rosalind sample output

#### Scenario: Composition length and sum are exact

- **WHEN** computing the k-mer composition of a DNA string of length `n`
- **THEN** the result contains exactly `4 ^ k` counts whose sum is `max(0, n − k + 1)`

#### Scenario: A string shorter than k yields all zeros

- **WHEN** computing the k-mer composition of a DNA string shorter than `k`
- **THEN** every one of the `4 ^ k` counts is 0

#### Scenario: Length one counts the individual nucleotides

- **WHEN** computing the 1-mer composition of a DNA string
- **THEN** the result is the counts of `A`, `C`, `G`, `T` in that order

#### Scenario: Overlapping occurrences are counted

- **WHEN** computing the 2-mer composition of the DNA string `AAAA`
- **THEN** the count for `AA` is 3

#### Scenario: Rendering is space-separated counts

- **WHEN** a `KmerComposition` is rendered via `format`
- **THEN** its counts appear in order separated by single spaces
