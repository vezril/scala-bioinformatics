package bio.algorithms.analysis

import bio.domain.analysis.{MultipleAlignment => Alignment, MultipleAlignmentProblem}
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MultipleAlignmentSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def fixture(s0: String, s1: String, s2: String, s3: String): MultipleAlignmentProblem =
    MultipleAlignmentProblem
      .from(Vector(dna(s0), dna(s1), dna(s2), dna(s3)))
      .getOrElse(sys.error(s"invalid MultipleAlignmentProblem fixture: ($s0, $s1, $s2, $s3)"))

  private def stripGaps(s: String): String = s.filterNot(_ == '-')

  /** Sum of `-1` per position where rows `j` and `k` differ, over every
    * unordered pair `(j, k)` with `j < k`. Equivalent to the MULT scoring
    * scheme (matched chars including gap-vs-gap score `0`).
    */
  private def pairwiseScore(aug: Vector[String]): Int = {
    val rows = aug.size
    val len  = aug.head.length
    var s    = 0
    var j    = 0
    while (j < rows) {
      var k = j + 1
      while (k < rows) {
        var p = 0
        while (p < len) {
          if (aug(j).charAt(p) != aug(k).charAt(p)) s -= 1
          p += 1
        }
        k += 1
      }
      j += 1
    }
    s
  }

  /** Assert the five MULT invariants relative to the producing problem. */
  private def assertValidAlignment(
      alignment: Alignment,
      problem: MultipleAlignmentProblem
  ): Unit = {
    alignment.augmentedStrings.size shouldBe 4

    val lengths = alignment.augmentedStrings.map(_.length).distinct
    lengths.size shouldBe 1

    val len = lengths.head
    var p   = 0
    while (p < len) {
      val col = alignment.augmentedStrings.map(_.charAt(p))
      col.forall(_ == '-') shouldBe false
      p += 1
    }

    (0 until 4).foreach { k =>
      stripGaps(alignment.augmentedStrings(k)) shouldBe problem.strings(k).value
    }

    pairwiseScore(alignment.augmentedStrings) shouldBe alignment.score
  }

  describe("MultipleAlignment.align") {
    it("returns score -18 with a valid alignment for the canonical Rosalind MULT sample") {
      val problem   = fixture("ATATCCG", "TCCG", "ATGTACTG", "ATGTCTG")
      val alignment = MultipleAlignment.align(problem)
      alignment.score shouldBe -18
      assertValidAlignment(alignment, problem)
    }

    it("returns the degenerate empty alignment for four empty strings") {
      val problem = fixture("", "", "", "")
      MultipleAlignment.align(problem) shouldBe Alignment(0, Vector("", "", "", ""))
    }

    it("returns score 0 with identity rows for four identical strings (ACGT)") {
      MultipleAlignment.align(fixture("ACGT", "ACGT", "ACGT", "ACGT")) shouldBe
        Alignment(0, Vector("ACGT", "ACGT", "ACGT", "ACGT"))
    }

    it("returns score -12 with ACGT-vs-three-empties (4 columns × 3 mismatched pairs)") {
      val problem   = fixture("ACGT", "", "", "")
      val alignment = MultipleAlignment.align(problem)
      alignment.score shouldBe -12
      alignment.augmentedStrings shouldBe Vector("ACGT", "----", "----", "----")
    }

    it("satisfies the five invariants for a mixed-length input (AC, A, AC, A)") {
      val problem   = fixture("AC", "A", "AC", "A")
      val alignment = MultipleAlignment.align(problem)
      assertValidAlignment(alignment, problem)
      // Optimal: each row aligns its character A on column 0 (all match), and
      // s0/s2 emit C on column 1 while s1/s3 emit gap. Score: column 0 is all
      // match (0), column 1 has the (s0,s1), (s0,s3), (s1,s2), (s2,s3)
      // pairs mismatched (gap vs C) = 4 × -1 = -4.
      alignment.score shouldBe -4
    }
  }
}
