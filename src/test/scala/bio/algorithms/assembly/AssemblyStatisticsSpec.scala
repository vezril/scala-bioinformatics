package bio.algorithms.assembly

import bio.domain.assembly.{AssemblyQuality, ContigCollection}
import bio.domain.nucleic.DnaString
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class AssemblyStatisticsSpec extends AnyFunSpec with Matchers {

  private def collection(contigs: String*): ContigCollection =
    ContigCollection
      .from(contigs.toVector.map(s => DnaString.from(s).toOption.get))
      .toOption
      .get

  private val sample: ContigCollection =
    collection("GATTACA", "TACTACTAC", "ATTGAT", "GAAGA")

  describe("AssemblyStatistics.nStatistic") {
    it("computes N50 for the canonical sample as 7") {
      AssemblyStatistics.nStatistic(sample, 50) shouldBe 7
    }

    it("computes N75 for the canonical sample as 6") {
      AssemblyStatistics.nStatistic(sample, 75) shouldBe 6
    }

    it("returns the single contig length for a one-contig collection") {
      val one = collection("GATTACA")
      AssemblyStatistics.nStatistic(one, 50) shouldBe 7
      AssemblyStatistics.nStatistic(one, 75) shouldBe 7
    }

    it("returns the common length when all contigs are equal length") {
      val equal = collection("ACGT", "TGCA", "GGGG")
      AssemblyStatistics.nStatistic(equal, 50) shouldBe 4
    }
  }

  describe("AssemblyStatistics.assess") {
    it("assesses the canonical sample as N50 7 and N75 6") {
      AssemblyStatistics.assess(sample) shouldBe AssemblyQuality(7, 6)
    }
  }
}
