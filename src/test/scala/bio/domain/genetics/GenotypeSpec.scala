package bio.domain.genetics

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GenotypeSpec extends AnyFunSpec with Matchers {

  describe("Genotype") {
    it("has exactly three case objects: HomozygousDominant, Heterozygous, HomozygousRecessive") {
      val genotypes: Seq[Genotype] =
        Seq(Genotype.HomozygousDominant, Genotype.Heterozygous, Genotype.HomozygousRecessive)
      genotypes should have size 3
    }

    it("supports exhaustive pattern matching without warnings") {
      def label(g: Genotype): String = g match {
        case Genotype.HomozygousDominant  => "AA"
        case Genotype.Heterozygous        => "Aa"
        case Genotype.HomozygousRecessive => "aa"
      }
      label(Genotype.HomozygousDominant) shouldBe "AA"
      label(Genotype.Heterozygous) shouldBe "Aa"
      label(Genotype.HomozygousRecessive) shouldBe "aa"
    }
  }
}
