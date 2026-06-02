package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MassMultisetSpec extends AnyFunSpec with Matchers {

  private val SampleS1 = Vector(186.07931, 287.12699, 548.20532, 580.18077,
    681.22845, 706.27446, 782.27613, 968.35544, 968.35544)

  describe("MassMultiset.from") {
    it("accepts the canonical sample multiset, preserving order and repeats") {
      MassMultiset.from(SampleS1).map(_.masses) shouldBe Right(SampleS1)
    }

    it("preserves a repeated mass") {
      val repeated = Vector(968.35544, 968.35544)
      MassMultiset.from(repeated).map(_.masses) shouldBe Right(repeated)
    }

    it("rejects an empty multiset") {
      MassMultiset.from(Vector.empty) shouldBe Left(MassMultisetError.EmptyMultiset)
    }

    it("rejects a multiset larger than 200 masses") {
      val tooMany = Vector.fill(201)(1.0)
      MassMultiset.from(tooMany) shouldBe Left(MassMultisetError.TooManyMasses(201, 200))
    }

    it("rejects a non-positive mass, reporting its index and value") {
      val withZero = Vector(1.0, 2.0, 0.0, 3.0)
      MassMultiset.from(withZero) shouldBe Left(MassMultisetError.NonPositiveMass(2, 0.0))
    }

    it("reports the first failure only — size before non-positive") {
      val tooManyWithNegative = Vector.fill(201)(1.0).updated(5, -4.0)
      MassMultiset.from(tooManyWithNegative) shouldBe Left(
        MassMultisetError.TooManyMasses(201, 200)
      )
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.protein.MassMultiset(Vector(1.0))""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.protein.MassMultiset.from(Vector(1.0)).toOption.get.copy(masses = Vector(2.0))"""
      )
    }
  }
}
