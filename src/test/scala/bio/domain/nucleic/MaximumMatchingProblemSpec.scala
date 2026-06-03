package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaximumMatchingProblemSpec extends AnyFunSpec with Matchers {

  private def rna(s: String): RnaString =
    RnaString.from(s).getOrElse(fail(s"invalid RNA string: $s"))

  describe("MaximumMatchingProblem.from") {
    it("accepts an RNA string within the length bound and records the counts") {
      val result = MaximumMatchingProblem.from(rna("AUGCUUC"))
      result.map(p => (p.aCount, p.uCount, p.cCount, p.gCount)) shouldBe Right((1, 3, 2, 1))
    }

    it("accepts an unbalanced RNA string") {
      MaximumMatchingProblem.from(rna("AUU")).map(p => (p.aCount, p.uCount)) shouldBe Right((1, 2))
    }

    it("accepts the empty RNA string with all counts zero") {
      MaximumMatchingProblem
        .from(rna(""))
        .map(p => (p.aCount, p.uCount, p.cCount, p.gCount)) shouldBe Right((0, 0, 0, 0))
    }

    it("rejects an RNA string longer than the bound") {
      val long = rna("A" * 101)
      MaximumMatchingProblem.from(long) shouldBe Left(
        MaximumMatchingProblemError.ExceedsMaxLength(101, 100)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.nucleic.MaximumMatchingProblem(
          |  bio.domain.nucleic.RnaString.from("AU").toOption.get, 1, 1, 0, 0
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """MaximumMatchingProblem.from(RnaString.from("AU").toOption.get).toOption.get.copy(aCount = 99)"""
      )
    }
  }
}
