## ADDED Requirements

### Requirement: DNA string is a validated domain type
The system SHALL model a DNA string as an ADT (`DnaString`) with a smart constructor that returns `Either[DnaError, DnaString]`. Valid input contains only the characters A, C, G, T (uppercase). The type SHALL NOT be constructable without going through the smart constructor.

#### Scenario: Valid DNA string is accepted
- **WHEN** a string containing only 'A', 'C', 'G', 'T' characters is passed to the smart constructor
- **THEN** the result is `Right(DnaString(...))`

#### Scenario: String with invalid character is rejected
- **WHEN** a string containing any character outside {A, C, G, T} is passed to the smart constructor
- **THEN** the result is `Left(DnaError.InvalidCharacter(...))`

#### Scenario: Empty string is valid
- **WHEN** an empty string is passed to the smart constructor
- **THEN** the result is `Right(DnaString(""))` (zero counts for all nucleotides)

#### Scenario: String exceeds 1000 characters
- **WHEN** a string longer than 1000 characters is passed to the smart constructor
- **THEN** the result is `Left(DnaError.ExceedsMaxLength(...))`

### Requirement: Nucleotide counts are returned as a domain type
The system SHALL model the result as an ADT (`NucleotideCounts`) with fields for `a`, `c`, `g`, `t` counts (all non-negative integers). The counting function SHALL be a pure function with signature `def count(dna: DnaString): NucleotideCounts`.

#### Scenario: Counts for a known DNA string
- **WHEN** `count` is called with `DnaString("AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC")`
- **THEN** the result is `NucleotideCounts(a=20, c=12, g=17, t=21)` (Rosalind sample)

#### Scenario: Single-character string counts correctly
- **WHEN** `count` is called with `DnaString("A")`
- **THEN** the result is `NucleotideCounts(a=1, c=0, g=0, t=0)`

#### Scenario: Empty string returns all zeros
- **WHEN** `count` is called with `DnaString("")`
- **THEN** the result is `NucleotideCounts(a=0, c=0, g=0, t=0)`

#### Scenario: String with only one nucleotide type
- **WHEN** `count` is called with `DnaString("TTTTTT")`
- **THEN** the result is `NucleotideCounts(a=0, c=0, g=0, t=6)`

### Requirement: Counts are formatted as space-separated integers
The system SHALL provide a function that formats `NucleotideCounts` as a space-separated string of four integers in A C G T order.

#### Scenario: Formatted output matches expected format
- **WHEN** `NucleotideCounts(a=20, c=12, g=17, t=21)` is formatted
- **THEN** the output is `"20 12 17 21"`

#### Scenario: Zero counts formatted correctly
- **WHEN** `NucleotideCounts(a=0, c=0, g=0, t=0)` is formatted
- **THEN** the output is `"0 0 0 0"`
