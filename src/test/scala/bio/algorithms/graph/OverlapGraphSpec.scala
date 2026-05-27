package bio.algorithms.graph

import bio.domain.graph.{OverlapEdge, OverlapLength}
import bio.domain.nucleic.DnaString
import bio.parsing.FastaRecord
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OverlapGraphSpec extends AnyFunSpec with Matchers {

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  private def rec(id: String, sequence: String): FastaRecord =
    FastaRecord(id, dna(sequence))

  private val k3: OverlapLength =
    OverlapLength.from(3).getOrElse(sys.error("OverlapLength.from(3) failed in fixture"))

  private val k1: OverlapLength =
    OverlapLength.from(1).getOrElse(sys.error("OverlapLength.from(1) failed in fixture"))

  describe("OverlapGraph.adjacency") {
    it("produces the canonical three edges for the Rosalind sample at k=3") {
      val records = Vector(
        rec("Rosalind_0498", "AAATAAA"),
        rec("Rosalind_2391", "AAATTTT"),
        rec("Rosalind_2323", "TTTTCCC"),
        rec("Rosalind_0442", "AAATCCC"),
        rec("Rosalind_5013", "GGGTGGG")
      )
      OverlapGraph.adjacency(records, k3) shouldBe Vector(
        OverlapEdge("Rosalind_0498", "Rosalind_2391"),
        OverlapEdge("Rosalind_0498", "Rosalind_0442"),
        OverlapEdge("Rosalind_2391", "Rosalind_2323")
      )
    }

    it("returns no edges for an empty record list") {
      OverlapGraph.adjacency(Vector.empty, k3) shouldBe Vector.empty
    }

    it("returns no edges for a single record (self-loops excluded)") {
      OverlapGraph.adjacency(Vector(rec("R1", "AAATAAA")), k3) shouldBe Vector.empty
    }

    it("excludes self-loops even when a record's own suffix equals its own prefix") {
      val records = Vector(
        rec("R1", "AAATAAA"), // suffix "AAA" == prefix "AAA"
        rec("R2", "GGGGGGG")
      )
      OverlapGraph.adjacency(records, k3) should not contain OverlapEdge("R1", "R1")
    }

    it("emits no edges involving a sequence shorter than k") {
      val records = Vector(
        rec("R1", "AC"),    // length 2 < k=3
        rec("R2", "ACGGG")
      )
      OverlapGraph.adjacency(records, k3) shouldBe Vector.empty
    }

    it("emits multiple outgoing edges when one source matches several targets") {
      val records = Vector(
        rec("R1", "AAATAAA"),
        rec("R2", "AAATTTT"),
        rec("R3", "AAATCCC")
      )
      OverlapGraph.adjacency(records, k3) shouldBe Vector(
        OverlapEdge("R1", "R2"),
        OverlapEdge("R1", "R3")
      )
    }

    it("respects direction — suffix(s)==prefix(t) does not imply the reverse edge") {
      val records = Vector(
        rec("R1", "AAATAAA"),
        rec("R2", "AAATTTT")
      )
      OverlapGraph.adjacency(records, k3) shouldBe Vector(OverlapEdge("R1", "R2"))
    }

    it("works at k=1 (single-character endpoint match)") {
      val records = Vector(
        rec("R1", "ACG"), // ends in "G"
        rec("R2", "GCT")  // starts with "G", ends in "T" (no reverse match — R1 starts with "A")
      )
      OverlapGraph.adjacency(records, k1) shouldBe Vector(OverlapEdge("R1", "R2"))
    }
  }
}
