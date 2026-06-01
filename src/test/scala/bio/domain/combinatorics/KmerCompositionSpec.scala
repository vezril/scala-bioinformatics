package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class KmerCompositionSpec extends AnyFunSpec with Matchers {

  describe("KmerComposition.format") {
    it("renders the counts space-separated in order") {
      KmerComposition(Vector(4, 1, 4, 3, 0, 1)).format shouldBe "4 1 4 3 0 1"
    }

    it("renders a single count with no surrounding spaces") {
      KmerComposition(Vector(7)).format shouldBe "7"
    }
  }
}
