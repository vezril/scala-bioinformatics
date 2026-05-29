package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SharedSplicedMotifProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("SharedSplicedMotifProblem.from") {
    it("accepts the canonical Rosalind LCSQ sample") {
      val result = SharedSplicedMotifProblem.from(dna("AACCTTGG"), dna("ACACTGTGA"))
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.left.value shouldBe "AACCTTGG"
      problem.right.value shouldBe "ACACTGTGA"
    }

    it("accepts two empty strings") {
      val result = SharedSplicedMotifProblem.from(dna(""), dna(""))
      result.isRight shouldBe true
    }

    it("accepts an empty left with a non-empty right") {
      val result = SharedSplicedMotifProblem.from(dna(""), dna("ACGT"))
      result.isRight shouldBe true
    }

    it("accepts a non-empty left with an empty right") {
      val result = SharedSplicedMotifProblem.from(dna("ACGT"), dna(""))
      result.isRight shouldBe true
    }

    it("accepts both strings at the 1000-character upper bound") {
      val result = SharedSplicedMotifProblem.from(dna("A" * 1000), dna("A" * 1000))
      result.isRight shouldBe true
    }

    it("rejects a 1001-character left as LeftTooLong(1001, 1000)") {
      SharedSplicedMotifProblem.from(dna("A" * 1001), dna("A")) shouldBe
        Left(SharedSplicedMotifProblemError.LeftTooLong(1001, 1000))
    }

    it("rejects a 1001-character right as RightTooLong(1001, 1000)") {
      SharedSplicedMotifProblem.from(dna("A"), dna("A" * 1001)) shouldBe
        Left(SharedSplicedMotifProblemError.RightTooLong(1001, 1000))
    }
  }

  describe("SharedSplicedMotifProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.SharedSplicedMotifProblem(dna("A"), dna("A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.SharedSplicedMotifProblem
          |  .from(dna("A"), dna("A")).toOption.get.copy(right = dna("C"))""".stripMargin
      )
    }
  }
}
