package bio.algorithms.graph

import bio.domain.graph.MaximalRepeatProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class IdentifyMaximalRepeatsSpec extends AnyFunSpec with Matchers {

  private def repeats(s: String, minLength: Int): Vector[String] =
    IdentifyMaximalRepeats
      .find(
        MaximalRepeatProblem
          .from(DnaString.from(s).getOrElse(sys.error(s"bad DnaString: $s")), minLength)
          .getOrElse(sys.error("invalid MaximalRepeatProblem fixture"))
      )
      .repeats

  describe("IdentifyMaximalRepeats.find") {
    it("identifies the canonical Rosalind MREP sample (minimum length 20)") {
      val sample =
        "TAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTATTATATAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTAT"
      repeats(sample, 20) should contain theSameElementsAs Vector(
        "TAGAGATAGAATGGGTCCAGAGTTTTGTAATTTCCATGGGTCCAGAGTTTTGTAATTTAT",
        "ATGGGTCCAGAGTTTTGTAATTT"
      )
    }

    it("finds short maximal repeats with a small minimum length") {
      val r = repeats("TAGTTAGCGAGA", 2)
      r should contain("AG")
      r should contain("TAG")
    }

    it("returns nothing when no substring repeats") {
      repeats("ACGT", 1) shouldBe empty
    }

    it("excludes maximal repeats shorter than the minimum length") {
      repeats("TAGTTAGCGAGA", 20) shouldBe empty
    }
  }
}
