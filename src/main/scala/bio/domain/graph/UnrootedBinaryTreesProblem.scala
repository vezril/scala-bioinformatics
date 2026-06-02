package bio.domain.graph

/** Validated input for the Rosalind EUBT ("Enumerating Unrooted Binary Trees")
  * problem — see [[bio.algorithms.graph.EnumerateUnrootedBinaryTrees.enumerate]].
  *
  * Wraps the taxa (species names). The smart constructor enforces, first failure wins:
  *   1. `taxa.size >= 3` (an unrooted binary tree needs at least 3 leaves), else `TooFewTaxa`;
  *   2. `taxa.size <= 10`, else `TooManyTaxa` (the tree count `(2n−5)!!` grows factorially);
  *   3. no duplicate taxon name, else `DuplicateTaxon`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via
  * [[UnrootedBinaryTreesProblem.from]].
  */
sealed abstract case class UnrootedBinaryTreesProblem(taxa: Vector[String])

object UnrootedBinaryTreesProblem {
  private val MinTaxa: Int = 3
  private val MaxTaxa: Int = 10

  def from(
      taxa: Vector[String]
  ): Either[UnrootedBinaryTreesProblemError, UnrootedBinaryTreesProblem] =
    if (taxa.size < MinTaxa)
      Left(UnrootedBinaryTreesProblemError.TooFewTaxa(taxa.size, MinTaxa))
    else if (taxa.size > MaxTaxa)
      Left(UnrootedBinaryTreesProblemError.TooManyTaxa(taxa.size, MaxTaxa))
    else
      firstDuplicate(taxa) match {
        case Some(name) => Left(UnrootedBinaryTreesProblemError.DuplicateTaxon(name))
        case None       => Right(new UnrootedBinaryTreesProblem(taxa) {})
      }

  /** The first taxon name that repeats an earlier one, in input order. */
  private def firstDuplicate(taxa: Vector[String]): Option[String] =
    taxa.iterator
      .foldLeft((Set.empty[String], Option.empty[String])) {
        case ((seen, found), name) =>
          if (found.isDefined) (seen, found)
          else if (seen.contains(name)) (seen, Some(name))
          else (seen + name, None)
      }
      ._2
}
