package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class KmerEnumerationSpec extends AnyFunSpec with Matchers {

  describe("KmerEnumeration.format") {
    it("renders the k-mers one per line in order") {
      KmerEnumeration(Vector("AA", "AC", "CA", "CC")).format shouldBe
        "AA\nAC\nCA\nCC"
    }

    it("renders a single k-mer with no trailing newline") {
      KmerEnumeration(Vector("AAA")).format shouldBe "AAA"
    }
  }
}
