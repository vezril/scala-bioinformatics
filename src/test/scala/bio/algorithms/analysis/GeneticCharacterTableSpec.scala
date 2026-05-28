package bio.algorithms.analysis

import bio.domain.analysis.GeneticCharacterTableProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GeneticCharacterTableSpec extends AnyFunSpec with Matchers {

  private def fixture(strings: String*): GeneticCharacterTableProblem = {
    val sequences = strings.map { s =>
      DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))
    }.toVector
    GeneticCharacterTableProblem
      .from(sequences)
      .getOrElse(sys.error(s"invalid GeneticCharacterTableProblem fixture: $strings"))
  }

  describe("GeneticCharacterTable.compute") {
    it("returns Vector(\"10110\", \"10100\") for the canonical Rosalind sample") {
      val problem = fixture(
        "ATGCTACC",
        "CGTTTACC",
        "ATTCGACC",
        "AGTCTCCC",
        "CGTCTATC"
      )
      GeneticCharacterTable.compute(problem) shouldBe Vector("10110", "10100")
    }

    it("returns Vector.empty for three identical ACGT strings") {
      GeneticCharacterTable.compute(fixture("ACGT", "ACGT", "ACGT")) shouldBe Vector.empty
    }

    it("returns Vector(\"1010\") when only column 1 is nontrivial") {
      // col 0: A,A,A,A (uniform — trivial)
      // col 1: A,C,A,C (2-2 split — nontrivial; row-0 char is 'A' so A→1, C→0)
      // col 2: T,T,T,T (uniform — trivial)
      val problem = fixture("AAT", "ACT", "AAT", "ACT")
      GeneticCharacterTable.compute(problem) shouldBe Vector("1010")
    }

    it("returns Vector.empty for a 5-row matrix whose every column is uniform or 4-1") {
      // col 0: A,A,A,A,A (uniform)
      // col 1: A,A,A,A,C (4-1 split — trivial)
      val problem = fixture("AA", "AA", "AA", "AA", "AC")
      GeneticCharacterTable.compute(problem) shouldBe Vector.empty
    }

    it("emits rows whose length always equals problem.size") {
      val problem = fixture(
        "ATGCTACC",
        "CGTTTACC",
        "ATTCGACC",
        "AGTCTCCC",
        "CGTCTATC"
      )
      val rows    = GeneticCharacterTable.compute(problem)
      rows should not be empty
      rows.foreach(row => row.length shouldBe problem.size)
    }
  }
}
