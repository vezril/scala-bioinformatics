package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class KmerCompositionProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("KmerCompositionProblemError") {
    it("exposes a NonPositiveK case carrying the requested length") {
      val err: KmerCompositionProblemError = KmerCompositionProblemError.NonPositiveK(0)
      err shouldBe KmerCompositionProblemError.NonPositiveK(0)
    }

    it("exposes a KExceedsMaximum case carrying the value and the maximum") {
      val err: KmerCompositionProblemError = KmerCompositionProblemError.KExceedsMaximum(11, 10)
      err shouldBe KmerCompositionProblemError.KExceedsMaximum(11, 10)
    }
  }
}
