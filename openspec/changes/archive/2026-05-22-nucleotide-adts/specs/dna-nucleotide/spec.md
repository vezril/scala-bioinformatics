## ADDED Requirements

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
