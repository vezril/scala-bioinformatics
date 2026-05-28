package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GeneticCharacterTableProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private val canonicalSample: Vector[DnaString] = Vector(
    dna("ATGCTACC"),
    dna("CGTTTACC"),
    dna("ATTCGACC"),
    dna("AGTCTCCC"),
    dna("CGTCTATC")
  )

  describe("GeneticCharacterTableProblem.from") {
    it("accepts the canonical 5×8 Rosalind sample") {
      val result = GeneticCharacterTableProblem.from(canonicalSample)
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.size shouldBe 5
      problem.length shouldBe 8
      problem.sequences shouldBe canonicalSample
    }

    it("accepts size-100, length-300 at the upper boundaries") {
      // 100 strings of length 300, each made entirely of 'A' — characterizable
      // (one symbol per column).
      val bigOnes  = Vector.fill(100)(dna("A" * 300))
      val result   = GeneticCharacterTableProblem.from(bigOnes)
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.size shouldBe 100
      problem.length shouldBe 300
    }

    it("rejects an empty input as EmptyInput") {
      GeneticCharacterTableProblem.from(Vector.empty) shouldBe
        Left(GeneticCharacterTableProblemError.EmptyInput)
    }

    it("rejects 101 strings as TooManyStrings(101, 100)") {
      val tooMany = Vector.fill(101)(dna("A"))
      GeneticCharacterTableProblem.from(tooMany) shouldBe
        Left(GeneticCharacterTableProblemError.TooManyStrings(101, 100))
    }

    it("rejects a 301-character string as StringTooLong(0, 301, 300)") {
      val tooLong = Vector(dna("A" * 301))
      GeneticCharacterTableProblem.from(tooLong) shouldBe
        Left(GeneticCharacterTableProblemError.StringTooLong(0, 301, 300))
    }

    it("rejects mismatched row lengths as InconsistentLength(1, 8, 7)") {
      val mismatched = Vector(dna("ATGCTACC"), dna("CGTTTAC"))
      GeneticCharacterTableProblem.from(mismatched) shouldBe
        Left(GeneticCharacterTableProblemError.InconsistentLength(1, 8, 7))
    }

    it("rejects a non-characterizable 3-symbol column as NonCharacterizable(0, 3)") {
      // Column 0 contains A, C, G — three distinct symbols → not characterizable.
      val nonChar = Vector(dna("AAA"), dna("CAA"), dna("GAA"))
      GeneticCharacterTableProblem.from(nonChar) shouldBe
        Left(GeneticCharacterTableProblemError.NonCharacterizable(0, 3))
    }
  }

  describe("GeneticCharacterTableProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.GeneticCharacterTableProblem(Vector.empty, 0, 0)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.GeneticCharacterTableProblem
          |  .from(canonicalSample).toOption.get.copy(size = 0)""".stripMargin
      )
    }
  }
}
