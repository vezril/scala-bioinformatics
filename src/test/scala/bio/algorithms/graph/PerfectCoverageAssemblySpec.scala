package bio.algorithms.graph

import bio.domain.graph.PerfectCoverageProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PerfectCoverageAssemblySpec extends AnyFunSpec with Matchers {

  private def problem(reads: String*): PerfectCoverageProblem =
    PerfectCoverageProblem
      .from(reads.toVector.map(s => DnaString.from(s).toOption.get))
      .toOption
      .get

  /** True when `a` is some rotation of `b` (same circular string / necklace). */
  private def isRotationOf(a: String, b: String): Boolean =
    a.length == b.length && (b + b).contains(a)

  describe("PerfectCoverageAssembly.assemble") {
    it("reconstructs the canonical sample chromosome as ACAGATT") {
      val result =
        PerfectCoverageAssembly.assemble(
          problem("ATTAC", "TACAG", "GATTA", "ACAGA", "CAGAT", "TTACA", "AGATT")
        )
      result.value shouldBe "ACAGATT"
    }

    it("produces a rotation of the Rosalind sample output GATTACA") {
      val result =
        PerfectCoverageAssembly.assemble(
          problem("ATTAC", "TACAG", "GATTA", "ACAGA", "CAGAT", "TTACA", "AGATT")
        )
      isRotationOf(result.value, "GATTACA") shouldBe true
    }

    it("reconstructs a length-1 chromosome from a single self-looping read") {
      PerfectCoverageAssembly.assemble(problem("AA")).value shouldBe "A"
    }

    it("reconstructs a length-2 chromosome from a two-read cycle") {
      PerfectCoverageAssembly.assemble(problem("AT", "TA")).value shouldBe "AT"
    }

    it("is unaffected by duplicate reads") {
      val result =
        PerfectCoverageAssembly.assemble(
          problem("ATTAC", "TACAG", "GATTA", "ACAGA", "CAGAT", "TTACA", "AGATT", "GATTA")
        )
      result.value shouldBe "ACAGATT"
    }
  }
}
