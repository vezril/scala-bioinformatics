package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ChromosomePairsErrorSpec extends AnyFunSpec with Matchers {

  describe("ChromosomePairsError.NonPositive") {
    it("carries the offending value (zero)") {
      ChromosomePairsError.NonPositive(0).value shouldBe 0
    }

    it("carries the offending value (negative)") {
      ChromosomePairsError.NonPositive(-5).value shouldBe -5
    }
  }

  describe("ChromosomePairsError.ExceedsMaximum") {
    it("carries the offending value and the maximum") {
      val err = ChromosomePairsError.ExceedsMaximum(51, 50)
      err.value shouldBe 51
      err.max shouldBe 50
    }
  }
}
