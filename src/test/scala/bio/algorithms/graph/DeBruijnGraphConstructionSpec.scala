package bio.algorithms.graph

import bio.domain.graph.{DeBruijnEdge, DeBruijnGraphProblem}
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DeBruijnGraphConstructionSpec extends AnyFunSpec with Matchers {

  private def problem(kmers: String*): DeBruijnGraphProblem =
    DeBruijnGraphProblem
      .from(kmers.toVector.map(s => DnaString.from(s).toOption.get))
      .toOption
      .get

  describe("DeBruijnGraphConstruction.construct") {
    it("builds the canonical sample graph with 9 ordered edges") {
      val result = DeBruijnGraphConstruction.construct(
        problem("TGAT", "CATG", "TCAT", "ATGC", "CATC", "CATC")
      )
      result.edges shouldBe Vector(
        DeBruijnEdge("ATC", "TCA"),
        DeBruijnEdge("ATG", "TGA"),
        DeBruijnEdge("ATG", "TGC"),
        DeBruijnEdge("CAT", "ATC"),
        DeBruijnEdge("CAT", "ATG"),
        DeBruijnEdge("GAT", "ATG"),
        DeBruijnEdge("GCA", "CAT"),
        DeBruijnEdge("TCA", "CAT"),
        DeBruijnEdge("TGA", "GAT")
      )
    }

    it("includes edges contributed by reverse complements (AAAA -> TTTT)") {
      val result = DeBruijnGraphConstruction.construct(problem("AAAA"))
      result.edges shouldBe Vector(
        DeBruijnEdge("AAA", "AAA"),
        DeBruijnEdge("TTT", "TTT")
      )
    }

    it("counts a reverse-complement palindrome once (ATAT)") {
      val result = DeBruijnGraphConstruction.construct(problem("ATAT"))
      result.edges shouldBe Vector(DeBruijnEdge("ATA", "TAT"))
    }

    it("does not produce duplicate edges from duplicate inputs (GGGG, GGGG)") {
      val result = DeBruijnGraphConstruction.construct(problem("GGGG", "GGGG"))
      result.edges shouldBe Vector(
        DeBruijnEdge("CCC", "CCC"),
        DeBruijnEdge("GGG", "GGG")
      )
    }
  }
}
