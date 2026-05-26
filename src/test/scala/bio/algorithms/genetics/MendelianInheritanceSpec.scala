package bio.algorithms.genetics

import bio.domain.genetics.Population
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalactic.TolerantNumerics

class MendelianInheritanceSpec extends AnyFunSpec with Matchers {

  private val SampleTolerance: Double = 1e-5
  private val AnalyticTolerance: Double = 1e-9

  private def pop(k: Int, m: Int, n: Int): Population =
    Population.from(k, m, n).getOrElse(sys.error(s"Invalid Population in test: ($k, $m, $n)"))

  private def runAndExtract(k: Int, m: Int, n: Int): Double =
    MendelianInheritance.probabilityOfDominantPhenotype(pop(k, m, n)).value

  describe("MendelianInheritance.probabilityOfDominantPhenotype") {
    it("matches the Rosalind sample (2, 2, 2) → 0.78333") {
      implicit val eq: org.scalactic.Equality[Double] = TolerantNumerics.tolerantDoubleEquality(SampleTolerance)
      runAndExtract(2, 2, 2) shouldEqual 0.78333
    }

    it("returns 1.0 for an all-homozygous-dominant population (2, 0, 0)") {
      implicit val eq: org.scalactic.Equality[Double] = TolerantNumerics.tolerantDoubleEquality(AnalyticTolerance)
      runAndExtract(2, 0, 0) shouldEqual 1.0
    }

    it("returns 0.0 for an all-homozygous-recessive population (0, 0, 2)") {
      implicit val eq: org.scalactic.Equality[Double] = TolerantNumerics.tolerantDoubleEquality(AnalyticTolerance)
      runAndExtract(0, 0, 2) shouldEqual 0.0
    }

    it("returns 0.75 for an all-heterozygous population (0, 2, 0)") {
      implicit val eq: org.scalactic.Equality[Double] = TolerantNumerics.tolerantDoubleEquality(AnalyticTolerance)
      runAndExtract(0, 2, 0) shouldEqual 0.75
    }

    it("returns 5/6 for a one-of-each population (1, 1, 1)") {
      implicit val eq: org.scalactic.Equality[Double] = TolerantNumerics.tolerantDoubleEquality(AnalyticTolerance)
      runAndExtract(1, 1, 1) shouldEqual (5.0 / 6.0)
    }

    it("returns 0.5 for heterozygous and recessive only (0, 1, 1)") {
      implicit val eq: org.scalactic.Equality[Double] = TolerantNumerics.tolerantDoubleEquality(AnalyticTolerance)
      runAndExtract(0, 1, 1) shouldEqual 0.5
    }

    it("returns 1.0 for dominant and recessive only (1, 0, 1) — offspring is always Aa") {
      implicit val eq: org.scalactic.Equality[Double] = TolerantNumerics.tolerantDoubleEquality(AnalyticTolerance)
      runAndExtract(1, 0, 1) shouldEqual 1.0
    }
  }
}
