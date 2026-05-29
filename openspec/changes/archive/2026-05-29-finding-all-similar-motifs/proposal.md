## Why

Rosalind spec 50 (KSIM — "Finding All Similar Motifs") asks us to locate
**every** approximate occurrence of a motif `s` inside a genome `t`:
report all substrings `t′` of `t` whose edit distance `d_E(s, t′)` is at
most `k`. This is the approximate-string-matching counterpart to the exact
substring search the framework already has (KMP), and it reuses the
edit-distance machinery (EDIT/EDTA) — generalising "find a motif" from a
single best hit (SIMS, one fitting alignment) to the *complete set* of
hits within an edit-distance budget.

## What Changes

- Add a validated `SimilarMotifsProblem` input bundle (subdomain
  `analysis`) wrapping `k: Int` (1 ≤ k ≤ 50), a `motif: DnaString` (`s`,
  ≤ 5 000 bp), and a `genome: DnaString` (`t`, ≤ 50 000 bp), constructed
  only through a smart constructor returning
  `Either[SimilarMotifsProblemError, …]`.
- Add a `SimilarMotifsProblemError` ADT (`KOutOfRange`, `MotifTooLong`,
  `GenomeTooLong`).
- Add a `SimilarMotif` output ADT (plain `final case class`) carrying
  `location: Int` (1-based start in `t`) and `length: Int`.
- Add a `bio.algorithms.analysis.SimilarMotifs` object with
  `findAll(problem): List[SimilarMotif]` implementing approximate string
  matching: a forward approximate-matching DP that identifies valid end
  positions, then a per-end backward DP that enumerates the exact
  `(location, length)` pairs with `d_E(s, t′) ≤ k`, banded to the
  `|len − m| ≤ k` length window. Results are sorted by `(location, length)`.
- Add a `KSIMProb` runner wired into `Main`, following the existing
  per-problem runner pattern.

## Capabilities

### New Capabilities
- `finding-all-similar-motifs`: enumerate every substring `t′` of a genome
  `t` with edit distance `d_E(s, t′) ≤ k` to a motif `s`, each reported as
  a `(location, length)` pair — input bundle, error ADT, output ADT, and
  the `SimilarMotifs.findAll` algorithm.

### Modified Capabilities
<!-- None. Brand-new capability; no existing spec requirements change. -->

## Impact

- **New code** (subdomain `analysis`, alongside SIMS/OSYM):
  - `src/main/scala/bio/domain/analysis/SimilarMotifsProblem.scala`
  - `src/main/scala/bio/domain/analysis/SimilarMotifsProblemError.scala`
  - `src/main/scala/bio/domain/analysis/SimilarMotif.scala`
  - `src/main/scala/bio/algorithms/analysis/SimilarMotifs.scala`
  - `src/main/scala/bio/problems/KSIMProb.scala` (+ wiring in `bio/Main.scala`)
- **New tests**:
  - `src/test/scala/bio/domain/analysis/SimilarMotifsProblemSpec.scala`
  - `src/test/scala/bio/algorithms/analysis/SimilarMotifsSpec.scala`
- **Dependencies**: none new. Reuses `DnaString` (cap 100 000 ≥ 50 000, so
  both inputs fit) and Cats Effect `IO` for the runner.
- **No breaking changes**: purely additive.
