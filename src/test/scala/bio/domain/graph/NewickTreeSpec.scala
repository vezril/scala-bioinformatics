package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class NewickTreeSpec extends AnyFunSpec with Matchers {

  describe("NewickTree construction") {
    it("represents a leaf as a node with a label and no children") {
      val leaf = NewickTree(Some("cat"), Vector.empty)
      leaf.label shouldBe Some("cat")
      leaf.children shouldBe empty
    }

    it("represents a labelled internal node with one child") {
      val tree = NewickTree(Some("dog"), Vector(NewickTree(Some("cat"), Vector.empty)))
      tree.label shouldBe Some("dog")
      tree.children should have size 1
    }

    it("represents an unlabelled internal node with two children") {
      val tree = NewickTree(
        None,
        Vector(
          NewickTree(Some("dog"), Vector.empty),
          NewickTree(Some("cat"), Vector.empty)
        )
      )
      tree.label shouldBe None
      tree.children should have size 2
    }
  }

  describe("NewickTree.labels") {
    it("returns just the leaf label for a single-leaf tree") {
      NewickTree(Some("cat"), Vector.empty).labels shouldBe Set("cat")
    }

    it("returns both the internal label and the child labels for a labelled internal node") {
      val tree = NewickTree(Some("dog"), Vector(NewickTree(Some("cat"), Vector.empty)))
      tree.labels shouldBe Set("dog", "cat")
    }

    it("returns only labelled nodes when the root is unlabelled") {
      val tree = NewickTree(
        None,
        Vector(
          NewickTree(Some("dog"), Vector.empty),
          NewickTree(Some("cat"), Vector.empty)
        )
      )
      tree.labels shouldBe Set("dog", "cat")
    }

    it("returns all labels of a 3-level tree (((a,b)c,(d,e)f)g)") {
      val tree = NewickTree(
        Some("g"),
        Vector(
          NewickTree(
            Some("c"),
            Vector(
              NewickTree(Some("a"), Vector.empty),
              NewickTree(Some("b"), Vector.empty)
            )
          ),
          NewickTree(
            Some("f"),
            Vector(
              NewickTree(Some("d"), Vector.empty),
              NewickTree(Some("e"), Vector.empty)
            )
          )
        )
      )
      tree.labels shouldBe Set("a", "b", "c", "d", "e", "f", "g")
    }
  }

  describe("NewickTree.render") {
    it("renders a leaf as its bare label terminated by a semicolon") {
      NewickTree(Some("cat"), Vector.empty).render shouldBe "cat;"
    }

    it("renders an internal node as parenthesised, comma-joined children") {
      val tree = NewickTree(
        None,
        Vector(
          NewickTree(Some("a"), Vector.empty),
          NewickTree(Some("b"), Vector.empty)
        )
      )
      tree.render shouldBe "(a,b);"
    }

    it("renders a nested unrooted tree terminated by a single semicolon") {
      val tree = NewickTree(
        None,
        Vector(
          NewickTree(Some("dog"), Vector.empty),
          NewickTree(
            None,
            Vector(
              NewickTree(Some("cat"), Vector.empty),
              NewickTree(Some("rabbit"), Vector.empty)
            )
          ),
          NewickTree(
            None,
            Vector(
              NewickTree(Some("rat"), Vector.empty),
              NewickTree(
                None,
                Vector(
                  NewickTree(Some("elephant"), Vector.empty),
                  NewickTree(Some("mouse"), Vector.empty)
                )
              )
            )
          )
        )
      )
      tree.render shouldBe "(dog,(cat,rabbit),(rat,(elephant,mouse)));"
    }
  }
}
