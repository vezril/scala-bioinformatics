package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InferredProteinSpec extends AnyFunSpec with Matchers {

  describe("InferredProtein.format") {
    it("renders the inferred protein's single-letter codes on one line") {
      val protein = ProteinString.from("WMQS").toOption.get
      InferredProtein(protein).format shouldBe "WMQS"
    }
  }
}
