package bio.algorithms.analysis

import bio.domain.analysis.SupersequenceProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ShortestCommonSupersequenceSpec extends AnyFunSpec with Matchers {

  private def scs(a: String, b: String): String =
    ShortestCommonSupersequence
      .build(
        SupersequenceProblem
          .from(
            DnaString.from(a).getOrElse(sys.error(s"bad DnaString: $a")),
            DnaString.from(b).getOrElse(sys.error(s"bad DnaString: $b"))
          )
          .getOrElse(sys.error("invalid SupersequenceProblem fixture"))
      )
      .value

  /** True when `sub` is a subsequence of `sup`. */
  private def isSubsequence(sub: String, sup: String): Boolean = {
    val rest = sub.foldLeft(sup) { (remaining, c) =>
      val i = remaining.indexOf(c.toInt)
      if (i < 0) return false else remaining.substring(i + 1)
    }
    rest != null // always true once the fold completes without early return
  }

  describe("ShortestCommonSupersequence.build") {
    it("computes a shortest common supersequence of the canonical sample") {
      val result = scs("ATCTGAT", "TGCATA")
      result.length shouldBe 9
      isSubsequence("ATCTGAT", result) shouldBe true
      isSubsequence("TGCATA", result) shouldBe true
    }

    it("returns the string itself when the other is empty") {
      scs("ACGT", "") shouldBe "ACGT"
    }

    it("returns the string itself for identical inputs") {
      scs("ACGT", "ACGT") shouldBe "ACGT"
    }

    it("concatenates disjoint strings") {
      val result = scs("AA", "CC")
      result.length shouldBe 4
      isSubsequence("AA", result) shouldBe true
      isSubsequence("CC", result) shouldBe true
    }
  }
}
