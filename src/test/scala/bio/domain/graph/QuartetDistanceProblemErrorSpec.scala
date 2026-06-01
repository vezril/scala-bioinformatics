package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class QuartetDistanceProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("QuartetDistanceProblemError") {
    it("exposes EmptyTaxa as a case object") {
      val err: QuartetDistanceProblemError = QuartetDistanceProblemError.EmptyTaxa
      err shouldBe QuartetDistanceProblemError.EmptyTaxa
    }

    it("carries the duplicated name in DuplicateTaxon") {
      QuartetDistanceProblemError.DuplicateTaxon("dog").name shouldBe "dog"
    }

    it("carries the tree index and offending taxon sets in TreeTaxaMismatch") {
      val err = QuartetDistanceProblemError.TreeTaxaMismatch(2, Set("d"), Set("e"))
      err.treeIndex shouldBe 2
      err.missing shouldBe Set("d")
      err.extra shouldBe Set("e")
    }
  }
}
