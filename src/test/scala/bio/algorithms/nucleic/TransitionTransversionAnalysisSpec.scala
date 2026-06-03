package bio.algorithms.nucleic

import bio.domain.nucleic.{DnaString, TransitionTransversionProblem, TransitionTransversionRatio}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TransitionTransversionAnalysisSpec extends AnyFunSpec with Matchers {

  private def analyze(a: String, b: String): TransitionTransversionRatio = {
    val first  = DnaString.from(a).getOrElse(fail(s"invalid DNA string: $a"))
    val second = DnaString.from(b).getOrElse(fail(s"invalid DNA string: $b"))
    val problem = TransitionTransversionProblem
      .from(first, second)
      .getOrElse(fail("invalid problem"))
    TransitionTransversionAnalysis.analyze(problem)
  }

  describe("TransitionTransversionAnalysis.analyze") {
    it("matches the canonical Rosalind sample") {
      val s1 =
        "GCAACGCACAACGAAAACCCTTAGGGACTGGATTATTTCGTGATCGTTGTAGTTATTGGAAGTACGGGCATCAACCCAGTT"
      val s2 =
        "TTATCTGACAAAGAAAGCCGTCAACGGCTGGATAATTTCGCGATCGTGCTGGTTACTGGCGGTACGAGTGTTCCTTTGGGT"
      analyze(s1, s2).format shouldBe "1.21428571429"
    }

    it("returns zero counts for identical sequences") {
      val r = analyze("ACGTACGT", "ACGTACGT")
      r.transitions shouldBe 0
      r.transversions shouldBe 0
      r.ratio shouldBe 0.0
    }

    it("counts a pure-transition pairing") {
      val r = analyze("AC", "GT")
      r.transitions shouldBe 2
      r.transversions shouldBe 0
    }

    it("counts a pure-transversion pairing") {
      val r = analyze("A", "C")
      r.transitions shouldBe 0
      r.transversions shouldBe 1
    }
  }
}
