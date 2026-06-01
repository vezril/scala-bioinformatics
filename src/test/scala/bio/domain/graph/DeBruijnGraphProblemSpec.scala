package bio.domain.graph

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DeBruijnGraphProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).toOption.get

  private val sampleKmers: Vector[DnaString] =
    Vector("TGAT", "CATG", "TCAT", "ATGC", "CATC", "CATC").map(dna)

  describe("DeBruijnGraphProblem.from") {
    it("accepts the canonical sample of six length-4 k-mers, preserving order") {
      val result = DeBruijnGraphProblem.from(sampleKmers)
      result.map(_.kmers) shouldBe Right(sampleKmers)
    }

    it("rejects an empty collection") {
      DeBruijnGraphProblem.from(Vector.empty) shouldBe
        Left(DeBruijnGraphProblemError.EmptyKmerCollection)
    }

    it("rejects more than 1000 k-mers") {
      val tooMany = Vector.fill(1001)(dna("ACGT"))
      DeBruijnGraphProblem.from(tooMany) shouldBe
        Left(DeBruijnGraphProblemError.TooManyKmers(1001, 1000))
    }

    it("rejects a k-mer too short to split, reporting the first such index") {
      val kmers = Vector(dna("AC"), dna("A"), dna("GT"))
      DeBruijnGraphProblem.from(kmers) shouldBe
        Left(DeBruijnGraphProblemError.KmerTooShort(1, 1, 2))
    }

    it("rejects a k-mer longer than the maximum, reporting the first such index") {
      val kmers = Vector(dna("A" * 50), dna("A" * 51))
      DeBruijnGraphProblem.from(kmers) shouldBe
        Left(DeBruijnGraphProblemError.KmerTooLong(1, 51, 50))
    }

    it("rejects k-mers of unequal length, reporting the first mismatch") {
      val kmers = Vector(dna("ACGT"), dna("ACGT"), dna("ACGTA"))
      DeBruijnGraphProblem.from(kmers) shouldBe
        Left(DeBruijnGraphProblemError.InconsistentLength(2, 5, 4))
    }

    it("reports the earliest failure when an input is invalid in multiple ways") {
      // empty wins over everything
      DeBruijnGraphProblem.from(Vector.empty) shouldBe
        Left(DeBruijnGraphProblemError.EmptyKmerCollection)

      // too-many wins over a too-short k-mer present in the collection
      val tooManyWithShort = dna("A") +: Vector.fill(1001)(dna("ACGT"))
      DeBruijnGraphProblem.from(tooManyWithShort) shouldBe
        Left(DeBruijnGraphProblemError.TooManyKmers(1002, 1000))

      // too-short wins over too-long (per-k-mer, index order)
      val shortBeforeLong = Vector(dna("A"), dna("A" * 51))
      DeBruijnGraphProblem.from(shortBeforeLong) shouldBe
        Left(DeBruijnGraphProblemError.KmerTooShort(0, 1, 2))
    }

    it("does not expose a public apply that bypasses validation") {
      assertDoesNotCompile(
        """bio.domain.graph.DeBruijnGraphProblem(Vector.empty[bio.domain.nucleic.DnaString])"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """DeBruijnGraphProblem.from(sampleKmers).toOption.get.copy(kmers = Vector.empty)"""
      )
    }
  }
}
