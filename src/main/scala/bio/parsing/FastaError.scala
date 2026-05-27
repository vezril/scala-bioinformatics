package bio.parsing

/** Failures from reading a FASTA file via [[FastaFileReader]].
  *
  * Distinct from [[FastaParseError]]: this trait covers the *file-reading* boundary
  * (parse failures plus I/O failures), while `FastaParseError` is the pure parser's
  * vocabulary. The two are kept separate so each capability can be archived and
  * evolved independently.
  */
sealed trait FastaError
object FastaError {

  /** The underlying I/O or decode operation failed (e.g. file not found, permission
    * denied, malformed UTF-8). The raw `Throwable` is preserved for debugging — we
    * deliberately do not classify subtypes here.
    */
  final case class IoFailure(cause: Throwable) extends FastaError

  /** The file was read successfully but its contents failed to parse. */
  final case class Parse(error: FastaParseError) extends FastaError
}
