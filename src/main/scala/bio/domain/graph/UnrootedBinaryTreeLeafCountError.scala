package bio.domain.graph

/** Construction failures for [[UnrootedBinaryTreeLeafCount]]. */
sealed trait UnrootedBinaryTreeLeafCountError
object UnrootedBinaryTreeLeafCountError {

  /** The requested leaf count was below the Rosalind INOD minimum of 3. */
  final case class BelowMinimum(n: Int, min: Int) extends UnrootedBinaryTreeLeafCountError

  /** The requested leaf count exceeded the Rosalind INOD maximum of 10000. */
  final case class ExceedsMaximum(n: Int, max: Int) extends UnrootedBinaryTreeLeafCountError
}
