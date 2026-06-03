package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectrumMatchProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("SpectrumMatchProblemError") {
    it("provides EmptyProteinList as a case object") {
      val err: SpectrumMatchProblemError = SpectrumMatchProblemError.EmptyProteinList
      err shouldBe SpectrumMatchProblemError.EmptyProteinList
    }

    it("provides EmptySpectrum as a case object") {
      val err: SpectrumMatchProblemError = SpectrumMatchProblemError.EmptySpectrum
      err shouldBe SpectrumMatchProblemError.EmptySpectrum
    }

    it("constructs NonPositiveMass carrying the index and value") {
      val err: SpectrumMatchProblemError = SpectrumMatchProblemError.NonPositiveMass(1, -2.0)
      err shouldBe SpectrumMatchProblemError.NonPositiveMass(1, -2.0)
    }
  }
}
