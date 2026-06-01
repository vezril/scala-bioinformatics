package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GenomeAssemblyProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("GenomeAssemblyProblemError") {
    it("provides EmptyReadCollection as a case object") {
      val err: GenomeAssemblyProblemError =
        GenomeAssemblyProblemError.EmptyReadCollection
      err shouldBe GenomeAssemblyProblemError.EmptyReadCollection
    }

    it("carries the count and the maximum in TooManyReads") {
      val err = GenomeAssemblyProblemError.TooManyReads(51, 50)
      err.count shouldBe 51
      err.max shouldBe 50
    }

    it("carries the index, length, and maximum in ReadTooLong") {
      val err = GenomeAssemblyProblemError.ReadTooLong(3, 1001, 1000)
      err.index shouldBe 3
      err.length shouldBe 1001
      err.max shouldBe 1000
    }
  }
}
