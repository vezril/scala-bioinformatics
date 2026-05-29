package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MultipleAlignmentProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def fourStrings(s0: String, s1: String, s2: String, s3: String): Vector[DnaString] =
    Vector(dna(s0), dna(s1), dna(s2), dna(s3))

  describe("MultipleAlignmentProblem.from") {
    it("accepts the canonical Rosalind MULT sample") {
      val result = MultipleAlignmentProblem.from(
        fourStrings("ATATCCG", "TCCG", "ATGTACTG", "ATGTCTG")
      )
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.strings.map(_.value) shouldBe Vector("ATATCCG", "TCCG", "ATGTACTG", "ATGTCTG")
    }

    it("accepts four empty strings") {
      val result = MultipleAlignmentProblem.from(fourStrings("", "", "", ""))
      result.isRight shouldBe true
      result.toOption.get.strings.map(_.value) shouldBe Vector("", "", "", "")
    }

    it("accepts four strings at the 10 bp upper bound") {
      val ten = "A" * 10
      val result = MultipleAlignmentProblem.from(fourStrings(ten, ten, ten, ten))
      result.isRight shouldBe true
    }

    it("rejects three strings as WrongNumberOfStrings(3, 4)") {
      val three = Vector(dna("ATGC"), dna("ATGC"), dna("ATGC"))
      MultipleAlignmentProblem.from(three) shouldBe
        Left(MultipleAlignmentProblemError.WrongNumberOfStrings(3, 4))
    }

    it("rejects five strings as WrongNumberOfStrings(5, 4)") {
      val five = Vector(dna("A"), dna("A"), dna("A"), dna("A"), dna("A"))
      MultipleAlignmentProblem.from(five) shouldBe
        Left(MultipleAlignmentProblemError.WrongNumberOfStrings(5, 4))
    }

    it("rejects an 11-character string at index 1 as StringTooLong(1, 11, 10)") {
      val eleven = "A" * 11
      MultipleAlignmentProblem.from(fourStrings("A", eleven, "A", "A")) shouldBe
        Left(MultipleAlignmentProblemError.StringTooLong(1, 11, 10))
    }

    it("reports the first offending index when multiple strings exceed the cap") {
      val eleven = "A" * 11
      MultipleAlignmentProblem.from(fourStrings("A", eleven, "A", eleven)) shouldBe
        Left(MultipleAlignmentProblemError.StringTooLong(1, 11, 10))
    }
  }

  describe("MultipleAlignmentProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.MultipleAlignmentProblem(fourStrings("A", "A", "A", "A"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.MultipleAlignmentProblem
          |  .from(fourStrings("A", "A", "A", "A")).toOption.get
          |  .copy(strings = fourStrings("C", "C", "C", "C"))""".stripMargin
      )
    }
  }
}
