## 1. InterwovenMotifProblemError ADT

- [x] 1.1 RED: write `bio.domain.analysis.InterwovenMotifProblemErrorSpec` asserting `TooManyPatterns(12, 10)` carries `count == 12` / `max == 10`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.analysis.InterwovenMotifProblemError` as a sealed trait with `TooManyPatterns(count, max)`, `TextTooLong(length, max)`, `PatternTooLong(length, max)`. Confirm the test passes.

## 2. InterwovenMotifProblem domain type

- [x] 2.1 RED: write `bio.domain.analysis.InterwovenMotifProblemSpec` covering: accepts valid text + ≤10 patterns ≤10bp, accepts empty pattern list, rejects 11 patterns (`TooManyPatterns(11,10)`), rejects text length 10001 (`TextTooLong(10001,10000)`), rejects a length-11 pattern (`PatternTooLong(11,10)`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.analysis.InterwovenMotifProblem` as `sealed abstract case class InterwovenMotifProblem(text: DnaString, patterns: Vector[DnaString])` with `from(...)` enforcing (first-failure-wins) MaxPatterns=10, MaxTextLength=10000, MaxPatternLength=10, returning `Right(new InterwovenMotifProblem(...) {})` or the appropriate `Left`. Confirm GREEN.

## 3. InterwovenMotifMatrix result type

- [x] 3.1 RED: write `bio.domain.analysis.InterwovenMotifMatrixSpec` asserting `InterwovenMotifMatrix(Vector(Vector(0,0,1),Vector(0,1,0),Vector(1,0,0))).format == "0 0 1\n0 1 0\n1 0 0"` and that an empty matrix formats to `""`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.analysis.InterwovenMotifMatrix(rows: Vector[Vector[Int]])` with `def format: String = rows.map(_.mkString(" ")).mkString("\n")`. Confirm GREEN.

## 4. InterwovenMotifs algorithm

- [x] 4.1 RED: write `bio.algorithms.analysis.InterwovenMotifsSpec` covering: canonical sample (`GACCACGGTT`, `ACAG`/`GT`/`CCG`) → `Vector(Vector(0,0,1),Vector(0,1,0),Vector(1,0,0))`; self-pair `GT` into `GACCACGGTT` → `M[0][0]==1`; `ACAG`&`CCG` into `GACCACAAAAGGTT` → `0`; `ACAG`&`CCG` into `ACACG` → `0`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.analysis.InterwovenMotifs.compute(problem): InterwovenMotifMatrix`. For each pair `j ≤ k`, decide interweavability via an interleaving-string DP scanned over every start window of the text (short-circuit on first success), mirror to `M[k][j]`. Imperative `Array[Boolean]` fill, pure signature. Confirm GREEN.
- [x] 4.3 REFACTOR: extract a private `canInterweave(s, t, u): Boolean` helper, confine `var`/`while` to it, keep `compute` pure/total; rerun the spec to confirm still GREEN.

## 5. ITWVProb runner

- [x] 5.1 Add `bio.problems.ITWVProb` reading `src/main/scala/resources/itwv_data.txt` (first line = text, remaining nonblank lines = patterns), building `DnaString`s and the `InterwovenMotifProblem`, running `InterwovenMotifs.compute`, and printing `result.format` through `IO`; errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `ITWVProb.solve()` and confirm `sbt run` prints the canonical matrix.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
