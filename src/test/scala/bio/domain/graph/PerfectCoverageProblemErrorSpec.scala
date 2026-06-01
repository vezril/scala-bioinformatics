package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PerfectCoverageProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("PerfectCoverageProblemError") {
    it("exposes an EmptyKmerCollection case object") {
      val err: PerfectCoverageProblemError = PerfectCoverageProblemError.EmptyKmerCollection
      err shouldBe PerfectCoverageProblemError.EmptyKmerCollection
    }

    it("exposes a KmerTooShort case carrying the index, length, and minimum") {
      val err: PerfectCoverageProblemError = PerfectCoverageProblemError.KmerTooShort(2, 1, 2)
      err shouldBe PerfectCoverageProblemError.KmerTooShort(2, 1, 2)
    }

    it("exposes a KmerTooLong case carrying the index, length, and maximum") {
      val err: PerfectCoverageProblemError = PerfectCoverageProblemError.KmerTooLong(3, 51, 50)
      err shouldBe PerfectCoverageProblemError.KmerTooLong(3, 51, 50)
    }

    it("exposes an InconsistentLength case carrying the index, length, and expected") {
      val err: PerfectCoverageProblemError =
        PerfectCoverageProblemError.InconsistentLength(1, 6, 5)
      err shouldBe PerfectCoverageProblemError.InconsistentLength(1, 6, 5)
    }
  }
}
