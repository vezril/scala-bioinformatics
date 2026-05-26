package bio.algorithms.recurrence

import bio.domain.recurrence.MortalRabbitProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MortalFibonacciRabbitsSpec extends AnyFunSpec with Matchers {

  private def mrp(months: Int, lifespan: Int): MortalRabbitProblem =
    MortalRabbitProblem
      .from(months, lifespan)
      .getOrElse(sys.error(s"invalid MortalRabbitProblem in fixture: ($months, $lifespan)"))

  describe("MortalFibonacciRabbits.population") {
    it("produces 4 for the Rosalind sample (n=6, m=3)") {
      MortalFibonacciRabbits.population(mrp(6, 3)) shouldBe BigInt(4)
    }

    it("returns 1 for n=1 with any lifespan (the seed pair is the population)") {
      MortalFibonacciRabbits.population(mrp(1, 5)) shouldBe BigInt(1)
    }

    it("returns 1 for n=1 with lifespan=1 (the seed pair is alive in month 1)") {
      MortalFibonacciRabbits.population(mrp(1, 1)) shouldBe BigInt(1)
    }

    it("returns 0 for n=2 with lifespan=1 (pair dies before reproducing)") {
      MortalFibonacciRabbits.population(mrp(2, 1)) shouldBe BigInt(0)
    }

    it("returns 0 for n=10 with lifespan=1 (population stays extinct)") {
      MortalFibonacciRabbits.population(mrp(10, 1)) shouldBe BigInt(0)
    }

    it("returns 1 for n=2 with lifespan=2 (pair matured but did not yet reproduce)") {
      MortalFibonacciRabbits.population(mrp(2, 2)) shouldBe BigInt(1)
    }

    it("equals classic F(6)=8 when lifespan exceeds n (no deaths within the window)") {
      MortalFibonacciRabbits.population(mrp(6, 100)) shouldBe BigInt(8)
    }

    it("equals classic F(10)=55 when lifespan exceeds n") {
      MortalFibonacciRabbits.population(mrp(10, 100)) shouldBe BigInt(55)
    }

    it("handles large n with a moderate lifespan without overflow (BigInt result)") {
      // n=90, m=20: pure sanity check that the algorithm completes and returns a non-negative BigInt
      val result = MortalFibonacciRabbits.population(mrp(90, 20))
      result should be >= BigInt(0)
    }
  }
}
