package bio.algorithms.analysis

import bio.domain.analysis.RandomMotifProblem
import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MatchingRandomMotifsAlgoSpec extends AnyFunSpec with Matchers {

  private def problem(motif: String, trials: Int, gc: Double): RandomMotifProblem =
    RandomMotifProblem
      .from(
        DnaString.from(motif).getOrElse(sys.error(s"invalid DnaString: $motif")),
        trials,
        Probability.from(gc).getOrElse(sys.error(s"invalid Probability: $gc"))
      )
      .getOrElse(sys.error("invalid RandomMotifProblem fixture"))

  private def p(motif: String, trials: Int, gc: Double): Double =
    MatchingRandomMotifs.probability(problem(motif, trials, gc)).probability

  describe("MatchingRandomMotifs.probability") {
    it("computes the canonical Rosalind RSTR sample within 0.001") {
      p("ATAGCCGA", 90000, 0.6) shouldBe 0.689 +- 0.001
    }

    it("returns the single-string match probability for a single trial") {
      p("G", 1, 0.5) shouldBe 0.25 +- 1e-12
    }

    it("weights every symbol equally under uniform GC-content") {
      p("AT", 1, 0.5) shouldBe 0.0625 +- 1e-12
    }

    it("yields zero when a motif symbol is impossible under the GC-content") {
      p("G", 5, 0.0) shouldBe 0.0 +- 1e-12
    }

    it("yields certainty for an empty motif") {
      p("", 5, 0.6) shouldBe 1.0 +- 1e-12
    }
  }
}
