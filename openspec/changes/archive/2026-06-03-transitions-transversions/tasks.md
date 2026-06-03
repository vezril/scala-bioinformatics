## 1. TransitionTransversionProblemError ADT

- [x] 1.1 RED: write `bio.domain.nucleic.TransitionTransversionProblemErrorSpec` asserting `LengthMismatch(7, 9)` carries `firstLength == 7` / `secondLength == 9`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.nucleic.TransitionTransversionProblemError` as a sealed trait with `SequenceTooLong(length, max)` and `LengthMismatch(firstLength, secondLength)`. Confirm the test passes.

## 2. TransitionTransversionProblem domain type

- [x] 2.1 RED: write `bio.domain.nucleic.TransitionTransversionProblemSpec` covering: accepts two length-10 strings, accepts two empty strings, rejects a length-1001 string (`SequenceTooLong(1001,1000)`), rejects unequal lengths (`LengthMismatch(4,5)`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.nucleic.TransitionTransversionProblem` as `sealed abstract case class TransitionTransversionProblem(first: DnaString, second: DnaString)` with `from(...)` enforcing (first-failure-wins) MaxLength=1000 (each), then equal length, returning `Right(new TransitionTransversionProblem(...) {})` or the appropriate `Left`. Confirm GREEN.

## 3. TransitionTransversionRatio result type

- [x] 3.1 RED: write `bio.domain.nucleic.TransitionTransversionRatioSpec` asserting `TransitionTransversionRatio(17, 14).ratio` ≈ 1.2142857 (within 0.0001) and `.format == "1.21428571429"`; and that `transversions = 0` gives `ratio == 0.0`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.nucleic.TransitionTransversionRatio(transitions: Int, transversions: Int)` with `val ratio: Double = if (transversions == 0) 0.0 else transitions.toDouble / transversions` and `def format: String = f"$ratio%.11f"`. Confirm GREEN.

## 4. TransitionTransversionAnalysis algorithm

- [x] 4.1 RED: write `bio.algorithms.nucleic.TransitionTransversionAnalysisSpec` covering: canonical sample (the two 80-bp strings) → `format == "1.21428571429"`; identical strings → both counts 0, ratio 0.0; `AC`/`GT` → transitions 2, transversions 0; `A`/`C` → transitions 0, transversions 1. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.nucleic.TransitionTransversionAnalysis.analyze(problem): TransitionTransversionRatio`. Zip the two sequence strings, keep mismatches, count transitions (both in `{A,G}` or both in `{C,T}`), `transversions = mismatches - transitions`. Confirm GREEN.
- [x] 4.3 REFACTOR: extract a private `isTransition(a, b): Boolean` helper using purine/pyrimidine sets; keep `analyze` pure/total; rerun the spec to confirm still GREEN.

## 5. TRANProb runner

- [x] 5.1 Add `bio.problems.TRANProb` reading `src/main/scala/resources/tran_data.txt` via `FastaFileReader.read`, taking the two records' `DnaString`s, building the `TransitionTransversionProblem`, running `TransitionTransversionAnalysis.analyze`, and printing `result.format` through `IO`; FASTA/validation errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `TRANProb.solve()` and confirm `sbt run` prints `1.21428571429` for the canonical dataset.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
