package bio.domain.protein

import bio.domain.nucleic.{RnaNucleotide, RnaString}

/** A codon: three consecutive RNA nucleotides read in order from an mRNA.
  *
  * Construction is public — the three components are themselves validated
  * `RnaNucleotide` ADT values, so every triple is a structurally valid codon.
  */
final case class Codon(first: RnaNucleotide, second: RnaNucleotide, third: RnaNucleotide)

object Codon {

  /** Build a codon from three characters. Returns `None` if any char is not a valid
    * RNA nucleotide letter (`A`, `C`, `G`, `U`).
    */
  def fromChars(a: Char, b: Char, c: Char): Option[Codon] =
    for {
      n1 <- RnaNucleotide.fromChar(a)
      n2 <- RnaNucleotide.fromChar(b)
      n3 <- RnaNucleotide.fromChar(c)
    } yield Codon(n1, n2, n3)

  /** Split a validated RNA string into successive codons. Returns
    * `Left(LengthNotMultipleOfThree(length))` if the length is not divisible by 3.
    *
    * Because the input is a validated `RnaString` (alphabet ⊆ {A, C, G, U}), every
    * character is guaranteed to produce a `RnaNucleotide` — `fromChar` cannot return
    * `None` here. The `.get` is therefore safe and is documented as such.
    */
  def parseAll(rna: RnaString): Either[TranslationError, Vector[Codon]] = {
    val s = rna.value
    if (s.length % 3 != 0)
      Left(TranslationError.LengthNotMultipleOfThree(s.length))
    else
      Right(
        s.grouped(3)
          .map(chunk =>
            Codon(
              RnaNucleotide.fromChar(chunk.charAt(0)).get,
              RnaNucleotide.fromChar(chunk.charAt(1)).get,
              RnaNucleotide.fromChar(chunk.charAt(2)).get
            )
          )
          .toVector
      )
  }
}
