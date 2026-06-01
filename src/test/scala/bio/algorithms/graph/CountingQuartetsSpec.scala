package bio.algorithms.graph

import bio.domain.graph.{CountingQuartetsProblem, NewickTree}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CountingQuartetsSpec extends AnyFunSpec with Matchers {

  /** A star tree with `n` leaves labelled `t0 .. t(n-1)` — leaf count `n`,
    * sufficient to build a valid [[CountingQuartetsProblem]] (the count depends
    * only on `n`, not the topology).
    */
  private def problemOfSize(n: Int): CountingQuartetsProblem = {
    val leaves = (0 until n).toVector.map(i => NewickTree(Some(s"t$i"), Vector.empty))
    CountingQuartetsProblem
      .from(n, NewickTree(None, leaves))
      .getOrElse(sys.error(s"invalid CountingQuartetsProblem fixture for n=$n"))
  }

  describe("CountingQuartets.count") {
    it("reproduces the canonical sample count of 15 for n = 6") {
      CountingQuartets.count(problemOfSize(6)) shouldBe 15
    }

    it("yields a single quartet at the minimum leaf count n = 4") {
      CountingQuartets.count(problemOfSize(4)) shouldBe 1
    }

    it("applies the modulus when the count exceeds 1,000,000 (n = 100)") {
      // C(100, 4) = 3,921,225 -> 3,921,225 mod 1,000,000 = 921,225
      CountingQuartets.count(problemOfSize(100)) shouldBe 921225
    }

    it("stays within the residue range at the maximum leaf count n = 5000") {
      val result = CountingQuartets.count(problemOfSize(5000))
      result should (be >= 0 and be < 1000000)
    }
  }
}
