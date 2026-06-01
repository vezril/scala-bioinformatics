package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class QuartetsProblemSpec extends AnyFunSpec with Matchers {

  private val sampleTaxa: Vector[String] =
    Vector("cat", "dog", "elephant", "ostrich", "mouse", "rabbit", "robot")
  private val sampleChars: Vector[String] =
    Vector("01xxx00", "x11xx00", "111x00x")

  describe("QuartetsProblem.from") {
    it("accepts the canonical Rosalind sample table") {
      val result = QuartetsProblem.from(sampleTaxa, sampleChars)
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.taxa shouldBe sampleTaxa
      problem.characters shouldBe sampleChars
    }

    it("rejects an empty taxa vector as EmptyTaxa") {
      QuartetsProblem.from(Vector.empty, Vector("0")) shouldBe
        Left(QuartetsProblemError.EmptyTaxa)
    }

    it("rejects duplicate taxon names as DuplicateTaxon") {
      QuartetsProblem.from(Vector("cat", "dog", "cat"), Vector("110")) shouldBe
        Left(QuartetsProblemError.DuplicateTaxon("cat"))
    }

    it("rejects an empty character table as EmptyTable") {
      QuartetsProblem.from(Vector("cat", "dog"), Vector.empty) shouldBe
        Left(QuartetsProblemError.EmptyTable)
    }

    it("rejects a row whose width differs from the taxon count as InconsistentWidth") {
      QuartetsProblem.from(sampleTaxa, Vector("0101100", "x11x00")) shouldBe
        Left(QuartetsProblemError.InconsistentWidth(1, 7, 6))
    }

    it("rejects a row containing a symbol other than 0/1/x as InvalidSymbol") {
      QuartetsProblem.from(Vector("a", "b", "c", "d"), Vector("01x2")) shouldBe
        Left(QuartetsProblemError.InvalidSymbol(0, 3, '2'))
    }

    it("checks emptiness/duplicates before width/symbol (first-failure-wins)") {
      // Empty taxa wins even though the row would also be a width mismatch.
      QuartetsProblem.from(Vector.empty, Vector("abc")) shouldBe
        Left(QuartetsProblemError.EmptyTaxa)
    }
  }

  describe("QuartetsProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.QuartetsProblem(Vector("a"), Vector("0"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.QuartetsProblem
          |  .from(Vector("a", "b"), Vector("10")).toOption.get.copy(characters = Vector("01"))""".stripMargin
      )
    }
  }
}
