package bio.algorithms.graph

import bio.domain.graph.GenomeAssemblyProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GenomeAssemblySpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def problem(reads: Vector[String]): GenomeAssemblyProblem =
    GenomeAssemblyProblem
      .from(reads.map(dna))
      .getOrElse(sys.error("invalid GenomeAssemblyProblem fixture"))

  describe("GenomeAssembly.assemble") {
    it("assembles the canonical sample into the shortest superstring") {
      val reads  = Vector("ATTAGACCTG", "CCTGCCGGAA", "AGACCTGCCG", "GCCGGAATAC")
      val result = GenomeAssembly.assemble(problem(reads))
      result shouldBe defined
      result.get.value shouldBe "ATTAGACCTGCCGGAATAC"
    }

    it("contains every read as a substring and is no longer than their concatenation") {
      val reads      = Vector("ATTAGACCTG", "CCTGCCGGAA", "AGACCTGCCG", "GCCGGAATAC")
      val superstr   = GenomeAssembly.assemble(problem(reads)).get.value
      val concatized = reads.mkString
      reads.foreach(r => superstr should include(r))
      superstr.length should be <= concatized.length
    }

    it("assembles a single read to itself") {
      val result = GenomeAssembly.assemble(problem(Vector("ACGTACGT")))
      result shouldBe defined
      result.get.value shouldBe "ACGTACGT"
    }

    it("returns None when reads share no qualifying overlap") {
      val result = GenomeAssembly.assemble(problem(Vector("AAAAAA", "CCCCCC")))
      result shouldBe None
    }
  }
}
