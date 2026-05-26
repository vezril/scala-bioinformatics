## MODIFIED Requirements

### Requirement: DNA string is a validated domain type
The system SHALL model a DNA string as `DnaString` with a smart constructor `DnaString.from(s: String): Either[SequenceError, DnaString]` (error type renamed from `DnaError` to `SequenceError`). The valid alphabet SHALL be derived from `DnaNucleotide.validChars` rather than a hardcoded literal set.

#### Scenario: Valid DNA string is accepted
- **WHEN** a string containing only 'A', 'C', 'G', 'T' characters is passed to the smart constructor
- **THEN** the result is `Right(DnaString(...))`

#### Scenario: String with invalid character is rejected
- **WHEN** a string containing any character outside {A, C, G, T} is passed to the smart constructor
- **THEN** the result is `Left(SequenceError.InvalidCharacter(...))`

#### Scenario: Empty string is valid
- **WHEN** an empty string is passed to the smart constructor
- **THEN** the result is `Right(DnaString(""))`

#### Scenario: String exceeds 1000 characters
- **WHEN** a string longer than 1000 characters is passed to the smart constructor
- **THEN** the result is `Left(SequenceError.ExceedsMaxLength(...))`

#### Scenario: String of exactly 1000 characters is accepted
- **WHEN** a DNA string of exactly 1000 valid characters is passed to the smart constructor
- **THEN** the result is `Right(DnaString(...))`

#### Scenario: RNA-specific U character is rejected by DnaString
- **WHEN** `DnaString.from("ACGU")` is called
- **THEN** the result is `Left(SequenceError.InvalidCharacter('U'))`

## REMOVED Requirements

### Requirement: DnaError is the validation error type for DNA strings
**Reason**: `DnaError` is renamed to `SequenceError` to serve as the shared validation error type for both `DnaString` and `RnaString`. The error cases (`InvalidCharacter`, `ExceedsMaxLength`) are molecule-agnostic.
**Migration**: Replace all references to `DnaError` with `SequenceError`. Replace `DnaError.InvalidCharacter` with `SequenceError.InvalidCharacter` and `DnaError.ExceedsMaxLength` with `SequenceError.ExceedsMaxLength`.
