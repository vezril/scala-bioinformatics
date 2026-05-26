package bio.domain.recurrence

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RabbitProblemSpec extends AnyFunSpec with Matchers {

  describe("RabbitProblem.from") {
    it("accepts a valid input (5, 3)") {
      val result = RabbitProblem.from(5, 3)
      result shouldBe a[Right[_, _]]
      val problem = result.toOption.get
      problem.months shouldBe 5
      problem.litterSize shouldBe 3
    }

    it("accepts zero litter size (10, 0)") {
      RabbitProblem.from(10, 0) shouldBe a[Right[_, _]]
    }

    it("accepts a single month (1, 3)") {
      RabbitProblem.from(1, 3) shouldBe a[Right[_, _]]
    }

    it("rejects zero months") {
      RabbitProblem.from(0, 3) shouldBe Left(RabbitProblemError.NonPositiveMonths(0))
    }

    it("rejects negative months") {
      RabbitProblem.from(-5, 3) shouldBe Left(RabbitProblemError.NonPositiveMonths(-5))
    }

    it("rejects negative litter size") {
      RabbitProblem.from(5, -1) shouldBe Left(RabbitProblemError.NegativeLitterSize(-1))
    }
  }

  describe("RabbitProblem construction invariants") {
    it("cannot be constructed via a public companion apply (smart constructor is the only path)") {
      assertDoesNotCompile("""bio.domain.RabbitProblem(5, 3)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile("""bio.domain.RabbitProblem.from(5, 3).toOption.get.copy(months = 99)""")
    }
  }
}
