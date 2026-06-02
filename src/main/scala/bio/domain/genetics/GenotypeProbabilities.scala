package bio.domain.genetics

/** Result of the Rosalind MEND ("Inferring Genotype from a Pedigree") problem — the
  * probabilities that the root individual exhibits the `AA`, `Aa`, and `aa` genotypes
  * (see [[bio.algorithms.genetics.InferGenotype.infer]]).
  *
  * `format` renders the three probabilities in that order, space-separated, each to
  * three decimal places.
  */
final case class GenotypeProbabilities(
    homozygousDominant: Double,
    heterozygous: Double,
    homozygousRecessive: Double
) {

  def format: String =
    f"$homozygousDominant%.3f $heterozygous%.3f $homozygousRecessive%.3f"
}
