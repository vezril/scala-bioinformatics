package bio.algorithms.protein

import bio.domain.protein.{ProteinString, SpectrumMatch, SpectrumMatchProblem}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MatchSpectrumSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString: $s"))

  private def best(proteins: Seq[String], spectrum: Vector[Double]): SpectrumMatch =
    MatchSpectrum.bestMatch(
      SpectrumMatchProblem
        .from(proteins.iterator.map(protein).toVector, spectrum)
        .getOrElse(sys.error("invalid SpectrumMatchProblem fixture"))
    )

  describe("MatchSpectrum.bestMatch") {
    it("computes the canonical Rosalind PRSM sample") {
      val result = best(
        Seq("GSDMQS", "VWICN", "IASWMQS", "PVSMGAD"),
        Vector(445.17838, 115.02694, 186.07931, 314.13789, 317.1198, 215.09061)
      )
      // GSDMQS and IASWMQS both achieve the maximum multiplicity of 3; Rosalind
      // accepts any maximiser, and the algorithm returns the first one (GSDMQS).
      result.multiplicity shouldBe 3
      Set("GSDMQS", "IASWMQS") should contain(result.protein)
    }

    it("matches a single candidate against its own residue weight") {
      best(Seq("A"), Vector(71.03711)) shouldBe SpectrumMatch(2, "A")
    }

    it("selects the candidate with the greater multiplicity") {
      best(Seq("A", "AA"), Vector(71.03711, 142.07422)) shouldBe SpectrumMatch(4, "AA")
    }
  }
}
