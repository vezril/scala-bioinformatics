package bio.domain.matrix

import bio.domain.nucleic.DnaString
import bio.parsing.FastaRecord
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ConsensusProfileProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  private def rec(id: String, sequence: String): FastaRecord =
    FastaRecord(id, dna(sequence))

  private val rosalindSample: Vector[FastaRecord] = Vector(
    rec("Rosalind_1", "ATCCAGCT"),
    rec("Rosalind_2", "GGGCAACT"),
    rec("Rosalind_3", "ATGGATCT"),
    rec("Rosalind_4", "AAGCAACC"),
    rec("Rosalind_5", "TTGGAACT"),
    rec("Rosalind_6", "ATGCCATT"),
    rec("Rosalind_7", "ATGGCACT")
  )

  describe("ConsensusProfileProblem.from") {
    it("accepts the Rosalind sample (seven equal-length records)") {
      val problem = ConsensusProfileProblem.from(rosalindSample).toOption.get
      problem.records shouldBe rosalindSample
    }

    it("accepts a single record") {
      val records = Vector(rec("R1", "ACGT"))
      val problem = ConsensusProfileProblem.from(records).toOption.get
      problem.records shouldBe records
    }

    it("rejects empty input as EmptyInput") {
      ConsensusProfileProblem.from(Vector.empty) shouldBe
        Left(ConsensusProfileProblemError.EmptyInput)
    }

    it("rejects length-mismatched records as LengthMismatch carrying the offending lengths") {
      val records = Vector(
        rec("R1", "ACGTACGT"), // length 8
        rec("R2", "ACGTACG"),  // length 7
        rec("R3", "ACGTACGT")  // length 8
      )
      ConsensusProfileProblem.from(records) shouldBe
        Left(ConsensusProfileProblemError.LengthMismatch(Vector(8, 7, 8)))
    }

    it("checks EmptyInput before LengthMismatch") {
      ConsensusProfileProblem.from(Vector.empty) shouldBe
        Left(ConsensusProfileProblemError.EmptyInput)
    }
  }

  describe("ConsensusProfileProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.matrix.ConsensusProfileProblem(Vector.empty)""")
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.matrix.ConsensusProfileProblem
          |  .from(Vector(bio.parsing.FastaRecord("R1", bio.domain.nucleic.DnaString.from("A").toOption.get)))
          |  .toOption.get.copy(records = Vector.empty)""".stripMargin
      )
    }
  }
}
