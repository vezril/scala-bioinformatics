package bio.algorithms.genetics

import bio.domain.genetics.ChromosomePairs
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class IndependentSegregationSpec extends AnyFunSpec with Matchers {

  private def pairs(n: Int): ChromosomePairs =
    ChromosomePairs.from(n).getOrElse(sys.error(s"invalid ChromosomePairs fixture: $n"))

  private val RosalindTolerance: Double = 0.001
  private val TightTolerance: Double    = 1e-9
  private val LooseTolerance: Double    = 1e-6

  describe("IndependentSegregation.logProbs") {
    it("produces the canonical Rosalind sample (n=5) within 0.001 absolute error per element") {
      val actual = IndependentSegregation.logProbs(pairs(5))
      val expected =
        Vector(0.000, -0.004, -0.024, -0.082, -0.206, -0.424, -0.765, -1.262, -1.969, -3.010)
      actual.size shouldBe expected.size
      actual.zip(expected).foreach { case (a, e) =>
        a shouldBe e +- RosalindTolerance
      }
    }

    it("produces log10(3/4) and log10(1/4) for n=1") {
      val actual = IndependentSegregation.logProbs(pairs(1))
      actual.size shouldBe 2
      actual(0) shouldBe Math.log10(0.75) +- TightTolerance
      actual(1) shouldBe Math.log10(0.25) +- TightTolerance
    }

    it("produces a vector whose length equals 2 * pairs.value (n=7 → length 14)") {
      IndependentSegregation.logProbs(pairs(7)).size shouldBe 14
    }

    it("last entry at n=50 equals log10(0.5^100) ≈ -30.103") {
      val result   = IndependentSegregation.logProbs(pairs(50))
      val expected = -100.0 * Math.log10(2.0)
      result(99) shouldBe expected +- LooseTolerance
    }

    it("first entry at n=10 equals log10(1 - 0.5^20) — sharing at least one chromosome is nearly certain") {
      val result   = IndependentSegregation.logProbs(pairs(10))
      val expected = Math.log10(1.0 - Math.pow(0.5, 20.0))
      result(0) shouldBe expected +- LooseTolerance
    }

    it("produces a length-100 vector at the upper bound (n=50) with every entry <= 0") {
      val result = IndependentSegregation.logProbs(pairs(50))
      result.size shouldBe 100
      result.foreach { v => v should be <= 0.0 }
    }

    it("entries are monotonically non-increasing (n=5)") {
      val result = IndependentSegregation.logProbs(pairs(5))
      result.sliding(2).foreach { case Vector(a, b) =>
        a should be >= b
      }
    }
  }
}
