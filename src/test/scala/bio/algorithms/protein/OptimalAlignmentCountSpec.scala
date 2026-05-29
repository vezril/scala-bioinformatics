package bio.algorithms.protein

import bio.domain.protein.{OptimalAlignmentCountProblem, ProteinString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OptimalAlignmentCountSpec extends AnyFunSpec with Matchers {

  private def fixture(left: String, right: String): OptimalAlignmentCountProblem = {
    val l = ProteinString.from(left).getOrElse(sys.error(s"invalid left: $left"))
    val r = ProteinString.from(right).getOrElse(sys.error(s"invalid right: $right"))
    OptimalAlignmentCountProblem
      .from(l, r)
      .getOrElse(sys.error(s"invalid OptimalAlignmentCountProblem fixture: ($left, $right)"))
  }

  describe("OptimalAlignmentCount.compute") {
    it("returns 4 for the canonical Rosalind CTEA sample PLEASANTLY / MEANLY") {
      OptimalAlignmentCount.compute(fixture("PLEASANTLY", "MEANLY")) shouldBe 4
    }

    it("returns 1 for identical inputs") {
      OptimalAlignmentCount.compute(fixture("MEANLY", "MEANLY")) shouldBe 1
    }

    it("returns 1 when left is empty") {
      OptimalAlignmentCount.compute(fixture("", "MEANLY")) shouldBe 1
    }

    it("returns 1 when right is empty") {
      OptimalAlignmentCount.compute(fixture("PLEASANTLY", "")) shouldBe 1
    }

    it("returns 1 when both strings are empty") {
      OptimalAlignmentCount.compute(fixture("", "")) shouldBe 1
    }

    it("returns 1 for two distinct single characters (substitution is the unique optimum)") {
      OptimalAlignmentCount.compute(fixture("A", "M")) shouldBe 1
    }

    it("returns 2 for `A` vs `AA` (inserted A can go on either side)") {
      OptimalAlignmentCount.compute(fixture("A", "AA")) shouldBe 2
    }

    it("is symmetric in its arguments") {
      val c1 = OptimalAlignmentCount.compute(fixture("PLEASANTLY", "MEANLY"))
      val c2 = OptimalAlignmentCount.compute(fixture("MEANLY", "PLEASANTLY"))
      c1 shouldBe c2
    }
  }
}
