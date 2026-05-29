## Why

Rosalind spec 40 (EDIT — "Edit Distance") asks for the minimum number of single-symbol edit operations (substitution, insertion, deletion) needed to transform one protein string into another. This is the classical Levenshtein distance and the cornerstone of every sequence-alignment algorithm that follows on the Rosalind track. We have edit-distance-flavoured DP for nucleic-acid LCS already (LCSQ, spec 39), but no Levenshtein implementation for proteins — adding it completes the basic alignment toolkit and is the prerequisite for downstream alignment problems.

## What Changes

- Add a validated domain bundle `bio.domain.protein.EditDistanceProblem` wrapping two `ProteinString`s with the Rosalind length cap (1000 aa each).
- Add an algorithm object `bio.algorithms.protein.EditDistance` exposing `compute(problem: EditDistanceProblem): Int` that returns the Levenshtein distance using the classical `O(m · n)` DP.
- Add an error ADT `EditDistanceProblemError` with the two cap violations (`LeftTooLong`, `RightTooLong`).
- Wire a `bio.problems.EDITProb` runner consistent with the other Prob-style entry points.

## Capabilities

### New Capabilities
- `edit-distance`: Validated two-protein input bundle and the classical `O(m · n)` Levenshtein DP returning the minimum number of substitutions/insertions/deletions needed to transform `left` into `right`.

### Modified Capabilities
<!-- none -->

## Impact

- New files under `bio.domain.protein` (problem + error ADT) and `bio.algorithms.protein` (algorithm).
- New runner under `bio.problems` (`EDITProb`).
- New spec test suites mirroring the conventions used for `SharedSplicedMotif` (LCSQ).
- No changes to existing capabilities; purely additive.
