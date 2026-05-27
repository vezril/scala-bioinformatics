package bio.domain.analysis

import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RandomMatchProblemSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  private def prob(d: Double): Probability =
    Probability.from(d).getOrElse(sys.error(s"invalid Probability in fixture: $d"))

  private val rosalindSampleDna: DnaString = dna("ACGATACAA")
  private val rosalindSampleGc: Vector[Probability] =
    Vector(0.129, 0.287, 0.423, 0.476, 0.641, 0.742, 0.783).map(prob)

  describe("RandomMatchProblem.from") {
    it("accepts the Rosalind sample parameters") {
      val problem = RandomMatchProblem.from(rosalindSampleDna, rosalindSampleGc).toOption.get
      problem.dna shouldBe rosalindSampleDna
      problem.gcContents shouldBe rosalindSampleGc
    }

    it("accepts an empty DNA") {
      val problem = RandomMatchProblem.from(dna(""), Vector(prob(0.5))).toOption.get
      problem.dna.value shouldBe ""
      problem.gcContents.size shouldBe 1
    }

    it("accepts an empty GC-content array") {
      val problem = RandomMatchProblem.from(dna("ACGT"), Vector.empty).toOption.get
      problem.dna.value shouldBe "ACGT"
      problem.gcContents shouldBe Vector.empty
    }

    it("accepts the upper-bound DNA length (100)") {
      val longDna = dna("A" * 100)
      RandomMatchProblem.from(longDna, Vector.empty) shouldBe a[Right[_, _]]
    }

    it("accepts the upper-bound GC-content count (20)") {
      val twenty = Vector.fill(20)(prob(0.5))
      RandomMatchProblem.from(dna("A"), twenty) shouldBe a[Right[_, _]]
    }

    it("rejects DNA longer than 100 as DnaTooLong(101, 100)") {
      val tooLong = dna("A" * 101)
      RandomMatchProblem.from(tooLong, Vector.empty) shouldBe
        Left(RandomMatchProblemError.DnaTooLong(101, 100))
    }

    it("rejects GC-content array larger than 20 as TooManyGcContents(21, 20)") {
      val twentyOne = Vector.fill(21)(prob(0.5))
      RandomMatchProblem.from(dna("A"), twentyOne) shouldBe
        Left(RandomMatchProblemError.TooManyGcContents(21, 20))
    }

    it("validates DNA length before GC-content array size") {
      val tooLong   = dna("A" * 101)
      val twentyOne = Vector.fill(21)(prob(0.5))
      RandomMatchProblem.from(tooLong, twentyOne) shouldBe
        Left(RandomMatchProblemError.DnaTooLong(101, 100))
    }
  }

  describe("RandomMatchProblem construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.analysis.RandomMatchProblem(
          |  bio.domain.nucleic.DnaString.from("A").toOption.get,
          |  Vector.empty
          |)""".stripMargin
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.analysis.RandomMatchProblem
          |  .from(bio.domain.nucleic.DnaString.from("A").toOption.get, Vector.empty)
          |  .toOption.get.copy(gcContents = Vector.empty)""".stripMargin
      )
    }
  }
}
