package bio.domain.graph

import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CountingQuartetsProblemSpec extends AnyFunSpec with Matchers {

  private def tree(newick: String): NewickTree =
    NewickParser.parse(newick).getOrElse(sys.error(s"invalid Newick fixture: $newick"))

  private val sampleTree = tree("(lobster,(cat,dog),(caterpillar,(elephant,mouse)));") // 6 leaves
  private val threeLeaf  = tree("(a,b,c);")

  describe("CountingQuartetsProblem.from") {
    it("accepts the canonical Rosalind sample input") {
      val result = CountingQuartetsProblem.from(6, sampleTree)
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.n shouldBe 6
      problem.tree shouldBe sampleTree
    }

    it("rejects a leaf count below the minimum as BelowMinimum") {
      CountingQuartetsProblem.from(3, threeLeaf) shouldBe
        Left(CountingQuartetsProblemError.BelowMinimum(3, 4))
    }

    it("rejects a leaf count above the maximum as ExceedsMaximum") {
      CountingQuartetsProblem.from(5001, sampleTree) shouldBe
        Left(CountingQuartetsProblemError.ExceedsMaximum(5001, 5000))
    }

    it("rejects a tree whose leaf count differs from the declared n as LeafCountMismatch") {
      CountingQuartetsProblem.from(5, sampleTree) shouldBe
        Left(CountingQuartetsProblemError.LeafCountMismatch(5, 6))
    }

    it("checks the bounds before the tree leaf count (first-failure-wins)") {
      // n = 3 fails the lower bound even though the tree leaf count (6) also mismatches.
      CountingQuartetsProblem.from(3, sampleTree) shouldBe
        Left(CountingQuartetsProblemError.BelowMinimum(3, 4))
    }
  }

  describe("CountingQuartetsProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.CountingQuartetsProblem(
          |  6,
          |  bio.domain.graph.NewickTree(Some("a"), Vector.empty)
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.CountingQuartetsProblem
          |  .from(6, bio.parsing.NewickParser.parse("(lobster,(cat,dog),(caterpillar,(elephant,mouse)));").toOption.get)
          |  .toOption.get.copy(n = 7)""".stripMargin
      )
    }
  }
}
