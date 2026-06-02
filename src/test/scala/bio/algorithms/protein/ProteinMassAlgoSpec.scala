package bio.algorithms.protein

import bio.domain.protein.{ProteinMassProblem, ProteinString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProteinMassAlgoSpec extends AnyFunSpec with Matchers {

  private def problem(s: String): ProteinMassProblem =
    ProteinMassProblem
      .from(ProteinString.from(s).getOrElse(sys.error(s"invalid protein: $s")))
      .getOrElse(sys.error(s"invalid ProteinMassProblem fixture: $s"))

  describe("ProteinMass.calculate") {
    it("computes the canonical Rosalind PRTM sample mass for SKADYEK") {
      ProteinMass.calculate(problem("SKADYEK")).mass shouldBe (821.39192 +- 1e-3)
    }

    it("gives a single residue its own monoisotopic mass") {
      ProteinMass.calculate(problem("W")).mass shouldBe (186.07931 +- 1e-5)
    }

    it("gives the empty protein a mass of zero") {
      ProteinMass.calculate(problem("")).mass shouldBe 0.0
    }
  }
}
