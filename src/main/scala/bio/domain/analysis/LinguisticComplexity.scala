package bio.domain.analysis

/** Result of the Rosalind LING ("Linguistic Complexity of a Genome") problem — the
  * ratio `lc(s) = sub(s) / m(4,n)` of distinct substrings observed to the maximum
  * possible (see [[bio.algorithms.analysis.LinguisticComplexityAnalysis.compute]]).
  *
  * `format` renders the ratio rounded to three decimal places.
  */
final case class LinguisticComplexity(value: Double) {

  def format: String = f"$value%.3f"
}
