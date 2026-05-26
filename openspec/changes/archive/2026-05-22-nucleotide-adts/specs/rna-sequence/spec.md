## ADDED Requirements

### Requirement: RnaString is a validated domain type
The system SHALL provide a `final class RnaString` with a smart constructor `RnaString.from(s: String): Either[SequenceError, RnaString]`. Valid input contains only the characters A, C, G, U (uppercase). The type SHALL NOT be constructable without going through the smart constructor. `RnaString` SHALL derive its valid alphabet from `RnaNucleotide.validChars`.

#### Scenario: Valid RNA string is accepted
- **WHEN** a string containing only 'A', 'C', 'G', 'U' is passed to the smart constructor
- **THEN** the result is `Right(RnaString(...))`

#### Scenario: String containing DNA-specific T is rejected
- **WHEN** `RnaString.from("ACGT")` is called
- **THEN** the result is `Left(SequenceError.InvalidCharacter('T'))`

#### Scenario: Empty RNA string is valid
- **WHEN** `RnaString.from("")` is called
- **THEN** the result is `Right(RnaString(""))`

#### Scenario: String with invalid character is rejected
- **WHEN** a string containing a character outside {A, C, G, U} is passed to the smart constructor
- **THEN** the result is `Left(SequenceError.InvalidCharacter(...))`

#### Scenario: String exceeding 1000 characters is rejected
- **WHEN** a string longer than 1000 characters is passed to the smart constructor
- **THEN** the result is `Left(SequenceError.ExceedsMaxLength(...))`

#### Scenario: String of exactly 1000 characters is accepted
- **WHEN** an RNA string of exactly 1000 valid characters is passed to the smart constructor
- **THEN** the result is `Right(RnaString(...))`

### Requirement: RnaNucleotideCounts is a domain type with format
The system SHALL provide `final case class RnaNucleotideCounts(a: Int, c: Int, g: Int, u: Int)` with a `format: String` method returning counts as four space-separated integers in A C G U order.

#### Scenario: RnaNucleotideCounts holds a, c, g, u fields
- **WHEN** `RnaNucleotideCounts(a = 5, c = 3, g = 7, u = 2)` is constructed
- **THEN** `counts.a == 5`, `counts.c == 3`, `counts.g == 7`, `counts.u == 2`

#### Scenario: format returns space-separated A C G U integers
- **WHEN** `RnaNucleotideCounts(a = 5, c = 3, g = 7, u = 2).format` is called
- **THEN** the result is `"5 3 7 2"`

#### Scenario: format handles all-zero counts
- **WHEN** `RnaNucleotideCounts(0, 0, 0, 0).format` is called
- **THEN** the result is `"0 0 0 0"`
