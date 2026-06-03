## 1. ReadCorrectionProblemError ADT

- [x] 1.1 RED: write `bio.domain.analysis.ReadCorrectionProblemErrorSpec` asserting `TooManyReads(1500, 1000)` carries `count == 1500` / `max == 1000`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.analysis.ReadCorrectionProblemError` as a sealed trait with `TooManyReads(count, max)`, `ReadTooLong(length, max)`, `UnequalLengths(lengths: Vector[Int])`. Confirm the test passes.

## 2. ReadCorrectionProblem domain type

- [x] 2.1 RED: write `bio.domain.analysis.ReadCorrectionProblemSpec` covering: accepts equal-length reads, accepts empty list, rejects 1001 reads (`TooManyReads(1001,1000)`), rejects a length-51 read (`ReadTooLong(51,50)`), rejects unequal lengths (`UnequalLengths(Vector(5,6))`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.analysis.ReadCorrectionProblem` as `sealed abstract case class ReadCorrectionProblem(reads: Vector[DnaString])` with `from(...)` enforcing (first-failure-wins) MaxReads=1000, MaxLength=50, then all-equal-length, returning `Right(new ReadCorrectionProblem(...) {})` or the appropriate `Left`. Confirm GREEN.

## 3. Correction and ReadCorrections result types

- [x] 3.1 RED: write `bio.domain.analysis.ReadCorrectionsSpec` asserting `Correction("TTCAT","TTGAT").format == "TTCAT->TTGAT"` and that a `ReadCorrections` of two corrections formats them on separate lines. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.analysis.Correction(oldRead: String, newRead: String)` with `format = s"$oldRead->$newRead"` and `bio.domain.analysis.ReadCorrections(corrections: Vector[Correction])` with `format = corrections.map(_.format).mkString("\n")`. Confirm GREEN.

## 4. ReadErrorCorrection algorithm

- [x] 4.1 RED: write `bio.algorithms.analysis.ReadErrorCorrectionSpec` covering: canonical sample → correction set `{TTCAT->TTGAT, GAGGA->GATGA, TTTCC->TTTCA}` (compare as a set of formatted strings); two identical reads → no corrections; `AAA`/`TTT` (reverse complements) → no corrections; empty → no corrections. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.analysis.ReadErrorCorrection.correct(problem): ReadCorrections`. Count read occurrences; `support(r) = counts(r) + counts(rc(r))` (or `counts(r)` when `rc(r) == r`); `correctForms` = both strands of every read with support ≥ 2; for each read in input order with support 1, find a correct form at Hamming distance 1 and emit `Correction`. Reuse `DnaReverseComplement`; inline Hamming on equal-length strings. Confirm GREEN.
- [x] 4.3 REFACTOR: extract private helpers (`reverseComplement`, `support`, `hamming`); keep `correct` pure/total; rerun the spec to confirm still GREEN.

## 5. CORRProb runner

- [x] 5.1 Add `bio.problems.CORRProb` reading `src/main/scala/resources/corr_data.txt` via `FastaFileReader.read`, extracting the `DnaString`s, building the `ReadCorrectionProblem`, running `ReadErrorCorrection.correct`, and printing `result.format` through `IO`; FASTA/validation errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `CORRProb.solve()` and confirm `sbt run` prints the three canonical corrections.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
