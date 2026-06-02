package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UnrootedBinaryTreesProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("UnrootedBinaryTreesProblemError") {
    it("constructs TooFewTaxa carrying count and min") {
      val err: UnrootedBinaryTreesProblemError =
        UnrootedBinaryTreesProblemError.TooFewTaxa(2, 3)
      err shouldBe UnrootedBinaryTreesProblemError.TooFewTaxa(2, 3)
    }

    it("constructs TooManyTaxa carrying count and max") {
      val err: UnrootedBinaryTreesProblemError =
        UnrootedBinaryTreesProblemError.TooManyTaxa(11, 10)
      err shouldBe UnrootedBinaryTreesProblemError.TooManyTaxa(11, 10)
    }

    it("constructs DuplicateTaxon carrying the name") {
      val err: UnrootedBinaryTreesProblemError =
        UnrootedBinaryTreesProblemError.DuplicateTaxon("cat")
      err shouldBe UnrootedBinaryTreesProblemError.DuplicateTaxon("cat")
    }
  }
}
