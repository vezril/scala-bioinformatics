package bio.algorithms.analysis

import bio.domain.analysis.{
  IsolatedSymbols => Result,
  IsolatedSymbolsProblem
}
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class IsolatedSymbolsSpec extends AnyFunSpec with Matchers {

  private def fixture(left: String, right: String): IsolatedSymbolsProblem = {
    val l = DnaString.from(left).getOrElse(sys.error(s"invalid left: $left"))
    val r = DnaString.from(right).getOrElse(sys.error(s"invalid right: $right"))
    IsolatedSymbolsProblem
      .from(l, r)
      .getOrElse(sys.error(s"invalid IsolatedSymbolsProblem fixture: ($left, $right)"))
  }

  describe("IsolatedSymbols (output ADT)") {
    it("constructs with named fields (matrixSum is a Long) and is value-equal to an identical instance") {
      val a = Result(globalScore = 3, matrixSum = -139L)
      a.globalScore shouldBe 3
      a.matrixSum shouldBe -139L
      a shouldBe Result(3, -139L)
    }
  }

  describe("IsolatedSymbols.compute") {
    it("returns the canonical Rosalind OSYM sample output (3, -139)") {
      IsolatedSymbols.compute(fixture("ATAGATA", "ACAGGTA")) shouldBe Result(3, -139L)
    }

    it("returns (1, 1) for identical single characters") {
      IsolatedSymbols.compute(fixture("A", "A")) shouldBe Result(1, 1L)
    }

    it("returns (-1, -1) for mismatched single characters") {
      IsolatedSymbols.compute(fixture("A", "C")) shouldBe Result(-1, -1L)
    }

    it("scores one point per symbol for identical strings") {
      IsolatedSymbols.compute(fixture("GATTACA", "GATTACA")).globalScore shouldBe 7
    }

    it("returns (0, 0) for two empty strings") {
      IsolatedSymbols.compute(fixture("", "")) shouldBe Result(0, 0L)
    }

    it("returns (-7, 0) for an empty left with a 7-bp right") {
      IsolatedSymbols.compute(fixture("", "GATTACA")) shouldBe Result(-7, 0L)
    }

    it("returns (-7, 0) for a 7-bp left with an empty right") {
      IsolatedSymbols.compute(fixture("GATTACA", "")) shouldBe Result(-7, 0L)
    }

    it("is symmetric in its arguments for both globalScore and matrixSum") {
      val a = IsolatedSymbols.compute(fixture("ATAGATA", "ACAGGTA"))
      val b = IsolatedSymbols.compute(fixture("ACAGGTA", "ATAGATA"))
      a.globalScore shouldBe b.globalScore
      a.matrixSum shouldBe b.matrixSum
    }
  }
}
