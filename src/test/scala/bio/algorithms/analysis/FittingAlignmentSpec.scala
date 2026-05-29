package bio.algorithms.analysis

import bio.domain.analysis.{
  FittingAlignment => Alignment,
  FittingAlignmentProblem
}
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FittingAlignmentSpec extends AnyFunSpec with Matchers {

  private def fixture(text: String, motif: String): FittingAlignmentProblem = {
    val s = DnaString.from(text).getOrElse(sys.error(s"invalid text: $text"))
    val t = DnaString.from(motif).getOrElse(sys.error(s"invalid motif: $motif"))
    FittingAlignmentProblem
      .from(s, t)
      .getOrElse(sys.error(s"invalid FittingAlignmentProblem fixture: ($text, $motif)"))
  }

  private val SampleText =
    "GCAAACCATAAGCCCTACGTGCCGCCTGTTTAAACTCGCGAACTGAATCTTCTGCTTCACGGTGAAAGTACCACAATGGTATCACACCCCAAGGAAAC"
  private val SampleMotif = "GCCGTCAGGCTGGTGTCCG"

  /** Mismatch score of an aligned pair: +1 per equal non-gap column, -1 per
    * mismatched-or-gap column.
    */
  private def mismatchScore(a: String, b: String): Int = {
    require(a.length == b.length)
    a.zip(b).foldLeft(0) { case (acc, (x, y)) =>
      if (x != '-' && y != '-' && x == y) acc + 1 else acc - 1
    }
  }

  describe("FittingAlignment (output ADT)") {
    it("constructs with named fields and is value-equal to an identical instance") {
      val a = Alignment(
        score = 5,
        augmentedText = "ACCATAAGCCCTACGTG-CCG",
        augmentedMotif = "GCCGTCAGGC-TG-GTGTCCG"
      )
      a.score shouldBe 5
      a.augmentedText shouldBe "ACCATAAGCCCTACGTG-CCG"
      a.augmentedMotif shouldBe "GCCGTCAGGC-TG-GTGTCCG"
      a shouldBe Alignment(5, "ACCATAAGCCCTACGTG-CCG", "GCCGTCAGGC-TG-GTGTCCG")
    }
  }

  describe("FittingAlignment.align") {
    it("returns score 5 with a valid augmented alignment for the canonical SIMS sample") {
      val result = FittingAlignment.align(fixture(SampleText, SampleMotif))
      result.score shouldBe 5
      result.augmentedText.length shouldBe result.augmentedMotif.length
      // No column has gaps in both rows.
      result.augmentedText
        .zip(result.augmentedMotif)
        .exists { case (a, b) => a == '-' && b == '-' } shouldBe false
      // The motif is fully consumed.
      result.augmentedMotif.replace("-", "") shouldBe SampleMotif
      // The recovered text is a contiguous substring of the input text.
      SampleText.contains(result.augmentedText.replace("-", "")) shouldBe true
      // The mismatch score of the aligned pair equals the reported score.
      mismatchScore(result.augmentedText, result.augmentedMotif) shouldBe 5
    }

    it("scores one point per motif symbol for a clean motif occurrence") {
      FittingAlignment.align(fixture("TTGATTACATT", "GATTACA")) shouldBe
        Alignment(7, "GATTACA", "GATTACA")
    }

    it("scores the full length with no gaps for identical text and motif") {
      FittingAlignment.align(fixture("ACGT", "ACGT")) shouldBe
        Alignment(4, "ACGT", "ACGT")
    }

    it("returns FittingAlignment(0, \"\", \"\") for an empty motif") {
      FittingAlignment.align(fixture("GATTACA", "")) shouldBe Alignment(0, "", "")
    }

    it("charges one gap per motif symbol for an empty text") {
      FittingAlignment.align(fixture("", "ACG")) shouldBe Alignment(-3, "---", "ACG")
    }

    it("always fully consumes the motif") {
      val result = FittingAlignment.align(fixture(SampleText, SampleMotif))
      result.augmentedMotif.replace("-", "") shouldBe SampleMotif
    }

    it("recovers a contiguous substring of the input text") {
      val result = FittingAlignment.align(fixture(SampleText, SampleMotif))
      SampleText.contains(result.augmentedText.replace("-", "")) shouldBe true
    }
  }
}
