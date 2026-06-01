package bio.domain.combinatorics

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class KmerCompositionProblemSpec extends AnyFunSpec with Matchers {

  private val sampleDna = DnaString.from("ACGTACGT").toOption.get

  describe("KmerCompositionProblem.from") {
    it("accepts a valid DNA string and length and preserves them") {
      val result = KmerCompositionProblem.from(sampleDna, 4)
      result.isRight shouldBe true
      result.foreach { p =>
        p.dna shouldBe sampleDna
        p.k shouldBe 4
      }
    }

    it("rejects a non-positive length") {
      KmerCompositionProblem.from(sampleDna, 0) shouldBe
        Left(KmerCompositionProblemError.NonPositiveK(0))
    }

    it("rejects a length greater than 10") {
      KmerCompositionProblem.from(sampleDna, 11) shouldBe
        Left(KmerCompositionProblemError.KExceedsMaximum(11, 10))
    }

    it("reports the non-positive length before the exceeds-maximum check") {
      KmerCompositionProblem.from(sampleDna, -5) shouldBe
        Left(KmerCompositionProblemError.NonPositiveK(-5))
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.combinatorics.KmerCompositionProblem(
          |  bio.domain.nucleic.DnaString.from("ACGT").toOption.get, 4
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """KmerCompositionProblem
          |  .from(bio.domain.nucleic.DnaString.from("ACGT").toOption.get, 4)
          |  .toOption
          |  .get
          |  .copy(k = 3)""".stripMargin
      )
    }
  }
}
