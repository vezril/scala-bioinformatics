package bio.algorithms.analysis

import bio.domain.analysis.SplicedMotifProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SplicedMotifSpec extends AnyFunSpec with Matchers {

  private def fixture(source: String, target: String): SplicedMotifProblem = {
    val src = DnaString.from(source).getOrElse(sys.error(s"invalid source: $source"))
    val tgt = DnaString.from(target).getOrElse(sys.error(s"invalid target: $target"))
    SplicedMotifProblem
      .from(src, tgt)
      .getOrElse(sys.error(s"invalid SplicedMotifProblem fixture: ($source, $target)"))
  }

  describe("SplicedMotif.find") {
    it("returns Some(Vector(3, 4, 5)) for the canonical Rosalind SSEQ sample (greedy answer)") {
      SplicedMotif.find(fixture("ACGTACGTGACG", "GTA")) shouldBe Some(Vector(3, 4, 5))
    }

    it("returns Some(Vector.empty) for an empty target") {
      SplicedMotif.find(fixture("ACGT", "")) shouldBe Some(Vector.empty)
    }

    it("returns Some(Vector(1, 2, 3, 4)) when source equals target `ACGT`") {
      SplicedMotif.find(fixture("ACGT", "ACGT")) shouldBe Some(Vector(1, 2, 3, 4))
    }

    it("returns None when the target is not a subsequence of the source") {
      SplicedMotif.find(fixture("AAA", "AAAA")) shouldBe None
    }

    it("returns Some(Vector(4)) when the target is the last character of the source") {
      SplicedMotif.find(fixture("ACGT", "T")) shouldBe Some(Vector(4))
    }

    it("returns Some(Vector.empty) for empty source with empty target") {
      SplicedMotif.find(fixture("", "")) shouldBe Some(Vector.empty)
    }

    it("returns None for empty source with non-empty target") {
      SplicedMotif.find(fixture("", "A")) shouldBe None
    }

    it("returns Some(Vector(1, 2, 5, 6)) for repeated target chars across source `AACGAACG`") {
      SplicedMotif.find(fixture("AACGAACG", "AAAA")) shouldBe Some(Vector(1, 2, 5, 6))
    }
  }
}
