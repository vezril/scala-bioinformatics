## MODIFIED Requirements

### Requirement: ProteinString is a validated domain type
The system SHALL provide a `sealed abstract case class ProteinString(value: String)` whose value contains only valid amino-acid single-letter codes (the 20 codes defined by `AminoAcid`). Construction SHALL be possible only through `ProteinString.from(s: String): Either[ProteinError, ProteinString]` (validates each char), `ProteinString.fromAminoAcids(aas: Seq[AminoAcid]): ProteinString` (total — every input is a valid protein), or `private[bio] def unsafeFrom(s: String): ProteinString` (internal, bypasses validation). The synthesized `apply` and `copy` SHALL NOT be public — direct construction `ProteinString("MAMA")` MUST be a compile error. The type SHALL reside in the `bio.domain.protein` package.

#### Scenario: Valid protein string is accepted
- **WHEN** `ProteinString.from("MAMA")` is called
- **THEN** the result is `Right(<ProteinString of "MAMA">)`

#### Scenario: Empty string is accepted
- **WHEN** `ProteinString.from("")` is called
- **THEN** the result is `Right(<ProteinString of "">)`

#### Scenario: String containing an invalid amino-acid letter is rejected
- **WHEN** `ProteinString.from("MAB")` is called (`B` is not one of the 20 codes)
- **THEN** the result is `Left(ProteinError.InvalidCharacter('B'))`

#### Scenario: Lowercase letters are rejected
- **WHEN** `ProteinString.from("mama")` is called
- **THEN** the result is `Left(ProteinError.InvalidCharacter('m'))`

#### Scenario: fromAminoAcids assembles a protein from typed amino acids
- **WHEN** `ProteinString.fromAminoAcids(Vector(AminoAcid.M, AminoAcid.A, AminoAcid.M, AminoAcid.A))` is called
- **THEN** the result `value` equals `"MAMA"`

#### Scenario: fromAminoAcids handles an empty input
- **WHEN** `ProteinString.fromAminoAcids(Vector.empty)` is called
- **THEN** the result `value` equals `""`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.protein.ProteinString("MAMA")` is compiled
- **THEN** the compiler rejects the expression
