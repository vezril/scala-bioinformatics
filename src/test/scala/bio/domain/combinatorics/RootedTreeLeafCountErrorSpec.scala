package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RootedTreeLeafCountErrorSpec extends AnyFunSpec with Matchers {

  describe("RootedTreeLeafCountError") {
    it("constructs NonPositive carrying the value") {
      val err: RootedTreeLeafCountError = RootedTreeLeafCountError.NonPositive(0)
      err shouldBe RootedTreeLeafCountError.NonPositive(0)
    }

    it("constructs ExceedsMaximum carrying the value and max") {
      val err: RootedTreeLeafCountError = RootedTreeLeafCountError.ExceedsMaximum(1001, 1000)
      err shouldBe RootedTreeLeafCountError.ExceedsMaximum(1001, 1000)
    }
  }
}
