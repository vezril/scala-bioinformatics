package bio.algorithms.combinatorics

import bio.domain.combinatorics.SubsetUniverseSize
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SubsetsSpec extends AnyFunSpec with Matchers {

  private def size(n: Int): SubsetUniverseSize =
    SubsetUniverseSize.from(n).getOrElse(sys.error(s"invalid SubsetUniverseSize fixture: $n"))

  describe("Subsets.count") {
    it("produces 8 for the Rosalind sample (n=3)") {
      Subsets.count(size(3)) shouldBe 8
    }

    it("produces 2 for n=1 (empty set and the singleton)") {
      Subsets.count(size(1)) shouldBe 2
    }

    it("produces 1024 for n=10 (no modulo wrap)") {
      Subsets.count(size(10)) shouldBe 1024
    }

    it("produces 524288 for n=19 (last value before modulo first wraps)") {
      Subsets.count(size(19)) shouldBe 524288
    }

    it("produces 48576 for n=20 (first modulo wrap — 2^20 = 1_048_576)") {
      Subsets.count(size(20)) shouldBe 48576
    }

    it("produces 69376 for n=1000 (= 2^1000 mod 1_000_000)") {
      Subsets.count(size(1000)) shouldBe 69376
    }
  }
}
