package bio.domain.assembly

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ContigCollectionErrorSpec extends AnyFunSpec with Matchers {

  describe("ContigCollectionError") {
    it("exposes an EmptyContigCollection case object") {
      val err: ContigCollectionError = ContigCollectionError.EmptyContigCollection
      err shouldBe ContigCollectionError.EmptyContigCollection
    }

    it("exposes a TooManyContigs case carrying the count and maximum") {
      val err: ContigCollectionError = ContigCollectionError.TooManyContigs(1001, 1000)
      err shouldBe ContigCollectionError.TooManyContigs(1001, 1000)
    }

    it("exposes an EmptyContig case carrying the index") {
      val err: ContigCollectionError = ContigCollectionError.EmptyContig(1)
      err shouldBe ContigCollectionError.EmptyContig(1)
    }

    it("exposes an ExceedsTotalLength case carrying the total and maximum") {
      val err: ContigCollectionError = ContigCollectionError.ExceedsTotalLength(50001, 50000)
      err shouldBe ContigCollectionError.ExceedsTotalLength(50001, 50000)
    }
  }
}
