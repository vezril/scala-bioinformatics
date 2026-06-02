package bio.algorithms.graph

import bio.domain.graph.{PatternTrieProblem, TrieEdge}
import bio.domain.nucleic.DnaNucleotide._
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class TrieConstructionSpec extends AnyFunSpec with Matchers {

  private def problem(ss: String*): PatternTrieProblem =
    PatternTrieProblem
      .from(ss.iterator.map(s => DnaString.from(s).getOrElse(sys.error(s"bad: $s"))).toVector)
      .getOrElse(sys.error("invalid PatternTrieProblem fixture"))

  private def edges(ss: String*): Vector[TrieEdge] =
    TrieConstruction.construct(problem(ss: _*)).edges

  describe("TrieConstruction.construct") {
    it("builds the canonical Rosalind TRIE sample, edges in creation order") {
      edges("ATAGA", "ATC", "GAT") shouldBe Vector(
        TrieEdge(1, 2, A),
        TrieEdge(2, 3, T),
        TrieEdge(3, 4, A),
        TrieEdge(4, 5, G),
        TrieEdge(5, 6, A),
        TrieEdge(3, 7, C),
        TrieEdge(1, 8, G),
        TrieEdge(8, 9, A),
        TrieEdge(9, 10, T)
      )
    }

    it("builds a single linear path for one pattern") {
      edges("AT") shouldBe Vector(TrieEdge(1, 2, A), TrieEdge(2, 3, T))
    }

    it("reuses a shared-prefix node and branches") {
      edges("AT", "AG") shouldBe Vector(
        TrieEdge(1, 2, A),
        TrieEdge(2, 3, T),
        TrieEdge(2, 4, G)
      )
    }

    it("produces no edges for an empty pattern collection") {
      TrieConstruction.construct(PatternTrieProblem.from(Vector.empty).toOption.get).edges shouldBe empty
    }
  }
}
