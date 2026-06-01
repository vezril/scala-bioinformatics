package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ConsistentCharacterTableSpec extends AnyFunSpec with Matchers {

  describe("ConsistentCharacterTable.format") {
    it("renders the rows one per line, in order") {
      ConsistentCharacterTable(Vector("000110", "100001", "100111")).format shouldBe
        "000110\n100001\n100111"
    }

    it("renders a single-row table as just that row") {
      ConsistentCharacterTable(Vector("101")).format shouldBe "101"
    }

    it("renders an empty table as the empty string") {
      ConsistentCharacterTable(Vector.empty).format shouldBe ""
    }
  }
}
