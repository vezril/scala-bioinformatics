package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SpectrumGraphProblemSpec extends AnyFunSpec with Matchers {

  private val canonical = Vector(
    3524.8542, 3623.5245, 3710.9335, 3841.974, 3929.00603,
    3970.0326, 4026.05879, 4057.0646, 4083.08025
  )

  describe("SpectrumGraphProblem.from") {
    it("accepts a valid mass list, preserving the masses") {
      SpectrumGraphProblem.from(canonical).map(_.masses) shouldBe Right(canonical)
    }

    it("rejects more than 100 masses") {
      val many = (1 to 101).map(_.toDouble).toVector
      SpectrumGraphProblem.from(many) shouldBe Left(
        SpectrumGraphProblemError.TooManyMasses(101, 100)
      )
    }

    it("rejects a non-positive mass") {
      SpectrumGraphProblem.from(Vector(10.0, -1.0)) shouldBe Left(
        SpectrumGraphProblemError.NonPositiveMass(1, -1.0)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.protein.SpectrumGraphProblem(Vector(10.0))""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.SpectrumGraphProblem.from(Vector(10.0)).toOption.get.copy()"""
      )
    }
  }
}
