package bio.domain.graph

import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CharacterTableProblemSpec extends AnyFunSpec with Matchers {

  private def parse(newick: String): NewickTree =
    NewickParser
      .parse(newick)
      .getOrElse(sys.error(s"could not parse Newick fixture: $newick"))

  /** Build a single-internal-node Newick tree whose `n` direct children are leaves
    * labelled `t001..t<n>`. Useful for boundary tests on the leaf-count cap.
    */
  private def buildFlatTree(n: Int): NewickTree =
    NewickTree(
      None,
      (1 to n).map(i => NewickTree(Some(f"t$i%03d"), Vector.empty)).toVector
    )

  describe("CharacterTableProblem.from") {
    it("accepts the canonical Rosalind sample tree and exposes lex-sorted leafLabels") {
      val tree    = parse("(dog,((elephant,mouse),robot),cat);")
      val result  = CharacterTableProblem.from(tree)
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.tree shouldBe tree
      problem.leafLabels shouldBe Vector("cat", "dog", "elephant", "mouse", "robot")
    }

    it("accepts a single-leaf tree") {
      val result = CharacterTableProblem.from(parse("a;"))
      result.isRight shouldBe true
      result.toOption.get.leafLabels shouldBe Vector("a")
    }

    it("accepts a 200-leaf flat tree at the upper boundary") {
      val result = CharacterTableProblem.from(buildFlatTree(200))
      result.isRight shouldBe true
      result.toOption.get.leafLabels.size shouldBe 200
    }

    it("rejects a 201-leaf flat tree as TooManyTaxa(201, 200)") {
      CharacterTableProblem.from(buildFlatTree(201)) shouldBe
        Left(CharacterTableProblemError.TooManyTaxa(201, 200))
    }
  }

  describe("CharacterTableProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.CharacterTableProblem(parse("a;"), Vector("a"))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.CharacterTableProblem.from(parse("a;")).toOption.get.copy(leafLabels = Vector.empty)"""
      )
    }
  }
}
