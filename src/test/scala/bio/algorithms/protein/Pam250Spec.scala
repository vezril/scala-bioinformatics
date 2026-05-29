package bio.algorithms.protein

import bio.domain.protein.AminoAcid
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class Pam250Spec extends AnyFunSpec with Matchers {

  describe("Pam250.score — self-substitution reference values") {
    it("score(A, A) == 2") { Pam250.score(AminoAcid.A, AminoAcid.A) shouldBe 2 }
    it("score(W, W) == 17") { Pam250.score(AminoAcid.W, AminoAcid.W) shouldBe 17 }
    it("score(C, C) == 12") { Pam250.score(AminoAcid.C, AminoAcid.C) shouldBe 12 }
    it("score(Y, Y) == 10") { Pam250.score(AminoAcid.Y, AminoAcid.Y) shouldBe 10 }
    it("score(L, L) == 6") { Pam250.score(AminoAcid.L, AminoAcid.L) shouldBe 6 }
    it("score(F, F) == 9") { Pam250.score(AminoAcid.F, AminoAcid.F) shouldBe 9 }
    it("score(P, P) == 6") { Pam250.score(AminoAcid.P, AminoAcid.P) shouldBe 6 }
  }

  describe("Pam250.score — cross-substitution reference values") {
    it("score(A, R) == -2") { Pam250.score(AminoAcid.A, AminoAcid.R) shouldBe -2 }
    it("score(W, C) == -8") { Pam250.score(AminoAcid.W, AminoAcid.C) shouldBe -8 }
    it("score(L, M) == 4") { Pam250.score(AminoAcid.L, AminoAcid.M) shouldBe 4 }
    it("score(F, Y) == 7") { Pam250.score(AminoAcid.F, AminoAcid.Y) shouldBe 7 }
    it("score(I, V) == 4") { Pam250.score(AminoAcid.I, AminoAcid.V) shouldBe 4 }
    it("score(L, I) == 2") { Pam250.score(AminoAcid.L, AminoAcid.I) shouldBe 2 }
  }

  describe("Pam250.score — matrix invariants") {
    it("is symmetric for every pair (a, b) ∈ AminoAcid.all × AminoAcid.all") {
      for {
        a <- AminoAcid.all
        b <- AminoAcid.all
      } withClue(s"score($a, $b) vs score($b, $a): ") {
        Pam250.score(a, b) shouldBe Pam250.score(b, a)
      }
    }
  }
}
