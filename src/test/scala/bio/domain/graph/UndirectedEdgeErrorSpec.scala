package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class UndirectedEdgeErrorSpec extends AnyFunSpec with Matchers {

  describe("UndirectedEdgeError.SelfLoop") {
    it("carries the offending node") {
      UndirectedEdgeError.SelfLoop(5).node shouldBe 5
    }
  }

  describe("UndirectedEdgeError.NonPositiveU") {
    it("carries the offending value") {
      UndirectedEdgeError.NonPositiveU(0).value shouldBe 0
    }
  }

  describe("UndirectedEdgeError.NonPositiveV") {
    it("carries the offending value") {
      UndirectedEdgeError.NonPositiveV(-1).value shouldBe -1
    }
  }
}
