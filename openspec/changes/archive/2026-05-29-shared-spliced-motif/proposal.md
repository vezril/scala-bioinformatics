## Why

Rosalind problem 39 ("Finding a Shared Spliced Motif", LCSQ) takes two DNA strings and asks for their *longest common subsequence* — a sequence of symbols that appears in order (not necessarily contiguous) in *both* strings, with maximum length. This is the textbook **Longest Common Subsequence** problem from algorithmic DP, and it's the subsequence analogue of the now-archived `shared-motif` (LCSM, spec 38). Where LCSM finds contiguous shared runs across many strings, LCSQ finds spread-out shared patterns across two — biologically the workhorse for measuring sequence similarity in the presence of indels (insertions/deletions). Adding it gives the framework its first classical two-string DP primitive and completes Rosalind problem 39 — the next problem after the now-archived LCSM (spec 38).

## What Changes

- Add a new validated domain type `bio.domain.analysis.SharedSplicedMotifProblem` wrapping two DNA strings (`left` and `right`). The smart constructor enforces both Rosalind length caps:
  - `left.value.length <= 1000`, else `LeftTooLong(length, max)`;
  - `right.value.length <= 1000`, else `RightTooLong(length, max)`.
  Empty strings are accepted (the LCS of any string with the empty string is `""`).
- Add a new algorithm object `bio.algorithms.analysis.SharedSplicedMotif` exposing `find(problem: SharedSplicedMotifProblem): String`. Returns a longest common subsequence of `left` and `right` as `String`. The spec explicitly permits *any* valid LCS when multiple longest matches exist; we adopt the **standard backtracking convention** (prefer "up" on ties: if `dp(i-1)(j) >= dp(i)(j-1)`, advance the source pointer) for deterministic tests.
- Algorithm shape: classical O(m·n) DP table `dp(i)(j) = LCS-length of left[0..i)` and `right[0..j)`. Fill bottom-up; reconstruct the LCS by backtracking from `dp(m)(n)`. At Rosalind cap `m, n ≤ 1000`: 10⁶ cells (~ a few MB) and ~10⁶ comparisons — milliseconds.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `SharedSplicedMotifProblemSpec`: accepts the canonical Rosalind sample (`AACCTTGG`, `ACACTGTGA`); accepts both empty strings; accepts each empty individually; accepts both at the 1000-char upper bound; rejects 1001-char `left` as `LeftTooLong`; rejects 1001-char `right` as `RightTooLong`; companion `apply`/`copy` leak-proofness.
  - `SharedSplicedMotifSpec`: canonical Rosalind sample → result has length 6 AND is a subsequence of both inputs (property-based, since multiple valid LCSes exist); identical strings → that string; empty left → `""`; empty right → `""`; no shared character (`ACG`, `TTT`) → `""`; single common char (`A`, `TA`) → `"A"`; two-char common subsequence (`ABC`, `XBYCZ`) — wait, alphabet is DNA, so `ACG`, `TCG` → `"CG"` (unique-LCS test).
- No changes to existing capabilities; no breaking changes. `DnaString` is reused as-is.

## Capabilities

### New Capabilities
- `shared-spliced-motif`: Finds a longest common subsequence of two DNA strings (Rosalind LCSQ). Includes the validated `SharedSplicedMotifProblem` input bundle (length caps on both strings) and the `SharedSplicedMotif.find` classical `O(m · n)` DP + backtracking algorithm returning an LCS as `String`.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/analysis/SharedSplicedMotifProblem.scala`
  - `src/main/scala/bio/domain/analysis/SharedSplicedMotifProblemError.scala`
  - `src/main/scala/bio/algorithms/analysis/SharedSplicedMotif.scala`
  - `src/test/scala/bio/domain/analysis/SharedSplicedMotifProblemSpec.scala`
  - `src/test/scala/bio/algorithms/analysis/SharedSplicedMotifSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Slots into the existing `bio.{algorithms,domain}.analysis` family alongside `motif-finding` (SUBS, spec 9), `spliced-motif` (SSEQ, spec 37), `shared-motif` (LCSM, spec 38), `hamming-distance` (HAMM, spec 10), `random-string-matching` (PROB, spec 19), `failure-array` (KMP, spec 31), and `genetic-character-table` (spec 33).
