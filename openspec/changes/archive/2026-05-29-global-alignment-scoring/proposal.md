## Why

Rosalind spec 42 (GLOB — "Global Alignment with Scoring Matrix") generalises plain Levenshtein (EDIT, spec 40) into the classical Needleman-Wunsch global alignment: substitutions are scored using the BLOSUM62 amino-acid substitution matrix, and gaps incur a fixed linear penalty of −5. This is the first Rosalind problem requiring a real biological scoring matrix — BLOSUM62 — and lays the foundation for downstream alignment variants (local alignment with SMITH-WATERMAN, affine-gap alignment, and PAM-matrix scoring on the Rosalind track).

## What Changes

- Add a `bio.algorithms.protein.Blosum62` object exposing the 20 × 20 BLOSUM62 amino-acid substitution-score matrix as a total `score(a: AminoAcid, b: AminoAcid): Int` function.
- Add a validated domain bundle `bio.domain.protein.GlobalAlignmentScoreProblem` wrapping two `ProteinString`s with the Rosalind length cap (1000 aa each).
- Add an algorithm object `bio.algorithms.protein.GlobalAlignmentScore` exposing `compute(problem: GlobalAlignmentScoreProblem): Int` that returns the maximum global alignment score using BLOSUM62 substitution scoring and a linear gap penalty of −5.
- Add an error ADT `GlobalAlignmentScoreProblemError` with the two cap violations (`LeftTooLong`, `RightTooLong`).
- Wire a `bio.problems.GLOBProb` runner consistent with the other Prob-style entry points.

## Capabilities

### New Capabilities
- `global-alignment-scoring`: BLOSUM62 substitution-score lookup, validated two-protein input bundle, and the classical Needleman-Wunsch `O(m · n)` DP returning the maximum global alignment score under BLOSUM62 + linear gap penalty −5.

### Modified Capabilities
<!-- none -->

## Impact

- New files under `bio.domain.protein` (problem + error ADT) and `bio.algorithms.protein` (`Blosum62` + algorithm).
- New runner under `bio.problems` (`GLOBProb`).
- New spec test suites mirroring the conventions used for `EditDistance` (EDIT) and `EditDistanceAlignment` (EDTA).
- No changes to existing capabilities; purely additive. The Levenshtein DP from spec 40/41 is untouched — `GlobalAlignmentScore` is a separate Needleman-Wunsch DP because the scoring function (BLOSUM62 vs unit-cost) and optimisation direction (maximise vs minimise) differ.
