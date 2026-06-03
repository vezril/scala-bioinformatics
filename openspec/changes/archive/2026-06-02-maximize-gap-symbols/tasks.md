## 1. Error type

- [x] 1.1 RED: write `MaxGapProblemErrorSpec` asserting `SequenceTooLong(length, max)` constructs and exposes its fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.analysis.MaxGapProblemError` — `sealed trait` + `final case class SequenceTooLong(length: Int, max: Int)`; run the spec green.

## 2. MaxGapProblem domain type

- [x] 2.1 RED: write `MaxGapProblemSpec` — accepts `AACGTA`/`ACACCTA` (Right, `s`/`t` preserved), rejects a 5001 bp `s` (`SequenceTooLong(5001, 5000)`), rejects a 5001 bp `t` with short `s` (`SequenceTooLong(5001, 5000)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.analysis.MaxGapProblem` as `sealed abstract case class MaxGapProblem(s: DnaString, t: DnaString)` with `MaxLength = 5000` and `from(s, t)` applying first-failure-wins (`s.value.length > 5000` → `SequenceTooLong`; then `t.value.length > 5000` → `SequenceTooLong`), building via `Right(new MaxGapProblem(s, t) {})`. Run green.

## 3. MaxGapSymbols result type

- [x] 3.1 RED: write `MaxGapSymbolsResultSpec` — `count` field exposure, `format` of `3` → `"3"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.analysis.MaxGapSymbols` — `final case class MaxGapSymbols(count: Int)` with `format: String = count.toString`. Run green.

## 4. MaximizeGapSymbols algorithm

- [x] 4.1 RED: write `MaximizeGapSymbolsSpec` — canonical `AACGTA`/`ACACCTA` → `3`; `ACGT`/`ACGT` → `0`; `AAAA`/`CCCC` → `8`; `ACGT`/`""` → `4`. Build inputs via `DnaString.from` + `MaxGapProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.analysis.MaximizeGapSymbols` with `maxGaps(problem): MaxGapSymbols = MaxGapSymbols(problem.s.value.length + problem.t.value.length - 2 * lcsLength(...))`. Implement a private `lcsLength(a: String, b: String): Int` rolling-row DP (alignment-family imperative `var`/`while`/`Array`; two `Array[Int]` of length `min(|a|,|b|)+1`, swap the shorter string in for the inner dimension; `match → diag+1` else `max(up, left)`). Run green.
- [x] 4.3 REFACTOR: confine the imperative DP to the private `lcsLength` helper (public `maxGaps` pure/total); confirm O(min) space (two rolling rows) and correct empty-string handling. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.MGAPProb` reading two FASTA DNA records from `mgap_data.txt` via `FastaFileReader.read(Paths.get(...))`, taking `records` head two (`s :: t :: _`), building `MaxGapProblem.from(s.dna, t.dna)`, running `MaximizeGapSymbols.maxGaps`, printing `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `MGAPProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
