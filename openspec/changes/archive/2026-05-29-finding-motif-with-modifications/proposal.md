## Why

Rosalind spec 48 (SIMS — "Finding a Motif with Modifications") asks us to
locate a motif `t` inside a much longer string `s` while tolerating
substitutions, insertions, and deletions. The right tool is a *fitting
alignment*: align a substring of `s` against **all** of `t`, maximizing a
simple mismatch score (+1 match, -1 for every mismatch / inserted /
deleted symbol). This completes the alignment family already in the
framework (global GLOB, local LOCA, edit EDTA) with the third classical
variant — fitting (semi-global) alignment — and is the natural next step
after spec 47.

## What Changes

- Add a validated `FittingAlignmentProblem` input bundle (subdomain
  `analysis`) wrapping a `text: DnaString` (`s`, ≤ 10 000 nt) and a
  `motif: DnaString` (`t`, ≤ 1 000 nt), constructed only through a smart
  constructor returning `Either[FittingAlignmentProblemError, …]`.
- Add a `FittingAlignmentProblemError` ADT (`TextTooLong`, `MotifTooLong`).
- Add a `FittingAlignment` output ADT (plain `final case class`) carrying
  `score: Int`, `augmentedText: String`, `augmentedMotif: String`.
- Add a `bio.algorithms.analysis.FittingAlignment` object with
  `align(problem): FittingAlignment` implementing the classic `O(m · n)`
  fitting-alignment DP + traceback under the +1/-1 mismatch score.
- Add a `SIMSProb` runner wired into `Main`, following the existing
  per-problem runner pattern.

## Capabilities

### New Capabilities
- `finding-motif-with-modifications`: PAM-free fitting (semi-global)
  alignment of a motif against the best-matching substring of a longer
  string under the +1/-1 mismatch score — input bundle, error ADT, output
  ADT, and the `FittingAlignment.align` algorithm.

### Modified Capabilities
<!-- None. This is a brand-new capability; no existing spec requirements change. -->

## Impact

- **New code** (subdomain `analysis`, mirroring the existing alignment
  capabilities):
  - `src/main/scala/bio/domain/analysis/FittingAlignmentProblem.scala`
  - `src/main/scala/bio/domain/analysis/FittingAlignmentProblemError.scala`
  - `src/main/scala/bio/domain/analysis/FittingAlignment.scala`
  - `src/main/scala/bio/algorithms/analysis/FittingAlignment.scala`
  - `src/main/scala/bio/problems/SIMSProb.scala` (+ wiring in `bio/Main.scala`)
- **New tests**:
  - `src/test/scala/bio/domain/analysis/FittingAlignmentProblemSpec.scala`
  - `src/test/scala/bio/algorithms/analysis/FittingAlignmentSpec.scala`
- **Dependencies**: none new. Reuses `DnaString` (cap 100 000 ≥ 10 000,
  so both inputs fit) and Cats Effect `IO` for the runner.
- **No breaking changes**: purely additive.
