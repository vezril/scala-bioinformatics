## 1. Domain error ADT

- [x] 1.1 Create `src/main/scala/bio/domain/analysis/SimilarMotifsProblemError.scala` with a `sealed trait SimilarMotifsProblemError` and companion `case class`es `KOutOfRange(k: Int, min: Int, max: Int)`, `MotifTooLong(length: Int, max: Int)`, and `GenomeTooLong(length: Int, max: Int)`.

## 2. Input bundle (TDD)

- [x] 2.1 RED: Write `src/test/scala/bio/domain/analysis/SimilarMotifsProblemSpec.scala` covering: accepts the canonical KSIM sample (`k=2`, `ACGTAG`, `ACGGATCGGCATCGT`); accepts `k=1` and `k=50`; accepts empty motif + empty genome; accepts motif at 5000 and genome at 50000; rejects `k=0` as `KOutOfRange(0, 1, 50)`; rejects `k=51` as `KOutOfRange(51, 1, 50)`; rejects 5001-bp motif as `MotifTooLong(5001, 5000)`; rejects 50001-bp genome as `GenomeTooLong(50001, 50000)`; first-failure-wins reports `KOutOfRange` when `k=0` and the motif is also too long; `assertDoesNotCompile` for companion `apply` and for `copy`.
- [x] 2.2 GREEN: Create `src/main/scala/bio/domain/analysis/SimilarMotifsProblem.scala` as a `sealed abstract case class SimilarMotifsProblem(k: Int, motif: DnaString, genome: DnaString)` with a `from` smart constructor enforcing the caps (k ∈ [1,50], then motif ≤ 5000, then genome ≤ 50000) first-failure-wins.
- [x] 2.3 Run `sbt "testOnly bio.domain.analysis.SimilarMotifsProblemSpec"` and confirm green.

## 3. Output ADT (TDD)

- [x] 3.1 RED: Add an output-ADT construction/equality test (named fields `location`/`length`, value-equality) — may live in the algorithm spec file.
- [x] 3.2 GREEN: Create `src/main/scala/bio/domain/analysis/SimilarMotif.scala` as a plain `final case class SimilarMotif(location: Int, length: Int)`.

## 4. Algorithm (TDD)

- [x] 4.1 RED: Write `src/test/scala/bio/algorithms/analysis/SimilarMotifsSpec.scala` covering: canonical KSIM sample → `List(SimilarMotif(1,4), SimilarMotif(1,5), SimilarMotif(1,6))`; `k=0` exact matching `ACG`/`ACGTACG` → `List(SimilarMotif(1,3), SimilarMotif(5,3))`; identical `GATTACA` with `k=0` → `List(SimilarMotif(1,7))`; motif longer than genome `ACGT`/`ACG` with `k=1` → contains `SimilarMotif(1,3)`; no hits `AAAA`/`CCCCCCCC` with `k=1` → `Nil`; empty genome → `Nil`; results sorted ascending by `(location, length)`.
- [x] 4.2 GREEN: Create `src/main/scala/bio/algorithms/analysis/SimilarMotifs.scala` with `findAll(problem): List[SimilarMotif]` implementing the forward approximate-matching DP (free start, `D[0][b]=0`, `D[a][0]=a`) to find valid end columns `b` with `D[m][b] <= k`, then a per-end backward DP over the `|len - m| <= k` length window that emits `SimilarMotif(b - c + 1, c)` when the exact edit distance of the whole motif against `t[b-c..b)` is `<= k`; sort the result by `(location, length)`.
- [x] 4.3 Run `sbt "testOnly bio.algorithms.analysis.SimilarMotifsSpec"` and confirm green.

## 5. Problem runner + wiring

- [x] 5.1 Create `src/main/scala/bio/problems/KSIMProb.scala` (mirrors `OSYMProb`) that builds the `k`, `motif`, and `genome` inputs, calls `SimilarMotifs.findAll`, and prints each hit as `location length` on its own line; handle the `Either` from `SimilarMotifsProblem.from`.
- [x] 5.2 Wire `KSIMProb.solve()` into `bio/Main.scala` following the existing commented-runner pattern.

## 6. Verify

- [x] 6.1 Run the full suite `sbt test` and confirm zero regressions across all suites.
