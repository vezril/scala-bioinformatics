package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FullSpectrumProblemSpec extends AnyFunSpec with Matchers {

  private val canonical = Vector(
    1988.21104821, 610.391039105, 738.485999105, 766.492149105, 863.544909105,
    867.528589105, 992.587499105, 995.623549105, 1120.6824591, 1124.6661391,
    1221.7188991, 1249.7250491, 1377.8200091
  )

  describe("FullSpectrumProblem.from") {
    it("accepts a valid mass list, preserving the masses") {
      FullSpectrumProblem.from(canonical).map(_.masses) shouldBe Right(canonical)
    }

    it("rejects a list whose size is not 2n+3") {
      FullSpectrumProblem.from(Vector(1.0, 2.0, 3.0, 4.0)) shouldBe Left(
        FullSpectrumProblemError.InvalidSize(4)
      )
    }

    it("rejects a list smaller than the minimum") {
      FullSpectrumProblem.from(Vector(1.0, 2.0, 3.0)) shouldBe Left(
        FullSpectrumProblemError.InvalidSize(3)
      )
    }

    it("rejects a non-positive mass") {
      FullSpectrumProblem.from(Vector(5.0, -1.0, 2.0, 3.0, 4.0)) shouldBe Left(
        FullSpectrumProblemError.NonPositiveMass(1, -1.0)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.protein.FullSpectrumProblem(Vector(1.0, 2.0, 3.0, 4.0, 5.0))""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.FullSpectrumProblem.from(Vector(1.0, 2.0, 3.0, 4.0, 5.0)).toOption.get.copy()"""
      )
    }
  }
}
