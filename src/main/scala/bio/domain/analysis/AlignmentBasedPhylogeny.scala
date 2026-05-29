package bio.domain.analysis

/** Result of the small-parsimony Sankoff algorithm — see
  * [[bio.algorithms.analysis.AlignmentBasedPhylogeny.solve]].
  *
  * Carries:
  *   - `totalDistance` — the minimum value of
  *     `d_H(T) = Σ_{edges} per-column-Hamming(s_u, s_v)` across all
  *     internal-node label assignments;
  *   - `internalAssignments` — one [[NamedSequence]] per internal (non-leaf)
  *     node of the tree, ordered by deterministic pre-order traversal
  *     (root first, then recursively left subtree then right subtree).
  *
  * Implemented as a plain `final case class` — free `apply`, `copy`,
  * equality, and pattern-matching, no smart constructor.
  */
final case class AlignmentBasedPhylogeny(
    totalDistance: Int,
    internalAssignments: Vector[NamedSequence]
)
