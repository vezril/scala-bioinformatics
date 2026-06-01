package bio.domain.graph

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GenomeAssemblyProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private val sampleReads: Vector[DnaString] =
    Vector("ATTAGACCTG", "CCTGCCGGAA", "AGACCTGCCG", "GCCGGAATAC").map(dna)

  describe("GenomeAssemblyProblem.from") {
    it("accepts the canonical sample reads") {
      val result = GenomeAssemblyProblem.from(sampleReads)
      result.isRight shouldBe true
      result.foreach(_.reads shouldBe sampleReads)
    }

    it("rejects an empty read collection") {
      GenomeAssemblyProblem.from(Vector.empty) shouldBe
        Left(GenomeAssemblyProblemError.EmptyReadCollection)
    }

    it("rejects more than 50 reads") {
      val many = Vector.fill(51)(dna("ACGT"))
      GenomeAssemblyProblem.from(many) shouldBe
        Left(GenomeAssemblyProblemError.TooManyReads(51, 50))
    }

    it("rejects a read longer than 1000 bp") {
      val reads = Vector(dna("ACGT"), dna("A" * 1001))
      GenomeAssemblyProblem.from(reads) shouldBe
        Left(GenomeAssemblyProblemError.ReadTooLong(1, 1001, 1000))
    }

    it("reports too-many before an over-long read") {
      val many = Vector.fill(50)(dna("ACGT")) :+ dna("A" * 1001)
      GenomeAssemblyProblem.from(many) shouldBe
        Left(GenomeAssemblyProblemError.TooManyReads(51, 50))
    }

    it("reports empty before any other failure") {
      GenomeAssemblyProblem.from(Vector.empty) shouldBe
        Left(GenomeAssemblyProblemError.EmptyReadCollection)
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.GenomeAssemblyProblem(Vector.empty[bio.domain.nucleic.DnaString])"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """GenomeAssemblyProblem
          |  .from(Vector(bio.domain.nucleic.DnaString.from("ACGT").toOption.get))
          |  .toOption
          |  .get
          |  .copy(reads = Vector.empty)""".stripMargin
      )
    }
  }
}
