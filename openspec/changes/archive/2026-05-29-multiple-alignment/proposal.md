## Why

Rosalind spec 43 (MULT — "Multiple Alignment") generalises pairwise alignment (EDIT, EDTA, GLOB) into a *4-string* simultaneous alignment problem. The score is summed over all `C(4, 2) = 6` augmented-string pairs using a simple linear scheme: matched symbols (including matched gap symbols) score 0, mismatches score -1. This is the first Rosalind problem requiring *N-dimensional* dynamic programming + traceback (the table is `(n+1)^4` instead of `(m+1) × (n+1)`) and is the natural prerequisite for any future multi-sequence alignment work.

## What Changes

- Add a validated domain bundle `bio.domain.analysis.MultipleAlignmentProblem` wrapping exactly four DNA strings each capped at 10 bp (Rosalind constraint).
- Add an output ADT `bio.domain.analysis.MultipleAlignment(score: Int, augmentedStrings: Vector[String])` carrying the maximum alignment score plus the four augmented strings.
- Add an algorithm object `bio.algorithms.analysis.MultipleAlignment` exposing `align(problem): MultipleAlignment` that returns one optimal alignment using a 4-dimensional `O(((n+1)^4) · 15)` DP + traceback.
- Add an error ADT `MultipleAlignmentProblemError` with `WrongNumberOfStrings(actual, expected)` and `StringTooLong(index, length, max)` variants.
- Wire a `bio.problems.MULTProb` runner consistent with the other Prob-style entry points.

## Capabilities

### New Capabilities
- `multiple-alignment`: Validated four-DNA-string input bundle (each ≤ 10 bp), an `MultipleAlignment` output ADT, and 4-dimensional DP + traceback returning one optimal alignment under the linear `match = 0` / `mismatch = -1` scoring scheme summed over all 6 pairs.

### Modified Capabilities
<!-- none -->

## Impact

- New files under `bio.domain.analysis` (problem + error ADT + output ADT) and `bio.algorithms.analysis` (algorithm).
- New runner under `bio.problems` (`MULTProb`).
- New spec test suites mirroring the conventions used for `EditDistanceAlignment` (EDTA) and `GlobalAlignmentScore` (GLOB).
- No changes to existing capabilities; purely additive. The pairwise alignment algorithms remain untouched — `MultipleAlignment` is a separate 4-D DP because the dimensionality, the per-cell move set (15 non-empty subsets of {0,1,2,3}), and the column scoring (sum over 6 pairs) are all different.
