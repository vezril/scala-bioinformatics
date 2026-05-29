package bio.algorithms.analysis

import bio.domain.analysis.{
  AlignmentBasedPhylogeny => Result,
  AlignmentBasedPhylogenyProblem,
  NamedSequence
}
import bio.domain.graph.NewickTree
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AlignmentBasedPhylogenySpec extends AnyFunSpec with Matchers {

  private def parseTree(s: String): NewickTree =
    NewickParser.parse(s).getOrElse(sys.error(s"invalid Newick fixture: $s"))

  private def fixture(treeStr: String, rows: Vector[NamedSequence]): AlignmentBasedPhylogenyProblem =
    AlignmentBasedPhylogenyProblem
      .from(parseTree(treeStr), rows)
      .getOrElse(sys.error(s"invalid problem fixture: $treeStr / $rows"))

  /** Per-column Hamming distance between two equal-length strings. */
  private def hamming(a: String, b: String): Int = {
    require(a.length == b.length, s"Hamming requires equal lengths: ${a.length} vs ${b.length}")
    var i      = 0
    var differ = 0
    while (i < a.length) {
      if (a.charAt(i) != b.charAt(i)) differ += 1
      i += 1
    }
    differ
  }

  /** Sum of per-edge Hamming distance for the (tree, leafLabels, internalLabels)
    * triple. Walks the tree, for every parent–child edge summing
    * `hamming(parentLabel, childLabel)`.
    */
  private def sumOverEdges(
      tree: NewickTree,
      leafSequences: Map[String, String],
      internalSequences: Map[String, String]
  ): Int = {
    def seqOf(node: NewickTree): String =
      if (node.children.isEmpty) leafSequences(node.label.get)
      else internalSequences(node.label.get)
    def walk(node: NewickTree): Int = {
      val mySeq = seqOf(node)
      node.children.iterator.map { child =>
        val childSeq = seqOf(child)
        hamming(mySeq, childSeq) + walk(child)
      }.sum
    }
    walk(tree)
  }

  /** Assert the five `AlignmentBasedPhylogeny` invariants. */
  private def assertValidPhylogeny(
      result: Result,
      problem: AlignmentBasedPhylogenyProblem
  ): Unit = {
    // 1. One entry per internal node.
    val internalLabels = collectInternalLabels(problem.tree)
    result.internalAssignments.map(_.label).toSet shouldBe internalLabels

    // 2. Each assigned sequence has the alignment row length.
    val L = problem.alignment.head.sequence.length
    result.internalAssignments.foreach(_.sequence.length shouldBe L)

    // 3. Pre-order ordering — root first.
    result.internalAssignments.head.label shouldBe problem.tree.label.get

    // 4. Sum-of-edges with reported labels matches reported totalDistance.
    val leafMap     = problem.alignment.map(r => r.label -> r.sequence).toMap
    val internalMap = result.internalAssignments.map(r => r.label -> r.sequence).toMap
    sumOverEdges(problem.tree, leafMap, internalMap) shouldBe result.totalDistance
  }

  private def collectInternalLabels(t: NewickTree): Set[String] =
    if (t.children.isEmpty) Set.empty
    else t.children.foldLeft(t.label.toSet)(_ ++ collectInternalLabels(_))

  private val canonicalSampleTree =
    "(((ostrich,cat)rat,(duck,fly)mouse)dog,(elephant,pikachu)hamster)robot;"
  private val canonicalSampleAlignment = Vector(
    NamedSequence("ostrich", "AC"),
    NamedSequence("cat", "CA"),
    NamedSequence("duck", "T-"),
    NamedSequence("fly", "GC"),
    NamedSequence("elephant", "-T"),
    NamedSequence("pikachu", "AA")
  )

  describe("AlignmentBasedPhylogeny.solve") {
    it("returns totalDistance 8 with valid invariants for the canonical Rosalind ALPH sample") {
      val problem = fixture(canonicalSampleTree, canonicalSampleAlignment)
      val result  = AlignmentBasedPhylogeny.solve(problem)
      result.totalDistance shouldBe 8
      result.internalAssignments.size shouldBe 5
      assertValidPhylogeny(result, problem)
    }

    it("returns totalDistance 0 with identical labels when all leaves share the same sequence") {
      val problem = fixture(
        "((a,b)c,(d,e)f)g;",
        Vector(
          NamedSequence("a", "AC"),
          NamedSequence("b", "AC"),
          NamedSequence("d", "AC"),
          NamedSequence("e", "AC")
        )
      )
      val result = AlignmentBasedPhylogeny.solve(problem)
      result.totalDistance shouldBe 0
      result.internalAssignments.foreach { ns =>
        ns.sequence shouldBe "AC"
      }
      result.internalAssignments.map(_.label).toSet shouldBe Set("c", "f", "g")
      assertValidPhylogeny(result, problem)
    }

    it("returns totalDistance 1 for a two-leaf tree with one differing character") {
      val problem = fixture(
        "(a,b)r;",
        Vector(NamedSequence("a", "AC"), NamedSequence("b", "AT"))
      )
      val result = AlignmentBasedPhylogeny.solve(problem)
      result.totalDistance shouldBe 1
      result.internalAssignments.size shouldBe 1
      result.internalAssignments.head.label shouldBe "r"
      // Either leaf is a valid optimum.
      Set("AC", "AT") should contain(result.internalAssignments.head.sequence)
      assertValidPhylogeny(result, problem)
    }

    it("returns totalDistance 1 for a single-column gap-vs-non-gap input") {
      val problem = fixture(
        "(a,b)r;",
        Vector(NamedSequence("a", "A"), NamedSequence("b", "-"))
      )
      val result = AlignmentBasedPhylogeny.solve(problem)
      result.totalDistance shouldBe 1
      result.internalAssignments.head.label shouldBe "r"
      Set("A", "-") should contain(result.internalAssignments.head.sequence)
      assertValidPhylogeny(result, problem)
    }

    it("satisfies the sum-of-edges invariant for a mixed input") {
      val problem = fixture(
        "((a,b)x,(c,d)y)r;",
        Vector(
          NamedSequence("a", "AAC"),
          NamedSequence("b", "ATC"),
          NamedSequence("c", "GAC"),
          NamedSequence("d", "GTC")
        )
      )
      val result = AlignmentBasedPhylogeny.solve(problem)
      assertValidPhylogeny(result, problem)
    }
  }
}
