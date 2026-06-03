package bio.algorithms.analysis

import bio.domain.analysis.ReadCorrectionProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReadErrorCorrectionSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(fail(s"invalid DNA string: $s"))

  private def correctionSet(reads: String*): Set[String] = {
    val problem = ReadCorrectionProblem
      .from(reads.toVector.map(dna))
      .getOrElse(fail("invalid problem"))
    ReadErrorCorrection.correct(problem).corrections.map(_.format).toSet
  }

  describe("ReadErrorCorrection.correct") {
    it("matches the canonical Rosalind sample") {
      correctionSet(
        "TCATC", "TTCAT", "TCATC", "TGAAA", "GAGGA", "TTTCA", "ATCAA", "TTGAT", "TTTCC"
      ) shouldBe Set("TTCAT->TTGAT", "GAGGA->GATGA", "TTTCC->TTTCA")
    }

    it("returns no corrections when a read is duplicated") {
      correctionSet("ACGTA", "ACGTA") shouldBe Set.empty
    }

    it("treats a read and its reverse complement as correct") {
      correctionSet("AAA", "TTT") shouldBe Set.empty
    }

    it("returns no corrections for an empty dataset") {
      correctionSet() shouldBe Set.empty
    }
  }
}
