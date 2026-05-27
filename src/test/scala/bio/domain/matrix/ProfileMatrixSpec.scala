package bio.domain.matrix

import bio.domain.nucleic.DnaNucleotide
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProfileMatrixSpec extends AnyFunSpec with Matchers {

  private val rosalindCounts: Map[DnaNucleotide, Vector[Int]] = Map(
    DnaNucleotide.A -> Vector(5, 1, 0, 0, 5, 5, 0, 0),
    DnaNucleotide.C -> Vector(0, 0, 1, 4, 2, 0, 6, 1),
    DnaNucleotide.G -> Vector(1, 1, 6, 3, 0, 1, 0, 0),
    DnaNucleotide.T -> Vector(1, 5, 0, 0, 0, 1, 1, 6)
  )

  describe("ProfileMatrix.from") {
    it("accepts a complete 4-key map with equal-length vectors and exposes the accessors") {
      val pm = ProfileMatrix.from(rosalindCounts).toOption.get
      pm.a shouldBe Vector(5, 1, 0, 0, 5, 5, 0, 0)
      pm.c shouldBe Vector(0, 0, 1, 4, 2, 0, 6, 1)
      pm.g shouldBe Vector(1, 1, 6, 3, 0, 1, 0, 0)
      pm.t shouldBe Vector(1, 5, 0, 0, 0, 1, 1, 6)
      pm.width shouldBe 8
    }

    it("accepts a 4-key map with empty vectors (width-0 matrix)") {
      val counts = Map[DnaNucleotide, Vector[Int]](
        DnaNucleotide.A -> Vector.empty,
        DnaNucleotide.C -> Vector.empty,
        DnaNucleotide.G -> Vector.empty,
        DnaNucleotide.T -> Vector.empty
      )
      val pm = ProfileMatrix.from(counts).toOption.get
      pm.width shouldBe 0
    }

    it("rejects a map missing the G key as MissingNucleotide(G)") {
      val counts = Map[DnaNucleotide, Vector[Int]](
        DnaNucleotide.A -> Vector(1),
        DnaNucleotide.C -> Vector(1),
        DnaNucleotide.T -> Vector(1)
      )
      ProfileMatrix.from(counts) shouldBe
        Left(ProfileMatrixError.MissingNucleotide(DnaNucleotide.G))
    }

    it("rejects a map with one column shorter than the others as UnequalColumnLengths") {
      val counts = Map[DnaNucleotide, Vector[Int]](
        DnaNucleotide.A -> Vector(1, 1),
        DnaNucleotide.C -> Vector(1, 1),
        DnaNucleotide.G -> Vector(1),
        DnaNucleotide.T -> Vector(1, 1)
      )
      val expected = Map[DnaNucleotide, Int](
        DnaNucleotide.A -> 2,
        DnaNucleotide.C -> 2,
        DnaNucleotide.G -> 1,
        DnaNucleotide.T -> 2
      )
      ProfileMatrix.from(counts) shouldBe
        Left(ProfileMatrixError.UnequalColumnLengths(expected))
    }
  }

  describe("ProfileMatrix construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.matrix.ProfileMatrix(Map.empty)""")
    }
  }
}
