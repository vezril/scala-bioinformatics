package bio.domain.analysis

/** A single approximate occurrence of a motif within a genome, found by the
  * Rosalind KSIM ("Finding All Similar Motifs") computation — see
  * [[bio.algorithms.analysis.SimilarMotifs.findAll]].
  *
  * Carries:
  *
  *   - `location` — the 1-based start position of the matching substring
  *     `t′` in the genome `t`;
  *   - `length` — the length of the matching substring `t′`.
  *
  * Implemented as a plain `final case class` (free `apply`, `copy`,
  * equality, pattern-matching) because it is a coordinate pair with no
  * cross-field invariant beyond what the producing algorithm guarantees.
  */
final case class SimilarMotif(
    location: Int,
    length: Int
)
