package bio.problems

import bio.domain.analysis.ReversingSubstitution
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RSUBProbSpec extends AnyFunSpec with Matchers {

  describe("RSUBProb.run") {
    it("parses the canonical Rosalind RSUB sample (Newick + FASTA) and finds 5 reversions") {
      val raw =
        """(((ostrich,cat)rat,mouse)dog,elephant)robot;
          |>robot
          |AATTG
          |>dog
          |GGGCA
          |>mouse
          |AAGAC
          |>rat
          |GTTGT
          |>cat
          |GAGGC
          |>ostrich
          |GTGTC
          |>elephant
          |AATTC
          |""".stripMargin

      val result = RSUBProb.run(raw)
      result.isRight shouldBe true
      val reversions = result.toOption.get
      reversions.toSet shouldBe Set(
        ReversingSubstitution("dog", "mouse", 1, 'A', 'G', 'A'),
        ReversingSubstitution("dog", "mouse", 2, 'A', 'G', 'A'),
        ReversingSubstitution("rat", "ostrich", 3, 'G', 'T', 'G'),
        ReversingSubstitution("rat", "cat", 3, 'G', 'T', 'G'),
        ReversingSubstitution("dog", "rat", 3, 'T', 'G', 'T')
      )
    }

    it("supports multi-line sequence chunks within a single FASTA record") {
      val raw =
        """(a,b)r;
          |>r
          |AC
          |GT
          |>a
          |AC
          |GT
          |>b
          |AC
          |GT
          |""".stripMargin

      val result = RSUBProb.run(raw)
      result.isRight shouldBe true
      // All three sequences are identical so there are no reversions.
      result.toOption.get shouldBe Vector.empty
    }

    it("returns an error when the alignment contains a non-DNA character") {
      val raw =
        """(a,b)r;
          |>r
          |AC
          |>a
          |A-
          |>b
          |AC
          |""".stripMargin

      RSUBProb.run(raw).isLeft shouldBe true
    }
  }

  describe("RSUBProb.formatReversion") {
    it("renders a reversion in Rosalind's `O->S->R` line format") {
      RSUBProb.formatReversion(
        ReversingSubstitution("dog", "mouse", 1, 'A', 'G', 'A')
      ) shouldBe "dog mouse 1 A->G->A"
    }
  }
}
