package bio.algorithms.nucleic

import bio.domain.nucleic.{DnaNucleotide, DnaString}

object DnaReverseComplement {

  /** Returns the reverse complement of a DNA string: the sequence is reversed and each
    * base is replaced with its Watson-Crick complement (A↔T, C↔G). The function is total
    * — every valid DnaString has a defined reverse complement.
    */
  def reverseComplement(dna: DnaString): DnaString = {
    val complemented = dna.value.iterator.flatMap { c =>
      DnaNucleotide.fromChar(c).map(DnaNucleotide.complement).map(DnaNucleotide.toChar)
    }.mkString
    DnaString.unsafeFrom(complemented.reverse)
  }
}
