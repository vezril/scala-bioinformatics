package bio.algorithms.analysis

import bio.domain.analysis.FailureArrayProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FailureArraySpec extends AnyFunSpec with Matchers {

  private def fixture(s: String): FailureArrayProblem = {
    val dna = DnaString
      .from(s)
      .getOrElse(sys.error(s"invalid DnaString fixture: $s"))
    FailureArrayProblem
      .from(dna)
      .getOrElse(sys.error(s"invalid FailureArrayProblem fixture: $s"))
  }

  describe("FailureArray.compute") {
    it("returns the canonical Rosalind KMP sample failure array for `CAGCATGGTATCACAGCAGAG`") {
      FailureArray.compute(fixture("CAGCATGGTATCACAGCAGAG")) shouldBe
        Vector(0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 3, 4, 5, 3, 0, 0)
    }

    it("returns Vector(0) for a single-character input `A`") {
      FailureArray.compute(fixture("A")) shouldBe Vector(0)
    }

    it("returns a strictly increasing array for the all-same input `AAAAA`") {
      FailureArray.compute(fixture("AAAAA")) shouldBe Vector(0, 1, 2, 3, 4)
    }

    it("returns an all-zero array for the no-self-overlap input `ACGT`") {
      FailureArray.compute(fixture("ACGT")) shouldBe Vector(0, 0, 0, 0)
    }

    it("returns a rising staircase for the periodic input `ACACACAC`") {
      FailureArray.compute(fixture("ACACACAC")) shouldBe Vector(0, 0, 1, 2, 3, 4, 5, 6)
    }

    it("returns Vector(0, 1) for the two-character matching pair `AA`") {
      FailureArray.compute(fixture("AA")) shouldBe Vector(0, 1)
    }

    it("returns Vector(0, 0) for the two-character non-matching pair `AT`") {
      FailureArray.compute(fixture("AT")) shouldBe Vector(0, 0)
    }
  }
}
