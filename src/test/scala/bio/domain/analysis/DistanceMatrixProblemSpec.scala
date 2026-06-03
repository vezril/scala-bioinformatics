package bio.domain.analysis

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DistanceMatrixProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(fail(s"invalid DNA string: $s"))

  describe("DistanceMatrixProblem.from") {
    it("accepts equal-length strings within the bounds") {
      val strings = Vector("TTTCCATTTA", "GATTCATTTC", "TTTCCATTTT", "GTTCCATTTA").map(dna)
      DistanceMatrixProblem.from(strings).map(_.strings) shouldBe Right(strings)
    }

    it("accepts an empty string list") {
      DistanceMatrixProblem.from(Vector.empty).map(_.strings) shouldBe Right(Vector.empty[DnaString])
    }

    it("rejects more than ten strings") {
      val strings = Vector.fill(11)(dna("ACGT"))
      DistanceMatrixProblem.from(strings) shouldBe Left(
        DistanceMatrixProblemError.TooManyStrings(11, 10)
      )
    }

    it("rejects a string longer than the bound") {
      val strings = Vector(dna("A" * 1001))
      DistanceMatrixProblem.from(strings) shouldBe Left(
        DistanceMatrixProblemError.StringTooLong(1001, 1000)
      )
    }

    it("rejects strings of unequal length") {
      val strings = Vector(dna("ACGT"), dna("ACGTA"))
      DistanceMatrixProblem.from(strings) shouldBe Left(
        DistanceMatrixProblemError.UnequalLengths(Vector(4, 5))
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.analysis.DistanceMatrixProblem(Vector.empty)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """DistanceMatrixProblem.from(Vector.empty).toOption.get.copy(strings = Vector.empty)"""
      )
    }
  }
}
