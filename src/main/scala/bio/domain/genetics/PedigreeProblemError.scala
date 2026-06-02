package bio.domain.genetics

/** Validation errors for [[PedigreeProblem]] (Rosalind MEND). */
sealed trait PedigreeProblemError

object PedigreeProblemError {

  /** A leaf carries a label that is not a valid genotype (`AA`, `Aa`, or `aa`). */
  final case class InvalidGenotype(label: String) extends PedigreeProblemError

  /** An internal node does not have exactly two children (a pedigree is a rooted
    * binary tree).
    */
  final case class NotBinary(childCount: Int) extends PedigreeProblemError
}
