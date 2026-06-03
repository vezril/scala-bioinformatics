package bio.algorithms.nucleic

import bio.domain.nucleic.{RnaString, WobbleMatchingProblem}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WobbleMatchingSpec extends AnyFunSpec with Matchers {

  private def countOf(s: String): BigInt = {
    val rna = RnaString.from(s).getOrElse(fail(s"invalid RNA string: $s"))
    val problem =
      WobbleMatchingProblem.from(rna).getOrElse(fail(s"invalid problem: $s"))
    WobbleMatching.count(problem).count
  }

  describe("WobbleMatching.count") {
    it("matches the canonical Rosalind sample") {
      countOf(
        "AUGCUAGUACGGAGCGAGUCUAGCGAGCGAUGUCGUGAGUACUAUAUAUGCGCAUAAGCCACGU"
      ) shouldBe BigInt("284850219977421")
    }

    it("counts exactly one (empty) matching for the empty string") {
      countOf("") shouldBe BigInt(1)
    }

    it("counts exactly one matching when no base can pair") {
      countOf("AAAA") shouldBe BigInt(1)
    }

    it("does not count a wobble pair closer than the minimum separation") {
      countOf("GU") shouldBe BigInt(1)
    }

    it("counts a wobble pair at the minimum separation") {
      countOf("GAAAU") shouldBe BigInt(2)
    }
  }
}
