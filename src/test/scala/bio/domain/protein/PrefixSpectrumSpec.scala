package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PrefixSpectrumSpec extends AnyFunSpec with Matchers {

  private val Sample =
    Vector(3524.8542, 3710.9335, 3841.974, 3970.0326, 4057.0646)

  describe("PrefixSpectrum.from") {
    it("accepts the canonical Rosalind SPEC sample, preserving order") {
      val result = PrefixSpectrum.from(Sample)
      result.isRight shouldBe true
      result.toOption.get.weights shouldBe Sample
    }

    it("accepts a single positive weight") {
      PrefixSpectrum.from(Vector(42.0)).isRight shouldBe true
    }

    it("rejects an empty list as EmptySpectrum") {
      PrefixSpectrum.from(Vector.empty) shouldBe
        Left(PrefixSpectrumError.EmptySpectrum)
    }

    it("rejects more than 100 weights as TooManyWeights(101, 100)") {
      val weights = Vector.tabulate(101)(i => (i + 1).toDouble)
      PrefixSpectrum.from(weights) shouldBe
        Left(PrefixSpectrumError.TooManyWeights(101, 100))
    }

    it("rejects a non-positive weight as NonPositiveWeight(index, value)") {
      PrefixSpectrum.from(Vector(10.0, 0.0, 20.0)) shouldBe
        Left(PrefixSpectrumError.NonPositiveWeight(1, 0.0))
    }

    it("reports EmptySpectrum first (first-failure-wins)") {
      PrefixSpectrum.from(Vector.empty) shouldBe
        Left(PrefixSpectrumError.EmptySpectrum)
    }
  }

  describe("PrefixSpectrum construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.protein.PrefixSpectrum(Vector(1.0))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.PrefixSpectrum
          |  .from(Vector(1.0)).toOption.get.copy(weights = Vector(2.0))""".stripMargin
      )
    }
  }
}
