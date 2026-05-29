package bio.domain.analysis

/** Construction failures for [[SimilarMotifsProblem]] (Rosalind KSIM —
  * "Finding All Similar Motifs").
  */
sealed trait SimilarMotifsProblemError
object SimilarMotifsProblemError {

  /** The edit-distance budget `k` fell outside the Rosalind KSIM range
    * `[1, 50]`.
    */
  final case class KOutOfRange(k: Int, min: Int, max: Int)
      extends SimilarMotifsProblemError

  /** The motif DNA string exceeded the Rosalind KSIM cap of 5 000 bp. */
  final case class MotifTooLong(length: Int, max: Int)
      extends SimilarMotifsProblemError

  /** The genome DNA string exceeded the Rosalind KSIM cap of 50 000 bp. */
  final case class GenomeTooLong(length: Int, max: Int)
      extends SimilarMotifsProblemError
}
