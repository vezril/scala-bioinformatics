## 1. SharedSplicedMotifProblem ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/analysis/SharedSplicedMotifProblemSpec.scala` covering: accepts the canonical Rosalind sample (`AACCTTGG`, `ACACTGTGA`); accepts two empty strings; accepts empty left + non-empty right; accepts non-empty left + empty right; accepts both at 1000-char boundary; rejects 1001-char left as `LeftTooLong(1001, 1000)`; rejects 1001-char right as `RightTooLong(1001, 1000)`; companion `apply` and `copy` leak-proofness via `assertDoesNotCompile`. Use a private `dna(s: String): DnaString` helper. Run `sbt Test/compile` and confirm Red.
- [x] 1.2 Create `src/main/scala/bio/domain/analysis/SharedSplicedMotifProblemError.scala` as a `sealed trait` with cases `LeftTooLong(length: Int, max: Int)` and `RightTooLong(length: Int, max: Int)`.
- [x] 1.3 Create `src/main/scala/bio/domain/analysis/SharedSplicedMotifProblem.scala` as `sealed abstract case class SharedSplicedMotifProblem(left: DnaString, right: DnaString)` with companion `from(left, right): Either[SharedSplicedMotifProblemError, SharedSplicedMotifProblem]` enforcing both 1000-char caps in `left → right` validation order.
- [x] 1.4 Run `sbt testOnly bio.domain.analysis.SharedSplicedMotifProblemSpec` and confirm Green.

## 2. SharedSplicedMotif algorithm (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/algorithms/analysis/SharedSplicedMotifSpec.scala` covering: canonical Rosalind sample → result has length 6 AND is a subsequence of both inputs (property-based); identical strings (`ACGT`, `ACGT`) → `"ACGT"`; empty left → `""`; empty right → `""`; no shared char (`ACG`, `TTT`) → `""`; single common char (`A`, `TA`) → `"A"`; unique LCS (`ACG`, `TCG`) → `"CG"`. Include a private `isSubsequence(needle: String, haystack: String): Boolean` helper for the property-based canonical-sample test. Use a private `fixture(left: String, right: String): SharedSplicedMotifProblem` helper. Run `sbt Test/compile` and confirm Red.
- [x] 2.2 Create `src/main/scala/bio/algorithms/analysis/SharedSplicedMotif.scala` exposing `def find(problem: SharedSplicedMotifProblem): String`. Implementation: short-circuit to `""` when either input is empty; otherwise allocate `Array.ofDim[Int](m + 1, n + 1)`, fill bottom-up via the classical LCS recurrence (`dp(i)(j) = dp(i-1)(j-1) + 1` on match, else `max(dp(i-1)(j), dp(i)(j-1))`); backtrack from `(m, n)` appending matches to a `StringBuilder` and preferring "up" on ties (`>=`). Return `sb.reverse.toString`. Scaladoc cites Rosalind LCSQ, the textbook DP recurrence, the tie-break convention, complexity, and cross-references the sister `SharedMotif` (LCSM, spec 38 — substring variant) and `SplicedMotif` (SSEQ, spec 37 — one-pattern subsequence).
- [x] 2.3 Run `sbt testOnly bio.algorithms.analysis.SharedSplicedMotifSpec` and confirm Green.

## 3. Refactor & Final Verification

- [x] 3.1 Re-read both new source files and tests; tighten Scaladoc, ensure naming and validation order match the rest of the framework, ensure no stray imports.
- [x] 3.2 Run `sbt test` and confirm the full suite passes with zero regressions.
