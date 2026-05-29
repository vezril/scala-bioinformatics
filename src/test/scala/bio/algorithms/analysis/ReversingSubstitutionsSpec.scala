package bio.algorithms.analysis

import bio.domain.analysis.{
  NamedSequence,
  ReversingSubstitution,
  ReversingSubstitutionsProblem
}
import bio.domain.graph.NewickTree
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversingSubstitutionsSpec extends AnyFunSpec with Matchers {

  private def parseTree(s: String): NewickTree =
    NewickParser.parse(s).getOrElse(sys.error(s"invalid Newick fixture: $s"))

  private def fixture(treeStr: String, rows: Vector[NamedSequence]): ReversingSubstitutionsProblem =
    ReversingSubstitutionsProblem
      .from(parseTree(treeStr), rows)
      .getOrElse(sys.error(s"invalid problem fixture"))

  describe("ReversingSubstitutions.findAll") {
    it("enumerates exactly the 5 expected reversions for the canonical Rosalind RSUB sample") {
      val problem = fixture(
        "(((ostrich,cat)rat,mouse)dog,elephant)robot;",
        Vector(
          NamedSequence("robot", "AATTG"),
          NamedSequence("dog", "GGGCA"),
          NamedSequence("mouse", "AAGAC"),
          NamedSequence("rat", "GTTGT"),
          NamedSequence("cat", "GAGGC"),
          NamedSequence("ostrich", "GTGTC"),
          NamedSequence("elephant", "AATTC")
        )
      )
      val result = ReversingSubstitutions.findAll(problem)

      val expected = Set(
        ReversingSubstitution("dog", "mouse", 1, 'A', 'G', 'A'),
        ReversingSubstitution("dog", "mouse", 2, 'A', 'G', 'A'),
        ReversingSubstitution("rat", "ostrich", 3, 'G', 'T', 'G'),
        ReversingSubstitution("rat", "cat", 3, 'G', 'T', 'G'),
        ReversingSubstitution("dog", "rat", 3, 'T', 'G', 'T')
      )

      result.toSet shouldBe expected
      result.size shouldBe expected.size // no duplicates
    }

    it("returns no reversions for a single-edge tree (insufficient depth)") {
      // (a)r; is the smallest legal tree but has one child, which is rejected
      // by NonBinaryInternalNode. Use a 2-leaf tree instead — even then no
      // reversion is possible because there is only one parent-child edge
      // depth from root to any leaf.
      val problem = fixture(
        "(a,b)r;",
        Vector(
          NamedSequence("r", "A"),
          NamedSequence("a", "G"),
          NamedSequence("b", "T")
        )
      )
      ReversingSubstitutions.findAll(problem) shouldBe Vector.empty
    }

    it("returns no reversions when every node has the same DNA string") {
      val problem = fixture(
        "(((a,b)x,c)y,(d,e)z)r;",
        Vector(
          NamedSequence("r", "ACGT"),
          NamedSequence("a", "ACGT"),
          NamedSequence("b", "ACGT"),
          NamedSequence("c", "ACGT"),
          NamedSequence("d", "ACGT"),
          NamedSequence("e", "ACGT"),
          NamedSequence("x", "ACGT"),
          NamedSequence("y", "ACGT"),
          NamedSequence("z", "ACGT")
        )
      )
      ReversingSubstitutions.findAll(problem) shouldBe Vector.empty
    }

    it("does NOT emit a reversion when a third symbol breaks the chain (binary tree)") {
      // Tree: ((d,e)y,f)r; with r=A, y=G, d=T, e=A, f=A.
      // At position 1:
      //   - edge (r,y): r=A, y=G ⇒ X=A, Y=G. DFS from y. Children d=T (neither
      //     X nor Y → stop), e=A (X! ⇒ reversion (y, e) since A→G→A).
      //     So WE DO get a reversion at this edge — (y, e).
      //   - edge (y,d): y=G, d=T ⇒ X=G, Y=T. d is a leaf, no children.
      //   - edge (y,e): y=G, e=A ⇒ X=G, Y=A. e is a leaf.
      //   - edge (r,f): r=A, f=A ⇒ no first sub.
      // So we expect exactly ONE reversion: (y, e, 1, A, G, A).
      // The key point: even though d=T sits between y=G and y's other child,
      // it does NOT cause us to miss the (y,e) reversion because the DFS
      // examines siblings independently — and it correctly does NOT emit a
      // reversion at position 1 starting from (r,y) along the path r→y→d
      // because d[0]=T breaks the chain.
      val problem = fixture(
        "((d,e)y,f)r;",
        Vector(
          NamedSequence("r", "A"),
          NamedSequence("y", "G"),
          NamedSequence("d", "T"),
          NamedSequence("e", "A"),
          NamedSequence("f", "A")
        )
      )
      val result = ReversingSubstitutions.findAll(problem).toSet
      result shouldBe Set(ReversingSubstitution("y", "e", 1, 'A', 'G', 'A'))
    }
  }
}
