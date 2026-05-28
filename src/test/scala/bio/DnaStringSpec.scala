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

    it("accepts a string of 1001 characters (previously over the old 1000 cap)") {
      val justOverOld = "A" * 1001
      DnaString.from(justOverOld) shouldBe a[Right[_, _]]
    }

    it("accepts a string of exactly 1000 characters") {
      val boundary = "ACGT" * 250
      DnaString.from(boundary) shouldBe a[Right[_, _]]
    }

    it("accepts a 99977-character DNA (the Rosalind KMP dataset size)") {
      val kmpSize = "A" * 99977
      DnaString.from(kmpSize) shouldBe a[Right[_, _]]
    }

    it("accepts a string of exactly 100000 characters (the new cap)") {
      val boundary = "ACGT" * 25000
      DnaString.from(boundary) shouldBe a[Right[_, _]]
    }

    it("rejects a string exceeding 100000 characters") {
      val tooLong = "A" * 100001
      DnaString.from(tooLong) shouldBe Left(SequenceError.ExceedsMaxLength(100001))
    }

    it("rejects RNA-specific U character") {
      DnaString.from("ACGU") shouldBe Left(SequenceError.InvalidCharacter('U'))
    }
  }
}
