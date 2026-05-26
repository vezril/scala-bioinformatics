package bio.algorithms.analysis

import bio.domain.analysis.GcContent
import bio.domain.nucleic.DnaString
import bio.parsing.FastaRecord
import bio.parsing.FastaParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class HighestGcSpec extends AnyFunSpec with Matchers {

  private val RosalindTolerance: Double = 1e-3

  private val RosalindSample: String =
    """>Rosalind_6404
      |CCTGCGGAAGATCGGCACTAGAATAGCCAGAACCGTTTCTCTGAGGCTTCCGGCCTTCCCTCCCACTAATAATTCTGAGG
      |>Rosalind_5959
      |CCATCGGTAGCGCATCCTTAGTCCAATTAAGTCCCTATCCAGGCGCTCCGCCGAAGGTCTATATCCATTTGTCAGCAGACACGC
      |>Rosalind_0808
      |CCACCCTCGTGGTATGGCTAGGCATTCAGGAACCGGAGAACGCTTCAGACCAGCCCGGACTGGGAACCTGCGGGCAGTAGGTGGAAT""".stripMargin

  private def dnaOf(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DNA in test fixture: $s"))

  describe("HighestGc.find") {
    it("identifies Rosalind_0808 with ~60.919540% on the Rosalind sample") {
      val records = FastaParser.parse(RosalindSample).getOrElse(sys.error("parse failed"))
      val (record, gc) = HighestGc.find(records).getOrElse(sys.error("no result"))
      record.id shouldBe "Rosalind_0808"
      gc.value shouldBe 60.919540 +- RosalindTolerance
    }

    it("returns None for an empty list") {
      HighestGc.find(List.empty) shouldBe None
    }

    it("returns a single record paired with its GC content") {
      val record = FastaRecord("only", dnaOf("GC"))
      val result = HighestGc.find(List(record))
      result.map(_._1) shouldBe Some(record)
      result.map(_._2.value) shouldBe Some(100.0)
    }

    it("resolves ties to the first record encountered") {
      val first  = FastaRecord("first", dnaOf("GCGC"))
      val second = FastaRecord("second", dnaOf("GCGC"))
      val (winner, _) = HighestGc.find(List(first, second)).getOrElse(sys.error("no result"))
      winner.id shouldBe "first"
    }

    it("returns the GcContent that matches re-running GcContent.of on the winner") {
      val records = FastaParser.parse(RosalindSample).getOrElse(sys.error("parse failed"))
      val (winner, gc) = HighestGc.find(records).getOrElse(sys.error("no result"))
      gc.value shouldBe GcContent.of(winner.dna).value
    }
  }
}
