## Why

Rosalind spec 41 (EDTA — "Edit Distance Alignment") is the natural follow-on to spec 40 (EDIT). Where EDIT returns only the integer Levenshtein distance, EDTA also returns *how* — an optimal alignment in the form of two augmented strings `s'` and `t'` (the original strings with `-` gap symbols inserted) that realise the minimum-cost transformation. This is the entry point for every alignment-with-traceback algorithm on the Rosalind track (global / local / affine alignment, scoring matrices, etc.) and finally exercises the DP-table-with-pointers pattern that the EDIT implementation deliberately deferred.

## What Changes

- Add a validated domain bundle `bio.domain.protein.EditDistanceAlignmentProblem` wrapping two `ProteinString`s with the Rosalind length cap (1000 aa each).
- Add an output ADT `bio.domain.protein.EditAlignment(distance: Int, augmentedLeft: String, augmentedRight: String)` representing the integer distance plus the two augmented strings (which may contain the `-` gap symbol and therefore are plain `String`, not `ProteinString`).
- Add an algorithm object `bio.algorithms.protein.EditDistanceAlignment` exposing `align(problem: EditDistanceAlignmentProblem): EditAlignment` that returns one optimal alignment using the classical `O(m · n)` DP + traceback.
- Add an error ADT `EditDistanceAlignmentProblemError` with the two cap violations (`LeftTooLong`, `RightTooLong`).
- Wire a `bio.problems.EDTAProb` runner consistent with the other Prob-style entry points.

## Capabilities

### New Capabilities
- `edit-distance-alignment`: Validated two-protein input bundle, an `EditAlignment` output ADT, and the classical `O(m · n)` Levenshtein DP + traceback returning one optimal alignment (integer distance + two augmented strings).

### Modified Capabilities
<!-- none -->

## Impact

- New files under `bio.domain.protein` (problem + error ADT + output ADT) and `bio.algorithms.protein` (algorithm).
- New runner under `bio.problems` (`EDTAProb`).
- New spec test suites mirroring the conventions used for `EditDistance` (EDIT).
- No changes to existing capabilities; purely additive. The `EditDistance.compute` algorithm from spec 40 is untouched — `EditDistanceAlignment` builds its own DP table because it also needs the table for traceback.
