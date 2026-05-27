package bio.algorithms.matrix

import bio.domain.matrix.ConsensusProfileProblem
import bio.domain.nucleic.DnaString
import bio.parsing.FastaRecord
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ConsensusProfileSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  private def rec(id: String, sequence: String): FastaRecord =
    FastaRecord(id, dna(sequence))

  private def problem(records: Vector[FastaRecord]): ConsensusProfileProblem =
    ConsensusProfileProblem
      .from(records)
      .getOrElse(sys.error("invalid ConsensusProfileProblem in fixture"))

  private val rosalindSample: Vector[FastaRecord] = Vector(
    rec("Rosalind_1", "ATCCAGCT"),
    rec("Rosalind_2", "GGGCAACT"),
    rec("Rosalind_3", "ATGGATCT"),
    rec("Rosalind_4", "AAGCAACC"),
    rec("Rosalind_5", "TTGGAACT"),
    rec("Rosalind_6", "ATGCCATT"),
    rec("Rosalind_7", "ATGGCACT")
  )

  describe("ConsensusProfile.compute") {
    it("produces the canonical profile and consensus for the Rosalind sample") {
      val result = ConsensusProfile.compute(problem(rosalindSample))
      result.consensus.value shouldBe "ATGCAACT"
      result.profile.a shouldBe Vector(5, 1, 0, 0, 5, 5, 0, 0)
      result.profile.c shouldBe Vector(0, 0, 1, 4, 2, 0, 6, 1)
      result.profile.g shouldBe Vector(1, 1, 6, 3, 0, 1, 0, 0)
      result.profile.t shouldBe Vector(1, 5, 0, 0, 0, 1, 1, 6)
    }

    it("returns the single record as consensus with all-1/all-0 columns for single-record input") {
      val result = ConsensusProfile.compute(problem(Vector(rec("R1", "ACGT"))))
      result.consensus.value shouldBe "ACGT"
      result.profile.a shouldBe Vector(1, 0, 0, 0)
      result.profile.c shouldBe Vector(0, 1, 0, 0)
      result.profile.g shouldBe Vector(0, 0, 1, 0)
      result.profile.t shouldBe Vector(0, 0, 0, 1)
    }

    it("breaks ties alphabetically: A wins over C when both tied at count 1") {
      val result = ConsensusProfile.compute(problem(Vector(rec("R1", "A"), rec("R2", "C"))))
      result.consensus.value shouldBe "A"
    }

    it("breaks ties alphabetically: C wins over G when both tied at count 1") {
      val result = ConsensusProfile.compute(problem(Vector(rec("R1", "C"), rec("R2", "G"))))
      result.consensus.value shouldBe "C"
    }

    it("produces a profile whose width matches the input record length") {
      val result = ConsensusProfile.compute(problem(rosalindSample))
      result.profile.width shouldBe 8
    }

    it("produces column counts that sum to the number of records at every column") {
      val result = ConsensusProfile.compute(problem(rosalindSample))
      val n      = rosalindSample.size
      (0 until result.profile.width).foreach { j =>
        (result.profile.a(j) + result.profile.c(j) + result.profile.g(j) + result.profile.t(j)) shouldBe n
      }
    }
  }
}
