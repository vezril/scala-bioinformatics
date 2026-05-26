package bio.algorithms.recurrence

import bio.domain.recurrence.RabbitProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FibonacciRabbitsSpec extends AnyFunSpec with Matchers {

  private def problem(months: Int, litterSize: Int): RabbitProblem =
    RabbitProblem.from(months, litterSize)
      .getOrElse(sys.error(s"Invalid RabbitProblem in test: ($months, $litterSize)"))

  describe("FibonacciRabbits.population") {
    it("matches the Rosalind sample (5, 3) → 19") {
      FibonacciRabbits.population(problem(5, 3)) shouldBe BigInt(19)
    }

    it("returns 1 for n=1 regardless of litter size") {
      FibonacciRabbits.population(problem(1, 3)) shouldBe BigInt(1)
    }

    it("returns 1 for n=2 regardless of litter size") {
      FibonacciRabbits.population(problem(2, 3)) shouldBe BigInt(1)
    }

    it("computes standard Fibonacci with k=1 at n=10 (F(10) = 55)") {
      FibonacciRabbits.population(problem(10, 1)) shouldBe BigInt(55)
    }

    it("returns constant 1 when litter size is zero") {
      FibonacciRabbits.population(problem(40, 0)) shouldBe BigInt(1)
    }

    it("computes standard Fibonacci F(5) = 5") {
      FibonacciRabbits.population(problem(5, 1)) shouldBe BigInt(5)
    }

    it("computes F(3) = 4 with k=3 (1 + 3*1)") {
      FibonacciRabbits.population(problem(3, 3)) shouldBe BigInt(4)
    }

    it("computes F(4) = 7 with k=3 (4 + 3*1)") {
      FibonacciRabbits.population(problem(4, 3)) shouldBe BigInt(7)
    }
  }
}
