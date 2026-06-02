package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GenotypeProbabilitiesResultSpec extends AnyFunSpec with Matchers {

  describe("GenotypeProbabilities result") {
    it("exposes the three probabilities") {
      val r = GenotypeProbabilities(0.156, 0.5, 0.344)
      r.homozygousDominant shouldBe 0.156
      r.heterozygous shouldBe 0.5
      r.homozygousRecessive shouldBe 0.344
    }

    it("formats three space-separated three-decimal values") {
      GenotypeProbabilities(0.156, 0.5, 0.344).format shouldBe "0.156 0.500 0.344"
    }
  }
}
