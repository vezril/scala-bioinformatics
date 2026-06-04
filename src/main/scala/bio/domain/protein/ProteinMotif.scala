package bio.domain.protein

import scala.annotation.tailrec

/** A parsed protein motif — an ordered sequence of [[MotifElement]] positions
  * (Rosalind MPRT). Construct via [[ProteinMotif.parse]], which translates the
  * shorthand (`X`, `[XY]`, `{X}`) into the element ADT.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor.
  */
sealed abstract case class ProteinMotif(elements: Vector[MotifElement]) {
  def length: Int = elements.length
}

object ProteinMotif {

  /** Parse a motif shorthand into a [[ProteinMotif]]:
    *   - a bare residue `X` → `OneOf(Set('X'))`;
    *   - a group `[XY…]` → `OneOf` of the listed residues;
    *   - a negation `{X…}` → `NoneOf` of the listed residues.
    */
  def parse(shorthand: String): Either[ProteinMotifError, ProteinMotif] =
    if (shorthand.isEmpty) Left(ProteinMotifError.EmptyMotif)
    else loop(shorthand, 0, Vector.empty).map(els => new ProteinMotif(els) {})

  @tailrec
  private def loop(
      s: String,
      i: Int,
      acc: Vector[MotifElement]
  ): Either[ProteinMotifError, Vector[MotifElement]] =
    if (i >= s.length) Right(acc)
    else
      s.charAt(i) match {
        case '[' =>
          group(s, i, ']') match {
            case Left(err)           => Left(err)
            case Right((residues, j)) => loop(s, j, acc :+ MotifElement.OneOf(residues))
          }
        case '{' =>
          group(s, i, '}') match {
            case Left(err)           => Left(err)
            case Right((residues, j)) => loop(s, j, acc :+ MotifElement.NoneOf(residues))
          }
        case c if c.isLetter =>
          loop(s, i + 1, acc :+ MotifElement.OneOf(Set(c)))
        case c =>
          Left(ProteinMotifError.UnexpectedCharacter(c, i))
      }

  /** Read a `[`/`{` group opened at `start`, returning the residue set and the
    * index just past the closing delimiter.
    */
  private def group(
      s: String,
      start: Int,
      close: Char
  ): Either[ProteinMotifError, (Set[Char], Int)] = {
    val end = s.indexOf(close.toInt, start + 1)
    if (end < 0) Left(ProteinMotifError.UnterminatedGroup(start))
    else Right((s.substring(start + 1, end).toSet, end + 1))
  }
}
