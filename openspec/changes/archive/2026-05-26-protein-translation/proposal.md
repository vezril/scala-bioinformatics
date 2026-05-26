## Why

Spec 8 of the project brief — "Translating RNA Into Protein" — is the framework's first dive into the central dogma of molecular biology: information flow from RNA codons to amino-acid sequences. Beyond the Rosalind problem, it establishes the framework's protein/amino-acid vocabulary (`AminoAcid` ADT, `Codon`, `ProteinString`) and the standard genetic code as reusable foundations for every future problem that touches translation, ORF detection, or protein analysis.

## What Changes

- **NEW** `AminoAcid` sealed ADT in `bio.domain` — 20 case objects, one per standard amino acid, each carrying its single-letter code (`F, L, I, V, S, P, T, A, Y, H, N, D, Q, K, E, C, R, G, W, M`)
- **NEW** `Codon` case class in `bio.domain` — three `RnaNucleotide`s, with helpers to build codons from chars / chunks
- **NEW** `CodonOutcome` sealed ADT in `bio.domain` — either an `AminoAcidProduct(aa: AminoAcid)` or `Stop`
- **NEW** `GeneticCode` in `bio.domain` — pure `translate(c: Codon): CodonOutcome` backed by the standard 64-entry RNA codon table
- **NEW** `ProteinString` in `bio.domain` — `sealed abstract case class ProteinString(value: String)` validated to contain only the 20 valid single-letter codes, with smart constructors `from` (validates) and `fromAminoAcids` (total)
- **NEW** `ProteinError` sealed ADT — `InvalidCharacter(ch: Char)`
- **NEW** `TranslationError` sealed ADT — `LengthNotMultipleOfThree(length: Int)`
- **NEW** `RnaTranslation.translate(rna: RnaString): Either[TranslationError, ProteinString]` in `bio.algorithms` — splits RNA into codons, looks up each in `GeneticCode`, halts at the first `Stop`, returns the assembled protein
- **MODIFIED** `RnaString` max length raised from 1000 to 10000 to accommodate Rosalind's "up to 10 kbp" translation inputs

## Capabilities

### New Capabilities

- `amino-acid`: Standard amino acid ADT (`AminoAcid`, 20 case objects with single-letter codes), `Codon` triple-nucleotide type, `CodonOutcome` (amino acid or stop), and the `GeneticCode` lookup encapsulating the standard RNA codon table.
- `protein-translation`: `ProteinString` validated domain type, `ProteinError`, `TranslationError`, and `RnaTranslation.translate` algorithm converting an `RnaString` into a `ProteinString`, halting on the first stop codon.

### Modified Capabilities

- `rna-sequence`: Raise `RnaString` max length from 1000 to 10000 nucleotides. Spec 8 explicitly allows mRNA inputs up to 10 kbp. Other validation rules (alphabet `{A, C, G, U}`) are unchanged.

## Impact

- New files in `bio.domain`: `AminoAcid.scala`, `Codon.scala`, `CodonOutcome.scala`, `GeneticCode.scala`, `ProteinString.scala`, `ProteinError.scala`, `TranslationError.scala`
- New file in `bio.algorithms`: `RnaTranslation.scala`
- Modified `RnaString.scala`: `MaxLength` raised from 1000 to 10000 (single-line change)
- New test files mirroring each new source file; an additional test in `RnaStringSpec` for the new length bound
- No new external dependencies
- All existing tests continue passing (existing valid lengths ≤1000 remain valid)
