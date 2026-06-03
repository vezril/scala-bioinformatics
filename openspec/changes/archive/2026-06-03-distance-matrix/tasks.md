## 1. DistanceMatrixProblemError ADT

- [x] 1.1 RED: write `bio.domain.analysis.DistanceMatrixProblemErrorSpec` asserting `UnequalLengths(Vector(4,4,6)).lengths == Vector(4,4,6)`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.analysis.DistanceMatrixProblemError` as a sealed trait with `TooManyStrings(count, max)`, `StringTooLong(length, max)`, `UnequalLengths(lengths: Vector[Int])`. Confirm the test passes.

## 2. DistanceMatrixProblem domain type

- [x] 2.1 RED: write `bio.domain.analysis.DistanceMatrixProblemSpec` covering: accepts 4 equal-length strings, accepts empty list, rejects 11 strings (`TooManyStrings(11,10)`), rejects a length-1001 string (`StringTooLong(1001,1000)`), rejects unequal lengths (`UnequalLengths(Vector(4,5))`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.analysis.DistanceMatrixProblem` as `sealed abstract case class DistanceMatrixProblem(strings: Vector[DnaString])` with `from(...)` enforcing (first-failure-wins) MaxStrings=10, MaxLength=1000, then all-equal-length, returning `Right(new DistanceMatrixProblem(...) {})` or the appropriate `Left`. Confirm GREEN.

## 3. DistanceMatrix result type

- [x] 3.1 RED: write `bio.domain.analysis.DistanceMatrixSpec` asserting `DistanceMatrix(Vector(Vector(0.0,0.4),Vector(0.4,0.0))).format == "0.00000 0.40000\n0.40000 0.00000"` and that an empty matrix formats to `""`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.analysis.DistanceMatrix(rows: Vector[Vector[Double]])` with `def format: String = rows.map(_.map(v => f"$v%.5f").mkString(" ")).mkString("\n")`. Confirm GREEN.

## 4. PDistanceMatrix algorithm

- [x] 4.1 RED: write `bio.algorithms.analysis.PDistanceMatrixSpec` covering: canonical sample (4 strings) → the expected 4×4 matrix (compare each entry within 0.001); single string → `Vector(Vector(0.0))`; diagonal all 0.0; two empty strings → 2×2 zero matrix. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.analysis.PDistanceMatrix.compute(problem): DistanceMatrix`. Build `Vector.tabulate(n, n)` with `D[i][j] = HammingDistance.between(s_i, s_j).getOrElse(0) / L.toDouble` (or `0.0` when `L == 0`). Pure functional. Confirm GREEN.
- [x] 4.3 REFACTOR: extract a private `pDistance(a, b): Double` helper, keep `compute` pure/total; rerun the spec to confirm still GREEN.

## 5. PDSTProb runner

- [x] 5.1 Add `bio.problems.PDSTProb` reading `src/main/scala/resources/pdst_data.txt` via `FastaFileReader.read`, extracting the `DnaString`s, building the `DistanceMatrixProblem`, running `PDistanceMatrix.compute`, and printing `result.format` through `IO`; FASTA/validation errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `PDSTProb.solve()` and confirm `sbt run` prints the canonical matrix.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
