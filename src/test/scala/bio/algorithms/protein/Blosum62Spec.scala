package bio.algorithms.protein

import bio.domain.protein.AminoAcid
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class Blosum62Spec extends AnyFunSpec with Matchers {

  describe("Blosum62.score — self-substitution reference values") {
    it("score(A, A) == 4") { Blosum62.score(AminoAcid.A, AminoAcid.A) shouldBe 4 }
    it("score(W, W) == 11") { Blosum62.score(AminoAcid.W, AminoAcid.W) shouldBe 11 }
    it("score(C, C) == 9") { Blosum62.score(AminoAcid.C, AminoAcid.C) shouldBe 9 }
    it("score(M, M) == 5") { Blosum62.score(AminoAcid.M, AminoAcid.M) shouldBe 5 }
    it("score(G, G) == 6") { Blosum62.score(AminoAcid.G, AminoAcid.G) shouldBe 6 }
  }

  describe("Blosum62.score — cross-substitution reference values") {
    it("score(A, R) == -1") { Blosum62.score(AminoAcid.A, AminoAcid.R) shouldBe -1 }
    it("score(W, C) == -2") { Blosum62.score(AminoAcid.W, AminoAcid.C) shouldBe -2 }
    it("score(L, M) == 2") { Blosum62.score(AminoAcid.L, AminoAcid.M) shouldBe 2 }
    it("score(P, Y) == -3") { Blosum62.score(AminoAcid.P, AminoAcid.Y) shouldBe -3 }
    it("score(N, D) == 1") { Blosum62.score(AminoAcid.N, AminoAcid.D) shouldBe 1 }
  }

  describe("Blosum62.score — matrix invariants") {
    it("is symmetric for every pair (a, b) ∈ AminoAcid.all × AminoAcid.all") {
      for {
        a <- AminoAcid.all
        b <- AminoAcid.all
      } withClue(s"score($a, $b) vs score($b, $a): ") {
        Blosum62.score(a, b) shouldBe Blosum62.score(b, a)
      }
    }
  }
}
