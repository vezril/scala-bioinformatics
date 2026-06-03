package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WobbleMatchingProblemSpec extends AnyFunSpec with Matchers {

  private def rna(s: String): RnaString =
    RnaString.from(s).getOrElse(fail(s"invalid RNA string: $s"))

  describe("WobbleMatchingProblem.from") {
    it("accepts an RNA string within the length bound") {
      val r = rna("AUGC")
      WobbleMatchingProblem.from(r).map(_.rna) shouldBe Right(r)
    }

    it("accepts the empty RNA string") {
      val r = rna("")
      WobbleMatchingProblem.from(r).map(_.rna) shouldBe Right(r)
    }

    it("rejects an RNA string longer than the bound") {
      val r = rna("A" * 201)
      WobbleMatchingProblem.from(r) shouldBe Left(
        WobbleMatchingProblemError.SequenceTooLong(201, 200)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.nucleic.WobbleMatchingProblem(bio.domain.nucleic.RnaString.from("AUGC").toOption.get)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """WobbleMatchingProblem.from(RnaString.from("AUGC").toOption.get).toOption.get.copy()"""
      )
    }
  }
}
