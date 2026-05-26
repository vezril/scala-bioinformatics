## ADDED Requirements

### Requirement: DnaNucleotide companion provides toChar
The `DnaNucleotide` companion object SHALL provide `def toChar(n: DnaNucleotide): Char` returning the canonical uppercase character for each base. The function SHALL be total over all four case objects.

#### Scenario: toChar returns 'A' for A
- **WHEN** `DnaNucleotide.toChar(DnaNucleotide.A)` is called
- **THEN** the result is `'A'`

#### Scenario: toChar returns 'C' for C
- **WHEN** `DnaNucleotide.toChar(DnaNucleotide.C)` is called
- **THEN** the result is `'C'`

#### Scenario: toChar returns 'G' for G
- **WHEN** `DnaNucleotide.toChar(DnaNucleotide.G)` is called
- **THEN** the result is `'G'`

#### Scenario: toChar returns 'T' for T
- **WHEN** `DnaNucleotide.toChar(DnaNucleotide.T)` is called
- **THEN** the result is `'T'`

#### Scenario: fromChar and toChar are inverse for valid bases
- **WHEN** any valid DNA character is round-tripped through `fromChar` then `toChar`
- **THEN** the result equals the original character

### Requirement: DnaNucleotide companion provides complement
The `DnaNucleotide` companion object SHALL provide `def complement(n: DnaNucleotide): DnaNucleotide` returning the Watson-Crick base-pair complement: A↔T and C↔G. The function SHALL be total over all four case objects and SHALL be its own inverse (applying it twice returns the original).

#### Scenario: A complements to T
- **WHEN** `DnaNucleotide.complement(DnaNucleotide.A)` is called
- **THEN** the result is `DnaNucleotide.T`

#### Scenario: T complements to A
- **WHEN** `DnaNucleotide.complement(DnaNucleotide.T)` is called
- **THEN** the result is `DnaNucleotide.A`

#### Scenario: C complements to G
- **WHEN** `DnaNucleotide.complement(DnaNucleotide.C)` is called
- **THEN** the result is `DnaNucleotide.G`

#### Scenario: G complements to C
- **WHEN** `DnaNucleotide.complement(DnaNucleotide.G)` is called
- **THEN** the result is `DnaNucleotide.C`

#### Scenario: complement is its own inverse
- **WHEN** `complement` is applied twice to any `DnaNucleotide` value
- **THEN** the result equals the original value
