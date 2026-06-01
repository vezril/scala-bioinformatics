package bio.domain.graph

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PerfectCoverageProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).toOption.get

  private val sampleReads: Vector[DnaString] =
    Vector("ATTAC", "TACAG", "GATTA", "ACAGA", "CAGAT", "TTACA", "AGATT").map(dna)

  describe("PerfectCoverageProblem.from") {
    it("accepts the canonical sample of seven length-5 reads, preserving order") {
      val result = PerfectCoverageProblem.from(sampleReads)
      result.map(_.kmers) shouldBe Right(sampleReads)
    }

    it("rejects an empty collection") {
      PerfectCoverageProblem.from(Vector.empty) shouldBe
        Left(PerfectCoverageProblemError.EmptyKmerCollection)
    }

    it("rejects a k-mer too short to split, reporting the first such index") {
      val reads = Vector(dna("AC"), dna("A"), dna("GT"))
      PerfectCoverageProblem.from(reads) shouldBe
        Left(PerfectCoverageProblemError.KmerTooShort(1, 1, 2))
    }

    it("rejects a k-mer longer than the maximum, reporting the first such index") {
      val reads = Vector(dna("A" * 50), dna("A" * 51))
      PerfectCoverageProblem.from(reads) shouldBe
        Left(PerfectCoverageProblemError.KmerTooLong(1, 51, 50))
    }

    it("rejects k-mers of unequal length, reporting the first mismatch") {
      val reads = Vector(dna("ACGTA"), dna("ACGTA"), dna("ACGTAC"))
      PerfectCoverageProblem.from(reads) shouldBe
        Left(PerfectCoverageProblemError.InconsistentLength(2, 6, 5))
    }

    it("reports the earliest failure when an input is invalid in multiple ways") {
      // empty wins over everything
      PerfectCoverageProblem.from(Vector.empty) shouldBe
        Left(PerfectCoverageProblemError.EmptyKmerCollection)

      // too-short wins over too-long (per-k-mer, index order)
      val shortBeforeLong = Vector(dna("A"), dna("A" * 51))
      PerfectCoverageProblem.from(shortBeforeLong) shouldBe
        Left(PerfectCoverageProblemError.KmerTooShort(0, 1, 2))

      // too-long wins over inconsistent-length
      val longBeforeInconsistent = Vector(dna("A" * 50), dna("A" * 51), dna("AC"))
      PerfectCoverageProblem.from(longBeforeInconsistent) shouldBe
        Left(PerfectCoverageProblemError.KmerTooLong(1, 51, 50))
    }

    it("does not expose a public apply that bypasses validation") {
      assertDoesNotCompile(
        """bio.domain.graph.PerfectCoverageProblem(Vector.empty[bio.domain.nucleic.DnaString])"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """PerfectCoverageProblem.from(sampleReads).toOption.get.copy(kmers = Vector.empty)"""
      )
    }
  }
}
