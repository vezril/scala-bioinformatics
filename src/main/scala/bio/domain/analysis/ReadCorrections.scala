package bio.domain.analysis

/** A single read correction in a Rosalind CORR solution: the mis-sequenced
  * `oldRead` and the correct `newRead` it should be replaced with (a single
  * symbol substitution). `format` renders it as `oldRead->newRead`.
  */
final case class Correction(oldRead: String, newRead: String) {
  def format: String = s"$oldRead->$newRead"
}

/** Result of the Rosalind CORR ("Error Correction in Reads") problem: the
  * ordered collection of single-symbol corrections (see
  * [[bio.algorithms.analysis.ReadErrorCorrection.correct]]).
  *
  * `format` renders each correction on its own line; an empty result formats to
  * the empty string.
  */
final case class ReadCorrections(corrections: Vector[Correction]) {
  def format: String = corrections.map(_.format).mkString("\n")
}
