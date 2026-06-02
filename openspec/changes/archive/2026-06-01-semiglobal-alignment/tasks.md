## 1. Error ADT

- [x] 1.1 RED: write `SemiglobalAlignmentProblemErrorSpec` asserting `STooLong` and `TTooLong` are constructible with `(length, max)` and are `SemiglobalAlignmentProblemError` subtypes; confirm it fails to compile (not found)
- [x] 1.2 GREEN: create `bio/domain/analysis/SemiglobalAlignmentProblemError.scala` — `sealed trait` with `final case class STooLong(length: Int, max: Int)` and `final case class TTooLong(length: Int, max: Int)`; confirm test passes

## 2. Validated problem bundle

- [x] 2.1 RED: write `SemiglobalAlignmentProblemSpec` covering: accepts canonical sample (`s = CAGCACTTGGATTCTCGG`, `t = CAGCGTGG`) preserving both strings; accepts two empty strings; rejects over-long `s` with `STooLong(10001, 10000)`; rejects over-long `t` with `TTooLong(10001, 10000)`; reports `STooLong` first when both over-long; `assertDoesNotCompile` for public `apply(...)` and `.copy(...)`; confirm it fails to compile
- [x] 2.2 GREEN: create `bio/domain/analysis/SemiglobalAlignmentProblem.scala` — `sealed abstract case class SemiglobalAlignmentProblem(s: DnaString, t: DnaString)`; `MaxLength = 10000`; `from(s, t)` first-failure-wins `STooLong` (s.length > 10000) then `TTooLong` (t.length > 10000); empty accepted; constructed via `Right(new SemiglobalAlignmentProblem(s, t) {})`; confirm tests pass

## 3. Result type

- [x] 3.1 RED: write `SemiglobalAlignmentSpec` (in `bio.domain.analysis`) asserting `format` on `SemiglobalAlignment(4, "CAGCA-CTTGGATTCTCGG", "---CAGCGTGG--------")` returns exactly `4\nCAGCA-CTTGGATTCTCGG\n---CAGCGTGG--------`; confirm it fails to compile
- [x] 3.2 GREEN: create `bio/domain/analysis/SemiglobalAlignment.scala` — `final case class SemiglobalAlignment(score: Int, augmentedS: String, augmentedT: String)` with `def format: String = s"$score\n$augmentedS\n$augmentedT"`; confirm test passes

## 4. Alignment algorithm

- [x] 4.1 RED: write `SemiglobalAlignmentAlgoSpec` (in `bio.algorithms.analysis`) asserting: canonical sample score `4`; valid-alignment invariants (equal augmented lengths, no column with gaps in both strings, gap-stripped `augmentedS` equals `s`, gap-stripped `augmentedT` equals `t`, recomputed free-end-gap score equals `4`); identical `GATTACA` → score `7` and both augmented strings equal `GATTACA`; contained `s = ACGTACGT`, `t = GTAC` → score `4`, gap-stripped augmentedS equals `ACGTACGT`, gap-stripped augmentedT equals `GTAC`; confirm it fails to compile
- [x] 4.2 GREEN: create `bio/algorithms/analysis/SemiglobalAlignment.scala` (alias domain result as `Result`) — imperative DP kernel: `Array.ofDim[Int](m+1, n+1)`, `dp(i)(0) = 0` and `dp(0)(j) = 0`, recurrence `max(dp(i-1)(j-1) + matchScore, dp(i-1)(j) - 1, dp(i)(j-1) - 1)` with match `+1`/mismatch `-1`/gap `-1`; answer = max over last row `dp(m)(j)` and last column `dp(i)(n)`; traceback emits trailing free gaps from corner `(m,n)` to best cell `(bi,bj)`, then core scored traceback while `i > 0 && j > 0` (tie-break diagonal > up > left), then leading free gaps (while `j > 0` emit `-`/`t[j-1]`, while `i > 0` emit `s[i-1]`/`-`); reverse both builders; confirm tests pass
- [x] 4.3 REFACTOR: review `align` for clarity (kernel confined to body, signature pure/total); rerun spec to confirm still green

## 5. IO runner and Main wiring

- [x] 5.1 GREEN: create `bio/problems/SMGBProb.scala` — `solve(): IO[Unit]` reads `smgb_data.txt` via `FastaFileReader.read`, takes the first two records as `s`/`t`, validates into `SemiglobalAlignmentProblem`, runs `align`, prints `result.format`; on FASTA/validation error or fewer than two records prints a descriptive message rather than throwing (mirror `OAPProb`)
- [x] 5.2 GREEN: wire `SMGBProb.solve()` into `bio/Main.scala`; run `sbt run` and confirm it prints first line `4` followed by a valid semiglobal alignment of the two strings

## 6. Verification

- [x] 6.1 Run full `sbt test` and confirm the whole suite is green with the new specs included
