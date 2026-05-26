## MODIFIED Requirements

### Requirement: Nucleotide counts are returned as a domain type
The system SHALL model the DNA counting result as `DnaNucleotideCounts` (renamed from `NucleotideCounts`) with fields `a`, `c`, `g`, `t`. The counting function SHALL be a pure function with signature `def count(dna: DnaString): DnaNucleotideCounts`. The implementation SHALL dispatch on `DnaNucleotide` values obtained via `DnaNucleotide.fromChar`, not raw `Char` literals.

#### Scenario: Counts for a known DNA string
- **WHEN** `count` is called with `DnaString("AGCTTTTCATTCTGACTGCAACGGGCAATATGTCTCTGTGTGGATTAAAAAAAGAGTGTCTGATAGCAGC")`
- **THEN** the result is `DnaNucleotideCounts(a=20, c=12, g=17, t=21)`

#### Scenario: Single-character string counts correctly
- **WHEN** `count` is called with `DnaString("A")`
- **THEN** the result is `DnaNucleotideCounts(a=1, c=0, g=0, t=0)`

#### Scenario: Empty string returns all zeros
- **WHEN** `count` is called with `DnaString("")`
- **THEN** the result is `DnaNucleotideCounts(a=0, c=0, g=0, t=0)`

#### Scenario: String with only one nucleotide type
- **WHEN** `count` is called with `DnaString("TTTTTT")`
- **THEN** the result is `DnaNucleotideCounts(a=0, c=0, g=0, t=6)`

### Requirement: Counts are formatted as space-separated integers
The system SHALL provide a `format: String` method on `DnaNucleotideCounts` returning four space-separated integers in A C G T order.

#### Scenario: Formatted output matches expected format
- **WHEN** `DnaNucleotideCounts(a=20, c=12, g=17, t=21).format` is called
- **THEN** the output is `"20 12 17 21"`

#### Scenario: Zero counts formatted correctly
- **WHEN** `DnaNucleotideCounts(a=0, c=0, g=0, t=0).format` is called
- **THEN** the output is `"0 0 0 0"`
