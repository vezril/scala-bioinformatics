## 1. Error ADT

- [x] 1.1 RED: write `OverlapAlignmentProblemErrorSpec` in `src/test/scala/bio/domain/analysis/` asserting `OverlapAlignmentProblemError` is a sealed trait with `STooLong(length, max)` and `TTooLong(length, max)` cases; confirm it fails to compile (`not found: type OverlapAlignmentProblemError`).
- [x] 1.2 GREEN: create `src/main/scala/bio/domain/analysis/OverlapAlignmentProblemError.scala` — `sealed trait OverlapAlignmentProblemError` with `final case class STooLong(length: Int, max: Int)` and `final case class TTooLong(length: Int, max: Int)`; confirm Green with `sbt "testOnly bio.domain.analysis.OverlapAlignmentProblemErrorSpec"`.

## 2. Validated problem bundle

- [x] 2.1 RED: write `OverlapAlignmentProblemSpec` covering: accepts the canonical sample (`s`/`t` preserved), accepts two empty strings, rejects over-long `s` with `STooLong(10001, 10000)`, rejects over-long `t` with `TTooLong(10001, 10000)`, reports `STooLong` first when both over-long, and `assertDoesNotCompile` for public `apply` and `copy`; confirm Red.
- [x] 2.2 GREEN: create `src/main/scala/bio/domain/analysis/OverlapAlignmentProblem.scala` — `sealed abstract case class OverlapAlignmentProblem(s: DnaString, t: DnaString)`; `MaxLength = 10000`; `from(s: DnaString, t: DnaString)` first-failure-wins `STooLong` then `TTooLong`, returning `Right(new OverlapAlignmentProblem(s, t) {})`; confirm Green.
- [x] 2.3 REFACTOR: review naming/structure against the `FittingAlignmentProblem` precedent; rerun the spec to confirm still Green.

## 3. Result type

- [x] 3.1 RED: write `OverlapAlignmentSpec` (result-type portion) asserting `OverlapAlignment(score, augmentedS, augmentedT)` exists and `format` renders `"1\nATTAGAC-AG\nAT-AGACCAT"` for `(1, "ATTAGAC-AG", "AT-AGACCAT")`; confirm Red.
- [x] 3.2 GREEN: create `src/main/scala/bio/domain/analysis/OverlapAlignment.scala` — `final case class OverlapAlignment(score: Int, augmentedS: String, augmentedT: String)` with `def format: String = s"$score\n$augmentedS\n$augmentedT"`; confirm Green.

## 4. Alignment algorithm

- [x] 4.1 RED: write `OverlapAlignmentAlgoSpec` in `src/test/scala/bio/algorithms/analysis/` covering: canonical sample `score == 1`; canonical alignment invariants (equal augmented lengths, no double-gap column, gap-stripped `augmentedS` is a suffix of `s`, gap-stripped `augmentedT` is a prefix of `t`, recomputed score `== 1`); identical `GATTACA` → score `7` and both augmented equal `GATTACA`; disjoint `AAAA`/`TTTT` → score `0` and both augmented empty; confirm Red.
- [x] 4.2 GREEN: create `src/main/scala/bio/algorithms/analysis/OverlapAlignment.scala` — `object OverlapAlignment { def align(problem: OverlapAlignmentProblem): bio.domain.analysis.OverlapAlignment = ... }` using the imperative DP kernel (rows = `s`, cols = `t`; `dp(i)(0) = 0`, `dp(0)(j) = -2*j`; recurrence with match `+1`, substitution `-2`, gap `-2`; answer = max over final row, smallest argmax `j`; traceback to column 0 with forced-left at `i == 0` and tie-break diagonal > up > left; build reversed `StringBuilder`s); confirm Green.
- [x] 4.3 REFACTOR: confine `var`/`while`/`Array` to the kernel, keep the signature pure/total, document the suffix-of-`s`/prefix-of-`t` semantics; rerun the spec to confirm still Green.

## 5. IO runner and Main wiring

- [x] 5.1 Create `src/main/scala/bio/problems/OAPProb.scala` — `solve(): IO[Unit]` reading `oap_data.txt` via `FastaFileReader.read`, taking the first two records as `s`/`t`, validating into `OverlapAlignmentProblem`, running `OverlapAlignment.align`, and printing `result.format`; on FASTA/validation error or fewer than two records, print a descriptive message via `IO.println` rather than throwing.
- [x] 5.2 Wire `OAPProb.solve()` into `bio.Main` (uncomment/add the `ASMQ`-style runner line) and confirm `sbt run` prints a first line of `1` followed by a valid overlap alignment.

## 6. Verification

- [x] 6.1 Run the full suite `sbt test` and confirm all tests pass (count increased by the new specs); confirm no `var`/`while`/`Array` leaked outside the alignment kernel.
