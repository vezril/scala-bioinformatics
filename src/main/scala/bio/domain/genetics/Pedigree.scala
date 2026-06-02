package bio.domain.genetics

/** A rooted binary pedigree for a Mendelian factor (Rosalind MEND).
  *
  * Time runs from the leaves (known ancestors) to the root (the individual whose
  * genotype is inferred): a [[Pedigree.KnownAncestor]] is a leaf with a known
  * [[Genotype]], and an [[Pedigree.Offspring]] is an individual produced by crossing
  * its two parent pedigrees.
  */
sealed trait Pedigree

object Pedigree {

  /** A leaf: an ancestor whose genotype is known. */
  final case class KnownAncestor(genotype: Genotype) extends Pedigree

  /** An internal node: the offspring of two parent pedigrees. */
  final case class Offspring(parent1: Pedigree, parent2: Pedigree) extends Pedigree
}
