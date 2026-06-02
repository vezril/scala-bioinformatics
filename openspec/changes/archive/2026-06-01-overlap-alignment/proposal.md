## Why

Rosalind problem OAP ("Overlap Alignment") asks for the best overlap between two
DNA strings — the highest-scoring alignment of a suffix of `s` against a prefix of
`t`. This is a foundational semiglobal-alignment primitive for read overlap and
assembly, and it extends the project's existing alignment family (fitting, global,
local, affine) with a new free-end-gap regime that none of the current algorithms
cover.

## What Changes

- Add a validated `OverlapAlignmentProblem` domain bundle wrapping two DNA strings
  `s` and `t` (each ≤ 10000 bp), constructed through a smart constructor returning
  `Either[OverlapAlignmentProblemError, OverlapAlignmentProblem]` with
  first-failure-wins validation (`s` length, then `t` length). Empty strings are
  accepted.
- Add an `OverlapAlignmentProblemError` ADT with `STooLong` and `TTooLong` cases.
- Add an `OverlapAlignment` result type carrying the optimal score and the two
  augmented (gapped) alignment strings, with a `format: String` rendering the score
  and the two aligned strings on separate lines.
- Add a pure, total `OverlapAlignment.align` algorithm computing the optimal overlap
  alignment (suffix of `s` vs prefix of `t`) with scoring match `+1`, substitution
  `-2`, gap `-2`, using the established imperative dynamic-programming convention
  internal to the alignment family.
- Add an `OAPProb` IO runner that reads the two FASTA records from `oap_data.txt`,
  validates them into an `OverlapAlignmentProblem`, computes the overlap alignment,
  and prints the formatted result through `IO`; invalid input prints an error rather
  than throwing. Wire it into `Main`.

## Capabilities

### New Capabilities
- `overlap-alignment`: Validates a pair of DNA strings and computes their optimal
  overlap alignment (a suffix of `s` against a prefix of `t`) with match `+1`,
  substitution `-2`, and linear gap `-2`, returning the score and the two aligned
  strings.

### Modified Capabilities
<!-- None — this is a purely additive capability. -->

## Impact

- New domain types in `bio.domain.analysis`: `OverlapAlignmentProblem`,
  `OverlapAlignmentProblemError`, `OverlapAlignment`.
- New algorithm in `bio.algorithms.analysis`: `OverlapAlignment`.
- New IO runner `bio.problems.OAPProb`, wired into `bio.Main`.
- Reads `src/main/scala/resources/oap_data.txt` via the existing
  `FastaFileReader`/`FastaRecord` infrastructure.
- No changes to existing domain types, algorithms, or specs.
