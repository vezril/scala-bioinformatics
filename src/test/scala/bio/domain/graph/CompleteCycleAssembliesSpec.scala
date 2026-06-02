package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CompleteCycleAssembliesSpec extends AnyFunSpec with Matchers {

  describe("CompleteCycleAssemblies.format") {
    it("renders the assembled strings one per line, in held order") {
      CompleteCycleAssemblies(Vector("ATG", "AGT")).format shouldBe "ATG\nAGT"
    }
  }
}
