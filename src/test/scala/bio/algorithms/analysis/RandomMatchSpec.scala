package bio.algorithms.analysis

import bio.domain.analysis.RandomMatchProblem
import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RandomMatchSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  private def prob(d: Double): Probability =
    Probability.from(d).getOrElse(sys.error(s"invalid Probability in fixture: $d"))

  private def problem(s: String, gcs: Vector[Double]): RandomMatchProblem =
    RandomMatchProblem
      .from(dna(s), gcs.map(prob))
      .getOrElse(sys.error("invalid RandomMatchProblem in fixture"))

  private val RosalindTolerance: Double = 0.001
  private val ExactTolerance: Double    = 1e-9

  describe("RandomMatch.logProbabilities") {
    it("produces the canonical Rosalind sample log probabilities within 0.001 absolute error") {
      val p        = problem("ACGATACAA", Vector(0.129, 0.287, 0.423, 0.476, 0.641, 0.742, 0.783))
      val actual   = RandomMatch.logProbabilities(p)
      val expected = Vector(-5.737, -5.217, -5.263, -5.360, -5.958, -6.628, -7.009)
      actual.size shouldBe expected.size
      actual.zip(expected).foreach { case (a, e) =>
        a shouldBe e +- RosalindTolerance
      }
    }

    it("returns 0.0 for every GC value when the DNA is empty") {
      val p = problem("", Vector(0.25, 0.5, 0.75))
      RandomMatch.logProbabilities(p) shouldBe Vector(0.0, 0.0, 0.0)
    }

    it("returns an empty vector when gcContents is empty") {
      val p = problem("ACGT", Vector.empty)
      RandomMatch.logProbabilities(p) shouldBe Vector.empty
    }

    it("returns Vector(Double.NegativeInfinity) for gc = 0 with a G in the sequence") {
      val p      = problem("G", Vector(0.0))
      val result = RandomMatch.logProbabilities(p)
      result.size shouldBe 1
      result.head shouldBe Double.NegativeInfinity
    }

    it("returns Vector(Double.NegativeInfinity) for gc = 1 with an A in the sequence") {
      val p      = problem("A", Vector(1.0))
      val result = RandomMatch.logProbabilities(p)
      result.size shouldBe 1
      result.head shouldBe Double.NegativeInfinity
    }

    it("returns log10(0.25) for a single G at gc = 0.5") {
      val p        = problem("G", Vector(0.5))
      val actual   = RandomMatch.logProbabilities(p).head
      val expected = Math.log10(0.25)
      actual shouldBe expected +- ExactTolerance
    }

    it("returns a vector whose length equals gcContents.size") {
      val p = problem("ACGAT", Vector(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7))
      RandomMatch.logProbabilities(p).size shouldBe 7
    }
  }
}
