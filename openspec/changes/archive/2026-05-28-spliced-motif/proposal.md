## Why

Rosalind problem 37 ("Finding a Spliced Motif", SSEQ) asks: given two DNA strings `s` and `t`, find one collection of indices into `s` such that the symbols of `t` appear (in order, not necessarily contiguous) at those positions — i.e., `t` is a *subsequence* of `s`. This is a step beyond `motif-finding` (SUBS, spec 9), which finds *substring* (contiguous) matches; spliced motifs model the way introns can split exons in pre-mRNA, where the post-splicing protein sequence is a noncontiguous selection from the original DNA. Adding it gives the framework its first subsequence-search primitive and completes Rosalind problem 37 — the next problem after the now-archived MOTZ (spec 36).

## What Changes

- Add a new validated domain type `bio.domain.analysis.SplicedMotifProblem` wrapping a source `DnaString s` and target `DnaString t`. The smart constructor enforces `s.value.length <= 1000` and `t.value.length <= 1000` (the Rosalind caps). Empty `s` and/or empty `t` are accepted (an empty target is trivially a subsequence of any source — yielding `Vector.empty`).
- Add a new algorithm object `bio.algorithms.analysis.SplicedMotif` exposing `find(problem: SplicedMotifProblem): Option[Vector[Int]]`. Returns `Some(indices)` with the lexicographically-leftmost (greedy) 1-indexed positions of `t`'s symbols inside `s`, or `None` when no valid subsequence exists (i.e., `t` is not a subsequence of `s`). The greedy walk is `O(|s| + |t|)`.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `SplicedMotifProblemSpec`: accepts the canonical Rosalind sample (`s = ACGTACGTGACG`, `t = GTA`); accepts empty `s` with empty `t`; accepts non-empty `s` with empty `t`; accepts at the 1000-char upper bound for `s` and `t`; rejects 1001-char `s`/`t` as `SourceTooLong`/`TargetTooLong`; companion `apply`/`copy` leak-proofness.
  - `SplicedMotifSpec`: canonical Rosalind sample → `Some(Vector(3, 4, 5))` (greedy answer; the published sample `3 8 10` is *also* valid but our deterministic rule emits the leftmost match); empty `t` → `Some(Vector.empty)`; `s = t` → `Some(Vector(1, 2, ..., n))`; `t` not a subsequence of `s` (e.g. `s = AAA, t = AAAA`) → `None`; single-char `t` matched at the end of `s` → `Some(Vector(n))`; mixed-alphabet edge cases.
- No changes to existing capabilities; no breaking changes. `DnaString` is reused as-is.

## Capabilities

### New Capabilities
- `spliced-motif`: Finds a subsequence-match of a target DNA pattern inside a source DNA string. Includes the validated `SplicedMotifProblem` input bundle (length caps on both strings) and the `SplicedMotif.find` greedy `O(|s| + |t|)` algorithm returning `Option[Vector[Int]]` of 1-indexed positions.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/analysis/SplicedMotifProblem.scala`
  - `src/main/scala/bio/domain/analysis/SplicedMotifProblemError.scala`
  - `src/main/scala/bio/algorithms/analysis/SplicedMotif.scala`
  - `src/test/scala/bio/domain/analysis/SplicedMotifProblemSpec.scala`
  - `src/test/scala/bio/algorithms/analysis/SplicedMotifSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Slots into the existing `bio.{algorithms,domain}.analysis` family alongside `motif-finding` (SUBS, spec 9), `hamming-distance` (HAMM, spec 10), `random-string-matching` (PROB, spec 19), `failure-array` (KMP, spec 31), and `genetic-character-table` (spec 33) — all string-analysis siblings.
