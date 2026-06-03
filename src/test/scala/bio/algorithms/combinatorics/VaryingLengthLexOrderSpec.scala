package bio.algorithms.combinatorics

import bio.domain.combinatorics.LexOrderProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class VaryingLengthLexOrderSpec extends AnyFunSpec with Matchers {

  private def enumerate(alphabet: Vector[Char], n: Int): Vector[String] = {
    val problem = LexOrderProblem.from(alphabet, n).getOrElse(fail("invalid problem"))
    VaryingLengthLexOrder.enumerate(problem).strings
  }

  describe("VaryingLengthLexOrder.enumerate") {
    it("matches the canonical Rosalind sample") {
      enumerate(Vector('D', 'N', 'A'), 3) shouldBe Vector(
        "D", "DD", "DDD", "DDN", "DDA", "DN", "DND", "DNN", "DNA", "DA", "DAD", "DAN", "DAA",
        "N", "ND", "NDD", "NDN", "NDA", "NN", "NND", "NNN", "NNA", "NA", "NAD", "NAN", "NAA",
        "A", "AD", "ADD", "ADN", "ADA", "AN", "AND", "ANN", "ANA", "AA", "AAD", "AAN", "AAA"
      )
    }

    it("yields exactly the alphabet in order for maxLength 1") {
      enumerate(Vector('D', 'N', 'A'), 1) shouldBe Vector("D", "N", "A")
    }

    it("yields a chain for a single-symbol alphabet") {
      enumerate(Vector('X'), 3) shouldBe Vector("X", "XX", "XXX")
    }
  }
}
