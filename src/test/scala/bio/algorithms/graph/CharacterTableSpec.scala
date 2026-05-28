package bio.algorithms.graph

import bio.domain.graph.CharacterTableProblem
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CharacterTableSpec extends AnyFunSpec with Matchers {

  private def fixture(newick: String): CharacterTableProblem = {
    val tree = NewickParser
      .parse(newick)
      .getOrElse(sys.error(s"could not parse Newick fixture: $newick"))
    CharacterTableProblem
      .from(tree)
      .getOrElse(sys.error(s"could not build CharacterTableProblem for: $newick"))
  }

  describe("CharacterTable.compute") {
    it("returns Vector(\"00110\", \"00111\") for the canonical Rosalind CSTR sample") {
      CharacterTable.compute(fixture("(dog,((elephant,mouse),robot),cat);")) shouldBe
        Vector("00110", "00111")
    }

    it("returns Vector.empty for a single-leaf tree") {
      CharacterTable.compute(fixture("a;")) shouldBe Vector.empty
    }

    it("returns Vector.empty for a flat tree with no internal edges") {
      CharacterTable.compute(fixture("(a,b,c,d);")) shouldBe Vector.empty
    }

    it("returns Vector(\"0011\") for the balanced quartet `((a,b),(c,d));`") {
      CharacterTable.compute(fixture("((a,b),(c,d));")) shouldBe Vector("0011")
    }

    it("returns three nontrivial splits for `((a,b),((c,d),(e,f)));`") {
      CharacterTable.compute(fixture("((a,b),((c,d),(e,f)));")) shouldBe
        Vector("000011", "001100", "001111")
    }

    it("every row in a non-empty result has length equal to leafLabels.size") {
      val problem = fixture("(dog,((elephant,mouse),robot),cat);")
      val rows    = CharacterTable.compute(problem)
      rows should not be empty
      rows.foreach(row => row.length shouldBe problem.leafLabels.size)
    }
  }
}
