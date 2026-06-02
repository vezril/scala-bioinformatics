package bio.algorithms.combinatorics

import bio.domain.combinatorics.RestrictionMapProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestrictionMapConstructionSpec extends AnyFunSpec with Matchers {

  private def problem(l: Int*): RestrictionMapProblem =
    RestrictionMapProblem
      .from(l.toVector)
      .getOrElse(sys.error(s"invalid RestrictionMapProblem fixture: ${l.toVector}"))

  /** All positive pairwise differences of `points`, sorted — the difference multiset. */
  private def deltaX(points: Vector[Int]): Vector[Int] =
    (for {
      i <- points.indices
      j <- (i + 1) until points.size
    } yield math.abs(points(i) - points(j))).sorted.toVector

  describe("RestrictionMapConstruction.solve") {
    it("reconstructs the canonical Rosalind PDPL sample") {
      val l = Vector(2, 2, 3, 3, 4, 5, 6, 7, 8, 10)
      val result = RestrictionMapConstruction.solve(problem(l: _*))
      result should not be empty
      val points = result.get.points
      deltaX(points) shouldBe l.sorted
      points.head shouldBe 0
      points should have size 5
    }

    it("reconstructs a single-distance multiset") {
      RestrictionMapConstruction.solve(problem(5)).map(_.points) shouldBe Some(Vector(0, 5))
    }

    it("reconstructs the trivial empty multiset") {
      RestrictionMapConstruction
        .solve(RestrictionMapProblem.from(Vector.empty).toOption.get)
        .map(_.points) shouldBe Some(Vector(0))
    }

    it("returns no solution for an unrealisable multiset") {
      RestrictionMapConstruction.solve(problem(1, 1, 1)) shouldBe None
    }
  }
}
