package bio.domain.recurrence

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MortalRabbitProblemSpec extends AnyFunSpec with Matchers {

  describe("MortalRabbitProblem.from") {
    it("accepts valid parameters and exposes both fields") {
      val problem = MortalRabbitProblem.from(6, 3).toOption.get
      problem.months   shouldBe 6
      problem.lifespan shouldBe 3
    }

    it("accepts the minimum-bound parameters (1, 1)") {
      val problem = MortalRabbitProblem.from(1, 1).toOption.get
      problem.months   shouldBe 1
      problem.lifespan shouldBe 1
    }

    it("rejects zero months") {
      MortalRabbitProblem.from(0, 3) shouldBe
        Left(MortalRabbitProblemError.NonPositiveMonths(0))
    }

    it("rejects negative months") {
      MortalRabbitProblem.from(-1, 3) shouldBe
        Left(MortalRabbitProblemError.NonPositiveMonths(-1))
    }

    it("rejects zero lifespan") {
      MortalRabbitProblem.from(5, 0) shouldBe
        Left(MortalRabbitProblemError.NonPositiveLifespan(0))
    }

    it("rejects negative lifespan") {
      MortalRabbitProblem.from(5, -2) shouldBe
        Left(MortalRabbitProblemError.NonPositiveLifespan(-2))
    }

    it("validates months before lifespan when both are invalid") {
      MortalRabbitProblem.from(0, 0) shouldBe
        Left(MortalRabbitProblemError.NonPositiveMonths(0))
    }
  }

  describe("MortalRabbitProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.recurrence.MortalRabbitProblem(1, 1)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.recurrence.MortalRabbitProblem.from(1, 1).toOption.get.copy(months = 99)"""
      )
    }
  }
}
