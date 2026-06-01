package bio.algorithms.graph

import bio.domain.graph.{CharacterBasedPhylogenyProblem, NewickTree}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CharacterBasedPhylogenySpec extends AnyFunSpec with Matchers {

  private val sampleTaxa =
    Vector("cat", "dog", "elephant", "mouse", "rabbit", "rat")
  private val sampleRows = Vector("011101", "001101", "001100")

  private def problem(
      taxa: Vector[String],
      rows: Vector[String]
  ): CharacterBasedPhylogenyProblem =
    CharacterBasedPhylogenyProblem
      .from(taxa, rows)
      .getOrElse(sys.error("invalid CharacterBasedPhylogenyProblem fixture"))

  /** The leaf labels beneath a node. */
  private def leavesUnder(node: NewickTree): Set[String] =
    if (node.children.isEmpty) node.label.toSet
    else node.children.foldLeft(Set.empty[String])(_ ++ leavesUnder(_))

  /** The set of non-trivial splits (both sides ≥ 2 taxa) induced by the tree's
    * edges, each represented orientation-independently as the unordered pair of
    * its two sides.
    */
  private def nonTrivialSplits(tree: NewickTree, taxa: Set[String]): Set[Set[Set[String]]] = {
    def walk(node: NewickTree, isRoot: Boolean): Vector[Set[String]] = {
      val here = if (isRoot) Vector.empty else Vector(leavesUnder(node))
      here ++ node.children.flatMap(child => walk(child, isRoot = false))
    }
    walk(tree, isRoot = true)
      .filter(side => side.size >= 2 && (taxa.size - side.size) >= 2)
      .map(side => Set(side, taxa.diff(side)))
      .toSet
  }

  describe("CharacterBasedPhylogeny.build") {
    it("reproduces the unrooted tree of the canonical Rosalind sample") {
      val tree     = CharacterBasedPhylogeny.build(problem(sampleTaxa, sampleRows))
      val taxaSet  = sampleTaxa.toSet
      val expected = Set(
        Set(Set("cat", "rabbit"), Set("dog", "elephant", "mouse", "rat")),
        Set(Set("elephant", "mouse", "rat"), Set("cat", "dog", "rabbit")),
        Set(Set("elephant", "mouse"), Set("cat", "dog", "rabbit", "rat"))
      )
      nonTrivialSplits(tree, taxaSet) shouldBe expected
    }

    it("places every taxon exactly once as a leaf and adds no other labels") {
      val tree = CharacterBasedPhylogeny.build(problem(sampleTaxa, sampleRows))
      tree.labels shouldBe sampleTaxa.toSet
      // each taxon appears exactly once
      def leaves(n: NewickTree): Vector[String] =
        if (n.children.isEmpty) n.label.toVector else n.children.flatMap(leaves)
      leaves(tree).sorted shouldBe sampleTaxa.sorted
    }

    it("produces a star tree when there are no non-trivial characters") {
      val tree = CharacterBasedPhylogeny.build(problem(sampleTaxa, Vector.empty))
      tree.children should have size sampleTaxa.size
      tree.children.foreach(_.children shouldBe empty)
      nonTrivialSplits(tree, sampleTaxa.toSet) shouldBe empty
    }

    it("ignores trivial (all-equal) character rows") {
      val tree = CharacterBasedPhylogeny.build(problem(sampleTaxa, Vector("000000", "111111")))
      nonTrivialSplits(tree, sampleTaxa.toSet) shouldBe empty
    }
  }
}
