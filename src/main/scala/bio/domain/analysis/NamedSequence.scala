package bio.domain.analysis

/** A FASTA-style record carrying a textual identifier and a sequence string.
  *
  * Used as both an *input* alignment row (where `sequence` is an
  * already-aligned DNA-with-gaps string) and an *output* internal-node
  * assignment (where `sequence` is the algorithm-chosen DNA-with-gaps
  * string for that node).
  *
  * Alphabet and length constraints are enforced by the *containing*
  * [[AlignmentBasedPhylogenyProblem]], not by `NamedSequence` itself.
  *
  * Implemented as a plain `final case class` — free `apply`, `copy`,
  * equality, and pattern-matching, no smart constructor.
  */
final case class NamedSequence(label: String, sequence: String)
