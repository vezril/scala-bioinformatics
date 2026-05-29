package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ReversingSubstitutionSpec extends AnyFunSpec with Matchers {

  describe("ReversingSubstitution") {
    it("constructs with named fields") {
      val rs = ReversingSubstitution(
        firstChangeSpecies = "dog",
        reversionSpecies = "mouse",
        position = 1,
        originalSymbol = 'A',
        substitutedSymbol = 'G',
        revertedSymbol = 'A'
      )
      rs.firstChangeSpecies shouldBe "dog"
      rs.reversionSpecies shouldBe "mouse"
      rs.position shouldBe 1
      rs.originalSymbol shouldBe 'A'
      rs.substitutedSymbol shouldBe 'G'
      rs.revertedSymbol shouldBe 'A'
    }

    it("is value-equal when all six fields match") {
      ReversingSubstitution("dog", "mouse", 1, 'A', 'G', 'A') shouldBe
        ReversingSubstitution("dog", "mouse", 1, 'A', 'G', 'A')
    }

    it("supports structural sharing via copy") {
      val rs = ReversingSubstitution("dog", "mouse", 1, 'A', 'G', 'A')
      rs.copy(position = 2).position shouldBe 2
      rs.copy(position = 2).firstChangeSpecies shouldBe "dog"
    }
  }
}
