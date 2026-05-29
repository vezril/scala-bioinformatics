## Why

Rosalind spec 47 (LOCA — "Local Alignment with Scoring Matrix") generalises GLOB's global Needleman-Wunsch into the *local* setting via Smith-Waterman: find the *substrings* `r ⊆ s` and `u ⊆ t` whose pairwise alignment score is maximised under the PAM250 substitution matrix and a linear gap penalty of -5. The 0-clamp trick — every DP cell can "start fresh" by choosing 0 over a negative-scoring extension — distinguishes local from global, and the traceback recovers the substrings as the contiguous regions that participated in the optimum. This is the second biological scoring matrix on the Rosalind track (after BLOSUM62 in GLOB) and the foundation for affine-gap and free-shift alignment variants downstream.

## What Changes

- Add a `bio.algorithms.protein.Pam250` object exposing the 20 × 20 PAM250 amino-acid substitution-score matrix as a total `score(a: AminoAcid, b: AminoAcid): Int` function (mirroring `Blosum62`).
- Add a validated domain bundle `bio.domain.protein.LocalAlignmentProblem` wrapping two `ProteinString`s with the Rosalind length cap (1000 aa each).
- Add an output ADT `bio.domain.protein.LocalAlignment(score: Int, leftSubstring: String, rightSubstring: String)` carrying the maximum local-alignment score plus the two substrings that achieve it.
- Add an algorithm object `bio.algorithms.protein.LocalAlignment` exposing `compute(problem: LocalAlignmentProblem): LocalAlignment` that runs Smith-Waterman and traces back from the global-max cell.
- Add an error ADT `LocalAlignmentProblemError` with the two cap violations (`LeftTooLong`, `RightTooLong`).
- Wire a `bio.problems.LOCAProb` runner consistent with the other Prob-style entry points.

## Capabilities

### New Capabilities
- `local-alignment-with-scoring`: PAM250 substitution-score lookup, validated two-protein input bundle, output ADT, and the classical Smith-Waterman `O(m · n)` DP + traceback returning the maximum local-alignment score plus the two recovered substrings under PAM250 + linear gap penalty -5.

### Modified Capabilities
<!-- none -->

## Impact

- New files under `bio.domain.protein` (problem + error ADT + output ADT) and `bio.algorithms.protein` (`Pam250` + algorithm).
- New runner under `bio.problems` (`LOCAProb`).
- New spec test suites mirroring `GlobalAlignmentScore` (GLOB) and `EditDistanceAlignment` (EDTA).
- No changes to existing capabilities; purely additive. The GLOB Needleman-Wunsch algorithm remains untouched — `LocalAlignment` is a separate DP because the 0-clamp at every cell + traceback-until-zero + substring-output (no gaps) are all structurally different from a global alignment.
