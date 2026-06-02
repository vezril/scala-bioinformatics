## ADDED Requirements

### Requirement: Suffix tree problem validation

The system SHALL provide a validated `SuffixTreeProblem` domain type wrapping the DNA string `s` as a `DnaString` of length at most 1000 bp; the terminator `$` is appended internally to form `s$`. It MUST be constructed only through a smart constructor `from(dna)` returning `Either[SuffixTreeProblemError, SuffixTreeProblem]`, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a DNA string within the length limit

- **WHEN** `SuffixTreeProblem.from` is called with the `DnaString` `ATAAATG`
- **THEN** it returns a `Right` whose `dna` is that string

#### Scenario: Accepts an empty DNA string

- **WHEN** `SuffixTreeProblem.from` is called with an empty `DnaString`
- **THEN** it returns a `Right`

#### Scenario: Rejects a DNA string longer than 1000 bp

- **WHEN** `SuffixTreeProblem.from` is called with a `DnaString` of length 1001
- **THEN** it returns `Left(SequenceTooLong(1001, 1000))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `SuffixTreeProblem(dna)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Suffix tree encoding result rendering

The system SHALL provide a `SuffixTreeEncoding` result type holding the edge-label substrings as a `Vector[String]` and exposing a `format: String` rendering one label per line. The empty result MUST render as the empty string.

#### Scenario: Exposes the edge labels

- **WHEN** a `SuffixTreeEncoding` result is constructed from a vector of labels
- **THEN** its `edges` field returns exactly that vector

#### Scenario: Formats one label per line

- **WHEN** `format` is called on a result holding `A$` and `$`
- **THEN** it returns `"A$\n$"`

#### Scenario: Empty result renders as the empty string

- **WHEN** `format` is called on a result holding no labels
- **THEN** it returns `""`

### Requirement: Suffix tree construction

The system SHALL provide an algorithm that, given a `SuffixTreeProblem`, constructs the suffix tree of `s$` and returns the substrings of `s$` that label its edges. The returned labels MAY be in any order, but as a multiset MUST equal the edge labels of the (unique) suffix tree of `s$`.

#### Scenario: Encodes the canonical Rosalind SUFF sample

- **WHEN** the algorithm is run on the DNA string `ATAAATG`
- **THEN** the returned edge labels, as a multiset, are exactly `AAATG$`, `G$`, `T`, `ATG$`, `TG$`, `A`, `A`, `AAATG$`, `G$`, `T`, `G$`, `$`

#### Scenario: Encodes the empty string as a single terminator edge

- **WHEN** the algorithm is run on the empty DNA string
- **THEN** the returned edge labels are exactly `$`

#### Scenario: Encodes a single-character string

- **WHEN** the algorithm is run on the DNA string `A`
- **THEN** the returned edge labels, as a multiset, are exactly `A$` and `$`

#### Scenario: Encodes a repeated-character string

- **WHEN** the algorithm is run on the DNA string `AAA`
- **THEN** the returned edge labels, as a multiset, are exactly `A`, `A`, `A$`, `$`, `$`, `$`
