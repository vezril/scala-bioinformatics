package bio.algorithms.nucleic

import bio.domain.nucleic.{MaximumMatchingProblem, RnaString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MaximumMatchingSpec extends AnyFunSpec with Matchers {

  private def countOf(s: String): BigInt = {
    val rna     = RnaString.from(s).getOrElse(fail(s"invalid RNA string: $s"))
    val problem = MaximumMatchingProblem.from(rna).getOrElse(fail(s"invalid problem: $s"))
    MaximumMatching.count(problem).count
  }

  describe("MaximumMatching.count") {
    it("matches the canonical Rosalind sample") {
      countOf("AUGCUUC") shouldBe BigInt(6)
    }

    it("counts exactly one (empty) matching for the empty string") {
      countOf("") shouldBe BigInt(1)
    }

    it("uses the falling factorial for an unbalanced string") {
      countOf("AUU") shouldBe BigInt(2)
    }

    it("reduces to the perfect-matching count for a balanced string") {
      countOf("AAUU") shouldBe BigInt(2)
    }

    it("counts exactly one matching when no base can pair") {
      countOf("AAA") shouldBe BigInt(1)
    }
  }
}
