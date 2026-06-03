package bio.algorithms.protein

import bio.domain.protein.SpectrumGraphProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectrumGraphSpec extends AnyFunSpec with Matchers {

  private def longest(masses: Vector[Double]): String =
    SpectrumGraph
      .longestPeptide(SpectrumGraphProblem.from(masses).getOrElse(sys.error("invalid fixture")))
      .peptide

  describe("SpectrumGraph.longestPeptide") {
    it("computes the canonical Rosalind SGRA sample") {
      val canonical = Vector(
        3524.8542, 3623.5245, 3710.9335, 3841.974, 3929.00603,
        3970.0326, 4026.05879, 4057.0646, 4083.08025
      )
      longest(canonical) shouldBe "WMSPG"
    }

    it("infers a single residue from one edge") {
      longest(Vector(10.0, 81.03711)) shouldBe "A"
    }

    it("returns the empty protein when no gap is a residue mass") {
      longest(Vector(10.0, 20.0)) shouldBe ""
    }
  }
}
