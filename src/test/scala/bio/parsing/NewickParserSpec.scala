package bio.parsing

import bio.domain.graph.NewickTree
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class NewickParserSpec extends AnyFunSpec with Matchers {

  describe("NewickParser.parse — happy path") {
    it("parses the canonical Rosalind sample `(cat)dog;`") {
      NewickParser.parse("(cat)dog;") shouldBe Right(
        NewickTree(Some("dog"), Vector(NewickTree(Some("cat"), Vector.empty)))
      )
    }

    it("parses the canonical Rosalind sample `(dog,cat);` with an unlabelled root") {
      NewickParser.parse("(dog,cat);") shouldBe Right(
        NewickTree(
          None,
          Vector(
            NewickTree(Some("dog"), Vector.empty),
            NewickTree(Some("cat"), Vector.empty)
          )
        )
      )
    }

    it("parses a deeply nested labelled tree `((a,b)c,(d,e)f)g;`") {
      val result = NewickParser.parse("((a,b)c,(d,e)f)g;")
      result.isRight shouldBe true
      val tree = result.toOption.get
      tree.label shouldBe Some("g")
      tree.children should have size 2
      tree.labels shouldBe Set("a", "b", "c", "d", "e", "f", "g")
    }

    it("parses a single leaf with terminating semicolon `a;`") {
      NewickParser.parse("a;") shouldBe Right(
        NewickTree(Some("a"), Vector.empty)
      )
    }

    it("tolerates surrounding whitespace around the input") {
      NewickParser.parse("  (dog,cat);  ") shouldBe Right(
        NewickTree(
          None,
          Vector(
            NewickTree(Some("dog"), Vector.empty),
            NewickTree(Some("cat"), Vector.empty)
          )
        )
      )
    }
  }

  describe("NewickParser.parse — error cases") {
    it("rejects empty input as EmptyInput") {
      NewickParser.parse("") shouldBe Left(NewickParseError.EmptyInput)
    }

    it("rejects whitespace-only input as EmptyInput") {
      NewickParser.parse("   ") shouldBe Left(NewickParseError.EmptyInput)
    }

    it("rejects input missing the terminating semicolon") {
      NewickParser.parse("(dog,cat)") shouldBe Left(NewickParseError.MissingTerminator)
    }

    it("rejects input with an unmatched open paren") {
      NewickParser.parse("(dog,cat;") match {
        case Left(_: NewickParseError.UnmatchedOpenParen) => succeed
        case other                                        => fail(s"expected UnmatchedOpenParen, got $other")
      }
    }

    it("rejects input with an unmatched close paren") {
      NewickParser.parse("dog,cat);") match {
        case Left(_: NewickParseError.UnmatchedCloseParen) => succeed
        case other                                         => fail(s"expected UnmatchedCloseParen, got $other")
      }
    }

    it("rejects content after the terminating semicolon as TrailingContent") {
      NewickParser.parse("(a,b);garbage") shouldBe Left(
        NewickParseError.TrailingContent("garbage")
      )
    }
  }
}
