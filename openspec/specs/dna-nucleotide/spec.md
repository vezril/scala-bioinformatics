## Purpose
Define the `DnaNucleotide` algebraic data type modeling the four DNA bases (A, C, G, T) along with companion utilities (`validChars`, `fromChar`) used to validate and parse individual DNA characters.

## Requirements
### Requirement: DnaNucleotide is a sealed ADT with four case objects
The system SHALL provide a `sealed trait DnaNucleotide` with exactly four case objects: `A`, `C`, `G`, and `T`. No other values of type `DnaNucleotide` SHALL be constructable.

#### Scenario: All four DNA bases exist as case objects
- **WHEN** the `DnaNucleotide` sealed trait is inspected
- **THEN** it has exactly four subtypes: `DnaNucleotide.A`, `DnaNucleotide.C`, `DnaNucleotide.G`, `DnaNucleotide.T`

#### Scenario: Pattern match on DnaNucleotide is exhaustive
- **WHEN** a `match` expression covers all four case objects
- **THEN** the compiler reports no non-exhaustive match warning

### Requirement: DnaNucleotide companion exposes validChars
The `DnaNucleotide` companion object SHALL expose `val validChars: Set[Char]` containing exactly `{'A', 'C', 'G', 'T'}`.

#### Scenario: validChars contains all four DNA characters
- **WHEN** `DnaNucleotide.validChars` is inspected
- **THEN** it equals `Set('A', 'C', 'G', 'T')`

#### Scenario: validChars does not contain RNA-specific U
- **WHEN** `DnaNucleotide.validChars.contains('U')` is evaluated
- **THEN** the result is `false`

### Requirement: DnaNucleotide companion provides fromChar
The `DnaNucleotide` companion object SHALL provide `def fromChar(c: Char): Option[DnaNucleotide]` returning `Some` for valid DNA characters and `None` for all others.

#### Scenario: fromChar returns Some for each valid base
- **WHEN** `DnaNucleotide.fromChar('A')` is called
- **THEN** the result is `Some(DnaNucleotide.A)`

#### Scenario: fromChar returns None for invalid character
- **WHEN** `DnaNucleotide.fromChar('U')` is called
- **THEN** the result is `None`

#### Scenario: fromChar returns None for lowercase
- **WHEN** `DnaNucleotide.fromChar('a')` is called
- **THEN** the result is `None`

#### Scenario: fromChar is total over all four bases
- **WHEN** `fromChar` is called with each of 'A', 'C', 'G', 'T'
- **THEN** each returns `Some` of the corresponding case object

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
