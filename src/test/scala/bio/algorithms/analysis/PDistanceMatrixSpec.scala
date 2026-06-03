package bio.algorithms.analysis

import bio.domain.analysis.DistanceMatrixProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PDistanceMatrixSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(fail(s"invalid DNA string: $s"))

  private def matrix(strings: String*): Vector[Vector[Double]] = {
    val problem = DistanceMatrixProblem
      .from(strings.toVector.map(dna))
      .getOrElse(fail("invalid problem"))
    PDistanceMatrix.compute(problem).rows
  }

  private val Tolerance = 0.001

  describe("PDistanceMatrix.compute") {
    it("matches the canonical Rosalind sample") {
      val result = matrix("TTTCCATTTA", "GATTCATTTC", "TTTCCATTTT", "GTTCCATTTA")
      val expected = Vector(
        Vector(0.0, 0.4, 0.1, 0.1),
        Vector(0.4, 0.0, 0.4, 0.3),
        Vector(0.1, 0.4, 0.0, 0.2),
        Vector(0.1, 0.3, 0.2, 0.0)
      )
      result.length shouldBe 4
      result.lazyZip(expected).foreach { case (row, exp) =>
        row.lazyZip(exp).foreach { case (got, want) => got shouldBe want +- Tolerance }
      }
    }

    it("yields a 1x1 zero matrix for a single string") {
      matrix("ACGT") shouldBe Vector(Vector(0.0))
    }

    it("always has a zero diagonal") {
      val result = matrix("ACGTACGT", "TTTTGGGG", "ACGTAAAA")
      result.indices.foreach(i => result(i)(i) shouldBe 0.0)
    }

    it("yields a zero matrix for all-empty strings") {
      matrix("", "") shouldBe Vector(Vector(0.0, 0.0), Vector(0.0, 0.0))
    }
  }
}
