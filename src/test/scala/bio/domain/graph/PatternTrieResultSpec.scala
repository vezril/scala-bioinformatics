package bio.domain.graph

import bio.domain.nucleic.DnaNucleotide._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PatternTrieResultSpec extends AnyFunSpec with Matchers {

  describe("PatternTrie result") {
    it("exposes the edges") {
      val edges = Vector(TrieEdge(1, 2, A), TrieEdge(2, 3, T))
      PatternTrie(edges).edges shouldBe edges
    }

    it("formats edges as parent/child/symbol triples, one per line") {
      val edges = Vector(
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
      PatternTrie(edges).format shouldBe
        "1 2 A\n2 3 T\n3 4 A\n4 5 G\n5 6 A\n3 7 C\n1 8 G\n8 9 A\n9 10 T"
    }

    it("renders the empty trie as the empty string") {
      PatternTrie(Vector.empty).format shouldBe ""
    }
  }
}
