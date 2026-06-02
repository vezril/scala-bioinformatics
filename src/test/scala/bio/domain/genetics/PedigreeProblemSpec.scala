package bio.domain.genetics

import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PedigreeProblemSpec extends AnyFunSpec with Matchers {

  private def fromNewick(s: String) =
    PedigreeProblem.from(
      NewickParser.parse(s).getOrElse(sys.error(s"invalid Newick fixture: $s"))
    )

  describe("PedigreeProblem.from") {
    it("builds a valid pedigree from a Newick tree") {
      fromNewick("((Aa,aa),(Aa,Aa));").isRight shouldBe true
    }

    it("accepts a single known-genotype root") {
      fromNewick("Aa;").isRight shouldBe true
    }

    it("rejects an unknown genotype label") {
      fromNewick("(Bb,aa);") shouldBe Left(PedigreeProblemError.InvalidGenotype("Bb"))
    }

    it("rejects a non-binary internal node") {
      fromNewick("(Aa,aa,AA);") shouldBe Left(PedigreeProblemError.NotBinary(3))
    }

    it("cannot be constructed via a public companion apply") {
      assertDoesNotCompile(
        """bio.domain.genetics.PedigreeProblem(bio.domain.genetics.Pedigree.KnownAncestor(bio.domain.genetics.Genotype.Heterozygous))"""
      )
    }

    it("does not expose a public copy method") {
      assertDoesNotCompile(
        """bio.domain.genetics.PedigreeProblem.from(bio.parsing.NewickParser.parse("Aa;").toOption.get).toOption.get.copy()"""
      )
    }
  }
}
