package bio.domain.matrix

import bio.domain.nucleic.DnaNucleotide
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProfileMatrixErrorSpec extends AnyFunSpec with Matchers {

  describe("ProfileMatrixError.MissingNucleotide") {
    it("carries the missing nucleotide") {
      ProfileMatrixError.MissingNucleotide(DnaNucleotide.G).missing shouldBe DnaNucleotide.G
    }
  }

  describe("ProfileMatrixError.UnequalColumnLengths") {
    it("carries the per-nucleotide lengths map") {
      val lengths = Map[DnaNucleotide, Int](
        DnaNucleotide.A -> 8,
        DnaNucleotide.C -> 8,
        DnaNucleotide.G -> 7,
        DnaNucleotide.T -> 8
      )
      ProfileMatrixError.UnequalColumnLengths(lengths).lengths shouldBe lengths
    }
  }
}
