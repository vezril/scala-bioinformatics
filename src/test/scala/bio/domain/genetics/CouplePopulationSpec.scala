package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CouplePopulationSpec extends AnyFunSpec with Matchers {

  describe("CouplePopulation.from") {
    it("accepts an all-zero population") {
      val result = CouplePopulation.from(0, 0, 0, 0, 0, 0)
      result shouldBe a[Right[_, _]]
      val pop = result.toOption.get
      pop.homDomHomDom shouldBe 0
      pop.homDomHet    shouldBe 0
      pop.homDomHomRec shouldBe 0
      pop.hetHet       shouldBe 0
      pop.hetHomRec    shouldBe 0
      pop.homRecHomRec shouldBe 0
    }

    it("accepts the Rosalind sample (1, 0, 0, 1, 0, 1) and exposes each field") {
      val pop = CouplePopulation.from(1, 0, 0, 1, 0, 1).toOption.get
      pop.homDomHomDom shouldBe 1
      pop.homDomHet    shouldBe 0
      pop.homDomHomRec shouldBe 0
      pop.hetHet       shouldBe 1
      pop.hetHomRec    shouldBe 0
      pop.homRecHomRec shouldBe 1
    }

    it("accepts the upper boundary (20000 ×6)") {
      val pop = CouplePopulation.from(20000, 20000, 20000, 20000, 20000, 20000).toOption.get
      pop.homDomHomDom shouldBe 20000
      pop.homRecHomRec shouldBe 20000
    }

    it("rejects a negative count, carrying the 1-based index and offending value") {
      CouplePopulation.from(1, 2, -3, 4, 5, 6) shouldBe
        Left(CouplePopulationError.NegativeCount(3, -3))
    }

    it("rejects a count exceeding 20000, carrying the 1-based index and offending value") {
      CouplePopulation.from(20001, 0, 0, 0, 0, 0) shouldBe
        Left(CouplePopulationError.ExceedsMaxCount(1, 20001))
    }

    it("short-circuits on the first invalid count when multiple are invalid") {
      CouplePopulation.from(-1, 20001, 0, 0, 0, 0) shouldBe
        Left(CouplePopulationError.NegativeCount(1, -1))
    }
  }

  describe("CouplePopulation construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.genetics.CouplePopulation(0, 0, 0, 0, 0, 0)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.genetics.CouplePopulation.from(0, 0, 0, 0, 0, 0).toOption.get.copy(homDomHomDom = 99)"""
      )
    }
  }
}
