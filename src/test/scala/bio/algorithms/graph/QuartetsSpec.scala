package bio.algorithms.graph

import bio.domain.graph.QuartetsProblem
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class QuartetsSpec extends AnyFunSpec with Matchers {

  private def problem(taxa: Vector[String], chars: Vector[String]): QuartetsProblem =
    QuartetsProblem
      .from(taxa, chars)
      .getOrElse(sys.error(s"invalid QuartetsProblem fixture: $taxa / $chars"))

  private val sampleTaxa: Vector[String] =
    Vector("cat", "dog", "elephant", "ostrich", "mouse", "rabbit", "robot")

  describe("Quartets.compute") {
    it("reproduces the canonical sample's four quartets") {
      val result =
        Quartets.compute(problem(sampleTaxa, Vector("01xxx00", "x11xx00", "111x00x")))
      result.map(_.render).toSet shouldBe Set(
        "{dog, elephant} {rabbit, robot}",
        "{cat, dog} {mouse, rabbit}",
        "{cat, elephant} {mouse, rabbit}",
        "{dog, elephant} {mouse, rabbit}"
      )
      result.size shouldBe 4
    }

    it("yields no quartet when a character has fewer than two taxa on a side") {
      Quartets.compute(problem(sampleTaxa, Vector("01xxx00"))) shouldBe empty
    }

    it("yields no quartet for an all-x character") {
      Quartets.compute(problem(Vector("a", "b", "c", "d"), Vector("xxxx"))) shouldBe empty
    }

    it("deduplicates a quartet inferable from two characters") {
      val result =
        Quartets.compute(problem(Vector("a", "b", "c", "d"), Vector("1100", "1100")))
      result.size shouldBe 1
      result.head.render shouldBe "{a, b} {c, d}"
    }

    it("yields a single quartet for a four-taxon even split") {
      val result = Quartets.compute(problem(Vector("a", "b", "c", "d"), Vector("1100")))
      result.size shouldBe 1
      result.head.render shouldBe "{a, b} {c, d}"
    }
  }
}
