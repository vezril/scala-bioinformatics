package bio.domain.analysis

/** Result of the Rosalind MGAP ("Maximizing the Gap Symbols of an Optimal Alignment")
  * problem — the maximum number of gap symbols in any maximum-score alignment (see
  * [[bio.algorithms.analysis.MaximizeGapSymbols.maxGaps]]).
  */
final case class MaxGapSymbols(count: Int) {

  def format: String = count.toString
}
