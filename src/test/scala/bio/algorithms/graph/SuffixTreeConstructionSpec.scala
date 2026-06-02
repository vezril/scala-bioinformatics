package bio.algorithms.graph

import bio.domain.graph.SuffixTreeProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SuffixTreeConstructionSpec extends AnyFunSpec with Matchers {

  private def labels(s: String): Vector[String] =
    SuffixTreeConstruction
      .encode(
        SuffixTreeProblem
          .from(DnaString.from(s).getOrElse(sys.error(s"invalid DnaString: $s")))
          .getOrElse(sys.error("invalid SuffixTreeProblem fixture"))
      )
      .edges
      .sorted

  describe("SuffixTreeConstruction.encode") {
    it("encodes the canonical Rosalind SUFF sample (edge-label multiset)") {
      labels("ATAAATG") shouldBe Vector(
        "AAATG$", "G$", "T", "ATG$", "TG$", "A", "A", "AAATG$", "G$", "T", "G$", "$"
      ).sorted
    }

    it("encodes the empty string as a single terminator edge") {
      labels("") shouldBe Vector("$")
    }

    it("encodes a single-character string") {
      labels("A") shouldBe Vector("$", "A$")
    }

    it("encodes a repeated-character string") {
      labels("AAA") shouldBe Vector("$", "$", "$", "A", "A", "A$")
    }
  }
}
