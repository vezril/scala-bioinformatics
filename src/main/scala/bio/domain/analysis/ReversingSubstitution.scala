package bio.domain.analysis

/** One reversing substitution found by
  * [[bio.algorithms.analysis.ReversingSubstitutions.findAll]].
  *
  * The reversion is anchored by two parent-child edges on a root-down tree
  * path: the *first-change edge* `(s, t)` and the *reversion edge* `(v, w)`,
  * with the path from `t` down to `v` preserving the substituted value (no
  * other change at position `i` in between).
  *
  *   - `firstChangeSpecies` is the label of `t` (the child of the first-change
  *     edge — the node where the substitution `originalSymbol -> substitutedSymbol`
  *     first appears at position `i`).
  *   - `reversionSpecies` is the label of `w` (the child of the reversion edge —
  *     the node where the substitution is reverted back to the original).
  *   - `position` is 1-indexed into the alignment.
  *   - `originalSymbol` is `s[i]`.
  *   - `substitutedSymbol` is `t[i]` (also equal to `v[i]` by definition).
  *   - `revertedSymbol` is `w[i]`. By construction this always equals
  *     `originalSymbol`; it is kept as a separate field so the output formatter
  *     can render Rosalind's `original->substituted->reverted` text verbatim.
  *
  * Implemented as a plain `final case class` — no smart constructor.
  */
final case class ReversingSubstitution(
    firstChangeSpecies: String,
    reversionSpecies: String,
    position: Int,
    originalSymbol: Char,
    substitutedSymbol: Char,
    revertedSymbol: Char
)
