## Why

Rosalind GAFF ("Global Alignment with Scoring Matrix and Affine Gap Penalty",
spec 52) is the next problem in the alignment series. It is the natural
generalisation of GLOB (linear gap) and GCON (constant gap): under an
*affine* gap penalty a gap of length `L` costs `a + b·(L−1)`, charging a
gap-opening penalty `a = 11` once and a gap-extension penalty `b = 1` for
each further symbol. Unlike GLOB/GCON, GAFF must also return *one optimal
alignment* (two augmented strings), not just the score — so it additionally
exercises traceback through the three-state DP.

## What Changes

- Add a validated `AffineGapAlignmentProblem` input bundle (two protein
  strings `left`/`right`, each capped at the GAFF limit of **100 aa**).
- Add an `AffineGapAlignment` result ADT carrying the maximum score plus the
  two augmented strings (`-`-padded) realising one optimal alignment.
- Add an `AffineGapAlignment` algorithm object implementing the three-state
  (Gotoh) affine-gap DP under BLOSUM62 (`a = 11`, `b = 1`) with traceback,
  reproducing the canonical Rosalind sample `PRTEINS`/`PRTWPSEIN` → `8`.
- Add a `GAFFProb` problem runner (score on the first line, the two augmented
  strings on the next two) and wire it into `Main`.

## Capabilities

### New Capabilities
- `global-alignment-affine-gap`: a validated input bundle, a result ADT
  (score + two augmented alignment strings), and a three-state affine-gap
  Needleman-Wunsch algorithm (BLOSUM62, gap-open 11, gap-extend 1) returning
  the maximum global alignment score and one optimal alignment.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files: `bio.domain.protein.AffineGapAlignmentProblem`,
  `AffineGapAlignmentProblemError`, `AffineGapAlignment` (result ADT);
  `bio.algorithms.protein.AffineGapAlignment`; `bio.problems.GAFFProb`.
- One-line wiring change in `bio/Main.scala`.
- Reuses the existing `Blosum62` matrix and `ProteinString` domain type. No
  changes to existing capabilities; rollback = delete the new files and the
  wiring line.
