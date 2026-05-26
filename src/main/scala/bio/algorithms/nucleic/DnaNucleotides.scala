package bio.algorithms.nucleic

import bio.domain.nucleic.{DnaNucleotide, DnaNucleotideCounts, DnaString}

object DnaNucleotides {
  def count(dna: DnaString): DnaNucleotideCounts =
    dna.value.foldLeft(DnaNucleotideCounts(0, 0, 0, 0)) { (acc, ch) =>
      DnaNucleotide.fromChar(ch) match {
        case Some(DnaNucleotide.A) => acc.copy(a = acc.a + 1)
        case Some(DnaNucleotide.C) => acc.copy(c = acc.c + 1)
        case Some(DnaNucleotide.G) => acc.copy(g = acc.g + 1)
        case Some(DnaNucleotide.T) => acc.copy(t = acc.t + 1)
        case None                  => acc
      }
    }
}
