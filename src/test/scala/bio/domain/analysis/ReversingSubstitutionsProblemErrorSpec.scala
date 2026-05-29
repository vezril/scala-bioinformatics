package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversingSubstitutionsProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("ReversingSubstitutionsProblemError") {
    it("EmptyAlignment is a singleton case object") {
      val err: ReversingSubstitutionsProblemError =
        ReversingSubstitutionsProblemError.EmptyAlignment
      err shouldBe ReversingSubstitutionsProblemError.EmptyAlignment
    }

    it("LengthMismatch carries rowIndex, length, expectedLength") {
      val err = ReversingSubstitutionsProblemError.LengthMismatch(1, 3, 2)
      err.rowIndex shouldBe 1
      err.length shouldBe 3
      err.expectedLength shouldBe 2
    }

    it("SequenceTooLong carries rowIndex, length, max") {
      val err = ReversingSubstitutionsProblemError.SequenceTooLong(0, 401, 400)
      err.rowIndex shouldBe 0
      err.length shouldBe 401
      err.max shouldBe 400
    }

    it("InvalidCharacter carries rowIndex, position, character") {
      val err = ReversingSubstitutionsProblemError.InvalidCharacter(2, 4, '-')
      err.rowIndex shouldBe 2
      err.position shouldBe 4
      err.character shouldBe '-'
    }

    it("TooManyStrings carries actual, max") {
      val err = ReversingSubstitutionsProblemError.TooManyStrings(101, 100)
      err.actual shouldBe 101
      err.max shouldBe 100
    }

    it("InternalNodeMissingLabel is a singleton case object") {
      ReversingSubstitutionsProblemError.InternalNodeMissingLabel shouldBe
        ReversingSubstitutionsProblemError.InternalNodeMissingLabel
    }

    it("LeafMissingLabel is a singleton case object") {
      ReversingSubstitutionsProblemError.LeafMissingLabel shouldBe
        ReversingSubstitutionsProblemError.LeafMissingLabel
    }

    it("NonBinaryInternalNode carries label and degree") {
      val err = ReversingSubstitutionsProblemError.NonBinaryInternalNode("root", 3)
      err.label shouldBe "root"
      err.degree shouldBe 3
    }

    it("NodeLabelMismatch carries the two symmetric-difference sets") {
      val err = ReversingSubstitutionsProblemError.NodeLabelMismatch(Set("c"), Set("d"))
      err.treeOnly shouldBe Set("c")
      err.alignmentOnly shouldBe Set("d")
    }

    it("makes all nine variants distinct subtypes of the sealed trait") {
      val errs: List[ReversingSubstitutionsProblemError] = List(
        ReversingSubstitutionsProblemError.EmptyAlignment,
        ReversingSubstitutionsProblemError.LengthMismatch(0, 0, 0),
        ReversingSubstitutionsProblemError.SequenceTooLong(0, 401, 400),
        ReversingSubstitutionsProblemError.InvalidCharacter(0, 0, 'X'),
        ReversingSubstitutionsProblemError.TooManyStrings(101, 100),
        ReversingSubstitutionsProblemError.InternalNodeMissingLabel,
        ReversingSubstitutionsProblemError.LeafMissingLabel,
        ReversingSubstitutionsProblemError.NonBinaryInternalNode("r", 3),
        ReversingSubstitutionsProblemError.NodeLabelMismatch(Set("a"), Set("b"))
      )
      errs.distinct.size shouldBe 9
    }
  }
}
