package bio.domain.analysis

import bio.domain.graph.NewickTree
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversingSubstitutionsProblemSpec extends AnyFunSpec with Matchers {

  private def parseTree(s: String): NewickTree =
    NewickParser.parse(s).getOrElse(sys.error(s"invalid Newick fixture: $s"))

  private val canonicalSampleTree = "(((ostrich,cat)rat,mouse)dog,elephant)robot;"

  private val canonicalSampleAlignment: Vector[NamedSequence] = Vector(
    NamedSequence("robot", "AATTG"),
    NamedSequence("dog", "GGGCA"),
    NamedSequence("mouse", "AAGAC"),
    NamedSequence("rat", "GTTGT"),
    NamedSequence("cat", "GAGGC"),
    NamedSequence("ostrich", "GTGTC"),
    NamedSequence("elephant", "AATTC")
  )

  describe("ReversingSubstitutionsProblem.from") {
    it("accepts the canonical Rosalind RSUB sample") {
      val result = ReversingSubstitutionsProblem.from(
        parseTree(canonicalSampleTree),
        canonicalSampleAlignment
      )
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.alignment shouldBe canonicalSampleAlignment
      problem.tree.label shouldBe Some("robot")
    }

    it("rejects an empty alignment as EmptyAlignment") {
      ReversingSubstitutionsProblem.from(
        parseTree("(a,b)r;"),
        Vector.empty
      ) shouldBe Left(ReversingSubstitutionsProblemError.EmptyAlignment)
    }

    it("rejects rows of differing lengths as LengthMismatch") {
      ReversingSubstitutionsProblem.from(
        parseTree("(a,b)r;"),
        Vector(
          NamedSequence("r", "AC"),
          NamedSequence("a", "AC"),
          NamedSequence("b", "ACC")
        )
      ) shouldBe Left(ReversingSubstitutionsProblemError.LengthMismatch(2, 3, 2))
    }

    it("rejects a sequence longer than 400 bp as SequenceTooLong") {
      val long = "A" * 401
      ReversingSubstitutionsProblem.from(
        parseTree("(a,b)r;"),
        Vector(
          NamedSequence("r", long),
          NamedSequence("a", long),
          NamedSequence("b", long)
        )
      ) shouldBe Left(ReversingSubstitutionsProblemError.SequenceTooLong(0, 401, 400))
    }

    it("rejects an invalid character (including '-') as InvalidCharacter") {
      ReversingSubstitutionsProblem.from(
        parseTree("(a,b)r;"),
        Vector(
          NamedSequence("r", "AC"),
          NamedSequence("a", "AC"),
          NamedSequence("b", "A-")
        )
      ) shouldBe Left(ReversingSubstitutionsProblemError.InvalidCharacter(2, 1, '-'))
    }

    it("rejects more than 100 alignment rows as TooManyStrings") {
      val rows = (1 to 101).map(i => NamedSequence(s"n$i", "A")).toVector
      ReversingSubstitutionsProblem.from(parseTree("(a,b)r;"), rows) shouldBe
        Left(ReversingSubstitutionsProblemError.TooManyStrings(101, 100))
    }

    it("rejects an unlabeled internal node as InternalNodeMissingLabel") {
      ReversingSubstitutionsProblem.from(
        parseTree("(a,b);"),
        Vector(NamedSequence("a", "A"), NamedSequence("b", "C"))
      ) shouldBe Left(ReversingSubstitutionsProblemError.InternalNodeMissingLabel)
    }

    it("rejects a non-binary internal node as NonBinaryInternalNode") {
      ReversingSubstitutionsProblem.from(
        parseTree("(a,b,c)r;"),
        Vector(
          NamedSequence("r", "A"),
          NamedSequence("a", "A"),
          NamedSequence("b", "A"),
          NamedSequence("c", "A")
        )
      ) shouldBe Left(ReversingSubstitutionsProblemError.NonBinaryInternalNode("r", 3))
    }

    it("rejects when node labels and alignment labels disagree as NodeLabelMismatch") {
      // Tree has node labels {a, b, r}; alignment has {a, b, x}.
      val result = ReversingSubstitutionsProblem.from(
        parseTree("(a,b)r;"),
        Vector(NamedSequence("a", "A"), NamedSequence("b", "C"), NamedSequence("x", "G"))
      )
      result shouldBe Left(
        ReversingSubstitutionsProblemError.NodeLabelMismatch(
          treeOnly = Set("r"),
          alignmentOnly = Set("x")
        )
      )
    }
  }

  describe("ReversingSubstitutionsProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.ReversingSubstitutionsProblem(
          |  parseTree("(a,b)r;"),
          |  Vector.empty
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.ReversingSubstitutionsProblem
          |  .from(parseTree("(a,b)r;"), Vector(
          |    NamedSequence("r", "A"), NamedSequence("a", "A"), NamedSequence("b", "A")
          |  )).toOption.get.copy(alignment = Vector.empty)""".stripMargin
      )
    }
  }
}
