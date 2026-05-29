## Why

Rosalind spec 53 (LAFF — "Local Alignment with Affine Gap Penalty") asks for the
maximum *local* alignment score of two protein strings under the BLOSUM62
substitution matrix and an affine gap penalty (gap-open 11, gap-extend 1),
together with the aligned substrings. This combines the local (Smith-Waterman)
model already implemented for LOCA with the affine three-state model already
implemented for GAFF — a distinct capability not yet covered.

## What Changes

- Add a validated input bundle for the LAFF problem: two `ProteinString`s, each
  capped at 10,000 amino acids per the Rosalind spec.
- Add a result ADT carrying the maximum local affine alignment score and the two
  aligned substrings `r` (of `s`) and `u` (of `t`).
- Add an algorithm that computes the score and one optimal substring pair via a
  local affine three-state (Gotoh-style Smith-Waterman) dynamic program under
  BLOSUM62 with gap-open 11 and gap-extend 1, reproducing the canonical sample
  `PLEASANTLY` / `MEANLY` → `12` with substrings `LEAS` / `MEAN`.
- Add a problem runner and wire it into `Main`.

## Capabilities

### New Capabilities
- `local-alignment-affine-gap`: Validated two-protein input bundle (≤ 10,000 aa
  each), a result ADT (score + two aligned substrings), and a local affine
  three-state alignment algorithm under BLOSUM62 with gap-open 11 / gap-extend 1.

### Modified Capabilities
<!-- None: this is a standalone new capability. -->

## Impact

- New domain types under `bio.domain.protein`
  (`LocalAffineAlignmentProblem`, `LocalAffineAlignmentProblemError`,
  `LocalAffineAlignment` result ADT).
- New algorithm under `bio.algorithms.protein.LocalAffineAlignment`
  (reuses the existing `Blosum62` and `AminoAcid` infrastructure).
- New runner `bio.problems.LAFFProb`, wired into `bio.Main`.
- No changes to existing capabilities or shared infrastructure.
