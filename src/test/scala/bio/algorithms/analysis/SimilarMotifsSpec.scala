package bio.algorithms.analysis

import bio.domain.analysis.{SimilarMotif, SimilarMotifsProblem}
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SimilarMotifsSpec extends AnyFunSpec with Matchers {

  private def fixture(k: Int, motif: String, genome: String): SimilarMotifsProblem = {
    val s = DnaString.from(motif).getOrElse(sys.error(s"invalid motif: $motif"))
    val t = DnaString.from(genome).getOrElse(sys.error(s"invalid genome: $genome"))
    SimilarMotifsProblem
      .from(k, s, t)
      .getOrElse(sys.error(s"invalid SimilarMotifsProblem fixture: ($k, $motif, $genome)"))
  }

  /** Naive O(n^2 * (m * L)) brute-force oracle: every substring of `genome`
    * whose unit-cost edit distance to `motif` is `<= k`, sorted by
    * `(location, length)`. Used to cross-check the banded two-pass DP.
    */
  private def bruteForce(k: Int, motif: String, genome: String): List[SimilarMotif] = {
    def editDistance(a: String, b: String): Int = {
      val prev = Array.tabulate(b.length + 1)(identity)
      val curr = new Array[Int](b.length + 1)
      var i    = 1
      while (i <= a.length) {
        curr(0) = i
        var j = 1
        while (j <= b.length) {
          val cost = if (a.charAt(i - 1) == b.charAt(j - 1)) 0 else 1
          curr(j) = math.min(
            math.min(prev(j) + 1, curr(j - 1) + 1),
            prev(j - 1) + cost
          )
          j += 1
        }
        System.arraycopy(curr, 0, prev, 0, b.length + 1)
        i += 1
      }
      prev(b.length)
    }

    val hits =
      for {
        start  <- genome.indices
        end    <- (start + 1) to genome.length
        sub     = genome.substring(start, end)
        if editDistance(motif, sub) <= k
      } yield SimilarMotif(start + 1, end - start)
    hits.toList.sortBy(m => (m.location, m.length))
  }

  describe("SimilarMotif (output ADT)") {
    it("constructs with named fields and is value-equal to an identical instance") {
      val a = SimilarMotif(location = 1, length = 4)
      a.location shouldBe 1
      a.length shouldBe 4
      a shouldBe SimilarMotif(1, 4)
    }
  }

  describe("SimilarMotifs.findAll") {
    it("returns the canonical Rosalind KSIM sample output") {
      SimilarMotifs.findAll(fixture(2, "ACGTAG", "ACGGATCGGCATCGT")) shouldBe
        List(SimilarMotif(1, 4), SimilarMotif(1, 5), SimilarMotif(1, 6))
    }

    it("finds every substring within one edit, across multiple starts and lengths") {
      SimilarMotifs.findAll(fixture(1, "ACG", "ACG")) shouldBe
        List(SimilarMotif(1, 2), SimilarMotif(1, 3), SimilarMotif(2, 2))
    }

    it("matches a motif longer than the genome within budget") {
      SimilarMotifs.findAll(fixture(1, "ACGT", "ACG")) shouldBe
        List(SimilarMotif(1, 3))
    }

    it("returns Nil when no substring is within the edit-distance budget") {
      SimilarMotifs.findAll(fixture(1, "AAAA", "CCCCCCCC")) shouldBe Nil
    }

    it("returns Nil for an empty genome") {
      SimilarMotifs.findAll(fixture(1, "ACG", "")) shouldBe Nil
    }

    it("returns hits sorted ascending by (location, length)") {
      val result = SimilarMotifs.findAll(fixture(1, "ACG", "ACG"))
      result shouldBe result.sortBy(m => (m.location, m.length))
    }

    it("agrees with the brute-force oracle on the canonical sample") {
      SimilarMotifs.findAll(fixture(2, "ACGTAG", "ACGGATCGGCATCGT")) shouldBe
        bruteForce(2, "ACGTAG", "ACGGATCGGCATCGT")
    }

    it("agrees with the brute-force oracle on assorted inputs") {
      val cases = List(
        (1, "ACG", "ACGTACG"),
        (2, "GATTACA", "GATTACAXGATTAC".replace("X", "A")),
        (3, "ACGT", "TTACGTACGTTT"),
        (1, "AAAA", "CCCCCCCC"),
        (2, "GCAT", "GCATGCATGCAT")
      )
      cases.foreach { case (k, s, t) =>
        withClue(s"(k=$k, motif=$s, genome=$t): ") {
          SimilarMotifs.findAll(fixture(k, s, t)) shouldBe bruteForce(k, s, t)
        }
      }
    }
  }
}
