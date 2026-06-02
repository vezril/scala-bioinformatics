package bio.algorithms.protein

import bio.domain.protein.PrefixSpectrum
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InferProteinFromSpectrumSpec extends AnyFunSpec with Matchers {

  private def spectrum(weights: Vector[Double]): PrefixSpectrum =
    PrefixSpectrum
      .from(weights)
      .getOrElse(sys.error(s"invalid PrefixSpectrum fixture: $weights"))

  private val Sample =
    Vector(3524.8542, 3710.9335, 3841.974, 3970.0326, 4057.0646)

  describe("InferProteinFromSpectrum.infer") {
    it("reconstructs the canonical Rosalind SPEC sample protein WMQS") {
      InferProteinFromSpectrum
        .infer(spectrum(Sample))
        .protein
        .value shouldBe "WMQS"
    }

    it("produces a protein of length n - 1 for an n-weight spectrum") {
      val result = InferProteinFromSpectrum.infer(spectrum(Sample))
      result.protein.value.length shouldBe (Sample.length - 1)
    }

    it("yields the empty protein for a single-weight spectrum") {
      InferProteinFromSpectrum
        .infer(spectrum(Vector(42.0)))
        .protein
        .value shouldBe ""
    }
  }
}
