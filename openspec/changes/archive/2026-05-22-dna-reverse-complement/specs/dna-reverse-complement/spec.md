## ADDED Requirements

### Requirement: DNA string is reverse-complemented
The system SHALL provide a pure function `DnaReverseComplement.reverseComplement(dna: DnaString): DnaString` that returns the reverse complement of the input. The reverse complement is formed by reversing the sequence of bases and replacing each base with its complement: A↔T and C↔G. The function SHALL be total — it MUST NOT return `Either` or `Option`.

#### Scenario: Rosalind sample reverse complement
- **WHEN** `reverseComplement` is called with `DnaString("AAAACCCGGT")`
- **THEN** the result is `DnaString("ACCGGGTTTT")`

#### Scenario: Empty DnaString produces empty DnaString
- **WHEN** `reverseComplement` is called with `DnaString("")`
- **THEN** the result is `DnaString("")`

#### Scenario: Single A becomes T
- **WHEN** `reverseComplement` is called with `DnaString("A")`
- **THEN** the result is `DnaString("T")`

#### Scenario: Single T becomes A
- **WHEN** `reverseComplement` is called with `DnaString("T")`
- **THEN** the result is `DnaString("A")`

#### Scenario: Single C becomes G
- **WHEN** `reverseComplement` is called with `DnaString("C")`
- **THEN** the result is `DnaString("G")`

#### Scenario: Single G becomes C
- **WHEN** `reverseComplement` is called with `DnaString("G")`
- **THEN** the result is `DnaString("C")`

#### Scenario: Palindromic sequence is its own reverse complement
- **WHEN** `reverseComplement` is called with `DnaString("GGCC")`
- **THEN** the result is `DnaString("GGCC")`

#### Scenario: All-same-base string is fully complemented
- **WHEN** `reverseComplement` is called with `DnaString("AAAA")`
- **THEN** the result is `DnaString("TTTT")`

#### Scenario: Applying reverse complement twice returns the original
- **WHEN** `reverseComplement` is applied twice to `DnaString("AAAACCCGGT")`
- **THEN** the result is `DnaString("AAAACCCGGT")`
