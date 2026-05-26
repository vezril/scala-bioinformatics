package bio.domain.genetics

sealed trait Genotype
object Genotype {
  case object HomozygousDominant  extends Genotype
  case object Heterozygous        extends Genotype
  case object HomozygousRecessive extends Genotype
}
