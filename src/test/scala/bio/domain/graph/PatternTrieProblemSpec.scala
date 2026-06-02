package bio.domain.graph

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PatternTrieProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def dnas(ss: String*): Vector[DnaString] = ss.iterator.map(dna).toVector

  /** Encode `i` (0..255) as a distinct 4-symbol DNA string. All such strings share
    * the same length, so the set is automatically prefix-free.
    */
  private def code4(i: Int): String = {
    val cs = "ACGT"
    s"${cs((i / 64) % 4)}${cs((i / 16) % 4)}${cs((i / 4) % 4)}${cs(i % 4)}"
  }

  describe("PatternTrieProblem.from") {
    it("accepts a valid collection of patterns, preserving them") {
      val patterns = dnas("ATAGA", "ATC", "GAT")
      PatternTrieProblem.from(patterns).map(_.patterns) shouldBe Right(patterns)
    }

    it("accepts an empty collection") {
      PatternTrieProblem.from(Vector.empty).isRight shouldBe true
    }

    it("accepts 100 distinct (prefix-free) patterns") {
      val patterns = (0 until 100).iterator.map(i => dna(code4(i))).toVector
      PatternTrieProblem.from(patterns).isRight shouldBe true
    }

    it("rejects more than 100 patterns") {
      val patterns = (0 until 101).iterator.map(i => dna(code4(i))).toVector
      PatternTrieProblem.from(patterns) shouldBe Left(
        PatternTrieProblemError.TooManyPatterns(101, 100)
      )
    }

    it("rejects a pattern longer than 100 bp") {
      PatternTrieProblem.from(dnas("A" * 101)) shouldBe Left(
        PatternTrieProblemError.PatternTooLong(0, 101, 100)
      )
    }

    it("rejects a pattern that is a proper prefix of another") {
      PatternTrieProblem.from(dnas("AT", "ATC")) shouldBe Left(
        PatternTrieProblemError.PrefixConflict(0, 1)
      )
    }

    it("rejects duplicate patterns") {
      PatternTrieProblem.from(dnas("AT", "AT")) shouldBe Left(
        PatternTrieProblemError.PrefixConflict(0, 1)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.PatternTrieProblem(Vector.empty[bio.domain.nucleic.DnaString])"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.PatternTrieProblem.from(Vector.empty[bio.domain.nucleic.DnaString]).toOption.get.copy()"""
      )
    }
  }
}
