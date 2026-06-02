package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class OpenReadingFramesResultSpec extends AnyFunSpec with Matchers {

  private def protein(s: String): ProteinString =
    ProteinString.from(s).getOrElse(sys.error(s"invalid ProteinString fixture: $s"))

  describe("OpenReadingFrames result") {
    it("exposes the candidate proteins") {
      val proteins = Vector(protein("MA"), protein("M"))
      OpenReadingFrames(proteins).proteins shouldBe proteins
    }

    it("formats one protein per line") {
      OpenReadingFrames(Vector(protein("MA"), protein("M"))).format shouldBe "MA\nM"
    }

    it("renders the empty result as the empty string") {
      OpenReadingFrames(Vector.empty).format shouldBe ""
    }
  }
}
