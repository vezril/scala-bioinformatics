package bio.parsing

import bio.domain.nucleic.SequenceError
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FastaParserSpec extends AnyFunSpec with Matchers {

  private def ids(records: List[FastaRecord]): List[String]      = records.map(_.id)
  private def seqs(records: List[FastaRecord]): List[String]     = records.map(_.dna.value)

  private val RosalindSample: String =
    """>Rosalind_6404
      |CCTGCGGAAGATCGGCACTAGAATAGCCAGAACCGTTTCTCTGAGGCTTCCGGCCTTCCCTCCCACTAATAATTCTGAGG
      |>Rosalind_5959
      |CCATCGGTAGCGCATCCTTAGTCCAATTAAGTCCCTATCCAGGCGCTCCGCCGAAGGTCTATATCCATTTGTCAGCAGACACGC
      |>Rosalind_0808
      |CCACCCTCGTGGTATGGCTAGGCATTCAGGAACCGGAGAACGCTTCAGACCAGCCCGGACTGGGAACCTGCGGGCAGTAGGTGGAAT""".stripMargin

  describe("FastaParser.parse") {
    it("parses a single record") {
      val result = FastaParser.parse(">Rosalind_0001\nACGTACGT")
      result.map(ids) shouldBe Right(List("Rosalind_0001"))
      result.map(seqs) shouldBe Right(List("ACGTACGT"))
    }

    it("concatenates multi-line sequences") {
      val result = FastaParser.parse(">Rosalind_0001\nACGT\nACGT")
      result.map(seqs) shouldBe Right(List("ACGTACGT"))
    }

    it("parses the Rosalind sample into three records in order") {
      val result = FastaParser.parse(RosalindSample)
      result.map(ids) shouldBe Right(List("Rosalind_6404", "Rosalind_5959", "Rosalind_0808"))
    }

    it("trims trailing whitespace from the header id") {
      val result = FastaParser.parse(">Rosalind_0808 \nACGT")
      result.map(ids) shouldBe Right(List("Rosalind_0808"))
    }

    it("parses empty input to an empty list") {
      FastaParser.parse("") shouldBe Right(List.empty)
    }

    it("parses whitespace-only input to an empty list") {
      FastaParser.parse("   \n\n  ") shouldBe Right(List.empty)
    }

    it("rejects sequence content before any header") {
      FastaParser.parse("ACGT\n>Rosalind_0001\nACGT") shouldBe Left(FastaParseError.MissingHeader)
    }

    it("rejects a bare > header") {
      FastaParser.parse(">\nACGT") shouldBe Left(FastaParseError.EmptyId)
    }

    it("rejects a > header followed only by whitespace") {
      FastaParser.parse(">   \nACGT") shouldBe Left(FastaParseError.EmptyId)
    }

    it("rejects invalid DNA characters with the offending record's id") {
      FastaParser.parse(">Rosalind_0001\nACGTX") shouldBe
        Left(FastaParseError.InvalidDna("Rosalind_0001", SequenceError.InvalidCharacter('X')))
    }

    it("accepts a header with no following sequence (empty DnaString)") {
      val result = FastaParser.parse(">Rosalind_0001")
      result.map(ids) shouldBe Right(List("Rosalind_0001"))
      result.map(seqs) shouldBe Right(List(""))
    }
  }
}
