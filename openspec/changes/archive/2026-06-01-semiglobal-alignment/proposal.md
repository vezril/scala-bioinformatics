## Why

Rosalind problem SMGB ("Semiglobal Alignment") asks for the best alignment of two DNA
strings in which gaps at the **leading or trailing ends of either string are free**.
This is the symmetric "free end-gap" regime — distinct from the project's existing
one-sided overlap alignment (OAP, suffix-of-`s` vs prefix-of-`t`) — and it rounds out
the alignment family (global, local, fitting, overlap) with the last common
end-gap-free variant used in read alignment and assembly.

## What Changes

- Add a validated `SemiglobalAlignmentProblem` domain bundle wrapping two DNA strings
  `s` and `t` (each ≤ 10000 bp), constructed through a smart constructor returning
  `Either[SemiglobalAlignmentProblemError, SemiglobalAlignmentProblem]` with
  first-failure-wins validation (`s` length, then `t` length). Empty strings are
  accepted.
- Add a `SemiglobalAlignmentProblemError` ADT with `STooLong` and `TTooLong` cases.
- Add a `SemiglobalAlignment` result type carrying the optimal score and the two
  augmented (gapped) alignment strings, with a `format` method rendering the score and
  the two aligned strings on separate lines.
- Add a pure, total `SemiglobalAlignment.align` algorithm computing the optimal
  semiglobal alignment (free leading/trailing gaps on both strings) with scoring match
  `+1`, substitution `-1`, gap `-1`, using the established imperative
  dynamic-programming convention internal to the alignment family.
- Add a `SMGBProb` IO runner that reads the two FASTA records from `smgb_data.txt`,
  validates them into a `SemiglobalAlignmentProblem`, computes the alignment, and
  prints the formatted result through `IO`; invalid input prints an error rather than
  throwing. Wire it into `Main`.

## Capabilities

### New Capabilities
- `semiglobal-alignment`: Validates a pair of DNA strings and computes their optimal
  semiglobal alignment — an alignment of all of `s` against all of `t` in which gaps
  appearing as prefixes or suffixes of either string do not contribute to the score
  (match `+1`, substitution `-1`, gap `-1`), returning the score and the two aligned
  strings.

### Modified Capabilities
<!-- None — this is a purely additive capability; overlap-alignment is unchanged. -->

## Impact

- New domain types in `bio.domain.analysis`: `SemiglobalAlignmentProblem`,
  `SemiglobalAlignmentProblemError`, `SemiglobalAlignment`.
- New algorithm in `bio.algorithms.analysis`: `SemiglobalAlignment`.
- New IO runner `bio.problems.SMGBProb`, wired into `bio.Main`.
- Reads `src/main/scala/resources/smgb_data.txt` via the existing
  `FastaFileReader`/`FastaRecord` infrastructure.
- No changes to existing domain types, algorithms, or specs (the overlap-alignment
  capability is the nearest sibling and is left untouched).
