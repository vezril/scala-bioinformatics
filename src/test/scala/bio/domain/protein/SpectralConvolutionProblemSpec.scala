package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectralConvolutionProblemSpec extends AnyFunSpec with Matchers {

  private def multiset(ms: Double*): MassMultiset =
    MassMultiset
      .from(ms.toVector)
      .getOrElse(sys.error(s"invalid MassMultiset fixture: $ms"))

  describe("SpectralConvolutionProblem") {
    it("bundles the two input multisets, exposing them unchanged") {
      val s1      = multiset(10.0, 20.0)
      val s2      = multiset(5.0)
      val problem = SpectralConvolutionProblem(s1, s2)

      problem.s1 shouldBe s1
      problem.s2 shouldBe s2
    }
  }
}
