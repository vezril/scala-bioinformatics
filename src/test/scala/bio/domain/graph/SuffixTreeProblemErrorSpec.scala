package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SuffixTreeProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("SuffixTreeProblemError") {
    it("constructs SequenceTooLong carrying length and max") {
      val err: SuffixTreeProblemError = SuffixTreeProblemError.SequenceTooLong(1001, 1000)
      err shouldBe SuffixTreeProblemError.SequenceTooLong(1001, 1000)
    }
  }
}
