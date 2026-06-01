package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.BitSet

class SplitSpec extends AnyFunSpec with Matchers {

  describe("Split.of") {
    it("is invariant under the order of the two sides") {
      Split.of(BitSet(0, 4), BitSet(1, 2, 3, 5)) shouldBe
        Split.of(BitSet(1, 2, 3, 5), BitSet(0, 4))
    }

    it("canonicalises to the side that does not contain index 0") {
      Split.of(BitSet(0, 4), BitSet(1, 2, 3, 5)).side shouldBe BitSet(1, 2, 3, 5)
    }

    it("keeps the given side when it already excludes index 0") {
      Split.of(BitSet(2, 3), BitSet(0, 1, 4, 5)).side shouldBe BitSet(2, 3)
    }

    it("distinguishes different bipartitions") {
      Split.of(BitSet(2, 3), BitSet(0, 1, 4, 5)) should not be
        Split.of(BitSet(3, 5), BitSet(0, 1, 2, 4))
    }
  }

  describe("Split construction invariants") {
    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.graph.Split(scala.collection.immutable.BitSet(1))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.graph.Split
          |  .of(scala.collection.immutable.BitSet(2), scala.collection.immutable.BitSet(0, 1))
          |  .copy(side = scala.collection.immutable.BitSet(9))""".stripMargin
      )
    }
  }
}
