package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MassMultisetErrorSpec extends AnyFunSpec with Matchers {

  describe("MassMultisetError") {
    it("constructs EmptyMultiset, TooManyMasses, and NonPositiveMass as MassMultisetError subtypes") {
      val empty: MassMultisetError  = MassMultisetError.EmptyMultiset
      val many: MassMultisetError   = MassMultisetError.TooManyMasses(201, 200)
      val nonPos: MassMultisetError = MassMultisetError.NonPositiveMass(2, 0.0)

      empty shouldBe MassMultisetError.EmptyMultiset
      many shouldBe MassMultisetError.TooManyMasses(201, 200)
      nonPos shouldBe MassMultisetError.NonPositiveMass(2, 0.0)
    }
  }
}
