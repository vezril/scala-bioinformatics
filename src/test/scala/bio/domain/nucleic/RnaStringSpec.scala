package bio.domain.nucleic

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RnaStringSpec extends AnyFunSpec with Matchers {

  describe("RnaString.from") {
    it("accepts a valid RNA string") {
      RnaString.from("ACGU") shouldBe a[Right[_, _]]
    }

    it("accepts an empty string") {
      RnaString.from("") shouldBe a[Right[_, _]]
    }

    it("rejects a string containing DNA-specific T") {
      RnaString.from("ACGT") shouldBe Left(SequenceError.InvalidCharacter('T'))
    }

    it("rejects a string with an invalid character") {
      RnaString.from("ACGUX") shouldBe Left(SequenceError.InvalidCharacter('X'))
    }

    it("accepts a string of 1001 characters (previously over the old 1000 cap)") {
      val justOverOld = "A" * 1001
      RnaString.from(justOverOld) shouldBe a[Right[_, _]]
    }

    it("accepts a string of exactly 1000 characters") {
      val boundary = "ACGU" * 250
      RnaString.from(boundary) shouldBe a[Right[_, _]]
    }

    it("accepts a 5000-character RNA (previously over the old 1000 cap)") {
      val mid = "ACGU" * 1250
      RnaString.from(mid) shouldBe a[Right[_, _]]
    }

    it("accepts a string of exactly 10000 characters (the new cap)") {
      val boundary = "ACGU" * 2500
      RnaString.from(boundary) shouldBe a[Right[_, _]]
    }

    it("rejects a string exceeding 10000 characters") {
      val tooLong = "A" * 10001
      RnaString.from(tooLong) shouldBe Left(SequenceError.ExceedsMaxLength(10001))
    }
  }
}
