package bio.algorithms.protein

import bio.domain.nucleic.DnaString
import bio.domain.protein.OpenReadingFrameProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OpenReadingFramesAlgoSpec extends AnyFunSpec with Matchers {

  private def problem(s: String): OpenReadingFrameProblem =
    OpenReadingFrameProblem
      .from(DnaString.from(s).getOrElse(sys.error(s"invalid DnaString: $s")))
      .getOrElse(sys.error(s"invalid OpenReadingFrameProblem fixture: $s"))

  private def candidates(s: String): Vector[String] =
    OpenReadingFrames.find(problem(s)).proteins.map(_.value)

  describe("OpenReadingFrames.find") {
    it("finds all distinct candidate proteins in the canonical Rosalind ORF sample") {
      candidates(
        "AGCCATGTAGCTAACTCAGGTTACATGGGGATGACCCCGCGACTTGGATTAGAGTCTCTTTTGGAATAAGCCTGAATGATCCGAGTAGCATCTCAG"
      ) should contain theSameElementsAs Vector(
        "MLLGSFRLIPKETLIQVAGSSPCNLS",
        "M",
        "MGMTPRLGLESLLE",
        "MTPRLGLESLLE"
      )
    }

    it("yields no candidate for a start codon with no downstream in-frame stop") {
      candidates("ATGAAA") shouldBe empty
    }

    it("returns no candidates when no start codon exists on either strand") {
      candidates("CCCCCC") shouldBe empty
    }

    it("collapses identical proteins from separate reading frames to one") {
      candidates("ATGTAAATGTAA") shouldBe Vector("M")
    }

    it("yields both the outer and inner protein for nested open reading frames") {
      val sample = candidates(
        "AGCCATGTAGCTAACTCAGGTTACATGGGGATGACCCCGCGACTTGGATTAGAGTCTCTTTTGGAATAAGCCTGAATGATCCGAGTAGCATCTCAG"
      )
      sample should contain("MGMTPRLGLESLLE")
      sample should contain("MTPRLGLESLLE")
    }
  }
}
