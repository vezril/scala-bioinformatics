package bio.algorithms.protein

import bio.domain.protein.FullSpectrumProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InferPeptideSpec extends AnyFunSpec with Matchers {

  private def infer(masses: Vector[Double]): String =
    InferPeptide
      .infer(FullSpectrumProblem.from(masses).getOrElse(sys.error("invalid fixture")))
      .peptide

  describe("InferPeptide.infer") {
    it("reconstructs the canonical Rosalind FULL sample") {
      val canonical = Vector(
        1988.21104821, 610.391039105, 738.485999105, 766.492149105, 863.544909105,
        867.528589105, 992.587499105, 995.623549105, 1120.6824591, 1124.6661391,
        1221.7188991, 1249.7250491, 1377.8200091
      )
      infer(canonical) shouldBe "KEKEP"
    }

    it("reconstructs a single-residue peptide") {
      // parent + b0, b1, y0, y1 for peptide "A" with offsets w1 = w2 = 1.0
      infer(Vector(90.0, 1.0, 72.03711, 1.0, 72.03711)) shouldBe "A"
    }
  }
}
