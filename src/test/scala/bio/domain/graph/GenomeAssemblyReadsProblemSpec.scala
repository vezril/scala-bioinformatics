package bio.domain.graph

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GenomeAssemblyReadsProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).toOption.get

  private val sampleReads: Vector[DnaString] =
    Vector("AATCT", "TGTAA", "GATTA", "ACAGA").map(dna)

  describe("GenomeAssemblyReadsProblem.from") {
    it("accepts the canonical sample of four length-5 reads, preserving order") {
      val result = GenomeAssemblyReadsProblem.from(sampleReads)
      result.map(_.reads) shouldBe Right(sampleReads)
    }

    it("rejects an empty collection") {
      GenomeAssemblyReadsProblem.from(Vector.empty) shouldBe
        Left(GenomeAssemblyReadsProblemError.EmptyReadCollection)
    }

    it("rejects a read too short to split, reporting the first such index") {
      val reads = Vector(dna("AC"), dna("A"), dna("GT"))
      GenomeAssemblyReadsProblem.from(reads) shouldBe
        Left(GenomeAssemblyReadsProblemError.ReadTooShort(1, 1, 2))
    }

    it("rejects a read longer than the maximum, reporting the first such index") {
      val reads = Vector(dna("A" * 50), dna("A" * 51))
      GenomeAssemblyReadsProblem.from(reads) shouldBe
        Left(GenomeAssemblyReadsProblemError.ReadTooLong(1, 51, 50))
    }

    it("rejects reads of unequal length, reporting the first mismatch") {
      val reads = Vector(dna("ACGTA"), dna("ACGTA"), dna("ACGT"))
      GenomeAssemblyReadsProblem.from(reads) shouldBe
        Left(GenomeAssemblyReadsProblemError.InconsistentLength(2, 4, 5))
    }

    it("reports the earliest failure when an input is invalid in multiple ways") {
      // empty wins over everything
      GenomeAssemblyReadsProblem.from(Vector.empty) shouldBe
        Left(GenomeAssemblyReadsProblemError.EmptyReadCollection)

      // too-short wins over too-long (per-read, index order)
      val shortBeforeLong = Vector(dna("A"), dna("A" * 51))
      GenomeAssemblyReadsProblem.from(shortBeforeLong) shouldBe
        Left(GenomeAssemblyReadsProblemError.ReadTooShort(0, 1, 2))

      // too-long wins over inconsistent-length
      val longBeforeInconsistent = Vector(dna("A" * 50), dna("A" * 51), dna("AC"))
      GenomeAssemblyReadsProblem.from(longBeforeInconsistent) shouldBe
        Left(GenomeAssemblyReadsProblemError.ReadTooLong(1, 51, 50))
    }

    it("does not expose a public apply that bypasses validation") {
      assertDoesNotCompile(
        """bio.domain.graph.GenomeAssemblyReadsProblem(Vector.empty[bio.domain.nucleic.DnaString])"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """GenomeAssemblyReadsProblem.from(sampleReads).toOption.get.copy(reads = Vector.empty)"""
      )
    }
  }
}
