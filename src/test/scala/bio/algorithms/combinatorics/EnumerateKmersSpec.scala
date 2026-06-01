package bio.algorithms.combinatorics

import bio.domain.combinatorics.KmerEnumerationProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EnumerateKmersSpec extends AnyFunSpec with Matchers {

  private def problem(alphabet: Vector[Char], length: Int): KmerEnumerationProblem =
    KmerEnumerationProblem.from(alphabet, length).toOption.get

  describe("EnumerateKmers.enumerate") {
    it("enumerates the canonical sample in lexicographic order") {
      val result = EnumerateKmers.enumerate(problem(Vector('A', 'C', 'G', 'T'), 2))
      result.kmers shouldBe Vector(
        "AA", "AC", "AG", "AT",
        "CA", "CC", "CG", "CT",
        "GA", "GC", "GG", "GT",
        "TA", "TC", "TG", "TT"
      )
    }

    it("produces exactly k^n k-mers each of width n") {
      val result = EnumerateKmers.enumerate(problem(Vector('A', 'C', 'G', 'T'), 3))
      result.kmers.size shouldBe 64
      all(result.kmers.map(_.length)) shouldBe 3
    }

    it("returns the alphabet symbols themselves for length 1") {
      val result = EnumerateKmers.enumerate(problem(Vector('A', 'C', 'G', 'T'), 1))
      result.kmers shouldBe Vector("A", "C", "G", "T")
    }

    it("repeats a single-symbol alphabet to fill the width") {
      val result = EnumerateKmers.enumerate(problem(Vector('A'), 3))
      result.kmers shouldBe Vector("AAA")
    }
  }
}
