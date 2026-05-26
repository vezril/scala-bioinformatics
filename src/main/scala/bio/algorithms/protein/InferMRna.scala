package bio.algorithms.protein

import bio.domain.nucleic.RnaNucleotide
import bio.domain.protein.{AminoAcid, Codon, CodonOutcome, GeneticCode, ProteinString}

/** Counts the number of distinct mRNA strings that could have translated to a given
  * protein under the standard RNA genetic code, modulo 1,000,000.
  *
  * For protein `p_1 p_2 ... p_n`, the count is the product of each amino acid's codon
  * degeneracy (number of codons that map to it), multiplied by the number of stop codons
  * (3 in the standard code), taken modulo 1,000,000.
  *
  * Codon degeneracies and the stop-codon count are derived from
  * [[GeneticCode.translate]] rather than hard-coded, so the table remains a single
  * source of truth.
  */
object InferMRna {

  private val Modulus: Int = 1_000_000

  // Derive codon counts by iterating all 64 codons exactly once at object init.
  private val (codonCount: Map[AminoAcid, Int], stopCodonCount: Int) = {
    val bases     = Vector(RnaNucleotide.A, RnaNucleotide.C, RnaNucleotide.G, RnaNucleotide.U)
    val allCodons = for { x <- bases; y <- bases; z <- bases } yield Codon(x, y, z)
    val grouped   = allCodons.groupBy(GeneticCode.translate)
    val perAa: Map[AminoAcid, Int] = grouped.collect {
      case (CodonOutcome.AminoAcidProduct(aa), codons) => aa -> codons.size
    }
    val stops: Int = grouped.get(CodonOutcome.Stop).map(_.size).getOrElse(0)
    (perAa, stops)
  }

  private val aminoAcidByCode: Map[Char, AminoAcid] =
    AminoAcid.all.map(aa => aa.code -> aa).toMap

  def count(protein: ProteinString): Int =
    protein.value.foldLeft(stopCodonCount) { (acc, ch) =>
      (acc * codonCount(aminoAcidByCode(ch))) % Modulus
    }
}
