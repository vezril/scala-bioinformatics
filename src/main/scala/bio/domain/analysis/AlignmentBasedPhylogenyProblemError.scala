package bio.domain.analysis

/** Construction failures for [[AlignmentBasedPhylogenyProblem]]. */
sealed trait AlignmentBasedPhylogenyProblemError
object AlignmentBasedPhylogenyProblemError {

  /** The supplied alignment vector was empty. */
  case object EmptyAlignment extends AlignmentBasedPhylogenyProblemError

  /** Row `rowIndex` of the alignment has a different length than the first row. */
  final case class LengthMismatch(rowIndex: Int, length: Int, expectedLength: Int)
      extends AlignmentBasedPhylogenyProblemError

  /** Row `rowIndex` exceeds the Rosalind ALPH per-sequence cap of `max` characters. */
  final case class SequenceTooLong(rowIndex: Int, length: Int, max: Int)
      extends AlignmentBasedPhylogenyProblemError

  /** Row `rowIndex` at column `position` contains `character`, which is not in
    * the allowed alphabet `{A, C, G, T, -}`.
    */
  final case class InvalidCharacter(rowIndex: Int, position: Int, character: Char)
      extends AlignmentBasedPhylogenyProblemError

  /** The tree's leaf count exceeded the Rosalind ALPH cap of `max`. */
  final case class TooManyLeaves(actual: Int, max: Int)
      extends AlignmentBasedPhylogenyProblemError

  /** At least one internal node of the tree has no label. */
  case object InternalNodeMissingLabel extends AlignmentBasedPhylogenyProblemError

  /** Internal node `label` has degree `degree` (i.e., `children.size`), which
    * is not 2 — Rosalind ALPH requires a rooted binary tree.
    */
  final case class NonBinaryInternalNode(label: String, degree: Int)
      extends AlignmentBasedPhylogenyProblemError

  /** The leaf labels in the tree do not coincide with the alignment row labels.
    * `treeOnly` contains labels that appear in the tree but not in the
    * alignment; `alignmentOnly` contains labels that appear in the alignment
    * but not in the tree. At least one of the two sets is non-empty.
    */
  final case class LeafLabelMismatch(treeOnly: Set[String], alignmentOnly: Set[String])
      extends AlignmentBasedPhylogenyProblemError
}
