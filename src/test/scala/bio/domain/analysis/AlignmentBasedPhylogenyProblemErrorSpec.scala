package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AlignmentBasedPhylogenyProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("AlignmentBasedPhylogenyProblemError.EmptyAlignment") {
    it("is a singleton case object representing an empty input alignment") {
      val err: AlignmentBasedPhylogenyProblemError =
        AlignmentBasedPhylogenyProblemError.EmptyAlignment
      err shouldBe AlignmentBasedPhylogenyProblemError.EmptyAlignment
    }
  }

  describe("AlignmentBasedPhylogenyProblemError.LengthMismatch") {
    it("carries the offending row index, its length, and the expected length") {
      val err = AlignmentBasedPhylogenyProblemError.LengthMismatch(1, 3, 2)
      err.rowIndex shouldBe 1
      err.length shouldBe 3
      err.expectedLength shouldBe 2
    }
  }

  describe("AlignmentBasedPhylogenyProblemError.SequenceTooLong") {
    it("carries the offending row index, its length, and the configured cap") {
      val err = AlignmentBasedPhylogenyProblemError.SequenceTooLong(0, 301, 300)
      err.rowIndex shouldBe 0
      err.length shouldBe 301
      err.max shouldBe 300
    }
  }

  describe("AlignmentBasedPhylogenyProblemError.InvalidCharacter") {
    it("carries the offending row index, column position, and character") {
      val err = AlignmentBasedPhylogenyProblemError.InvalidCharacter(2, 4, 'X')
      err.rowIndex shouldBe 2
      err.position shouldBe 4
      err.character shouldBe 'X'
    }
  }

  describe("AlignmentBasedPhylogenyProblemError.TooManyLeaves") {
    it("carries the actual leaf count and the configured cap") {
      val err = AlignmentBasedPhylogenyProblemError.TooManyLeaves(501, 500)
      err.actual shouldBe 501
      err.max shouldBe 500
    }
  }

  describe("AlignmentBasedPhylogenyProblemError.InternalNodeMissingLabel") {
    it("is a singleton case object") {
      AlignmentBasedPhylogenyProblemError.InternalNodeMissingLabel shouldBe
        AlignmentBasedPhylogenyProblemError.InternalNodeMissingLabel
    }
  }

  describe("AlignmentBasedPhylogenyProblemError.NonBinaryInternalNode") {
    it("carries the offending node label and its degree") {
      val err = AlignmentBasedPhylogenyProblemError.NonBinaryInternalNode("root", 3)
      err.label shouldBe "root"
      err.degree shouldBe 3
    }
  }

  describe("AlignmentBasedPhylogenyProblemError.LeafLabelMismatch") {
    it("carries the symmetric-difference sets of tree-only and alignment-only labels") {
      val err = AlignmentBasedPhylogenyProblemError.LeafLabelMismatch(
        treeOnly = Set("c"),
        alignmentOnly = Set("d")
      )
      err.treeOnly shouldBe Set("c")
      err.alignmentOnly shouldBe Set("d")
    }
  }

  describe("AlignmentBasedPhylogenyProblemError as an ADT") {
    it("makes all eight variants distinct subtypes of the sealed trait") {
      val errs: List[AlignmentBasedPhylogenyProblemError] = List(
        AlignmentBasedPhylogenyProblemError.EmptyAlignment,
        AlignmentBasedPhylogenyProblemError.LengthMismatch(1, 3, 2),
        AlignmentBasedPhylogenyProblemError.SequenceTooLong(0, 301, 300),
        AlignmentBasedPhylogenyProblemError.InvalidCharacter(0, 0, 'X'),
        AlignmentBasedPhylogenyProblemError.TooManyLeaves(501, 500),
        AlignmentBasedPhylogenyProblemError.InternalNodeMissingLabel,
        AlignmentBasedPhylogenyProblemError.NonBinaryInternalNode("r", 3),
        AlignmentBasedPhylogenyProblemError.LeafLabelMismatch(Set("a"), Set("b"))
      )
      errs.distinct.size shouldBe 8
    }
  }
}
