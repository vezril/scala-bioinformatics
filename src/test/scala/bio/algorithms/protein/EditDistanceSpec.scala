package bio.algorithms.protein

import bio.domain.protein.{EditDistanceProblem, ProteinString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EditDistanceSpec extends AnyFunSpec with Matchers {

  private def fixture(left: String, right: String): EditDistanceProblem = {
    val l = ProteinString.from(left).getOrElse(sys.error(s"invalid left: $left"))
    val r = ProteinString.from(right).getOrElse(sys.error(s"invalid right: $right"))
    EditDistanceProblem
      .from(l, r)
      .getOrElse(sys.error(s"invalid EditDistanceProblem fixture: ($left, $right)"))
  }

  describe("EditDistance.compute") {
    it("returns 5 for the canonical Rosalind EDIT sample PLEASANTLY / MEANLY") {
      EditDistance.compute(fixture("PLEASANTLY", "MEANLY")) shouldBe 5
    }

    it("returns 0 for identical inputs") {
      EditDistance.compute(fixture("MEANLY", "MEANLY")) shouldBe 0
    }

    it("returns the length of right when left is empty") {
      EditDistance.compute(fixture("", "MEANLY")) shouldBe 6
    }

    it("returns the length of left when right is empty") {
      EditDistance.compute(fixture("PLEASANTLY", "")) shouldBe 10
    }

    it("returns 0 when both strings are empty") {
      EditDistance.compute(fixture("", "")) shouldBe 0
    }

    it("returns 1 for a single substitution (A → M)") {
      EditDistance.compute(fixture("A", "M")) shouldBe 1
    }

    it("returns 1 for a single insertion (MEANLY → MEANLLY)") {
      EditDistance.compute(fixture("MEANLY", "MEANLLY")) shouldBe 1
    }

    it("returns 1 for a single deletion (MEANLY → MEANL)") {
      EditDistance.compute(fixture("MEANLY", "MEANL")) shouldBe 1
    }

    it("returns 3 for completely disjoint same-length inputs AAA / MMM") {
      EditDistance.compute(fixture("AAA", "MMM")) shouldBe 3
    }

    it("is symmetric in its arguments") {
      val d1 = EditDistance.compute(fixture("PLEASANTLY", "MEANLY"))
      val d2 = EditDistance.compute(fixture("MEANLY", "PLEASANTLY"))
      d1 shouldBe d2
    }
  }
}
