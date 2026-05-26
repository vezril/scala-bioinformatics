## MODIFIED Requirements

### Requirement: RnaString is a validated domain type
The system SHALL provide a `final class RnaString` with a smart constructor `RnaString.from(s: String): Either[SequenceError, RnaString]`. Valid input contains only the characters A, C, G, U (uppercase). The type SHALL NOT be constructable without going through the smart constructor. `RnaString` SHALL derive its valid alphabet from `RnaNucleotide.validChars`. The maximum accepted length SHALL be 10000 characters (raised from 1000 to accommodate mRNA translation inputs of up to 10 kbp).

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

#### Scenario: String exceeding 10000 characters is rejected
- **WHEN** a string longer than 10000 characters is passed to the smart constructor
- **THEN** the result is `Left(SequenceError.ExceedsMaxLength(...))`

#### Scenario: String of exactly 10000 characters is accepted
- **WHEN** an RNA string of exactly 10000 valid characters is passed to the smart constructor
- **THEN** the result is `Right(RnaString(...))`

#### Scenario: String between the old and new bounds is accepted
- **WHEN** an RNA string of length 5000 (previously over the cap of 1000, now under the cap of 10000) is passed to the smart constructor
- **THEN** the result is `Right(RnaString(...))`
