package bio.algorithms.graph

import bio.domain.graph.GenomeAssemblyReadsProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GenomeAssemblyReadsSpec extends AnyFunSpec with Matchers {

  private def problem(reads: String*): GenomeAssemblyReadsProblem =
    GenomeAssemblyReadsProblem
      .from(reads.toVector.map(s => DnaString.from(s).toOption.get))
      .toOption
      .get

  /** True when `a` is some rotation of `b` (same circular string / necklace). */
  private def isRotationOf(a: String, b: String): Boolean =
    a.length == b.length && (b + b).contains(a)

  /** Reverse complement of a raw DNA string, for assertion convenience. */
  private def rc(s: String): String =
    s.reverse.map {
      case 'A' => 'T'
      case 'T' => 'A'
      case 'C' => 'G'
      case 'G' => 'C'
    }

  describe("GenomeAssemblyReads.assemble") {
    it("reconstructs the canonical sample chromosome as AATCTGT") {
      val result =
        GenomeAssemblyReads.assemble(problem("AATCT", "TGTAA", "GATTA", "ACAGA"))
      result.value shouldBe "AATCTGT"
    }

    it("produces a rotation of a strand of the Rosalind sample GATTACA") {
      val result =
        GenomeAssemblyReads.assemble(problem("AATCT", "TGTAA", "GATTA", "ACAGA"))
      val onGattaca = isRotationOf(result.value, "GATTACA")
      val onReverse = isRotationOf(result.value, rc("GATTACA"))
      (onGattaca || onReverse) shouldBe true
    }

    it("is unaffected by duplicate reads") {
      val result =
        GenomeAssemblyReads.assemble(
          problem("AATCT", "TGTAA", "GATTA", "ACAGA", "GATTA")
        )
      result.value shouldBe "AATCTGT"
    }

    it("is unaffected by reads already supplied on both strands") {
      // rc("GATTA") == "TAATC"; adding it should not change the assembly.
      val result =
        GenomeAssemblyReads.assemble(
          problem("AATCT", "TGTAA", "GATTA", "ACAGA", "TAATC")
        )
      result.value shouldBe "AATCTGT"
    }
  }
}
