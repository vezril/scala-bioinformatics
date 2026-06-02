package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProteinMassSpec extends AnyFunSpec with Matchers {

  describe("ProteinMass.format") {
    it("renders the total mass to three decimal places") {
      ProteinMass(821.39192).format shouldBe "821.392"
    }
  }
}
