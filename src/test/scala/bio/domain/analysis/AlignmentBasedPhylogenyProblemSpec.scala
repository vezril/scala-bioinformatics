package bio.domain.analysis

import bio.domain.graph.NewickTree
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AlignmentBasedPhylogenyProblemSpec extends AnyFunSpec with Matchers {

  private def parseTree(s: String): NewickTree =
    NewickParser.parse(s).getOrElse(sys.error(s"invalid Newick fixture: $s"))

  private val canonicalSampleTree =
    "(((ostrich,cat)rat,(duck,fly)mouse)dog,(elephant,pikachu)hamster)robot;"

  private val canonicalSampleAlignment: Vector[NamedSequence] = Vector(
    NamedSequence("ostrich", "AC"),
    NamedSequence("cat", "CA"),
    NamedSequence("duck", "T-"),
    NamedSequence("fly", "GC"),
    NamedSequence("elephant", "-T"),
    NamedSequence("pikachu", "AA")
  )

  describe("AlignmentBasedPhylogenyProblem.from") {
    it("accepts the canonical Rosalind ALPH sample") {
      val result = AlignmentBasedPhylogenyProblem.from(
        parseTree(canonicalSampleTree),
        canonicalSampleAlignment
      )
      result.isRight shouldBe true
      val problem = result.toOption.get
      problem.alignment shouldBe canonicalSampleAlignment
      problem.tree.label shouldBe Some("robot")
    }

    it("rejects an empty alignment as EmptyAlignment") {
      AlignmentBasedPhylogenyProblem.from(
        parseTree("(a,b)r;"),
        Vector.empty
      ) shouldBe Left(AlignmentBasedPhylogenyProblemError.EmptyAlignment)
    }

    it("rejects alignment rows of differing lengths as LengthMismatch") {
      AlignmentBasedPhylogenyProblem.from(
        parseTree("(a,b)r;"),
        Vector(NamedSequence("a", "AC"), NamedSequence("b", "ACC"))
      ) shouldBe Left(AlignmentBasedPhylogenyProblemError.LengthMismatch(1, 3, 2))
    }

    it("rejects a sequence longer than 300 bp as SequenceTooLong") {
      val long = "A" * 301
      AlignmentBasedPhylogenyProblem.from(
        parseTree("(a,b)r;"),
        Vector(NamedSequence("a", long), NamedSequence("b", long))
      ) shouldBe Left(AlignmentBasedPhylogenyProblemError.SequenceTooLong(0, 301, 300))
    }

    it("rejects an invalid character as InvalidCharacter") {
      AlignmentBasedPhylogenyProblem.from(
        parseTree("(a,b)r;"),
        Vector(NamedSequence("a", "AC"), NamedSequence("b", "AX"))
      ) shouldBe Left(AlignmentBasedPhylogenyProblemError.InvalidCharacter(1, 1, 'X'))
    }

    it("rejects an unlabeled internal node as InternalNodeMissingLabel") {
      // Root has no label
      AlignmentBasedPhylogenyProblem.from(
        parseTree("(a,b);"),
        Vector(NamedSequence("a", "A"), NamedSequence("b", "C"))
      ) shouldBe Left(AlignmentBasedPhylogenyProblemError.InternalNodeMissingLabel)
    }

    it("rejects a non-binary internal node as NonBinaryInternalNode") {
      AlignmentBasedPhylogenyProblem.from(
        parseTree("(a,b,c)r;"),
        Vector(
          NamedSequence("a", "A"),
          NamedSequence("b", "C"),
          NamedSequence("c", "G")
        )
      ) shouldBe Left(AlignmentBasedPhylogenyProblemError.NonBinaryInternalNode("r", 3))
    }

    it("rejects when leaf labels do not match alignment labels as LeafLabelMismatch") {
      val result = AlignmentBasedPhylogenyProblem.from(
        parseTree("((a,b)x,c)r;"),
        Vector(
          NamedSequence("a", "A"),
          NamedSequence("b", "C"),
          NamedSequence("d", "G") // tree expects `c`, alignment has `d`
        )
      )
      result shouldBe Left(
        AlignmentBasedPhylogenyProblemError.LeafLabelMismatch(
          treeOnly = Set("c"),
          alignmentOnly = Set("d")
        )
      )
    }
  }

  describe("AlignmentBasedPhylogenyProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.AlignmentBasedPhylogenyProblem(
          |  parseTree("(a,b)r;"),
          |  Vector(NamedSequence("a", "A"), NamedSequence("b", "C"))
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.AlignmentBasedPhylogenyProblem
          |  .from(parseTree("(a,b)r;"), Vector(NamedSequence("a", "A"), NamedSequence("b", "C")))
          |  .toOption.get.copy(alignment = Vector.empty)""".stripMargin
      )
    }
  }
}
