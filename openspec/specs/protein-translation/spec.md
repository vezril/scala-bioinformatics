## Purpose
Define the framework's protein-output capability: the `ProteinString` validated domain type, the error ADTs guarding it (`ProteinError`, `TranslationError`), and the `RnaTranslation.translate` algorithm that converts a validated `RnaString` into a `ProteinString` per the standard genetic code, halting on the first stop codon. Serves Rosalind's "Translating RNA Into Protein" problem and any downstream analysis that turns coding sequences into proteins.

## Requirements

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

### Requirement: ProteinError is a sealed ADT of protein-construction failures
The system SHALL provide a `sealed trait ProteinError` with case `final case class InvalidCharacter(ch: Char)`.

#### Scenario: InvalidCharacter carries the offending char
- **WHEN** `ProteinError.InvalidCharacter('B')` is constructed
- **THEN** the value's `ch` field equals `'B'`

### Requirement: TranslationError is a sealed ADT of translation failures
The system SHALL provide a `sealed trait TranslationError` with case `final case class LengthNotMultipleOfThree(length: Int)`.

#### Scenario: LengthNotMultipleOfThree carries the offending length
- **WHEN** `TranslationError.LengthNotMultipleOfThree(5)` is constructed
- **THEN** the value's `length` field equals `5`

### Requirement: RnaTranslation.translate converts an RnaString into a ProteinString
The system SHALL provide `RnaTranslation.translate(rna: RnaString): Either[TranslationError, ProteinString]`. The function SHALL split the RNA into successive codons (left to right, no overlap), look up each in `GeneticCode`, accumulate the resulting amino acids, and halt on the first `Stop` (the Stop itself is NOT included in the output). Translation that reaches the end of the input without encountering a Stop SHALL still succeed with the protein built so far.

#### Scenario: Rosalind sample produces MAMAPRTEINSTRING
- **WHEN** `RnaTranslation.translate(<RnaString of "AUGGCCAUGGCGCCCAGAACUGAGAUCAAUAGUACCCGUAUUAACGGGUGA">)` is called
- **THEN** the result is `Right(<ProteinString of "MAMAPRTEINSTRING">)`

#### Scenario: Empty RNA produces an empty protein
- **WHEN** `RnaTranslation.translate(<empty RnaString>)` is called
- **THEN** the result is `Right(<ProteinString of "">)`

#### Scenario: A leading Stop codon produces an empty protein
- **WHEN** `RnaTranslation.translate(<RnaString of "UAA">)` is called
- **THEN** the result is `Right(<ProteinString of "">)`

#### Scenario: Stop terminates translation early
- **WHEN** `RnaTranslation.translate(<RnaString of "AUGUAAGCC">)` is called (stop at codon 2; codon 3 ignored)
- **THEN** the result is `Right(<ProteinString of "M">)`

#### Scenario: RNA without a Stop translates to the end
- **WHEN** `RnaTranslation.translate(<RnaString of "AUGGCC">)` is called (no stop codon)
- **THEN** the result is `Right(<ProteinString of "MA">)`

#### Scenario: Length not divisible by 3 is rejected
- **WHEN** `RnaTranslation.translate(<RnaString of "AUGGC">)` is called (length 5)
- **THEN** the result is `Left(TranslationError.LengthNotMultipleOfThree(5))`
