package bio.algorithms.protein

import bio.domain.protein.ProteinMotif
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MotifSearchSpec extends AnyFunSpec with Matchers {

  private val motif: ProteinMotif =
    ProteinMotif.parse("N{P}[ST]{P}").getOrElse(fail("invalid motif"))

  private def locations(protein: String): Vector[Int] =
    MotifSearch.findLocations(motif, protein)

  describe("MotifSearch.findLocations") {
    it("finds all motif occurrences including overlaps") {
      locations("NQSANQTA") shouldBe Vector(1, 5)
    }

    it("returns no positions when the motif is absent") {
      locations("AAAA") shouldBe Vector.empty[Int]
    }

    it("respects the negated and grouped positions") {
      locations("NPSA") shouldBe Vector.empty[Int]
    }

    it("returns no positions when the protein is shorter than the motif") {
      locations("NQ") shouldBe Vector.empty[Int]
    }
  }
}
