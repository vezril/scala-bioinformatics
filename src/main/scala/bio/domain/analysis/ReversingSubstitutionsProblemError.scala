package bio.domain.analysis

/** Construction failures for [[ReversingSubstitutionsProblem]]. */
sealed trait ReversingSubstitutionsProblemError
object ReversingSubstitutionsProblemError {

  /** The supplied alignment vector was empty. */
  case object EmptyAlignment extends ReversingSubstitutionsProblemError

  /** Row `rowIndex` of the alignment has a different length than the first row. */
  final case class LengthMismatch(rowIndex: Int, length: Int, expectedLength: Int)
      extends ReversingSubstitutionsProblemError

  /** Row `rowIndex` exceeds the Rosalind RSUB per-sequence cap of `max` characters. */
  final case class SequenceTooLong(rowIndex: Int, length: Int, max: Int)
      extends ReversingSubstitutionsProblemError

  /** Row `rowIndex` at column `position` contains `character`, which is not
    * in the allowed alphabet `{A, C, G, T}`. (No gap support.)
    */
  final case class InvalidCharacter(rowIndex: Int, position: Int, character: Char)
      extends ReversingSubstitutionsProblemError

  /** The alignment exceeds the Rosalind RSUB row-count cap of `max`. */
  final case class TooManyStrings(actual: Int, max: Int)
      extends ReversingSubstitutionsProblemError

  /** At least one internal node of the tree has no label. */
  case object InternalNodeMissingLabel extends ReversingSubstitutionsProblemError

  /** At least one leaf of the tree has no label. */
  case object LeafMissingLabel extends ReversingSubstitutionsProblemError

  /** Internal node `label` has degree `degree` (i.e., `children.size`), which
    * is not 2 — Rosalind RSUB requires a rooted binary tree.
    */
  final case class NonBinaryInternalNode(label: String, degree: Int)
      extends ReversingSubstitutionsProblemError

  /** The set of all tree node labels (leaves + internals) does not coincide
    * with the alignment row labels. `treeOnly` contains labels in the tree
    * but not in the alignment; `alignmentOnly` contains labels in the
    * alignment but not in the tree.
    */
  final case class NodeLabelMismatch(treeOnly: Set[String], alignmentOnly: Set[String])
      extends ReversingSubstitutionsProblemError
}
