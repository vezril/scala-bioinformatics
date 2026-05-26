package bio.domain.analysis

import bio.domain.nucleic.{DnaNucleotide, DnaString}

/** GC content of a DNA sequence, stored as a percentage in `[0.0, 100.0]`.
  *
  * Implemented as `sealed abstract case class` to prevent Scala 2.13's case-class `apply`
  * and `copy` synthesis from leaking around the smart constructor. Construct via
  * [[GcContent.from]] (validates input) or, for trusted internal callers,
  * [[GcContent.unsafeFrom]].
  *
  * Stored as percentage (e.g., `60.919540`) rather than fraction so it matches the
  * Rosalind output format and the colloquial domain meaning of "GC content".
  */
sealed abstract case class GcContent(value: Double)

object GcContent {
  private val Min: Double = 0.0
  private val Max: Double = 100.0

  def from(value: Double): Either[GcContentError, GcContent] =
    if (value.isNaN || value.isInfinite) Left(GcContentError.NotFinite)
    else if (value < Min || value > Max) Left(GcContentError.OutOfRange(value))
    else Right(new GcContent(value) {})

  /** Internal constructor for trusted callers that guarantee `value` is finite and in
    * `[0, 100]`. Bypasses validation. Use [[from]] for any externally-sourced input.
    */
  private[bio] def unsafeFrom(value: Double): GcContent = new GcContent(value) {}

  /** Computes the GC content of `dna` as a percentage in `[0, 100]`. Total: an empty
    * `DnaString` returns `GcContent(0.0)` by convention (there are no G or C bases).
    */
  def of(dna: DnaString): GcContent = {
    val (gc, total) = dna.value.foldLeft((0, 0)) { case ((gcCount, n), ch) =>
      DnaNucleotide.fromChar(ch) match {
        case Some(DnaNucleotide.G) | Some(DnaNucleotide.C) => (gcCount + 1, n + 1)
        case Some(_)                                       => (gcCount, n + 1)
        case None                                          => (gcCount, n)
      }
    }
    if (total == 0) unsafeFrom(0.0)
    else unsafeFrom(100.0 * gc / total)
  }
}
