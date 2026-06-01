package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CharacterBasedPhylogenyProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("CharacterBasedPhylogenyProblemError") {
    it("provides EmptyTaxa as a case object") {
      val err: CharacterBasedPhylogenyProblemError =
        CharacterBasedPhylogenyProblemError.EmptyTaxa
      err shouldBe CharacterBasedPhylogenyProblemError.EmptyTaxa
    }

    it("carries the repeated name in DuplicateTaxon") {
      CharacterBasedPhylogenyProblemError.DuplicateTaxon("cat").name shouldBe "cat"
    }

    it("carries the count and the maximum in ExceedsMaximumTaxa") {
      val err = CharacterBasedPhylogenyProblemError.ExceedsMaximumTaxa(81, 80)
      err.count shouldBe 81
      err.max shouldBe 80
    }

    it("carries the row index and the expected/actual lengths in RowLengthMismatch") {
      val err = CharacterBasedPhylogenyProblemError.RowLengthMismatch(2, 6, 5)
      err.rowIndex shouldBe 2
      err.expected shouldBe 6
      err.actual shouldBe 5
    }

    it("carries the row index and the offending character in InvalidCharacter") {
      val err = CharacterBasedPhylogenyProblemError.InvalidCharacter(1, 'x')
      err.rowIndex shouldBe 1
      err.ch shouldBe 'x'
    }

    it("carries the two conflicting row indices in ConflictingCharacters") {
      val err = CharacterBasedPhylogenyProblemError.ConflictingCharacters(0, 2)
      err.rowIndexA shouldBe 0
      err.rowIndexB shouldBe 2
    }
  }
}
