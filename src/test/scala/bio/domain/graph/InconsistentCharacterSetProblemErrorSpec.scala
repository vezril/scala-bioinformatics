package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InconsistentCharacterSetProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("InconsistentCharacterSetProblemError") {
    it("provides EmptyTable as a case object") {
      val err: InconsistentCharacterSetProblemError =
        InconsistentCharacterSetProblemError.EmptyTable
      err shouldBe InconsistentCharacterSetProblemError.EmptyTable
    }

    it("carries the row index and expected/actual widths in RaggedTable") {
      val err = InconsistentCharacterSetProblemError.RaggedTable(2, 6, 5)
      err.rowIndex shouldBe 2
      err.expected shouldBe 6
      err.actual shouldBe 5
    }

    it("carries the count and the maximum in ExceedsMaximumTaxa") {
      val err = InconsistentCharacterSetProblemError.ExceedsMaximumTaxa(101, 100)
      err.count shouldBe 101
      err.max shouldBe 100
    }

    it("carries the row index and the offending character in InvalidCharacter") {
      val err = InconsistentCharacterSetProblemError.InvalidCharacter(1, 'x')
      err.rowIndex shouldBe 1
      err.ch shouldBe 'x'
    }
  }
}
