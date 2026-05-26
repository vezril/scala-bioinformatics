package bio.algorithms.nucleic

import bio.domain.nucleic.{DnaNucleotide, DnaString, RnaNucleotide, RnaString}

object RnaTranscription {

  /** Transcribes a DNA string to its corresponding RNA string by replacing T with U.
    * A, C, G bases are preserved. The function is total — every valid DnaString
    * has a defined RnaString transcription.
    */
  def transcribe(dna: DnaString): RnaString = {
    val transcribed = dna.value.iterator.flatMap { c =>
      DnaNucleotide.fromChar(c).map(dnaToRna).map(RnaNucleotide.toChar)
    }.mkString
    RnaString.unsafeFrom(transcribed)
  }

  /** Maps a DNA nucleotide to its RNA counterpart. T becomes U; A, C, G are preserved. */
  private def dnaToRna(n: DnaNucleotide): RnaNucleotide = n match {
    case DnaNucleotide.A => RnaNucleotide.A
    case DnaNucleotide.C => RnaNucleotide.C
    case DnaNucleotide.G => RnaNucleotide.G
    case DnaNucleotide.T => RnaNucleotide.U
  }
}
