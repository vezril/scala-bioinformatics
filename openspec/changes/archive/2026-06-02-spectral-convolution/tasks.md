## 1. MassMultisetError ADT

- [x] 1.1 RED: write `MassMultisetErrorSpec` asserting `EmptyMultiset`, `TooManyMasses(size, max)`, and `NonPositiveMass(index, value)` exist and carry their fields; confirm it fails to compile (type not found)
- [x] 1.2 GREEN: create `bio/domain/protein/MassMultisetError.scala` (sealed trait + the three cases) until the spec compiles and passes
- [x] 1.3 REFACTOR: tidy and confirm `sbt test` still green for the new spec

## 2. MassMultiset domain type

- [x] 2.1 RED: write `MassMultisetSpec` for the smart constructor — accepts the 9 sample S₁ masses (order + repeats preserved), preserves a duplicate `968.35544`, rejects empty → `EmptyMultiset`, rejects 201 masses → `TooManyMasses(201, 200)`, rejects `0.0` at index 2 → `NonPositiveMass(2, 0.0)`, first-failure (201 masses with a negative → `TooManyMasses`), and `assertDoesNotCompile` for `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio/domain/protein/MassMultiset.scala` as a `sealed abstract case class` wrapping `Vector[Double]` with `from(...)` (first-failure-wins empty → too-many → non-positive via `zipWithIndex.collectFirst`); make the spec pass
- [x] 2.3 REFACTOR: ensure MaxSize is a single named constant; rerun the spec green

## 3. SpectralConvolutionProblem domain type

- [x] 3.1 RED: write `SpectralConvolutionProblemSpec` asserting a problem built from two `MassMultiset`s exposes `s1` and `s2` unchanged; confirm RED
- [x] 3.2 GREEN: create `bio/domain/protein/SpectralConvolutionProblem.scala` as a plain `final case class(s1: MassMultiset, s2: MassMultiset)`; make the spec pass
- [x] 3.3 REFACTOR: rerun green

## 4. SpectralConvolution result type

- [x] 4.1 RED: write `SpectralConvolutionResultSpec` (domain) asserting `SpectralConvolution(3, 85.03163).format` is `"3\n85.03163"` and `SpectralConvolution(3, -85.03163).format` second line is `85.03163`; confirm RED
- [x] 4.2 GREEN: create `bio/domain/protein/SpectralConvolution.scala` (`multiplicity: Int`, `shift: Double`, `format` emitting multiplicity then `|shift|` to 5 decimals); make the spec pass
- [x] 4.3 REFACTOR: rerun green

## 5. SpectralConvolution algorithm

- [x] 5.1 RED: write `SpectralConvolutionAlgoSpec` (in `bio.algorithms.protein`) — canonical sample → multiplicity 3 and `|shift|` within 1e-5 of 85.03163; `[10,20] ⊖ [5]` → multiplicity 1; the `9.9999` majority case → multiplicity 3 and `|shift|` ≈ 9.9999; two differences equal to 5 decimals but off by 1e-11 land in one bucket; confirm RED
- [x] 5.2 GREEN: create `bio/algorithms/protein/SpectralConvolution.scala` (alias the domain result as `Result`) computing all `s1 − s2` differences, bucketing by `round(d*1e5)/1e5`, deterministic max via `sortBy { (v,c) => (-c, abs(v)) }.head`; pure FP, no `var`/`while`/`Array`; make the spec pass
- [x] 5.3 REFACTOR: confirm purity (no mutation), single rounding helper; rerun green

## 6. CONVProb runner + wiring

- [x] 6.1 GREEN: create `bio/problems/CONVProb.scala` reading `conv_data.txt`, parsing the first two non-empty lines as whitespace-separated doubles (`toDoubleOption`), validating into two `MassMultiset`s, building the problem, convolving, printing `format`; errors printed via `IO.println`, never thrown
- [x] 6.2 GREEN: point `bio/Main.scala` at `CONVProb.solve()`; run `sbt run` and confirm it prints `3` then `85.03163`
- [x] 6.3 REFACTOR: run the full `sbt test` suite and confirm all green
