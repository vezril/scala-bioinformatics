package bio.algorithms.graph

import bio.domain.graph.NewickDistanceProblem
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class NewickDistanceSpec extends AnyFunSpec with Matchers {

  private def fixture(newick: String, x: String, y: String): NewickDistanceProblem = {
    val tree = NewickParser
      .parse(newick)
      .getOrElse(sys.error(s"could not parse fixture Newick string: $newick"))
    NewickDistanceProblem
      .from(tree, x, y)
      .getOrElse(sys.error(s"could not build NewickDistanceProblem for ($x, $y) in $newick"))
  }

  describe("NewickDistance.between") {
    it("returns 1 for the canonical Rosalind sample `(cat)dog;` with query `dog cat`") {
      NewickDistance.between(fixture("(cat)dog;", "dog", "cat")) shouldBe 1
    }

    it("returns 2 for the canonical Rosalind sample `(dog,cat);` with query `dog cat`") {
      NewickDistance.between(fixture("(dog,cat);", "dog", "cat")) shouldBe 2
    }

    it("returns 0 for a node compared with itself") {
      NewickDistance.between(fixture("(dog,cat);", "dog", "dog")) shouldBe 0
    }

    it("returns 4 for `((a,b)c,(d,e)f)g;` between leaves under different subtrees (a→e)") {
      NewickDistance.between(fixture("((a,b)c,(d,e)f)g;", "a", "e")) shouldBe 4
    }

    it("returns 2 for `((a,b)c,(d,e)f)g;` between siblings under one subtree (a→b)") {
      NewickDistance.between(fixture("((a,b)c,(d,e)f)g;", "a", "b")) shouldBe 2
    }

    it("returns 2 for `((a,b)c,(d,e)f)g;` between leaf a and the root g") {
      NewickDistance.between(fixture("((a,b)c,(d,e)f)g;", "a", "g")) shouldBe 2
    }
  }
}
