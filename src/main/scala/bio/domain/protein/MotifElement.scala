package bio.domain.protein

/** A single position of a protein motif (Rosalind MPRT shorthand).
  *
  *   - [[MotifElement.OneOf]] matches any residue in `residues` (a literal residue
  *     `X` → `OneOf(Set('X'))`, a group `[XY]` → `OneOf(Set('X','Y'))`).
  *   - [[MotifElement.NoneOf]] matches any residue *not* in `residues`
  *     (a negation `{X}` → `NoneOf(Set('X'))`).
  */
sealed trait MotifElement {
  def matches(c: Char): Boolean
}

object MotifElement {

  final case class OneOf(residues: Set[Char]) extends MotifElement {
    def matches(c: Char): Boolean = residues.contains(c)
  }

  final case class NoneOf(residues: Set[Char]) extends MotifElement {
    def matches(c: Char): Boolean = !residues.contains(c)
  }
}
