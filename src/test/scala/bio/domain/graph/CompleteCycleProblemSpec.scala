package bio.domain.graph

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CompleteCycleProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private val SampleReads: Vector[String] = Vector(
    "CAG", "AGT", "GTT", "TTT", "TTG", "TGG", "GGC", "GCG", "CGT", "GTT",
    "TTC", "TCA", "CAA", "AAT", "ATT", "TTC", "TCA"
  )

  describe("CompleteCycleProblem.from") {
    it("accepts the canonical sample reads, preserving order and repeats") {
      val result = CompleteCycleProblem.from(SampleReads.map(dna))
      result.isRight shouldBe true
      result.toOption.get.kmers.map(_.value) shouldBe SampleReads
    }

    it("rejects an empty collection") {
      CompleteCycleProblem.from(Vector.empty) shouldBe
        Left(CompleteCycleProblemError.EmptyKmerCollection)
    }

    it("rejects more than the maximum number of reads") {
      val tooMany = Vector.fill(51)(dna("AC"))
      CompleteCycleProblem.from(tooMany) shouldBe
        Left(CompleteCycleProblemError.TooManyReads(51, 50))
    }

    it("rejects a read shorter than the minimum length") {
      val reads = Vector(dna("AC"), dna("A"), dna("CG"))
      CompleteCycleProblem.from(reads) shouldBe
        Left(CompleteCycleProblemError.KmerTooShort(1, 1, 2))
    }

    it("rejects a read longer than the maximum length") {
      val reads = Vector(dna("ACGTAC"), dna("ACGTACG"), dna("ACGTAC"))
      CompleteCycleProblem.from(reads) shouldBe
        Left(CompleteCycleProblemError.KmerTooLong(1, 7, 6))
    }

    it("rejects a read of inconsistent length") {
      val reads = Vector(dna("ACG"), dna("AC"), dna("CGT"))
      CompleteCycleProblem.from(reads) shouldBe
        Left(CompleteCycleProblemError.InconsistentLength(1, 2, 3))
    }
  }

  describe("CompleteCycleProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.CompleteCycleProblem(Vector(dna("AC")))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.CompleteCycleProblem
          |  .from(Vector(dna("AC"))).toOption.get.copy(kmers = Vector(dna("CG")))""".stripMargin
      )
    }
  }
}
