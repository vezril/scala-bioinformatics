package bio.parsing

import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FastaRecordSpec extends AnyFunSpec with Matchers {

  describe("FastaRecord") {
    it("exposes id and dna fields after construction") {
      val dna = DnaString.from("ACGT").getOrElse(sys.error("invalid"))
      val record = FastaRecord("Rosalind_0001", dna)
      record.id shouldBe "Rosalind_0001"
      record.dna.value shouldBe "ACGT"
    }
  }
}
