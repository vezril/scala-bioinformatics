package bio.algorithms.graph

import bio.domain.graph.InconsistentCharacterSetProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FixInconsistentCharacterSetSpec extends AnyFunSpec with Matchers {

  private def problem(rows: Vector[String]): InconsistentCharacterSetProblem =
    InconsistentCharacterSetProblem
      .from(rows)
      .getOrElse(sys.error("invalid InconsistentCharacterSetProblem fixture"))

  /** Two rows conflict iff all four cross-intersections are non-empty. */
  private def conflict(a: String, b: String): Boolean = {
    val idx = a.indices
    val a1  = idx.filter(a(_) == '1').toSet
    val a0  = idx.filter(a(_) == '0').toSet
    val b1  = idx.filter(b(_) == '1').toSet
    val b0  = idx.filter(b(_) == '0').toSet
    a1.intersect(b1).nonEmpty && a1.intersect(b0).nonEmpty &&
    a0.intersect(b1).nonEmpty && a0.intersect(b0).nonEmpty
  }

  private def isConsistent(rows: Vector[String]): Boolean =
    rows.indices.forall(i => (i + 1 until rows.size).forall(j => !conflict(rows(i), rows(j))))

  describe("FixInconsistentCharacterSet.fix") {
    it("repairs the canonical sample to a consistent submatrix missing one row") {
      val input  = Vector("100001", "000110", "111000", "100111")
      val result = FixInconsistentCharacterSet.fix(problem(input))
      result shouldBe defined
      val rows = result.get.rows
      rows should have size (input.size - 1)
      rows.foreach(r => input should contain(r))
      isConsistent(rows) shouldBe true
    }

    it("keeps every retained row at the original width and drawn from the input") {
      val input = Vector("100001", "000110", "111000", "100111")
      val rows  = FixInconsistentCharacterSet.fix(problem(input)).get.rows
      rows.foreach(_.length shouldBe 6)
      rows.toSet.subsetOf(input.toSet) shouldBe true
    }

    it("fixes conflicts that all share a single character by deleting that character") {
      // row0 conflicts with row1 and with row2; row1 and row2 are nested (compatible).
      val input = Vector("100100", "111000", "110000")
      conflict(input(0), input(1)) shouldBe true
      conflict(input(0), input(2)) shouldBe true
      conflict(input(1), input(2)) shouldBe false
      val result = FixInconsistentCharacterSet.fix(problem(input))
      result shouldBe defined
      result.get.rows should have size 2
      isConsistent(result.get.rows) shouldBe true
    }

    it("returns None when two independent conflicts cannot be fixed by one deletion") {
      // (0,1) conflict on the first block, (2,3) conflict on the second block; disjoint.
      val table = Vector("11000000", "10100000", "00001100", "00001010")
      conflict(table(0), table(1)) shouldBe true
      conflict(table(2), table(3)) shouldBe true
      conflict(table(0), table(2)) shouldBe false
      FixInconsistentCharacterSet.fix(problem(table)) shouldBe None
    }

    it("returns a single-row deletion for an already-consistent table") {
      val input = Vector("110000", "111100", "111110")
      isConsistent(input) shouldBe true
      val result = FixInconsistentCharacterSet.fix(problem(input))
      result shouldBe defined
      result.get.rows should have size (input.size - 1)
      isConsistent(result.get.rows) shouldBe true
    }
  }
}
