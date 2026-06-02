package bio.domain.genetics

import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SexLinkedProblemSpec extends AnyFunSpec with Matchers {

  private def probs(ds: Double*): Vector[Probability] =
    ds.iterator
      .map(d => Probability.from(d).getOrElse(sys.error(s"invalid Probability: $d")))
      .toVector

  describe("SexLinkedProblem") {
    it("wraps and exposes the male proportions") {
      val ps = probs(0.1, 0.5, 0.8)
      SexLinkedProblem(ps).maleProportions shouldBe ps
    }

    it("accepts an empty array") {
      SexLinkedProblem(Vector.empty).maleProportions shouldBe empty
    }
  }
}
