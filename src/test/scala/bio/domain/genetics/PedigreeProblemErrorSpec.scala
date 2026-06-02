package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PedigreeProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("PedigreeProblemError") {
    it("constructs InvalidGenotype carrying the label") {
      val err: PedigreeProblemError = PedigreeProblemError.InvalidGenotype("Bb")
      err shouldBe PedigreeProblemError.InvalidGenotype("Bb")
    }

    it("constructs NotBinary carrying the child count") {
      val err: PedigreeProblemError = PedigreeProblemError.NotBinary(3)
      err shouldBe PedigreeProblemError.NotBinary(3)
    }
  }
}
