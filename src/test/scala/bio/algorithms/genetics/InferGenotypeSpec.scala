package bio.algorithms.genetics

import bio.domain.genetics.{GenotypeProbabilities, PedigreeProblem}
import bio.parsing.NewickParser
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class InferGenotypeSpec extends AnyFunSpec with Matchers {

  private def infer(newick: String): GenotypeProbabilities =
    InferGenotype.infer(
      PedigreeProblem
        .from(NewickParser.parse(newick).getOrElse(sys.error(s"bad Newick: $newick")))
        .getOrElse(sys.error(s"invalid PedigreeProblem fixture: $newick"))
    )

  describe("InferGenotype.infer") {
    it("computes the canonical Rosalind MEND sample") {
      val r = infer("((((Aa,aa),(Aa,Aa)),((aa,aa),(aa,AA))),Aa);")
      r.homozygousDominant shouldBe 0.156 +- 0.001
      r.heterozygous shouldBe 0.5 +- 0.001
      r.homozygousRecessive shouldBe 0.344 +- 0.001
    }

    it("returns a point mass for a known-genotype root") {
      val r = infer("Aa;")
      r.homozygousDominant shouldBe 0.0 +- 1e-9
      r.heterozygous shouldBe 1.0 +- 1e-9
      r.homozygousRecessive shouldBe 0.0 +- 1e-9
    }

    it("crosses two heterozygous parents") {
      val r = infer("(Aa,Aa);")
      r.homozygousDominant shouldBe 0.25 +- 0.001
      r.heterozygous shouldBe 0.5 +- 0.001
      r.homozygousRecessive shouldBe 0.25 +- 0.001
    }

    it("crosses a homozygous dominant with a homozygous recessive") {
      val r = infer("(AA,aa);")
      r.homozygousDominant shouldBe 0.0 +- 1e-9
      r.heterozygous shouldBe 1.0 +- 1e-9
      r.homozygousRecessive shouldBe 0.0 +- 1e-9
    }
  }
}
