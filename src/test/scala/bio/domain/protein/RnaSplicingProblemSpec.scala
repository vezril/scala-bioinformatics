package bio.domain.protein

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RnaSplicingProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  private val rosalindSource: DnaString = dna(
    "ATGGTCTACATAGCTGACAAACAGCACGTAGCAATCGGTCGAATCTCGAGAGGCATATGGTCACATGATCGGTCGAGCGTGTTTCAAAGTTTGCGCCTAG"
  )
  private val rosalindIntrons: Vector[DnaString] =
    Vector(dna("ATCGGTCGAA"), dna("ATCGGTCGAGCGTGT"))

  describe("RnaSplicingProblem.from") {
    it("accepts the Rosalind sample inputs") {
      val problem = RnaSplicingProblem.from(rosalindSource, rosalindIntrons).toOption.get
      problem.source shouldBe rosalindSource
      problem.introns shouldBe rosalindIntrons
    }

    it("accepts an empty introns vector") {
      val problem = RnaSplicingProblem.from(dna("ACGT"), Vector.empty).toOption.get
      problem.source.value shouldBe "ACGT"
      problem.introns shouldBe Vector.empty
    }

    it("accepts an empty source") {
      val problem = RnaSplicingProblem.from(dna(""), Vector.empty).toOption.get
      problem.source.value shouldBe ""
    }

    it("rejects an empty intron at position 0 as EmptyIntron(0)") {
      RnaSplicingProblem.from(dna("ACGT"), Vector(dna(""))) shouldBe
        Left(RnaSplicingProblemError.EmptyIntron(0))
    }

    it("rejects an empty intron at a later position, carrying that 0-indexed position") {
      val introns = Vector(dna("AC"), dna(""), dna("GT"))
      RnaSplicingProblem.from(dna("ACGT"), introns) shouldBe
        Left(RnaSplicingProblemError.EmptyIntron(1))
    }
  }

  describe("RnaSplicingProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.RnaSplicingProblem(
          |  bio.domain.nucleic.DnaString.from("A").toOption.get,
          |  Vector.empty
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.RnaSplicingProblem
          |  .from(bio.domain.nucleic.DnaString.from("A").toOption.get, Vector.empty)
          |  .toOption.get.copy(introns = Vector.empty)""".stripMargin
      )
    }
  }
}
