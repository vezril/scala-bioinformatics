package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InconsistentCharacterSetProblemSpec extends AnyFunSpec with Matchers {

  private val sampleRows = Vector("100001", "000110", "111000", "100111")

  describe("InconsistentCharacterSetProblem.from") {
    it("accepts the canonical sample table") {
      val result = InconsistentCharacterSetProblem.from(sampleRows)
      result.isRight shouldBe true
      result.foreach(_.rows shouldBe sampleRows)
    }

    it("rejects an empty table") {
      InconsistentCharacterSetProblem.from(Vector.empty) shouldBe
        Left(InconsistentCharacterSetProblemError.EmptyTable)
    }

    it("rejects a ragged table") {
      InconsistentCharacterSetProblem.from(Vector("100001", "00011")) shouldBe
        Left(InconsistentCharacterSetProblemError.RaggedTable(1, 6, 5))
    }

    it("rejects a table with more than 100 taxa") {
      val wide = Vector("1" * 101, "0" * 101)
      InconsistentCharacterSetProblem.from(wide) shouldBe
        Left(InconsistentCharacterSetProblemError.ExceedsMaximumTaxa(101, 100))
    }

    it("rejects a non-binary character symbol") {
      InconsistentCharacterSetProblem.from(Vector("10000x")) shouldBe
        Left(InconsistentCharacterSetProblemError.InvalidCharacter(0, 'x'))
    }

    it("reports a ragged row before an invalid character in a later row") {
      InconsistentCharacterSetProblem.from(Vector("100001", "00011", "10000x")) shouldBe
        Left(InconsistentCharacterSetProblemError.RaggedTable(1, 6, 5))
    }

    it("reports empty before any other failure") {
      InconsistentCharacterSetProblem.from(Vector.empty) shouldBe
        Left(InconsistentCharacterSetProblemError.EmptyTable)
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.InconsistentCharacterSetProblem(Vector("10", "01"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """InconsistentCharacterSetProblem
          |  .from(Vector("10", "01"))
          |  .toOption
          |  .get
          |  .copy(rows = Vector("11"))""".stripMargin
      )
    }
  }
}
