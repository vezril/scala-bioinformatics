package bio.domain.combinatorics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.SortedSet

class SetOperationsResultSpec extends AnyFunSpec with Matchers {

  describe("SetOperationsResult.format") {
    it("renders the six sets on six lines in ascending, brace-delimited form") {
      val result = SetOperationsResult(
        union = SortedSet(1, 2, 3, 4, 5, 8, 10),
        intersection = SortedSet(2, 5),
        aMinusB = SortedSet(1, 3, 4),
        bMinusA = SortedSet(8, 10),
        aComplement = SortedSet(6, 7, 8, 9, 10),
        bComplement = SortedSet(1, 3, 4, 6, 7, 9)
      )
      result.format shouldBe
        """{1, 2, 3, 4, 5, 8, 10}
          |{2, 5}
          |{1, 3, 4}
          |{8, 10}
          |{6, 7, 8, 9, 10}
          |{1, 3, 4, 6, 7, 9}""".stripMargin
    }

    it("renders an empty set as {}") {
      val result = SetOperationsResult(
        union = SortedSet(1),
        intersection = SortedSet.empty[Int],
        aMinusB = SortedSet(1),
        bMinusA = SortedSet.empty[Int],
        aComplement = SortedSet.empty[Int],
        bComplement = SortedSet(1)
      )
      result.format.linesIterator.toList shouldBe List(
        "{1}",
        "{}",
        "{1}",
        "{}",
        "{}",
        "{1}"
      )
    }
  }
}
