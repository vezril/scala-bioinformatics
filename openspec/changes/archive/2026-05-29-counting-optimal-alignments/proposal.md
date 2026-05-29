## Why

Rosalind spec 46 (CTEA — "Counting Optimal Alignments") asks how *many* distinct optimal alignments exist between two protein strings under the standard Levenshtein edit-distance metric, returned modulo `134_217_727` (= `2^27 - 1`). This is the natural counterpart to EDIT (spec 40, the integer distance) and EDTA (spec 41, *one* optimal alignment): instead of finding a minimum or constructing an example, we enumerate the equivalence class. The algorithmic trick is a one-shot extension of the Levenshtein DP — fill a parallel "number of optimal predecessors" table alongside the cost table — and it's a foundational step toward downstream counting-style alignment problems on the Rosalind track.

## What Changes

- Add a validated domain bundle `bio.domain.protein.OptimalAlignmentCountProblem` wrapping two `ProteinString`s with the Rosalind length cap (1000 aa each).
- Add an algorithm object `bio.algorithms.protein.OptimalAlignmentCount` exposing `compute(problem: OptimalAlignmentCountProblem): Int` that returns the number of optimal alignments modulo `2^27 - 1`.
- Add an error ADT `OptimalAlignmentCountProblemError` with the two cap violations (`LeftTooLong`, `RightTooLong`).
- Wire a `bio.problems.CTEAProb` runner consistent with the other Prob-style entry points.

## Capabilities

### New Capabilities
- `counting-optimal-alignments`: Validated two-protein input bundle and the parallel Levenshtein + count DP returning the integer count of optimal alignments modulo `134_217_727`.

### Modified Capabilities
<!-- none -->

## Impact

- New files under `bio.domain.protein` (problem + error ADT) and `bio.algorithms.protein` (algorithm).
- New runner under `bio.problems` (`CTEAProb`).
- New spec test suites mirroring the conventions used for `EditDistance` (EDIT).
- No changes to existing capabilities; purely additive. The `EditDistance.compute` and `EditDistanceAlignment.align` algorithms remain untouched — `OptimalAlignmentCount` runs its own DP loop because it carries a parallel `cnt(i)(j)` accumulator that's structurally easier to read inline than to bolt onto either prior implementation.
