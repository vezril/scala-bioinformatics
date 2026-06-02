package bio.algorithms.genetics

import bio.domain.genetics.SexLinkedProblem
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SexLinkedInheritanceSpec extends AnyFunSpec with Matchers {

  private def carriers(ds: Double*): Vector[Double] =
    SexLinkedInheritance
      .carrierProbabilities(
        SexLinkedProblem(
          ds.iterator
            .map(d => Probability.from(d).getOrElse(sys.error(s"invalid Probability: $d")))
            .toVector
        )
      )
      .values

  describe("SexLinkedInheritance.carrierProbabilities") {
    it("computes the canonical Rosalind SEXL sample") {
      val r = carriers(0.1, 0.5, 0.8)
      r should have size 3
      r(0) shouldBe 0.18 +- 0.001
      r(1) shouldBe 0.5 +- 0.001
      r(2) shouldBe 0.32 +- 0.001
    }

    it("yields zero carriers at the allele-frequency extremes") {
      carriers(0.0, 1.0) shouldBe Vector(0.0, 0.0)
    }

    it("yields the maximum carrier probability at frequency one half") {
      carriers(0.5).head shouldBe 0.5 +- 0.001
    }

    it("returns an empty result for an empty array") {
      carriers() shouldBe empty
    }
  }
}
