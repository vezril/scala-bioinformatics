package bio.algorithms.protein

import bio.domain.protein.{MassMultiset, SpectralConvolutionProblem}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectralConvolutionAlgoSpec extends AnyFunSpec with Matchers {

  private def multiset(ms: Double*): MassMultiset =
    MassMultiset
      .from(ms.toVector)
      .getOrElse(sys.error(s"invalid MassMultiset fixture: $ms"))

  private def problem(s1: MassMultiset, s2: MassMultiset): SpectralConvolutionProblem =
    SpectralConvolutionProblem(s1, s2)

  private val SampleS1 = multiset(186.07931, 287.12699, 548.20532, 580.18077,
    681.22845, 706.27446, 782.27613, 968.35544, 968.35544)
  private val SampleS2 = multiset(101.04768, 158.06914, 202.09536, 318.09979,
    419.14747, 463.17369)

  describe("SpectralConvolution.convolve") {
    it("computes the canonical Rosalind CONV sample multiplicity and shift") {
      val result = SpectralConvolution.convolve(problem(SampleS1, SampleS2))
      result.multiplicity shouldBe 3
      math.abs(result.shift) shouldBe (85.03163 +- 1e-5)
    }

    it("counts a single occurrence when no shift recurs") {
      val result = SpectralConvolution.convolve(problem(multiset(10.0, 20.0), multiset(5.0)))
      result.multiplicity shouldBe 1
    }

    it("finds the most common shift for a clear majority") {
      val s1     = multiset(10.0, 20.0, 30.0)
      val s2     = multiset(0.0001, 10.0001, 20.0001)
      val result = SpectralConvolution.convolve(problem(s1, s2))
      result.multiplicity shouldBe 3
      math.abs(result.shift) shouldBe (9.9999 +- 1e-5)
    }

    it("counts two differences equal to five decimals but off by 1e-11 in one bucket") {
      // 0.30000000001 - 0.10000000000 and 0.20000000000 - 0.00000000000 ... use
      // values whose differences round to the same 5-decimal shift.
      val s1     = multiset(5.0 + 1e-11, 5.00001)
      val s2     = multiset(4.00001, 4.0)
      // shifts: (5+1e-11)-4.00001 ≈ 0.99999, (5.00001)-4.0 = 1.00001,
      //         (5+1e-11)-4.0 ≈ 1.0, 5.00001-4.00001 = 1.0  -> two land on 1.00000
      val result = SpectralConvolution.convolve(problem(s1, s2))
      result.multiplicity shouldBe 2
      math.abs(result.shift) shouldBe (1.0 +- 1e-5)
    }
  }
}
