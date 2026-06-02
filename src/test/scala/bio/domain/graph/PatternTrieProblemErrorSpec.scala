package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PatternTrieProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("PatternTrieProblemError") {
    it("constructs TooManyPatterns carrying size and max") {
      val err: PatternTrieProblemError =
        PatternTrieProblemError.TooManyPatterns(101, 100)
      err shouldBe PatternTrieProblemError.TooManyPatterns(101, 100)
    }

    it("constructs PatternTooLong carrying index, length, and max") {
      val err: PatternTrieProblemError =
        PatternTrieProblemError.PatternTooLong(0, 101, 100)
      err shouldBe PatternTrieProblemError.PatternTooLong(0, 101, 100)
    }

    it("constructs PrefixConflict carrying the prefix and containing indices") {
      val err: PatternTrieProblemError =
        PatternTrieProblemError.PrefixConflict(0, 1)
      err shouldBe PatternTrieProblemError.PrefixConflict(0, 1)
    }
  }
}
