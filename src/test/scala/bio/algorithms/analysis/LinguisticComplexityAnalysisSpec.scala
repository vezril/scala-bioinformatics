package bio.algorithms.analysis

import bio.domain.analysis.LinguisticComplexityProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LinguisticComplexityAnalysisSpec extends AnyFunSpec with Matchers {

  private def lc(s: String): Double =
    LinguisticComplexityAnalysis
      .compute(
        LinguisticComplexityProblem(
          DnaString.from(s).getOrElse(sys.error(s"invalid DnaString: $s"))
        )
      )
      .value

  /** Number of distinct non-empty substrings of `s`, computed directly. */
  private def bruteDistinct(s: String): Long =
    (for {
      i <- 0 until s.length
      j <- (i + 1) to s.length
    } yield s.substring(i, j)).toSet.size.toLong

  /** Maximum possible distinct substrings for a length-n string over a 4-letter alphabet. */
  private def maxSubstrings(n: Int): Long =
    (1 to n).foldLeft(0L) { (acc, k) =>
      val positions = (n - k + 1).toLong
      val pow       = (0 until k).foldLeft(1L)((p, _) => if (p > positions) p else p * 4L)
      acc + math.min(pow, positions)
    }

  private def bruteLc(s: String): Double =
    if (s.isEmpty) 0.0 else bruteDistinct(s).toDouble / maxSubstrings(s.length).toDouble

  describe("LinguisticComplexityAnalysis.compute") {
    it("computes the canonical Rosalind LING sample") {
      lc("ATTTGGATT") shouldBe 0.875 +- 0.001
    }

    it("gives maximal complexity for a single character") {
      lc("A") shouldBe 1.0 +- 1e-9
    }

    it("gives low complexity for a highly repetitive string") {
      lc("AAAA") shouldBe 0.4 +- 0.001
    }

    it("agrees with the direct distinct-substring count on assorted strings") {
      val samples = Seq("A", "AC", "AAAA", "ATTTGGATT", "GATTACA", "ACGTACGTACGT", "TTTTTTTTTT", "ACGTACGA")
      samples.foreach(s => lc(s) shouldBe bruteLc(s) +- 1e-9)
    }
  }
}
