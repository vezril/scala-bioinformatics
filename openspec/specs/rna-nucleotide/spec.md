## Purpose
Define the `RnaNucleotide` algebraic data type modeling the four RNA bases (A, C, G, U) as a type entirely independent of `DnaNucleotide`, along with companion utilities (`validChars`, `fromChar`) used to validate and parse individual RNA characters.

## Requirements
### Requirement: RnaNucleotide is a sealed ADT with four case objects
The system SHALL provide a `sealed trait RnaNucleotide` with exactly four case objects: `A`, `C`, `G`, and `U`. No other values of type `RnaNucleotide` SHALL be constructable. `RnaNucleotide` and `DnaNucleotide` SHALL be entirely independent types with no shared supertype.

#### Scenario: All four RNA bases exist as case objects
- **WHEN** the `RnaNucleotide` sealed trait is inspected
- **THEN** it has exactly four subtypes: `RnaNucleotide.A`, `RnaNucleotide.C`, `RnaNucleotide.G`, `RnaNucleotide.U`

#### Scenario: RnaNucleotide.U is distinct from DnaNucleotide.T at the type level
- **WHEN** a function accepting `RnaNucleotide` is passed `DnaNucleotide.T`
- **THEN** the compiler produces a type error

### Requirement: RnaNucleotide companion exposes validChars
The `RnaNucleotide` companion object SHALL expose `val validChars: Set[Char]` containing exactly `{'A', 'C', 'G', 'U'}`.

#### Scenario: validChars contains all four RNA characters
- **WHEN** `RnaNucleotide.validChars` is inspected
- **THEN** it equals `Set('A', 'C', 'G', 'U')`

#### Scenario: validChars does not contain DNA-specific T
- **WHEN** `RnaNucleotide.validChars.contains('T')` is evaluated
- **THEN** the result is `false`

### Requirement: RnaNucleotide companion provides fromChar
The `RnaNucleotide` companion object SHALL provide `def fromChar(c: Char): Option[RnaNucleotide]` returning `Some` for valid RNA characters and `None` for all others.

#### Scenario: fromChar returns Some for each valid RNA base
- **WHEN** `RnaNucleotide.fromChar('U')` is called
- **THEN** the result is `Some(RnaNucleotide.U)`

#### Scenario: fromChar returns None for DNA-specific T
- **WHEN** `RnaNucleotide.fromChar('T')` is called
- **THEN** the result is `None`

#### Scenario: fromChar returns None for lowercase
- **WHEN** `RnaNucleotide.fromChar('u')` is called
- **THEN** the result is `None`

#### Scenario: fromChar is total over all four RNA bases
- **WHEN** `fromChar` is called with each of 'A', 'C', 'G', 'U'
- **THEN** each returns `Some` of the corresponding case object
