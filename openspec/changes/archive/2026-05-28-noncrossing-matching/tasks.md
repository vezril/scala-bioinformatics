## 1. NoncrossingMatchingProblem ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/nucleic/NoncrossingMatchingProblemSpec.scala` covering: accepts the canonical Rosalind sample `AUAU` (auCount=2, cgCount=0); accepts the empty RNA string with both counts `0`; accepts the maximum 300-char input `"AU" * 150`; rejects a 301-char input as `ExceedsMaxLength(301, 300)`; rejects `"AAU"` as `UnpairedAU(2, 1)`; rejects `"CCG"` as `UnpairedCG(2, 1)`; companion `apply` and `copy` leak-proofness via `assertDoesNotCompile`. Use a private `rna(s: String): RnaString` helper. Run `sbt Test/compile` and confirm Red.
- [x] 1.2 Create `src/main/scala/bio/domain/nucleic/NoncrossingMatchingProblemError.scala` as a `sealed trait` with cases `ExceedsMaxLength(length: Int, max: Int)`, `UnpairedAU(aCount: Int, uCount: Int)`, `UnpairedCG(cCount: Int, gCount: Int)`.
- [x] 1.3 Create `src/main/scala/bio/domain/nucleic/NoncrossingMatchingProblem.scala` as `sealed abstract case class NoncrossingMatchingProblem(rna: RnaString, auCount: Int, cgCount: Int)` with companion `from(rna: RnaString): Either[NoncrossingMatchingProblemError, NoncrossingMatchingProblem]`. The companion's private `countSymbols(s: String)` does a single pass returning `(a, c, g, u)`. Validation order: length cap (300) → AU balance → CG balance.
- [x] 1.4 Run `sbt testOnly bio.domain.nucleic.NoncrossingMatchingProblemSpec` and confirm Green.

## 2. NoncrossingMatching algorithm (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/algorithms/nucleic/NoncrossingMatchingSpec.scala` covering: canonical Rosalind sample `AUAU → 2`; empty input → `1`; `AU → 1`; `CG → 1`; `AUAUAU → 5` (Catalan `C(3)`); `AAAAUUUU → 1` (fully-nested); `AUCG → 1`; `CGCG → 2`; `"AU" * 14 → 674440` (`C(14)` mod 1 000 000). Use a private `fixture(s: String): NoncrossingMatchingProblem` helper. Run `sbt Test/compile` and confirm Red.
- [x] 2.2 Create `src/main/scala/bio/algorithms/nucleic/NoncrossingMatching.scala` exposing `def count(problem: NoncrossingMatchingProblem): Int`. Implementation: a 2D `Array[Array[Int]]` of size `(n+1) × (n+1)` (where `n = rna.value.length`); initialise `dp(i)(i-1) = 1` for empty intervals (model `j = i - 1` as `j = i` shifted by 1 — see below); fill by *increasing even length* `len = 2, 4, ..., n` over `i = 0..n-len`; for each cell `(i, j)` with `j = i + len - 1`, sum `dp(i+1)(k-1) * dp(k+1)(j)` for every `k` in `i+1..j` such that `pairs(s(i), s(k))` and `(k - i)` is odd. Implement intermediate-product safety: `(dp(i+1)(k-1).toLong * dp(k+1)(j) % Mod).toInt`. Use `len = 0` interval as the empty-string short-circuit returning `1`. Scaladoc cites Rosalind CAT, the recurrence, the modulo cast, and the time/space complexity.
- [x] 2.3 Run `sbt testOnly bio.algorithms.nucleic.NoncrossingMatchingSpec` and confirm Green.

## 3. Refactor & Final Verification

- [x] 3.1 Re-read both new source files and tests; tighten Scaladoc to (a) cite Rosalind problem 35 (CAT) and the recurrence, (b) cross-reference the sister `PerfectMatching` algorithm (spec 34 — same input shape, different counting question — closed-form factorial vs DP), and (c) explain the `Long`-then-mod-then-cast trick for intermediate products. No stray imports.
- [x] 3.2 Run `sbt test` and confirm the full suite passes with zero regressions.
