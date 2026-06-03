package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TransitionTransversionProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(fail(s"invalid DNA string: $s"))

  describe("TransitionTransversionProblem.from") {
    it("accepts two equal-length sequences within the bound") {
      val a = dna("ACGTACGTAC")
      val b = dna("TGCATGCATG")
      val result = TransitionTransversionProblem.from(a, b)
      result.map(_.first) shouldBe Right(a)
      result.map(_.second) shouldBe Right(b)
    }

    it("accepts two equal empty sequences") {
      val empty = dna("")
      TransitionTransversionProblem.from(empty, empty).map(_.first) shouldBe Right(empty)
    }

    it("rejects a sequence longer than the bound") {
      val long  = dna("A" * 1001)
      val other = dna("C" * 1001)
      TransitionTransversionProblem.from(long, other) shouldBe Left(
        TransitionTransversionProblemError.SequenceTooLong(1001, 1000)
      )
    }

    it("rejects sequences of unequal length") {
      TransitionTransversionProblem.from(dna("ACGT"), dna("ACGTA")) shouldBe Left(
        TransitionTransversionProblemError.LengthMismatch(4, 5)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.nucleic.TransitionTransversionProblem(
          |  bio.domain.nucleic.DnaString.from("AC").toOption.get,
          |  bio.domain.nucleic.DnaString.from("GT").toOption.get
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """TransitionTransversionProblem.from(
          |  DnaString.from("AC").toOption.get,
          |  DnaString.from("GT").toOption.get
          |).toOption.get.copy()""".stripMargin
      )
    }
  }
}
