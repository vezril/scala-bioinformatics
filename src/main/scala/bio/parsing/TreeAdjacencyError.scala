package bio.parsing

/** File-reading failures for [[TreeAdjacencyFileReader]].
  *
  * Mirrors [[FastaError]]'s split between I/O failures and pure-parse failures so the
  * boundary stays consistent across the framework's file readers.
  */
sealed trait TreeAdjacencyError
object TreeAdjacencyError {

  /** Any underlying I/O or decode exception from reading the file. The raw
    * `Throwable` is preserved for debugging; we deliberately do not classify subtypes.
    */
  final case class IoFailure(cause: Throwable) extends TreeAdjacencyError

  /** The file was read successfully but its contents failed to parse. */
  final case class Parse(error: TreeAdjacencyParseError) extends TreeAdjacencyError
}
