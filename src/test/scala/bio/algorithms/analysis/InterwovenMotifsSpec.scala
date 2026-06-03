package bio.algorithms.analysis

import bio.domain.analysis.InterwovenMotifProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InterwovenMotifsSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(fail(s"invalid DNA string: $s"))

  private def matrix(text: String, patterns: String*): Vector[Vector[Int]] = {
    val problem = InterwovenMotifProblem
      .from(dna(text), patterns.toVector.map(dna))
      .getOrElse(fail("invalid problem"))
    InterwovenMotifs.compute(problem).rows
  }

  describe("InterwovenMotifs.compute") {
    it("matches the canonical Rosalind sample") {
      matrix("GACCACGGTT", "ACAG", "GT", "CCG") shouldBe Vector(
        Vector(0, 0, 1),
        Vector(0, 1, 0),
        Vector(1, 0, 0)
      )
    }

    it("counts a pattern interwoven with itself") {
      matrix("GACCACGGTT", "GT") shouldBe Vector(Vector(1))
    }

    it("forbids reuse of overlapping characters (disjoint coverage)") {
      // ACAG and CCG cannot be interwoven into GACCACAAAAGGTT (problem statement).
      matrix("GACCACAAAAGGTT", "ACAG", "CCG")(0)(1) shouldBe 0
    }

    it("requires an interleaving to cover a contiguous window exactly") {
      // ACAG and CCG cannot be interwoven into ACACG (problem statement).
      matrix("ACACG", "ACAG", "CCG")(0)(1) shouldBe 0
    }
  }
}
