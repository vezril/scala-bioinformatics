package bio.domain.matrix

import bio.domain.nucleic.{DnaNucleotide, DnaString}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ConsensusProfileResultSpec extends AnyFunSpec with Matchers {

  describe("ConsensusProfileResult") {
    it("exposes consensus and profile fields") {
      val dna = DnaString.from("ATGCAACT").getOrElse(sys.error("invalid DnaString fixture"))
      val pm = ProfileMatrix
        .from(
          Map[DnaNucleotide, Vector[Int]](
            DnaNucleotide.A -> Vector(5, 1, 0, 0, 5, 5, 0, 0),
            DnaNucleotide.C -> Vector(0, 0, 1, 4, 2, 0, 6, 1),
            DnaNucleotide.G -> Vector(1, 1, 6, 3, 0, 1, 0, 0),
            DnaNucleotide.T -> Vector(1, 5, 0, 0, 0, 1, 1, 6)
          )
        )
        .toOption
        .get
      val result = ConsensusProfileResult(dna, pm)
      result.consensus.value shouldBe "ATGCAACT"
      result.profile shouldBe pm
    }
  }
}
