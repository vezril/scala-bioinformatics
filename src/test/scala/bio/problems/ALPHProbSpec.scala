package bio.problems

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ALPHProbSpec extends AnyFunSpec with Matchers {

  describe("ALPHProb.run") {
    it("parses the canonical Rosalind ALPH sample (Newick + FASTA with gaps) and computes distance 8") {
      val raw =
        """(((ostrich,cat)rat,(duck,fly)mouse)dog,(elephant,pikachu)hamster)robot;
          |>ostrich
          |AC
          |>cat
          |CA
          |>duck
          |T-
          |>fly
          |GC
          |>elephant
          |-T
          |>pikachu
          |AA
          |""".stripMargin

      val result = ALPHProb.run(raw)
      result.isRight shouldBe true
      val phylo = result.toOption.get
      phylo.totalDistance shouldBe 8
      phylo.internalAssignments.size shouldBe 5
      phylo.internalAssignments.head.label shouldBe "robot"
      phylo.internalAssignments.map(_.label).toSet shouldBe
        Set("rat", "mouse", "dog", "hamster", "robot")
    }

    it("supports multi-line sequence chunks within a single FASTA record") {
      val raw =
        """(a,b)r;
          |>a
          |AC
          |GT
          |>b
          |--
          |TT
          |""".stripMargin

      val result = ALPHProb.run(raw)
      result.isRight shouldBe true
      val phylo = result.toOption.get
      // The leaf rows reconstructed by the parser are "ACGT" and "--TT".
      // Total distance = hamming(root, leaf_a) + hamming(root, leaf_b).
      phylo.internalAssignments.head.sequence.length shouldBe 4
    }

    it("returns an error message when the file has no Newick line") {
      ALPHProb.run("").isLeft shouldBe true
    }
  }
}
