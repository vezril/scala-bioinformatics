## Why

Rosalind spec 51 (GCON — "Global Alignment with Constant Gap Penalty")
asks for the maximum global alignment score of two protein strings under
BLOSUM62, but with a **constant** gap penalty: every gap (a maximal run of
contiguous insertions or deletions) is charged a flat `5`, regardless of
its length. This is the third gap model in the alignment family — after the
*linear* penalty of GLOB (spec 42) and the edit-distance unit gap (EDIT) —
and introduces the multi-state (Gotoh-style) DP needed for any
length-aware gap model, the foundation for affine gaps later.

## What Changes

- Add a validated `ConstantGapAlignmentScoreProblem` input bundle
  (subdomain `protein`) wrapping two protein strings `left` (`s`) and
  `right` (`t`), each ≤ 1 000 aa, constructed only through a smart
  constructor returning `Either[ConstantGapAlignmentScoreProblemError, …]`.
- Add a `ConstantGapAlignmentScoreProblemError` ADT (`LeftTooLong`,
  `RightTooLong`).
- Add a `bio.algorithms.protein.ConstantGapAlignmentScore` object with
  `compute(problem): Int` implementing the three-state `O(m · n)`
  constant-gap DP under BLOSUM62 (gap-open `-5`, gap-extend `0`), returning
  the maximum alignment score.
- Add a `GCONProb` runner wired into `Main`, following the existing
  per-problem runner pattern.

## Capabilities

### New Capabilities
- `global-alignment-constant-gap`: maximum global alignment score of two
  protein strings under BLOSUM62 with a constant (length-independent) gap
  penalty of 5 — input bundle, error ADT, and the
  `ConstantGapAlignmentScore.compute` algorithm.

### Modified Capabilities
<!-- None. Brand-new capability; no existing spec requirements change. -->

## Impact

- **New code** (subdomain `protein`, alongside GLOB/LOCA):
  - `src/main/scala/bio/domain/protein/ConstantGapAlignmentScoreProblem.scala`
  - `src/main/scala/bio/domain/protein/ConstantGapAlignmentScoreProblemError.scala`
  - `src/main/scala/bio/algorithms/protein/ConstantGapAlignmentScore.scala`
  - `src/main/scala/bio/problems/GCONProb.scala` (+ wiring in `bio/Main.scala`)
- **New tests**:
  - `src/test/scala/bio/domain/protein/ConstantGapAlignmentScoreProblemSpec.scala`
  - `src/test/scala/bio/algorithms/protein/ConstantGapAlignmentScoreSpec.scala`
- **Dependencies**: none new. Reuses `ProteinString`, `AminoAcid`,
  `Blosum62`, and Cats Effect `IO`.
- **No breaking changes**: purely additive.
