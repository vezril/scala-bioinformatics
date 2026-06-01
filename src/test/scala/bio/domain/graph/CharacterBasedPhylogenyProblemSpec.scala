package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CharacterBasedPhylogenyProblemSpec extends AnyFunSpec with Matchers {

  private val sampleTaxa =
    Vector("cat", "dog", "elephant", "mouse", "rabbit", "rat")
  private val sampleRows = Vector("011101", "001101", "001100")

  describe("CharacterBasedPhylogenyProblem.from") {
    it("accepts the canonical consistent sample") {
      val result = CharacterBasedPhylogenyProblem.from(sampleTaxa, sampleRows)
      result.isRight shouldBe true
      result.foreach { p =>
        p.taxa shouldBe sampleTaxa
        p.characters shouldBe sampleRows
      }
    }

    it("accepts a table with no characters") {
      CharacterBasedPhylogenyProblem.from(sampleTaxa, Vector.empty).isRight shouldBe true
    }

    it("rejects an empty taxa list") {
      CharacterBasedPhylogenyProblem.from(Vector.empty, Vector.empty) shouldBe
        Left(CharacterBasedPhylogenyProblemError.EmptyTaxa)
    }

    it("rejects a duplicated taxon") {
      CharacterBasedPhylogenyProblem.from(Vector("a", "b", "a"), Vector.empty) shouldBe
        Left(CharacterBasedPhylogenyProblemError.DuplicateTaxon("a"))
    }

    it("rejects more than 80 taxa") {
      val taxa = (1 to 81).map(i => s"t$i").toVector
      CharacterBasedPhylogenyProblem.from(taxa, Vector.empty) shouldBe
        Left(CharacterBasedPhylogenyProblemError.ExceedsMaximumTaxa(81, 80))
    }

    it("rejects a character row whose length differs from the taxa count") {
      CharacterBasedPhylogenyProblem.from(sampleTaxa, Vector("01110")) shouldBe
        Left(CharacterBasedPhylogenyProblemError.RowLengthMismatch(0, 6, 5))
    }

    it("rejects a character row containing a non-binary symbol") {
      CharacterBasedPhylogenyProblem.from(sampleTaxa, Vector("01110x")) shouldBe
        Left(CharacterBasedPhylogenyProblemError.InvalidCharacter(0, 'x'))
    }

    it("rejects conflicting characters") {
      // {a,b}|{c,d} vs {a,c}|{b,d} conflict: all four intersections non-empty.
      CharacterBasedPhylogenyProblem.from(Vector("a", "b", "c", "d"), Vector("1100", "1010")) shouldBe
        Left(CharacterBasedPhylogenyProblemError.ConflictingCharacters(0, 1))
    }

    it("reports the earliest failure when multiple problems exist (empty before others)") {
      CharacterBasedPhylogenyProblem.from(Vector.empty, Vector("xyz")) shouldBe
        Left(CharacterBasedPhylogenyProblemError.EmptyTaxa)
    }

    it("reports a row-length mismatch before an invalid character in a later row") {
      val result = CharacterBasedPhylogenyProblem.from(sampleTaxa, Vector("01110", "01110x"))
      result shouldBe Left(CharacterBasedPhylogenyProblemError.RowLengthMismatch(0, 6, 5))
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.CharacterBasedPhylogenyProblem(Vector("a"), Vector.empty)"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """CharacterBasedPhylogenyProblem
          |  .from(Vector("a", "b"), Vector.empty)
          |  .toOption
          |  .get
          |  .copy(taxa = Vector("z"))""".stripMargin
      )
    }
  }
}
