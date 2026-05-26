## ADDED Requirements

### Requirement: AminoAcid is a sealed ADT of the 20 standard amino acids
The system SHALL provide a `sealed trait AminoAcid` with exactly 20 case-object members, one per standard amino acid: `F`, `L`, `I`, `V`, `S`, `P`, `T`, `A`, `Y`, `H`, `N`, `D`, `Q`, `K`, `E`, `C`, `R`, `G`, `W`, `M`. Each case object SHALL expose a `code: Char` field returning its canonical single-letter IUPAC code.

#### Scenario: Each amino acid exposes its single-letter code
- **WHEN** the `code` field of `AminoAcid.M` is read
- **THEN** the result is `'M'`

#### Scenario: All 20 amino acids extend AminoAcid
- **WHEN** the sealed trait `AminoAcid` is enumerated
- **THEN** exactly 20 case objects extend it, each with a distinct `code`

### Requirement: Codon pairs three RnaNucleotides
The system SHALL provide a `final case class Codon(first: RnaNucleotide, second: RnaNucleotide, third: RnaNucleotide)`. Construction is public — every triple of validated `RnaNucleotide` values is a structurally valid codon.

#### Scenario: Codon exposes its three nucleotides
- **WHEN** `Codon(RnaNucleotide.A, RnaNucleotide.U, RnaNucleotide.G)` is constructed
- **THEN** `codon.first == A`, `codon.second == U`, `codon.third == G`

### Requirement: Codon.fromChars builds a Codon from three characters
The system SHALL provide `Codon.fromChars(a: Char, b: Char, c: Char): Option[Codon]` returning `Some(codon)` when all three chars are valid RNA nucleotide letters (`A`, `C`, `G`, `U`), and `None` otherwise.

#### Scenario: Three valid RNA chars produce a codon
- **WHEN** `Codon.fromChars('A', 'U', 'G')` is called
- **THEN** the result is `Some(Codon(A, U, G))`

#### Scenario: A non-RNA character yields None
- **WHEN** `Codon.fromChars('A', 'T', 'G')` is called (T is not valid RNA)
- **THEN** the result is `None`

### Requirement: Codon.parseAll splits an RnaString into successive codons
The system SHALL provide `Codon.parseAll(rna: RnaString): Either[TranslationError, Vector[Codon]]`. The function SHALL split the underlying string into successive triples in order; it SHALL return `Left(TranslationError.LengthNotMultipleOfThree(length))` if the length is not divisible by 3. An empty `RnaString` SHALL return `Right(Vector.empty)`.

#### Scenario: Length divisible by 3 produces successive codons
- **WHEN** `Codon.parseAll(<RnaString of "AUGCCC">)` is called
- **THEN** the result is `Right(Vector(Codon(A, U, G), Codon(C, C, C)))`

#### Scenario: Empty RnaString produces an empty vector
- **WHEN** `Codon.parseAll(<empty RnaString>)` is called
- **THEN** the result is `Right(Vector.empty)`

#### Scenario: Length not divisible by 3 is rejected
- **WHEN** `Codon.parseAll(<RnaString of "AUGCC">)` is called (length 5)
- **THEN** the result is `Left(TranslationError.LengthNotMultipleOfThree(5))`

### Requirement: CodonOutcome is a sealed ADT of translation outputs
The system SHALL provide a `sealed trait CodonOutcome` with cases `final case class AminoAcidProduct(aa: AminoAcid)` and `case object Stop`.

#### Scenario: AminoAcidProduct wraps an AminoAcid
- **WHEN** `CodonOutcome.AminoAcidProduct(AminoAcid.M)` is constructed
- **THEN** the value's `aa` field equals `AminoAcid.M`

#### Scenario: Stop is a value of CodonOutcome
- **WHEN** `CodonOutcome.Stop` is referenced
- **THEN** it is a subtype of `CodonOutcome`

### Requirement: GeneticCode.translate implements the standard RNA codon table
The system SHALL provide `GeneticCode.translate(c: Codon): CodonOutcome` returning the standard RNA codon table mapping. The function SHALL be total over all 64 codons: 61 codons map to `AminoAcidProduct(<amino acid>)` and 3 codons (`UAA`, `UAG`, `UGA`) map to `Stop`.

#### Scenario: AUG codes for Methionine
- **WHEN** `GeneticCode.translate(Codon(A, U, G))` is called
- **THEN** the result is `AminoAcidProduct(AminoAcid.M)`

#### Scenario: UUU codes for Phenylalanine
- **WHEN** `GeneticCode.translate(Codon(U, U, U))` is called
- **THEN** the result is `AminoAcidProduct(AminoAcid.F)`

#### Scenario: UAA is a Stop codon
- **WHEN** `GeneticCode.translate(Codon(U, A, A))` is called
- **THEN** the result is `Stop`

#### Scenario: UAG is a Stop codon
- **WHEN** `GeneticCode.translate(Codon(U, A, G))` is called
- **THEN** the result is `Stop`

#### Scenario: UGA is a Stop codon
- **WHEN** `GeneticCode.translate(Codon(U, G, A))` is called
- **THEN** the result is `Stop`

#### Scenario: UGG codes for Tryptophan (sole codon)
- **WHEN** `GeneticCode.translate(Codon(U, G, G))` is called
- **THEN** the result is `AminoAcidProduct(AminoAcid.W)`

#### Scenario: All 64 codons map to a defined CodonOutcome
- **WHEN** every possible `Codon(x, y, z)` for x, y, z in `{A, C, G, U}` is passed to `translate`
- **THEN** every call returns a `CodonOutcome` (no exceptions, no missing cases)
