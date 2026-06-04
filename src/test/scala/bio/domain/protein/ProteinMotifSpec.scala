package bio.domain.protein

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ProteinMotifSpec extends AnyFunSpec with Matchers {
  describe("ProteinMotif.parse") {
    it("parses the N-glycosylation motif") {
      ProteinMotif.parse("N{P}[ST]{P}").map(_.elements) shouldBe Right(
        Vector(
          MotifElement.OneOf(Set('N')),
          MotifElement.NoneOf(Set('P')),
          MotifElement.OneOf(Set('S', 'T')),
          MotifElement.NoneOf(Set('P'))
        )
      )
    }

    it("rejects an empty motif") {
      ProteinMotif.parse("") shouldBe Left(ProteinMotifError.EmptyMotif)
    }

    it("rejects an unterminated group") {
      ProteinMotif.parse("[ST") shouldBe Left(ProteinMotifError.UnterminatedGroup(0))
    }

    it("rejects an unexpected character") {
      ProteinMotif.parse("A]B") shouldBe Left(ProteinMotifError.UnexpectedCharacter(']', 1))
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile("""bio.domain.protein.ProteinMotif(Vector.empty)""")
    }
  }
}
