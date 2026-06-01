package bio.domain.graph

import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class QuartetDistanceProblemSpec extends AnyFunSpec with Matchers {

  private def tree(newick: String): NewickTree =
    NewickParser.parse(newick).getOrElse(sys.error(s"invalid Newick fixture: $newick"))

  private val sampleTaxa: Vector[String] = Vector("A", "B", "C", "D", "E")
  private val sampleT1                   = tree("(A,C,((B,D),E));")
  private val sampleT2                   = tree("(C,(B,D),(A,E));")

  describe("QuartetDistanceProblem.from") {
    it("accepts the canonical Rosalind sample input") {
      val result = QuartetDistanceProblem.from(sampleTaxa, sampleT1, sampleT2)
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.taxa shouldBe sampleTaxa
      problem.tree1 shouldBe sampleT1
      problem.tree2 shouldBe sampleT2
    }

    it("rejects an empty taxa vector as EmptyTaxa") {
      QuartetDistanceProblem.from(Vector.empty, sampleT1, sampleT2) shouldBe
        Left(QuartetDistanceProblemError.EmptyTaxa)
    }

    it("rejects duplicate taxon names as DuplicateTaxon") {
      val t = tree("(A,(B,A));")
      QuartetDistanceProblem.from(Vector("A", "B", "A"), t, t) shouldBe
        Left(QuartetDistanceProblemError.DuplicateTaxon("A"))
    }

    it("rejects a first tree whose leaf labels differ from the taxa") {
      val taxa = Vector("a", "b", "c", "d")
      val t1   = tree("(a,b,(c,e));") // missing d, extra e
      val t2   = tree("(a,b,(c,d));")
      QuartetDistanceProblem.from(taxa, t1, t2) shouldBe
        Left(QuartetDistanceProblemError.TreeTaxaMismatch(1, Set("d"), Set("e")))
    }

    it("reports the second tree when only it mismatches") {
      val taxa = Vector("a", "b", "c", "d")
      val t1   = tree("(a,b,(c,d));")
      val t2   = tree("(a,b,c);") // missing d
      QuartetDistanceProblem.from(taxa, t1, t2) shouldBe
        Left(QuartetDistanceProblemError.TreeTaxaMismatch(2, Set("d"), Set.empty))
    }

    it("checks emptiness/duplicates before tree labels (first-failure-wins)") {
      QuartetDistanceProblem.from(Vector.empty, sampleT1, sampleT2) shouldBe
        Left(QuartetDistanceProblemError.EmptyTaxa)
    }
  }

  describe("QuartetDistanceProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.QuartetDistanceProblem(
          |  Vector("a"),
          |  bio.domain.graph.NewickTree(Some("a"), Vector.empty),
          |  bio.domain.graph.NewickTree(Some("a"), Vector.empty)
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.QuartetDistanceProblem
          |  .from(Vector("a", "b"),
          |        bio.parsing.NewickParser.parse("(a,b);").toOption.get,
          |        bio.parsing.NewickParser.parse("(a,b);").toOption.get)
          |  .toOption.get.copy(taxa = Vector("x"))""".stripMargin
      )
    }
  }
}
