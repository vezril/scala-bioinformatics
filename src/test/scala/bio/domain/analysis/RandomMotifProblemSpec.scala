package bio.domain.analysis

import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RandomMotifProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def prob(d: Double): Probability =
    Probability.from(d).getOrElse(sys.error(s"invalid Probability fixture: $d"))

  describe("RandomMotifProblem.from") {
    it("accepts a motif and trial count within bounds, preserving the inputs") {
      val motif = dna("ATAGCCGA")
      val gc = prob(0.6)
      val result = RandomMotifProblem.from(motif, 90000, gc)
      result.map(_.motif) shouldBe Right(motif)
      result.map(_.trials) shouldBe Right(90000)
      result.map(_.gcContent) shouldBe Right(gc)
    }

    it("accepts the upper bounds (10 bp motif, 100000 trials)") {
      RandomMotifProblem.from(dna("A" * 10), 100000, prob(0.5)).isRight shouldBe true
    }

    it("accepts an empty motif") {
      RandomMotifProblem.from(dna(""), 5, prob(0.5)).isRight shouldBe true
    }

    it("rejects a motif longer than 10 bp") {
      RandomMotifProblem.from(dna("A" * 11), 5, prob(0.5)) shouldBe Left(
        RandomMotifProblemError.MotifTooLong(11, 10)
      )
    }

    it("rejects a non-positive trial count") {
      RandomMotifProblem.from(dna("AT"), 0, prob(0.5)) shouldBe Left(
        RandomMotifProblemError.NonPositiveTrials(0)
      )
    }

    it("rejects a trial count above the maximum") {
      RandomMotifProblem.from(dna("AT"), 100001, prob(0.5)) shouldBe Left(
        RandomMotifProblemError.TooManyTrials(100001, 100000)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.RandomMotifProblem(bio.domain.nucleic.DnaString.from("AT").toOption.get, 5, bio.domain.stats.Probability.from(0.5).toOption.get)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.RandomMotifProblem.from(bio.domain.nucleic.DnaString.from("AT").toOption.get, 5, bio.domain.stats.Probability.from(0.5).toOption.get).toOption.get.copy()"""
      )
    }
  }
}
