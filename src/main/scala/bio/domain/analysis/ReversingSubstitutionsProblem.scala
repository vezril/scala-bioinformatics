package bio.domain.analysis

import bio.domain.graph.NewickTree

/** Validated input bundle for the Rosalind RSUB ("Identifying Reversing
  * Substitutions") problem — see
  * [[bio.algorithms.analysis.ReversingSubstitutions.findAll]].
  *
  * Wraps:
  *   - `tree` — a rooted binary [[NewickTree]] with every node labeled
  *     (internals AND leaves);
  *   - `alignment` — a non-empty `Vector[NamedSequence]` of equal-length DNA
  *     strings over the alphabet `{A, C, G, T}` (no gaps), with at most 100
  *     rows and each sequence at most 400 characters. The set of row labels
  *     MUST equal the set of *all* node labels in the tree (leaves and
  *     internals).
  *
  * Validations are checked in priority order:
  *
  *   1. `EmptyAlignment`;
  *   2. `LengthMismatch(rowIndex, length, expectedLength)`;
  *   3. `SequenceTooLong(rowIndex, length, 400)`;
  *   4. `InvalidCharacter(rowIndex, position, ch)`;
  *   5. `TooManyStrings(actual, 100)`;
  *   6. `InternalNodeMissingLabel`;
  *   7. `LeafMissingLabel`;
  *   8. `NonBinaryInternalNode(label, degree)`;
  *   9. `NodeLabelMismatch(treeOnly, alignmentOnly)`.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply`
  * and `copy` cannot leak around the smart constructor — construct via
  * [[ReversingSubstitutionsProblem.from]].
  */
sealed abstract case class ReversingSubstitutionsProblem(
    tree: NewickTree,
    alignment: Vector[NamedSequence]
)

object ReversingSubstitutionsProblem {
  private val MaxRows: Int          = 100
  private val MaxSequenceLen: Int   = 400
  private val ValidChars: Set[Char] = Set('A', 'C', 'G', 'T')

  def from(
      tree: NewickTree,
      alignment: Vector[NamedSequence]
  ): Either[ReversingSubstitutionsProblemError, ReversingSubstitutionsProblem] = {
    if (alignment.isEmpty)
      Left(ReversingSubstitutionsProblemError.EmptyAlignment)
    else
      validateLengths(alignment)
        .orElse(validateLengthCap(alignment))
        .orElse(validateAlphabet(alignment))
        .orElse(validateRowCount(alignment))
        .orElse(validateTree(tree))
        .orElse(validateLabelCoverage(tree, alignment))
        .toLeft(new ReversingSubstitutionsProblem(tree, alignment) {})
  }

  private def validateLengths(
      alignment: Vector[NamedSequence]
  ): Option[ReversingSubstitutionsProblemError] = {
    val expected = alignment.head.sequence.length
    alignment.iterator.zipWithIndex
      .find { case (row, _) => row.sequence.length != expected }
      .map { case (row, idx) =>
        ReversingSubstitutionsProblemError.LengthMismatch(idx, row.sequence.length, expected)
      }
  }

  private def validateLengthCap(
      alignment: Vector[NamedSequence]
  ): Option[ReversingSubstitutionsProblemError] =
    alignment.iterator.zipWithIndex
      .find { case (row, _) => row.sequence.length > MaxSequenceLen }
      .map { case (row, idx) =>
        ReversingSubstitutionsProblemError.SequenceTooLong(idx, row.sequence.length, MaxSequenceLen)
      }

  private def validateAlphabet(
      alignment: Vector[NamedSequence]
  ): Option[ReversingSubstitutionsProblemError] = {
    var i = 0
    while (i < alignment.size) {
      val seq = alignment(i).sequence
      var p   = 0
      while (p < seq.length) {
        val ch = seq.charAt(p)
        if (!ValidChars.contains(ch))
          return Some(ReversingSubstitutionsProblemError.InvalidCharacter(i, p, ch))
        p += 1
      }
      i += 1
    }
    None
  }

  private def validateRowCount(
      alignment: Vector[NamedSequence]
  ): Option[ReversingSubstitutionsProblemError] =
    if (alignment.size > MaxRows)
      Some(ReversingSubstitutionsProblemError.TooManyStrings(alignment.size, MaxRows))
    else None

  private def validateTree(
      tree: NewickTree
  ): Option[ReversingSubstitutionsProblemError] = {
    if (hasUnlabeledInternal(tree))
      Some(ReversingSubstitutionsProblemError.InternalNodeMissingLabel)
    else if (hasUnlabeledLeaf(tree))
      Some(ReversingSubstitutionsProblemError.LeafMissingLabel)
    else
      firstNonBinaryInternal(tree).map { case (label, degree) =>
        ReversingSubstitutionsProblemError.NonBinaryInternalNode(label, degree)
      }
  }

  private def validateLabelCoverage(
      tree: NewickTree,
      alignment: Vector[NamedSequence]
  ): Option[ReversingSubstitutionsProblemError] = {
    val treeLabels      = collectAllLabels(tree)
    val alignmentLabels = alignment.map(_.label).toSet
    val treeOnly        = treeLabels.diff(alignmentLabels)
    val alignmentOnly   = alignmentLabels.diff(treeLabels)
    if (treeOnly.isEmpty && alignmentOnly.isEmpty) None
    else
      Some(ReversingSubstitutionsProblemError.NodeLabelMismatch(treeOnly, alignmentOnly))
  }

  private def hasUnlabeledInternal(t: NewickTree): Boolean =
    if (t.children.isEmpty) false
    else t.label.isEmpty || t.children.exists(hasUnlabeledInternal)

  private def hasUnlabeledLeaf(t: NewickTree): Boolean =
    if (t.children.isEmpty) t.label.isEmpty
    else t.children.exists(hasUnlabeledLeaf)

  private def firstNonBinaryInternal(t: NewickTree): Option[(String, Int)] = {
    if (t.children.nonEmpty && t.children.size != 2)
      Some((t.label.getOrElse(""), t.children.size))
    else
      t.children.iterator.flatMap(firstNonBinaryInternal).nextOption()
  }

  /** Collect labels from every node — both leaves and internals. */
  private def collectAllLabels(t: NewickTree): Set[String] =
    t.children.foldLeft(t.label.toSet)(_ ++ collectAllLabels(_))
}
