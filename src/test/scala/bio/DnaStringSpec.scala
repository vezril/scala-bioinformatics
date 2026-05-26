package bio

import bio.domain.nucleic.{SequenceError, DnaString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DnaStringSpec extends AnyFunSpec with Matchers {

  describe("DnaString.from") {
    it("accepts a valid DNA string") {
      DnaString.from("ACGT") shouldBe a[Right[_, _]]
    }

    it("accepts an empty string") {
      DnaString.from("") shouldBe a[Right[_, _]]
    }

    it("rejects a string with an invalid character") {
      DnaString.from("ACGTX") shouldBe Left(SequenceError.InvalidCharacter('X'))
    }

    it("rejects a string with a lowercase character") {
      DnaString.from("acgt") shouldBe Left(SequenceError.InvalidCharacter('a'))
    }

    it("rejects a string exceeding 1000 characters") {
      val long = "A" * 1001
      DnaString.from(long) shouldBe Left(SequenceError.ExceedsMaxLength(1001))
    }

    it("accepts a string of exactly 1000 characters") {
      val boundary = "ACGT" * 250
      DnaString.from(boundary) shouldBe a[Right[_, _]]
    }

    it("rejects RNA-specific U character") {
      DnaString.from("ACGU") shouldBe Left(SequenceError.InvalidCharacter('U'))
    }
  }
}
