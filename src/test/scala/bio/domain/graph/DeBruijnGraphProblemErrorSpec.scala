package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DeBruijnGraphProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("DeBruijnGraphProblemError") {
    it("exposes an EmptyKmerCollection case object") {
      val err: DeBruijnGraphProblemError = DeBruijnGraphProblemError.EmptyKmerCollection
      err shouldBe DeBruijnGraphProblemError.EmptyKmerCollection
    }

    it("exposes a TooManyKmers case carrying the count and the maximum") {
      val err: DeBruijnGraphProblemError = DeBruijnGraphProblemError.TooManyKmers(1001, 1000)
      err shouldBe DeBruijnGraphProblemError.TooManyKmers(1001, 1000)
    }

    it("exposes a KmerTooShort case carrying the index, length, and minimum") {
      val err: DeBruijnGraphProblemError = DeBruijnGraphProblemError.KmerTooShort(2, 1, 2)
      err shouldBe DeBruijnGraphProblemError.KmerTooShort(2, 1, 2)
    }

    it("exposes a KmerTooLong case carrying the index, length, and maximum") {
      val err: DeBruijnGraphProblemError = DeBruijnGraphProblemError.KmerTooLong(3, 51, 50)
      err shouldBe DeBruijnGraphProblemError.KmerTooLong(3, 51, 50)
    }

    it("exposes an InconsistentLength case carrying the index, length, and expected") {
      val err: DeBruijnGraphProblemError =
        DeBruijnGraphProblemError.InconsistentLength(1, 5, 4)
      err shouldBe DeBruijnGraphProblemError.InconsistentLength(1, 5, 4)
    }
  }
}
