# Open Reading Frames

## Purpose

Locate every distinct candidate protein translatable from an open reading frame across the six reading frames of a DNA string (Rosalind ORF). An open reading frame begins at a start codon and ends at the first in-frame stop codon downstream, and the search spans both the DNA string and its reverse complement, returning each distinct protein once.

## Requirements

### Requirement: Open reading frame problem validation

The system SHALL provide a validated `OpenReadingFrameProblem` domain type that wraps a `DnaString` of length at most 1000 bp. It MUST be constructed only through a smart constructor `from(dna)` that returns `Either[OpenReadingFrameProblemError, OpenReadingFrameProblem]`, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a DNA string within the length limit

- **WHEN** `OpenReadingFrameProblem.from` is called with a `DnaString` of length 25
- **THEN** it returns a `Right` containing an `OpenReadingFrameProblem` whose `dna` is that string

#### Scenario: Accepts an empty DNA string

- **WHEN** `OpenReadingFrameProblem.from` is called with an empty `DnaString`
- **THEN** it returns a `Right` containing an `OpenReadingFrameProblem`

#### Scenario: Rejects a DNA string longer than the maximum

- **WHEN** `OpenReadingFrameProblem.from` is called with a `DnaString` of length 1001
- **THEN** it returns a `Left` containing `SequenceTooLong(1001, 1000)`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `OpenReadingFrameProblem(dna)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Open reading frame result rendering

The system SHALL provide an `OpenReadingFrames` result type that holds the distinct candidate protein strings as a `Vector[ProteinString]` and exposes a `format: String` rendering one protein per line. The empty result MUST render as the empty string.

#### Scenario: Exposes the candidate proteins

- **WHEN** an `OpenReadingFrames` result is constructed from a vector of protein strings
- **THEN** its `proteins` field returns exactly that vector

#### Scenario: Formats one protein per line

- **WHEN** `format` is called on a result holding the proteins `MA` and `M`
- **THEN** it returns the string `"MA\nM"`

#### Scenario: Empty result renders as the empty string

- **WHEN** `format` is called on a result holding no proteins
- **THEN** it returns `""`

### Requirement: Locating open reading frames across six reading frames

The system SHALL provide an algorithm that, given an `OpenReadingFrameProblem`, returns every distinct candidate protein translatable from an open reading frame. An open reading frame begins at a start codon `AUG` and ends at the first in-frame stop codon downstream, with no other stop codon in between; the candidate protein is the translation of that frame up to (but not including) the stop codon. The search MUST consider all six reading frames — the three frames of the DNA string and the three frames of its reverse complement — and MUST return each distinct protein exactly once.

#### Scenario: Finds all distinct candidate proteins in the canonical Rosalind ORF sample

- **WHEN** the algorithm is run on the DNA string `AGCCATGTAGCTAACTCAGGTTACATGGGGATGACCCCGCGACTTGGATTAGAGTCTCTTTTGGAATAAGCCTGAATGATCCGAGTAGCATCTCAG`
- **THEN** the distinct candidate proteins are exactly `MLLGSFRLIPKETLIQVAGSSPCNLS`, `M`, `MGMTPRLGLESLLE`, and `MTPRLGLESLLE`

#### Scenario: A start codon with no downstream in-frame stop yields no candidate

- **WHEN** the algorithm is run on the DNA string `ATGAAA` (whose only start codon has no following in-frame stop on either strand)
- **THEN** the result contains no candidate proteins

#### Scenario: Returns no candidates when no start codon exists

- **WHEN** the algorithm is run on the DNA string `CCCCCC` (no `AUG` on either strand)
- **THEN** the result contains no candidate proteins

#### Scenario: Identical proteins from separate open reading frames collapse to one

- **WHEN** the algorithm is run on a DNA string containing two separate ORFs that each translate to the protein `M`
- **THEN** the result contains the protein `M` exactly once

#### Scenario: Nested open reading frames each yield a candidate

- **WHEN** an open reading frame contains an internal `AUG` start codon before its stop
- **THEN** both the outer protein and the inner (shorter) protein appear as distinct candidates
