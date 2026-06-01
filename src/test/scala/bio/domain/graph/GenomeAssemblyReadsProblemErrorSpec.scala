package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GenomeAssemblyReadsProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("GenomeAssemblyReadsProblemError") {
    it("exposes an EmptyReadCollection case object") {
      val err: GenomeAssemblyReadsProblemError =
        GenomeAssemblyReadsProblemError.EmptyReadCollection
      err shouldBe GenomeAssemblyReadsProblemError.EmptyReadCollection
    }

    it("exposes a ReadTooShort case carrying the index, length, and minimum") {
      val err: GenomeAssemblyReadsProblemError =
        GenomeAssemblyReadsProblemError.ReadTooShort(1, 1, 2)
      err shouldBe GenomeAssemblyReadsProblemError.ReadTooShort(1, 1, 2)
    }

    it("exposes a ReadTooLong case carrying the index, length, and maximum") {
      val err: GenomeAssemblyReadsProblemError =
        GenomeAssemblyReadsProblemError.ReadTooLong(3, 51, 50)
      err shouldBe GenomeAssemblyReadsProblemError.ReadTooLong(3, 51, 50)
    }

    it("exposes an InconsistentLength case carrying the index, length, and expected") {
      val err: GenomeAssemblyReadsProblemError =
        GenomeAssemblyReadsProblemError.InconsistentLength(2, 4, 5)
      err shouldBe GenomeAssemblyReadsProblemError.InconsistentLength(2, 4, 5)
    }
  }
}
