package bio.algorithms.protein

import bio.domain.nucleic.RnaString
import bio.domain.protein.{
  Codon,
  CodonOutcome,
  GeneticCode,
  ProteinString,
  TranslationError
}

/** Translation of an mRNA sequence into a protein, per the standard RNA genetic code.
  *
  * The algorithm splits the RNA into successive codons, looks each one up in
  * [[GeneticCode]], and accumulates the resulting amino acids. Translation halts on
  * the first Stop codon (the Stop itself is NOT included in the output protein). If
  * the RNA contains no Stop codon, translation runs to the end of the input — this
  * is treated as a valid (truncated) protein, not an error.
  */
object RnaTranslation {

  def translate(rna: RnaString): Either[TranslationError, ProteinString] =
    Codon.parseAll(rna).map { codons =>
      val aminoAcids = codons.iterator
        .map(GeneticCode.translate)
        .takeWhile {
          case CodonOutcome.Stop                => false
          case CodonOutcome.AminoAcidProduct(_) => true
        }
        .collect { case CodonOutcome.AminoAcidProduct(aa) => aa }
        .toVector
      ProteinString.fromAminoAcids(aminoAcids)
    }
}
