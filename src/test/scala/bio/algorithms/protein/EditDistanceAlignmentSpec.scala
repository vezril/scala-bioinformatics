package bio.algorithms.protein

import bio.domain.protein.{EditAlignment, EditDistanceAlignmentProblem, ProteinString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EditDistanceAlignmentSpec extends AnyFunSpec with Matchers {

  private def fixture(left: String, right: String): EditDistanceAlignmentProblem = {
    val l = ProteinString.from(left).getOrElse(sys.error(s"invalid left: $left"))
    val r = ProteinString.from(right).getOrElse(sys.error(s"invalid right: $right"))
    EditDistanceAlignmentProblem
      .from(l, r)
      .getOrElse(sys.error(s"invalid EditDistanceAlignmentProblem fixture: ($left, $right)"))
  }

  /** Remove all `-` gap symbols from `s`, recovering the original (ungapped)
    * sequence.
    */
  private def stripGaps(s: String): String = s.filterNot(_ == '-')

  /** Position-wise Hamming distance between two strings (assumed equal
    * length).
    */
  private def hamming(a: String, b: String): Int = {
    require(a.length == b.length, s"Hamming requires equal lengths: ${a.length} vs ${b.length}")
    var i      = 0
    var differ = 0
    while (i < a.length) {
      if (a.charAt(i) != b.charAt(i)) differ += 1
      i += 1
    }
    differ
  }

  /** Assert all four `EditAlignment` invariants relative to the producing
    * problem.
    */
  private def assertValidAlignment(
      alignment: EditAlignment,
      problem: EditDistanceAlignmentProblem
  ): Unit = {
    alignment.augmentedLeft.length shouldBe alignment.augmentedRight.length

    val doubleGap = alignment.augmentedLeft.zip(alignment.augmentedRight).exists {
      case ('-', '-') => true
      case _          => false
    }
    doubleGap shouldBe false

    stripGaps(alignment.augmentedLeft) shouldBe problem.left.value
    stripGaps(alignment.augmentedRight) shouldBe problem.right.value

    hamming(alignment.augmentedLeft, alignment.augmentedRight) shouldBe alignment.distance
  }

  describe("EditDistanceAlignment.align") {
    it("returns the canonical Rosalind EDTA alignment for PRETTY / PRTTEIN") {
      EditDistanceAlignment.align(fixture("PRETTY", "PRTTEIN")) shouldBe
        EditAlignment(4, "PRETTY--", "PR-TTEIN")
    }

    it("returns a gap-free identity alignment for identical inputs") {
      EditDistanceAlignment.align(fixture("MEANLY", "MEANLY")) shouldBe
        EditAlignment(0, "MEANLY", "MEANLY")
    }

    it("aligns an empty left as all gaps over the right") {
      EditDistanceAlignment.align(fixture("", "MEANLY")) shouldBe
        EditAlignment(6, "------", "MEANLY")
    }

    it("aligns an empty right as the left over all gaps") {
      EditDistanceAlignment.align(fixture("PLEASANTLY", "")) shouldBe
        EditAlignment(10, "PLEASANTLY", "----------")
    }

    it("returns the degenerate empty alignment for two empty strings") {
      EditDistanceAlignment.align(fixture("", "")) shouldBe
        EditAlignment(0, "", "")
    }

    it("prefers the diagonal move for a single substitution (A vs M)") {
      EditDistanceAlignment.align(fixture("A", "M")) shouldBe
        EditAlignment(1, "A", "M")
    }

    it("emits one gap on the left for a single insertion (MEANLY vs MEANLLY)") {
      val problem   = fixture("MEANLY", "MEANLLY")
      val alignment = EditDistanceAlignment.align(problem)
      alignment.distance shouldBe 1
      alignment.augmentedLeft.length shouldBe 7
      alignment.augmentedRight shouldBe "MEANLLY"
      stripGaps(alignment.augmentedLeft) shouldBe "MEANLY"
      assertValidAlignment(alignment, problem)
    }

    it("emits one gap on the right for a single deletion (MEANLY vs MEANL)") {
      val problem   = fixture("MEANLY", "MEANL")
      val alignment = EditDistanceAlignment.align(problem)
      alignment.distance shouldBe 1
      alignment.augmentedRight.length shouldBe 6
      alignment.augmentedLeft shouldBe "MEANLY"
      stripGaps(alignment.augmentedRight) shouldBe "MEANL"
      assertValidAlignment(alignment, problem)
    }

    it("satisfies the four alignment invariants for PLEASANTLY / MEANLY") {
      val problem   = fixture("PLEASANTLY", "MEANLY")
      val alignment = EditDistanceAlignment.align(problem)
      assertValidAlignment(alignment, problem)
      // PLEASANTLY → MEANLY has edit distance 5 (verified in EditDistanceSpec).
      alignment.distance shouldBe 5
    }
  }
}
