package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReadCorrectionProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(fail(s"invalid DNA string: $s"))

  describe("ReadCorrectionProblem.from") {
    it("accepts equal-length reads within the bounds") {
      val reads = Vector("TCATC", "TTCAT", "TCATC").map(dna)
      ReadCorrectionProblem.from(reads).map(_.reads) shouldBe Right(reads)
    }

    it("accepts an empty read list") {
      ReadCorrectionProblem.from(Vector.empty).map(_.reads) shouldBe Right(Vector.empty[DnaString])
    }

    it("rejects more than 1000 reads") {
      val reads = Vector.fill(1001)(dna("ACGT"))
      ReadCorrectionProblem.from(reads) shouldBe Left(
        ReadCorrectionProblemError.TooManyReads(1001, 1000)
      )
    }

    it("rejects a read longer than the bound") {
      val reads = Vector(dna("A" * 51))
      ReadCorrectionProblem.from(reads) shouldBe Left(
        ReadCorrectionProblemError.ReadTooLong(51, 50)
      )
    }

    it("rejects reads of unequal length") {
      val reads = Vector(dna("ACGTA"), dna("ACGTAC"))
      ReadCorrectionProblem.from(reads) shouldBe Left(
        ReadCorrectionProblemError.UnequalLengths(Vector(5, 6))
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.analysis.ReadCorrectionProblem(Vector.empty)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """ReadCorrectionProblem.from(Vector.empty).toOption.get.copy(reads = Vector.empty)"""
      )
    }
  }
}
