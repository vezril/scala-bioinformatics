package bio.domain.matrix

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ConsensusProfileProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("ConsensusProfileProblemError.EmptyInput") {
    it("is a singleton (same reference each time)") {
      val a = ConsensusProfileProblemError.EmptyInput
      val b = ConsensusProfileProblemError.EmptyInput
      (a eq b) shouldBe true
    }
  }

  describe("ConsensusProfileProblemError.LengthMismatch") {
    it("carries the offending lengths in input order") {
      ConsensusProfileProblemError.LengthMismatch(Vector(8, 7, 8)).lengths shouldBe Vector(8, 7, 8)
    }
  }
}
