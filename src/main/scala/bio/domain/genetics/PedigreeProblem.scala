package bio.domain.genetics

import bio.domain.graph.NewickTree

/** Validated input for the Rosalind MEND ("Inferring Genotype from a Pedigree")
  * problem — see [[bio.algorithms.genetics.InferGenotype.infer]].
  *
  * Built from a parsed [[NewickTree]] by [[PedigreeProblem.from]], which converts the
  * tree into a [[Pedigree]], validating (first failure wins) that:
  *   - every leaf carries a valid genotype label (`AA`, `Aa`, or `aa`), else `InvalidGenotype`;
  *   - every internal node has exactly two children, else `NotBinary`.
  *
  * A single-leaf tree (e.g. `Aa;`) is a valid degenerate pedigree.
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and `copy`
  * cannot leak around the smart constructor — construct via [[PedigreeProblem.from]].
  */
sealed abstract case class PedigreeProblem(pedigree: Pedigree)

object PedigreeProblem {

  def from(tree: NewickTree): Either[PedigreeProblemError, PedigreeProblem] =
    convert(tree).map(p => new PedigreeProblem(p) {})

  /** Recursively convert a parsed Newick node into a validated `Pedigree`. */
  private def convert(node: NewickTree): Either[PedigreeProblemError, Pedigree] =
    node.children.size match {
      case 0 => genotypeOf(node.label).map(Pedigree.KnownAncestor)
      case 2 =>
        for {
          p1 <- convert(node.children(0))
          p2 <- convert(node.children(1))
        } yield Pedigree.Offspring(p1, p2)
      case n => Left(PedigreeProblemError.NotBinary(n))
    }

  /** Map a leaf label to its genotype, or fail with `InvalidGenotype`. */
  private def genotypeOf(label: Option[String]): Either[PedigreeProblemError, Genotype] =
    label match {
      case Some("AA") => Right(Genotype.HomozygousDominant)
      case Some("Aa") => Right(Genotype.Heterozygous)
      case Some("aa") => Right(Genotype.HomozygousRecessive)
      case other      => Left(PedigreeProblemError.InvalidGenotype(other.getOrElse("")))
    }
}
