package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectrumMatchProblemSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  private val proteins =
    Vector("GSDMQS", "VWICN", "IASWMQS", "PVSMGAD").map(protein)
  private val spectrum =
    Vector(445.17838, 115.02694, 186.07931, 314.13789, 317.1198, 215.09061)

  describe("SpectrumMatchProblem.from") {
    it("accepts a valid set of candidates and spectrum, preserving them") {
      val result = SpectrumMatchProblem.from(proteins, spectrum)
      result.map(_.proteins) shouldBe Right(proteins)
      result.map(_.spectrum) shouldBe Right(spectrum)
    }

    it("rejects an empty candidate list") {
      SpectrumMatchProblem.from(Vector.empty, spectrum) shouldBe Left(
        SpectrumMatchProblemError.EmptyProteinList
      )
    }

    it("rejects an empty spectrum") {
      SpectrumMatchProblem.from(proteins, Vector.empty) shouldBe Left(
        SpectrumMatchProblemError.EmptySpectrum
      )
    }

    it("rejects a non-positive spectrum value") {
      SpectrumMatchProblem.from(proteins, Vector(1.0, -2.0)) shouldBe Left(
        SpectrumMatchProblemError.NonPositiveMass(1, -2.0)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.SpectrumMatchProblem(Vector.empty[bio.domain.protein.ProteinString], Vector(1.0))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.SpectrumMatchProblem.from(Vector(bio.domain.protein.ProteinString.from("A").toOption.get), Vector(1.0)).toOption.get.copy()"""
      )
    }
  }
}
