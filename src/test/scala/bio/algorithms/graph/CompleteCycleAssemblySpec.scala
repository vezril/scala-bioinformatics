package bio.algorithms.graph

import bio.domain.graph.CompleteCycleProblem
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CompleteCycleAssemblySpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString fixture: $s"))

  private def problem(reads: String*): CompleteCycleProblem =
    CompleteCycleProblem
      .from(reads.toVector.map(dna))
      .getOrElse(sys.error(s"invalid CompleteCycleProblem fixture: ${reads.mkString(",")}"))

  private val SampleReads: Seq[String] = Seq(
    "CAG", "AGT", "GTT", "TTT", "TTG", "TGG", "GGC", "GCG", "CGT", "GTT",
    "TTC", "TCA", "CAA", "AAT", "ATT", "TTC", "TCA"
  )

  private val SampleExpected: Set[String] = Set(
    "CAGTTCAATTTGGCGTT",
    "CAGTTCAATTGGCGTTT",
    "CAGTTTCAATTGGCGTT",
    "CAGTTTGGCGTTCAATT",
    "CAGTTGGCGTTCAATTT",
    "CAGTTGGCGTTTCAATT"
  )

  describe("CompleteCycleAssembly.assemble") {
    it("enumerates the six circular strings of the canonical sample") {
      val result = CompleteCycleAssembly.assemble(problem(SampleReads: _*))
      result.strings should contain theSameElementsAs SampleExpected
    }

    it("produces strings that each begin with the first input read") {
      val result = CompleteCycleAssembly.assemble(problem(SampleReads: _*))
      all(result.strings) should startWith("CAG")
    }

    it("returns a single string for a simple cycle with no repeats") {
      val result = CompleteCycleAssembly.assemble(problem("AT", "TG", "GA"))
      result.strings shouldBe Vector("ATG")
    }

    it("returns multiple strings when a branch admits more than one cycle") {
      val result =
        CompleteCycleAssembly.assemble(problem("CA", "AT", "TA", "AG", "GA", "AC"))
      result.strings should contain theSameElementsAs Set("CATAGA", "CAGATA")
    }
  }
}
