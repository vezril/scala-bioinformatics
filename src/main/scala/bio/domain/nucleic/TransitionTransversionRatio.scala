package bio.domain.nucleic

/** Result of the Rosalind TRAN ("Transitions and Transversions") problem: the
  * `transitions` and `transversions` substitution counts and their ratio (see
  * [[bio.algorithms.nucleic.TransitionTransversionAnalysis.analyze]]).
  *
  * `ratio` is `transitions / transversions`, defined as `0.0` when there are no
  * transversions (degenerate inputs such as identical strings); `format` renders
  * the ratio to 11 decimal places, matching the Rosalind sample output.
  */
final case class TransitionTransversionRatio(transitions: Int, transversions: Int) {
  val ratio: Double = if (transversions == 0) 0.0 else transitions.toDouble / transversions

  def format: String = f"$ratio%.11f"
}
