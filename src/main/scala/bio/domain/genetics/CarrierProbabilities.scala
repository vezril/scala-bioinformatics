package bio.domain.genetics

/** Result of the Rosalind SEXL ("Sex-Linked Inheritance") problem — the probability,
  * for each gene, that a randomly selected female is a carrier (see
  * [[bio.algorithms.genetics.SexLinkedInheritance.carrierProbabilities]]).
  *
  * `format` renders the values in order, space-separated, each to three decimal places.
  * The empty result renders as the empty string.
  */
final case class CarrierProbabilities(values: Vector[Double]) {

  def format: String = values.map(v => f"$v%.3f").mkString(" ")
}
