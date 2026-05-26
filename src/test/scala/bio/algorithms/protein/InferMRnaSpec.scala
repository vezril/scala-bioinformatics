package bio.algorithms.protein

import bio.domain.protein.ProteinString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InferMRnaSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid protein in test fixture: $s"))

  describe("InferMRna.count") {
    it("produces 12 for the Rosalind sample \"MA\"") {
      InferMRna.count(protein("MA")) shouldBe 12
    }

    it("returns 3 for an empty protein (only the stop-codon factor contributes)") {
      InferMRna.count(protein("")) shouldBe 3
    }

    it("returns 3 for a single Methionine (1 codon for M × 3 stops)") {
      InferMRna.count(protein("M")) shouldBe 3
    }

    it("returns 3 for a single Tryptophan (1 codon for W × 3 stops)") {
      InferMRna.count(protein("W")) shouldBe 3
    }

    it("returns 18 for a single Leucine (6 codons for L × 3 stops)") {
      InferMRna.count(protein("L")) shouldBe 18
    }

    it("returns 18 for a single Arginine (6 codons for R × 3 stops)") {
      InferMRna.count(protein("R")) shouldBe 18
    }

    it("returns 108 for two consecutive Leucines (6 × 6 × 3)") {
      InferMRna.count(protein("LL")) shouldBe 108
    }

    it("returns 102976 for the spec-8 sample protein \"MAMAPRTEINSTRING\" (= 191102976 mod 1000000)") {
      InferMRna.count(protein("MAMAPRTEINSTRING")) shouldBe 102976
    }

    it("wraps modulo for an 8-Leucine protein (6^8 × 3 = 5038848 mod 1000000 = 38848)") {
      InferMRna.count(protein("LLLLLLLL")) shouldBe 38848
    }

    it("returns 3 for any-length all-Methionine protein (1^n × 3 = 3)") {
      InferMRna.count(protein("MMMMMMMMMM")) shouldBe 3
    }
  }
}
