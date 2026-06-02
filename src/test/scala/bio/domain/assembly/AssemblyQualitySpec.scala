package bio.domain.assembly

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AssemblyQualitySpec extends AnyFunSpec with Matchers {

  describe("AssemblyQuality") {
    it("carries the N50 and N75 values") {
      val quality = AssemblyQuality(7, 6)
      quality.n50 shouldBe 7
      quality.n75 shouldBe 6
    }

    it("formats as the two values separated by a single space") {
      AssemblyQuality(7, 6).format shouldBe "7 6"
    }
  }
}
