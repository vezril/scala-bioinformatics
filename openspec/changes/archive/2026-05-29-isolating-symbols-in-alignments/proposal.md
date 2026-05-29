## Why

Rosalind spec 49 (OSYM — "Isolating Symbols in Alignments") asks, for two
DNA strings `s` and `t`, both the maximum *global* alignment score and the
sum of a matrix `M` where `M[j][k]` is the best score of any global
alignment that forces `s[j]` to be aligned with `t[k]`. It reuses the
+1/-1 *mismatch score* introduced in spec 48 (SIMS) and extends the
alignment toolkit with the classic forward/backward (prefix+suffix) DP
decomposition — the foundation for "alignment with a constraint" and for
counting/posterior-style alignment questions.

## What Changes

- Add a validated `IsolatedSymbolsProblem` input bundle (subdomain
  `analysis`) wrapping two DNA strings `left` (`s`) and `right` (`t`),
  each ≤ 1 000 bp, constructed only through a smart constructor returning
  `Either[IsolatedSymbolsProblemError, …]`.
- Add an `IsolatedSymbolsProblemError` ADT (`LeftTooLong`, `RightTooLong`).
- Add an `IsolatedSymbols` output ADT (plain `final case class`) carrying
  `globalScore: Int` and `matrixSum: Long`.
- Add a `bio.algorithms.analysis.IsolatedSymbols` object with
  `compute(problem): IsolatedSymbols` implementing the forward + backward
  `O(m · n)` DP under the +1/-1 mismatch score and summing the `M` matrix.
- Add an `OSYMProb` runner wired into `Main`, following the existing
  per-problem runner pattern.

## Capabilities

### New Capabilities
- `isolating-symbols-in-alignments`: maximum global alignment score plus
  the sum of the symbol-isolation matrix `M` for two DNA strings under the
  +1/-1 mismatch score — input bundle, error ADT, output ADT, and the
  `IsolatedSymbols.compute` algorithm.

### Modified Capabilities
<!-- None. Brand-new capability; no existing spec requirements change. -->

## Impact

- **New code** (subdomain `analysis`, alongside the SIMS fitting alignment):
  - `src/main/scala/bio/domain/analysis/IsolatedSymbolsProblem.scala`
  - `src/main/scala/bio/domain/analysis/IsolatedSymbolsProblemError.scala`
  - `src/main/scala/bio/domain/analysis/IsolatedSymbols.scala`
  - `src/main/scala/bio/algorithms/analysis/IsolatedSymbols.scala`
  - `src/main/scala/bio/problems/OSYMProb.scala` (+ wiring in `bio/Main.scala`)
- **New tests**:
  - `src/test/scala/bio/domain/analysis/IsolatedSymbolsProblemSpec.scala`
  - `src/test/scala/bio/algorithms/analysis/IsolatedSymbolsSpec.scala`
- **Dependencies**: none new. Reuses `DnaString` and Cats Effect `IO`.
- **No breaking changes**: purely additive.
