package bio.algorithms.analysis

import bio.domain.analysis.SharedSplicedMotifProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SharedSplicedMotifSpec extends AnyFunSpec with Matchers {

  private def fixture(left: String, right: String): SharedSplicedMotifProblem = {
    val l = DnaString.from(left).getOrElse(sys.error(s"invalid left: $left"))
    val r = DnaString.from(right).getOrElse(sys.error(s"invalid right: $right"))
    SharedSplicedMotifProblem
      .from(l, r)
      .getOrElse(sys.error(s"invalid SharedSplicedMotifProblem fixture: ($left, $right)"))
  }

  /** True iff every character of `needle` appears in order (not necessarily
    * contiguously) inside `haystack`.
    */
  private def isSubsequence(needle: String, haystack: String): Boolean = {
    var i = 0
    var j = 0
    while (i < needle.length && j < haystack.length) {
      if (needle.charAt(i) == haystack.charAt(j)) i += 1
      j += 1
    }
    i == needle.length
  }

  describe("SharedSplicedMotif.find") {
    it("returns a length-6 LCS that is a subsequence of both for the canonical Rosalind sample") {
      val problem = fixture("AACCTTGG", "ACACTGTGA")
      val result  = SharedSplicedMotif.find(problem)
      result.length shouldBe 6
      isSubsequence(result, "AACCTTGG") shouldBe true
      isSubsequence(result, "ACACTGTGA") shouldBe true
    }

    it("returns the string itself for identical inputs") {
      SharedSplicedMotif.find(fixture("ACGT", "ACGT")) shouldBe "ACGT"
    }

    it("returns the empty string when left is empty") {
      SharedSplicedMotif.find(fixture("", "ACGT")) shouldBe ""
    }

    it("returns the empty string when right is empty") {
      SharedSplicedMotif.find(fixture("ACGT", "")) shouldBe ""
    }

    it("returns the empty string when no character is shared") {
      SharedSplicedMotif.find(fixture("ACG", "TTT")) shouldBe ""
    }

    it("returns the single common character `A` for inputs `A` and `TA`") {
      SharedSplicedMotif.find(fixture("A", "TA")) shouldBe "A"
    }

    it("returns the unique LCS `CG` for inputs `ACG` and `TCG`") {
      SharedSplicedMotif.find(fixture("ACG", "TCG")) shouldBe "CG"
    }
  }
}
