package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SharedMotifProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  describe("SharedMotifProblem.from") {
    it("accepts the canonical 3-string Rosalind LCSM sample") {
      val result = SharedMotifProblem.from(
        Vector(dna("GATTACA"), dna("TAGACCA"), dna("ATACA"))
      )
      result.isRight shouldBe true
      result.toOption.get.sequences.size shouldBe 3
    }

    it("accepts a single-string collection") {
      val result = SharedMotifProblem.from(Vector(dna("ACGT")))
      result.isRight shouldBe true
      result.toOption.get.sequences.size shouldBe 1
    }

    it("accepts 100 strings at the upper boundary") {
      val many   = Vector.fill(100)(dna("ACGT"))
      val result = SharedMotifProblem.from(many)
      result.isRight shouldBe true
      result.toOption.get.sequences.size shouldBe 100
    }

    it("accepts a collection containing an empty string") {
      val result = SharedMotifProblem.from(Vector(dna("ACGT"), dna("")))
      result.isRight shouldBe true
    }

    it("rejects an empty collection as EmptyCollection") {
      SharedMotifProblem.from(Vector.empty) shouldBe
        Left(SharedMotifProblemError.EmptyCollection)
    }

    it("rejects 101 strings as TooManyStrings(101, 100)") {
      val tooMany = Vector.fill(101)(dna("A"))
      SharedMotifProblem.from(tooMany) shouldBe
        Left(SharedMotifProblemError.TooManyStrings(101, 100))
    }

    it("rejects a 1001-character string as StringTooLong(0, 1001, 1000)") {
      SharedMotifProblem.from(Vector(dna("A" * 1001))) shouldBe
        Left(SharedMotifProblemError.StringTooLong(0, 1001, 1000))
    }
  }

  describe("SharedMotifProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.SharedMotifProblem(Vector.empty)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.SharedMotifProblem
          |  .from(Vector(dna("A"))).toOption.get.copy(sequences = Vector.empty)""".stripMargin
      )
    }
  }
}
