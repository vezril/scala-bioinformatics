package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AminoAcidSpec extends AnyFunSpec with Matchers {

  describe("AminoAcid") {
    it("exposes the single-letter code on each case object") {
      AminoAcid.M.code shouldBe 'M'
      AminoAcid.A.code shouldBe 'A'
      AminoAcid.F.code shouldBe 'F'
      AminoAcid.W.code shouldBe 'W'
    }

    it("enumerates to exactly 20 distinct case objects with 20 distinct codes") {
      val all: Set[AminoAcid] = Set(
        AminoAcid.F, AminoAcid.L, AminoAcid.I, AminoAcid.V,
        AminoAcid.S, AminoAcid.P, AminoAcid.T, AminoAcid.A,
        AminoAcid.Y, AminoAcid.H, AminoAcid.N, AminoAcid.D,
        AminoAcid.Q, AminoAcid.K, AminoAcid.E, AminoAcid.C,
        AminoAcid.R, AminoAcid.G, AminoAcid.W, AminoAcid.M
      )
      all should have size 20
      all.map(_.code) should have size 20
    }

    it("provides every standard single-letter code in {F,L,I,V,S,P,T,A,Y,H,N,D,Q,K,E,C,R,G,W,M}") {
      val expectedCodes: Set[Char] =
        Set('F','L','I','V','S','P','T','A','Y','H','N','D','Q','K','E','C','R','G','W','M')
      val actualCodes: Set[Char] = Set(
        AminoAcid.F.code, AminoAcid.L.code, AminoAcid.I.code, AminoAcid.V.code,
        AminoAcid.S.code, AminoAcid.P.code, AminoAcid.T.code, AminoAcid.A.code,
        AminoAcid.Y.code, AminoAcid.H.code, AminoAcid.N.code, AminoAcid.D.code,
        AminoAcid.Q.code, AminoAcid.K.code, AminoAcid.E.code, AminoAcid.C.code,
        AminoAcid.R.code, AminoAcid.G.code, AminoAcid.W.code, AminoAcid.M.code
      )
      actualCodes shouldBe expectedCodes
    }
  }

  describe("AminoAcid monoisotopic masses") {
    it("reports the canonical monoisotopic mass of a residue") {
      AminoAcid.W.monoisotopicMass shouldBe 186.07931
    }

    it("matches a difference to the nearest residue (Q over K)") {
      AminoAcid.closestByMass(128.0586) shouldBe AminoAcid.Q
    }

    it("breaks the isobaric I/L tie deterministically by canonical order (L first)") {
      AminoAcid.closestByMass(113.08406) shouldBe AminoAcid.L
    }
  }

  describe("AminoAcid.fromChar") {
    it("lifts a valid single-letter code to its residue") {
      AminoAcid.fromChar('W') shouldBe Some(AminoAcid.W)
    }

    it("returns None for a non-amino-acid character") {
      AminoAcid.fromChar('B') shouldBe None
    }
  }
}
