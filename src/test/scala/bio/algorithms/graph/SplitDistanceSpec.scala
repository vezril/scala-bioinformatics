package bio.algorithms.graph

import bio.domain.graph.{NewickTree, SplitDistanceProblem}
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SplitDistanceSpec extends AnyFunSpec with Matchers {

  private def tree(newick: String): NewickTree =
    NewickParser.parse(newick).getOrElse(sys.error(s"invalid Newick fixture: $newick"))

  private def problem(
      taxa: Vector[String],
      t1: NewickTree,
      t2: NewickTree
  ): SplitDistanceProblem =
    SplitDistanceProblem
      .from(taxa, t1, t2)
      .getOrElse(sys.error(s"invalid SplitDistanceProblem fixture: $taxa"))

  private val sampleTaxa: Vector[String] =
    Vector("dog", "rat", "elephant", "mouse", "cat", "rabbit")
  private val sampleT1 = tree("(rat,(dog,cat),(rabbit,(elephant,mouse)));")
  private val sampleT2 = tree("(rat,(cat,dog),(elephant,(mouse,rabbit)));")

  describe("SplitDistance.compute") {
    it("reproduces the canonical sample distance of 2") {
      SplitDistance.compute(problem(sampleTaxa, sampleT1, sampleT2)) shouldBe 2
    }

    it("yields distance 0 for two identical trees") {
      SplitDistance.compute(problem(sampleTaxa, sampleT1, sampleT1)) shouldBe 0
    }

    it("yields the maximum 2*(n-3) when no nontrivial split is shared") {
      val taxa = Vector("a", "b", "c", "d")
      val t1   = tree("(a,b,(c,d));") // split {a,b}|{c,d}
      val t2   = tree("(a,c,(b,d));") // split {a,c}|{b,d}
      SplitDistance.compute(problem(taxa, t1, t2)) shouldBe 2 * (taxa.size - 3)
    }

    it("is symmetric in the order of the two trees") {
      SplitDistance.compute(problem(sampleTaxa, sampleT1, sampleT2)) shouldBe
        SplitDistance.compute(problem(sampleTaxa, sampleT2, sampleT1))
    }
  }
}
