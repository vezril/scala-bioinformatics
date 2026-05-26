## ADDED Requirements

### Requirement: DNA string is transcribed to RNA by replacing T with U
The system SHALL provide a pure function `RnaTranscription.transcribe(dna: DnaString): RnaString` that produces the RNA transcription of a DNA string by replacing every occurrence of `DnaNucleotide.T` with `RnaNucleotide.U` and mapping all other bases (A, C, G) to their RNA equivalents. The function SHALL be total — it MUST NOT return an `Either` or `Option`.

#### Scenario: Rosalind sample transcription
- **WHEN** `transcribe` is called with `DnaString("GATGGAACTTGACTACGTAAATT")`
- **THEN** the result is `RnaString("GAUGGAACUUGACUACGUAAAUU")`

#### Scenario: T is replaced by U in all positions
- **WHEN** `transcribe` is called with `DnaString("TTTT")`
- **THEN** the result is `RnaString("UUUU")`

#### Scenario: A, C, G bases are preserved unchanged
- **WHEN** `transcribe` is called with `DnaString("ACGACG")`
- **THEN** the result is `RnaString("ACGACG")`

#### Scenario: Empty DNA string produces empty RNA string
- **WHEN** `transcribe` is called with `DnaString("")`
- **THEN** the result is `RnaString("")`

#### Scenario: Mixed bases transcribe correctly
- **WHEN** `transcribe` is called with `DnaString("ATCG")`
- **THEN** the result is `RnaString("AUCG")`

#### Scenario: Single T transcribes to U
- **WHEN** `transcribe` is called with `DnaString("T")`
- **THEN** the result is `RnaString("U")`

#### Scenario: Single non-T base is preserved
- **WHEN** `transcribe` is called with `DnaString("A")`
- **THEN** the result is `RnaString("A")`
