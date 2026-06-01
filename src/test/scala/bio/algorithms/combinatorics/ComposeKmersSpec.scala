package bio.algorithms.combinatorics

import bio.domain.combinatorics.KmerCompositionProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ComposeKmersSpec extends AnyFunSpec with Matchers {

  private def problem(dna: String, k: Int): KmerCompositionProblem =
    KmerCompositionProblem.from(DnaString.from(dna).toOption.get, k).toOption.get

  private val sampleDna =
    "CTTCGAAAGTTTGGGCCGAGTCTTACAGTCGGTCTTGAAGCAAAGTAACGAACTCCACGG" +
      "CCCTGACTACCGAACCAGTTGTGAGTACTCAACTGGGTGAGAGTGCAGTCCCTATTGAGT" +
      "TTCCGAGACTCACCGGGATTTTCGATCCAGCCTCAGTCCAGTCTTGTGGCCAACTCACCA" +
      "AATGACGTTGGAATATCCCTGTCTAGCTCACGCAGTACTTAGTAAGAGGTCGCTGCAGCG" +
      "GGGCAAGGAGATCGGAAAATGTGCTCTATATGCGACTAAAGCTCCTAACTTACACGTAGA" +
      "CTTGCCCGTGTTAAAAACTCGGCTCACATGCTGTCTGCGGCTGGCTGTATACAGTATCTA" +
      "CCTAATACCCTTCAGTTCGCCGCACAAAAGCTGGGAGTTACCGCGGAAATCACAG"

  private val sampleExpected =
    ("4 1 4 3 0 1 1 5 1 3 1 2 2 1 2 0 1 1 3 1 2 1 3 1 1 1 1 2 2 5 1 3 0 2 2 1 1 1 1 3 " +
      "1 0 0 1 5 5 1 5 0 2 0 2 1 2 1 1 1 2 0 1 0 0 1 1 3 2 1 0 3 2 3 0 0 2 0 8 0 0 1 0 " +
      "2 1 3 0 0 0 1 4 3 2 1 1 3 1 2 1 3 1 2 1 2 1 1 1 2 3 2 1 1 0 1 1 3 2 1 2 6 2 1 1 " +
      "1 2 3 3 3 2 3 0 3 2 1 1 0 0 1 4 3 0 1 5 0 2 0 1 2 1 3 0 1 2 2 1 1 0 3 0 0 4 5 0 " +
      "3 0 2 1 1 3 0 3 2 2 1 1 0 2 1 0 2 2 1 2 0 2 2 5 2 2 1 1 2 1 2 2 2 2 1 1 3 4 0 2 " +
      "1 1 0 1 2 2 1 1 1 5 2 0 3 2 1 1 2 2 3 0 3 0 1 3 1 2 3 0 2 1 2 2 1 2 3 0 1 2 3 1 " +
      "1 3 1 0 1 1 3 0 2 1 2 2 0 2 1 1")
      .split("\\s+")
      .map(_.toInt)
      .toVector

  describe("ComposeKmers.compose") {
    it("computes the canonical 4-mer composition of the Rosalind sample") {
      val result = ComposeKmers.compose(problem(sampleDna, 4))
      result.counts shouldBe sampleExpected
    }

    it("produces exactly 4^k counts whose sum is max(0, n - k + 1)") {
      val dna    = "ACGTACGTAC"
      val k      = 3
      val result = ComposeKmers.compose(problem(dna, k))
      result.counts.size shouldBe math.pow(4, k).toInt
      result.counts.sum shouldBe (dna.length - k + 1)
    }

    it("yields all zeros when the string is shorter than k") {
      val result = ComposeKmers.compose(problem("AC", 4))
      result.counts.size shouldBe 256
      result.counts.sum shouldBe 0
    }

    it("counts the individual nucleotides for length 1") {
      val result = ComposeKmers.compose(problem("AACGT", 1))
      // ordered alphabet A, C, G, T
      result.counts shouldBe Vector(2, 1, 1, 1)
    }

    it("counts overlapping occurrences") {
      val result = ComposeKmers.compose(problem("AAAA", 2))
      // 2-mers in lexicographic order: AA AC AG AT CA ... ; AA appears 3 times
      result.counts.head shouldBe 3
      result.counts.sum shouldBe 3
    }
  }
}
