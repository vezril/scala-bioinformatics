package bio.domain.stats

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProbabilitySpec extends AnyFunSpec with Matchers {

  describe("Probability.from") {
    it("accepts 0.5") {
      Probability.from(0.5).map(_.value) shouldBe Right(0.5)
    }

    it("accepts the lower bound 0.0") {
      Probability.from(0.0).map(_.value) shouldBe Right(0.0)
    }

    it("accepts the upper bound 1.0") {
      Probability.from(1.0).map(_.value) shouldBe Right(1.0)
    }

    it("rejects a value greater than 1") {
      Probability.from(1.5) shouldBe Left(ProbabilityError.OutOfRange(1.5))
    }

    it("rejects a negative value") {
      Probability.from(-0.1) shouldBe Left(ProbabilityError.OutOfRange(-0.1))
    }

    it("rejects NaN") {
      Probability.from(Double.NaN) shouldBe Left(ProbabilityError.NotFinite)
    }

    it("rejects positive infinity") {
      Probability.from(Double.PositiveInfinity) shouldBe Left(ProbabilityError.NotFinite)
    }

    it("rejects negative infinity") {
      Probability.from(Double.NegativeInfinity) shouldBe Left(ProbabilityError.NotFinite)
    }
  }
}
