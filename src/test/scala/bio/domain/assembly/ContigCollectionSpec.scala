package bio.domain.assembly

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ContigCollectionSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).toOption.get

  private val sampleContigs: Vector[DnaString] =
    Vector("GATTACA", "TACTACTAC", "ATTGAT", "GAAGA").map(dna)

  describe("ContigCollection.from") {
    it("accepts the canonical sample of four contigs, preserving order") {
      val result = ContigCollection.from(sampleContigs)
      result.map(_.contigs) shouldBe Right(sampleContigs)
    }

    it("rejects an empty collection") {
      ContigCollection.from(Vector.empty) shouldBe
        Left(ContigCollectionError.EmptyContigCollection)
    }

    it("rejects more than 1000 contigs") {
      val contigs = Vector.fill(1001)(dna("A"))
      ContigCollection.from(contigs) shouldBe
        Left(ContigCollectionError.TooManyContigs(1001, 1000))
    }

    it("rejects a zero-length contig, reporting the first such index") {
      val contigs = Vector(dna("ACG"), dna(""), dna("TT"))
      ContigCollection.from(contigs) shouldBe
        Left(ContigCollectionError.EmptyContig(1))
    }

    it("rejects a collection whose combined length exceeds the maximum") {
      // two contigs of 25000 and 25001 → total 50001
      val contigs = Vector(dna("A" * 25000), dna("A" * 25001))
      ContigCollection.from(contigs) shouldBe
        Left(ContigCollectionError.ExceedsTotalLength(50001, 50000))
    }

    it("reports the earliest failure when an input is invalid in multiple ways") {
      // empty wins over everything
      ContigCollection.from(Vector.empty) shouldBe
        Left(ContigCollectionError.EmptyContigCollection)

      // too-many wins over empty-contig
      val manyWithEmpty = Vector.fill(1001)(dna("")).updated(0, dna("A"))
      ContigCollection.from(manyWithEmpty) shouldBe
        Left(ContigCollectionError.TooManyContigs(1001, 1000))
    }

    it("does not expose a public apply that bypasses validation") {
      assertDoesNotCompile(
        """bio.domain.assembly.ContigCollection(Vector.empty[bio.domain.nucleic.DnaString])"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """ContigCollection.from(sampleContigs).toOption.get.copy(contigs = Vector.empty)"""
      )
    }
  }
}
