package bio.domain.protein

import bio.domain.nucleic.RnaNucleotide
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GeneticCodeSpec extends AnyFunSpec with Matchers {

  import RnaNucleotide.{A, C, G, U}
  import CodonOutcome.{AminoAcidProduct, Stop}

  describe("GeneticCode.translate") {
    it("AUG codes for Methionine") {
      GeneticCode.translate(Codon(A, U, G)) shouldBe AminoAcidProduct(AminoAcid.M)
    }

    it("UUU codes for Phenylalanine") {
      GeneticCode.translate(Codon(U, U, U)) shouldBe AminoAcidProduct(AminoAcid.F)
    }

    it("UGG codes for Tryptophan (sole codon)") {
      GeneticCode.translate(Codon(U, G, G)) shouldBe AminoAcidProduct(AminoAcid.W)
    }

    it("UAA is a Stop codon") {
      GeneticCode.translate(Codon(U, A, A)) shouldBe Stop
    }

    it("UAG is a Stop codon") {
      GeneticCode.translate(Codon(U, A, G)) shouldBe Stop
    }

    it("UGA is a Stop codon") {
      GeneticCode.translate(Codon(U, G, A)) shouldBe Stop
    }

    it("is total over all 64 codons") {
      val bases = Vector(A, C, G, U)
      val allCodons = for {
        x <- bases
        y <- bases
        z <- bases
      } yield Codon(x, y, z)
      allCodons should have size 64
      val outcomes: Vector[CodonOutcome] = allCodons.map(GeneticCode.translate)
      outcomes should have size 64
      outcomes.foreach { o =>
        o match {
          case _: AminoAcidProduct => succeed
          case Stop                => succeed
        }
      }
    }

    it("has exactly 3 Stop codons (UAA, UAG, UGA)") {
      val bases = Vector(A, C, G, U)
      val stopCodons = for {
        x <- bases; y <- bases; z <- bases
        codon = Codon(x, y, z)
        if GeneticCode.translate(codon) == Stop
      } yield codon
      stopCodons should contain theSameElementsAs Vector(
        Codon(U, A, A),
        Codon(U, A, G),
        Codon(U, G, A)
      )
    }

    it("translates a sample of canonical mappings correctly") {
      // Spot check covering each of the four base groups
      GeneticCode.translate(Codon(C, U, U)) shouldBe AminoAcidProduct(AminoAcid.L)
      GeneticCode.translate(Codon(G, U, U)) shouldBe AminoAcidProduct(AminoAcid.V)
      GeneticCode.translate(Codon(C, C, C)) shouldBe AminoAcidProduct(AminoAcid.P)
      GeneticCode.translate(Codon(A, A, A)) shouldBe AminoAcidProduct(AminoAcid.K)
      GeneticCode.translate(Codon(G, G, G)) shouldBe AminoAcidProduct(AminoAcid.G)
      GeneticCode.translate(Codon(A, G, U)) shouldBe AminoAcidProduct(AminoAcid.S)
      GeneticCode.translate(Codon(A, G, A)) shouldBe AminoAcidProduct(AminoAcid.R)
      GeneticCode.translate(Codon(U, G, C)) shouldBe AminoAcidProduct(AminoAcid.C)
      GeneticCode.translate(Codon(U, A, C)) shouldBe AminoAcidProduct(AminoAcid.Y)
      GeneticCode.translate(Codon(C, A, U)) shouldBe AminoAcidProduct(AminoAcid.H)
      GeneticCode.translate(Codon(A, A, U)) shouldBe AminoAcidProduct(AminoAcid.N)
      GeneticCode.translate(Codon(G, A, U)) shouldBe AminoAcidProduct(AminoAcid.D)
      GeneticCode.translate(Codon(C, A, A)) shouldBe AminoAcidProduct(AminoAcid.Q)
      GeneticCode.translate(Codon(G, A, A)) shouldBe AminoAcidProduct(AminoAcid.E)
      GeneticCode.translate(Codon(A, C, U)) shouldBe AminoAcidProduct(AminoAcid.T)
      GeneticCode.translate(Codon(G, C, U)) shouldBe AminoAcidProduct(AminoAcid.A)
      GeneticCode.translate(Codon(A, U, U)) shouldBe AminoAcidProduct(AminoAcid.I)
    }
  }
}
