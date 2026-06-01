package bio.domain.graph

import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SplitDistanceProblemSpec extends AnyFunSpec with Matchers {

  private def tree(newick: String): NewickTree =
    NewickParser.parse(newick).getOrElse(sys.error(s"invalid Newick fixture: $newick"))

  private val sampleTaxa: Vector[String] =
    Vector("dog", "rat", "elephant", "mouse", "cat", "rabbit")
  private val sampleT1 = tree("(rat,(dog,cat),(rabbit,(elephant,mouse)));")
  private val sampleT2 = tree("(rat,(cat,dog),(elephant,(mouse,rabbit)));")

  describe("SplitDistanceProblem.from") {
    it("accepts the canonical Rosalind sample input") {
      val result = SplitDistanceProblem.from(sampleTaxa, sampleT1, sampleT2)
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.taxa shouldBe sampleTaxa
      problem.tree1 shouldBe sampleT1
      problem.tree2 shouldBe sampleT2
    }

    it("rejects an empty taxa vector as EmptyTaxa") {
      SplitDistanceProblem.from(Vector.empty, sampleT1, sampleT2) shouldBe
        Left(SplitDistanceProblemError.EmptyTaxa)
    }

    it("rejects duplicate taxon names as DuplicateTaxon") {
      val t = tree("(dog,(rat,dog));")
      SplitDistanceProblem.from(Vector("dog", "rat", "dog"), t, t) shouldBe
        Left(SplitDistanceProblemError.DuplicateTaxon("dog"))
    }

    it("rejects a first tree whose leaf labels differ from the taxa") {
      val taxa = Vector("a", "b", "c", "d")
      val t1   = tree("(a,b,(c,e));") // missing d, extra e
      val t2   = tree("(a,b,(c,d));")
      SplitDistanceProblem.from(taxa, t1, t2) shouldBe
        Left(SplitDistanceProblemError.TreeTaxaMismatch(1, Set("d"), Set("e")))
    }

    it("reports the second tree when only it mismatches") {
      val taxa = Vector("a", "b", "c", "d")
      val t1   = tree("(a,b,(c,d));")
      val t2   = tree("(a,b,c);") // missing d
      SplitDistanceProblem.from(taxa, t1, t2) shouldBe
        Left(SplitDistanceProblemError.TreeTaxaMismatch(2, Set("d"), Set.empty))
    }

    it("checks emptiness/duplicates before tree labels (first-failure-wins)") {
      // Empty taxa wins even though the trees would also mismatch the empty taxa set.
      SplitDistanceProblem.from(Vector.empty, sampleT1, sampleT2) shouldBe
        Left(SplitDistanceProblemError.EmptyTaxa)
    }
  }

  describe("SplitDistanceProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.SplitDistanceProblem(
          |  Vector("a"),
          |  bio.domain.graph.NewickTree(Some("a"), Vector.empty),
          |  bio.domain.graph.NewickTree(Some("a"), Vector.empty)
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.SplitDistanceProblem
          |  .from(Vector("a", "b"),
          |        bio.parsing.NewickParser.parse("(a,b);").toOption.get,
          |        bio.parsing.NewickParser.parse("(a,b);").toOption.get)
          |  .toOption.get.copy(taxa = Vector("x"))""".stripMargin
      )
    }
  }
}
