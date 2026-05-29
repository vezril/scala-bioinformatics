package bio.domain.analysis

import bio.domain.graph.NewickTree

/** Validated input bundle for the Rosalind ALPH ("Alignment-Based
  * Phylogeny") problem — see
  * [[bio.algorithms.analysis.AlignmentBasedPhylogeny.solve]].
  *
  * Wraps:
  *   - `tree` — a rooted binary [[NewickTree]] with every internal node and
  *     every leaf labeled;
  *   - `alignment` — a non-empty collection of [[NamedSequence]] rows of equal
  *     length, where each `label` matches some leaf of the tree (set
  *     equality), each `sequence` is over the alphabet `{A, C, G, T, -}`, and
  *     each sequence has at most 300 characters.
  *
  * Validations are checked in this priority order:
  *
  *   1. `EmptyAlignment` — `alignment.isEmpty`.
  *   2. `LengthMismatch(rowIndex, length, expectedLength)` — first row whose
  *      length differs from row 0's length.
  *   3. `SequenceTooLong(rowIndex, length, 300)` — first row exceeding the cap.
  *   4. `InvalidCharacter(rowIndex, position, ch)` — first non-DNA-or-gap
  *      character encountered (row-major scan).
  *   5. `TooManyLeaves(actual, 500)` — tree has > 500 leaves.
  *   6. `InternalNodeMissingLabel` — at least one internal node has
  *      `label == None`.
  *   7. `NonBinaryInternalNode(label, degree)` — first internal node with
  *      `children.size != 2`.
  *   8. `LeafLabelMismatch(treeOnly, alignmentOnly)` — the sets of leaf
  *      labels in the tree and in the alignment differ.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[AlignmentBasedPhylogenyProblem.from]].
  */
sealed abstract case class AlignmentBasedPhylogenyProblem(
    tree: NewickTree,
    alignment: Vector[NamedSequence]
)

object AlignmentBasedPhylogenyProblem {
  private val MaxLeaves: Int       = 500
  private val MaxSequenceLen: Int  = 300
  private val ValidChars: Set[Char] = Set('A', 'C', 'G', 'T', '-')

  def from(
      tree: NewickTree,
      alignment: Vector[NamedSequence]
  ): Either[AlignmentBasedPhylogenyProblemError, AlignmentBasedPhylogenyProblem] = {
    if (alignment.isEmpty)
      Left(AlignmentBasedPhylogenyProblemError.EmptyAlignment)
    else
      validateAlignmentLengths(alignment)
        .orElse(validateAlignmentCap(alignment))
        .orElse(validateAlignmentAlphabet(alignment))
        .orElse(validateTree(tree))
        .orElse(validateLabelCoverage(tree, alignment))
        .toLeft(new AlignmentBasedPhylogenyProblem(tree, alignment) {})
  }

  private def validateAlignmentLengths(
      alignment: Vector[NamedSequence]
  ): Option[AlignmentBasedPhylogenyProblemError] = {
    val expected = alignment.head.sequence.length
    val offending = alignment.iterator.zipWithIndex.find { case (row, _) =>
      row.sequence.length != expected
    }
    offending.map { case (row, idx) =>
      AlignmentBasedPhylogenyProblemError.LengthMismatch(idx, row.sequence.length, expected)
    }
  }

  private def validateAlignmentCap(
      alignment: Vector[NamedSequence]
  ): Option[AlignmentBasedPhylogenyProblemError] = {
    val offending = alignment.iterator.zipWithIndex.find { case (row, _) =>
      row.sequence.length > MaxSequenceLen
    }
    offending.map { case (row, idx) =>
      AlignmentBasedPhylogenyProblemError.SequenceTooLong(idx, row.sequence.length, MaxSequenceLen)
    }
  }

  private def validateAlignmentAlphabet(
      alignment: Vector[NamedSequence]
  ): Option[AlignmentBasedPhylogenyProblemError] = {
    var i = 0
    while (i < alignment.size) {
      val seq = alignment(i).sequence
      var p   = 0
      while (p < seq.length) {
        val ch = seq.charAt(p)
        if (!ValidChars.contains(ch))
          return Some(AlignmentBasedPhylogenyProblemError.InvalidCharacter(i, p, ch))
        p += 1
      }
      i += 1
    }
    None
  }

  private def validateTree(
      tree: NewickTree
  ): Option[AlignmentBasedPhylogenyProblemError] = {
    val leafCount = countLeaves(tree)
    if (leafCount > MaxLeaves)
      Some(AlignmentBasedPhylogenyProblemError.TooManyLeaves(leafCount, MaxLeaves))
    else if (hasUnlabeledInternal(tree))
      Some(AlignmentBasedPhylogenyProblemError.InternalNodeMissingLabel)
    else
      firstNonBinaryInternal(tree).map { case (label, degree) =>
        AlignmentBasedPhylogenyProblemError.NonBinaryInternalNode(label, degree)
      }
  }

  private def validateLabelCoverage(
      tree: NewickTree,
      alignment: Vector[NamedSequence]
  ): Option[AlignmentBasedPhylogenyProblemError] = {
    val treeLeafLabels      = collectLeafLabels(tree)
    val alignmentLabels     = alignment.map(_.label).toSet
    val treeOnly            = treeLeafLabels.diff(alignmentLabels)
    val alignmentOnly       = alignmentLabels.diff(treeLeafLabels)
    if (treeOnly.isEmpty && alignmentOnly.isEmpty) None
    else
      Some(AlignmentBasedPhylogenyProblemError.LeafLabelMismatch(treeOnly, alignmentOnly))
  }

  private def countLeaves(t: NewickTree): Int =
    if (t.children.isEmpty) 1 else t.children.iterator.map(countLeaves).sum

  private def hasUnlabeledInternal(t: NewickTree): Boolean =
    if (t.children.isEmpty) false
    else t.label.isEmpty || t.children.exists(hasUnlabeledInternal)

  private def firstNonBinaryInternal(t: NewickTree): Option[(String, Int)] = {
    if (t.children.nonEmpty && t.children.size != 2)
      Some((t.label.getOrElse(""), t.children.size))
    else
      t.children.iterator.flatMap(firstNonBinaryInternal).nextOption()
  }

  private def collectLeafLabels(t: NewickTree): Set[String] =
    if (t.children.isEmpty) t.label.toSet
    else t.children.foldLeft(Set.empty[String])(_ ++ collectLeafLabels(_))
}
