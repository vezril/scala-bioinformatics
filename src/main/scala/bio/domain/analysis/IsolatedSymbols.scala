package bio.domain.analysis

/** Result of the Rosalind OSYM ("Isolating Symbols in Alignments")
  * computation — see [[bio.algorithms.analysis.IsolatedSymbols.compute]].
  *
  * Carries:
  *
  *   - `globalScore` — the maximum global alignment score of the two input
  *     DNA strings under the +1/-1 mismatch score (`+1` per matched symbol,
  *     `-1` per mismatched, inserted, or deleted symbol);
  *   - `matrixSum` — the sum of every element of the symbol-isolation
  *     matrix `M`, where `M[j][k]` is the maximum score of any global
  *     alignment that aligns `left[j]` with `right[k]`.
  *
  * `matrixSum` is a `Long`: `M` has up to `m · n ≈ 10^6` entries, each as
  * negative as roughly `-(m + n)`, so the sum can approach `-2 · 10^9` and
  * overflow an `Int`.
  *
  * Implemented as a plain `final case class` (free `apply`, `copy`,
  * equality, pattern-matching) because it is a result value with no
  * cross-field invariant beyond what the producing algorithm guarantees.
  */
final case class IsolatedSymbols(
    globalScore: Int,
    matrixSum: Long
)
