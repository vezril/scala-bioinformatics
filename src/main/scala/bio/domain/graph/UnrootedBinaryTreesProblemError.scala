package bio.domain.graph

/** Validation errors for [[UnrootedBinaryTreesProblem]] (Rosalind EUBT). */
sealed trait UnrootedBinaryTreesProblemError

object UnrootedBinaryTreesProblemError {

  /** Fewer than the minimum number of taxa needed for an unrooted binary tree. */
  final case class TooFewTaxa(count: Int, min: Int) extends UnrootedBinaryTreesProblemError

  /** More taxa than the maximum allowed (the tree count grows factorially). */
  final case class TooManyTaxa(count: Int, max: Int) extends UnrootedBinaryTreesProblemError

  /** A taxon name appears more than once. */
  final case class DuplicateTaxon(name: String) extends UnrootedBinaryTreesProblemError
}
