package bio.domain.analysis

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AlignmentBasedPhylogenySpec extends AnyFunSpec with Matchers {

  describe("AlignmentBasedPhylogeny (domain ADT)") {
    it("constructs with named fields") {
      val abp = AlignmentBasedPhylogeny(
        totalDistance = 8,
        internalAssignments = Vector(
          NamedSequence("robot", "AC"),
          NamedSequence("dog", "AC"),
          NamedSequence("rat", "AC"),
          NamedSequence("mouse", "TC"),
          NamedSequence("hamster", "AT")
        )
      )
      abp.totalDistance shouldBe 8
      abp.internalAssignments.size shouldBe 5
      abp.internalAssignments.head shouldBe NamedSequence("robot", "AC")
    }

    it("is value-equal when both fields match") {
      AlignmentBasedPhylogeny(8, Vector(NamedSequence("r", "A"))) shouldBe
        AlignmentBasedPhylogeny(8, Vector(NamedSequence("r", "A")))
    }

    it("supports structural sharing via copy") {
      val abp = AlignmentBasedPhylogeny(8, Vector(NamedSequence("r", "A")))
      abp.copy(totalDistance = 0).totalDistance shouldBe 0
    }
  }
}
