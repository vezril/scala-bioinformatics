## 1. Error ADT (Red → Green)

- [x] 1.1 Write failing tests for `MultipleAlignmentProblemError` in `src/test/scala/bio/domain/analysis/MultipleAlignmentProblemErrorSpec.scala` covering `WrongNumberOfStrings(actual, expected)` and `StringTooLong(index, length, max)` shapes, equality, and ADT distinctness.
- [x] 1.2 Create `src/main/scala/bio/domain/analysis/MultipleAlignmentProblemError.scala` as a `sealed trait` with two case classes.
- [x] 1.3 Run `sbt test` and confirm the new error-ADT tests pass.

## 2. MultipleAlignment output ADT (Red → Green)

- [x] 2.1 Write failing tests for `MultipleAlignment` in `src/test/scala/bio/domain/analysis/MultipleAlignmentSpec.scala` covering named-field construction, value equality, and structural sharing (`copy`).
- [x] 2.2 Create `src/main/scala/bio/domain/analysis/MultipleAlignment.scala` as a plain `final case class` carrying `score: Int` and `augmentedStrings: Vector[String]`.
- [x] 2.3 Run `sbt test` and confirm the `MultipleAlignment` tests pass.

## 3. Domain bundle (Red → Green)

- [x] 3.1 Write failing tests for `MultipleAlignmentProblem.from` in `src/test/scala/bio/domain/analysis/MultipleAlignmentProblemSpec.scala` covering every scenario (canonical sample, four empties, four-at-cap, three-strings rejection, five-strings rejection, single 11-char string rejection with correct index, multiple over-cap reports first index).
- [x] 3.2 Add no-leak compile-time guards for `apply` and `copy` via `assertDoesNotCompile`.
- [x] 3.3 Create `src/main/scala/bio/domain/analysis/MultipleAlignmentProblem.scala` as `sealed abstract case class MultipleAlignmentProblem(strings: Vector[DnaString])` with a smart constructor `from(strings)` enforcing the count check before the length check.
- [x] 3.4 Run `sbt test` and confirm all domain-level tests pass.

## 4. Algorithm (Red → Green)

- [x] 4.1 Write failing tests for `MultipleAlignment.align` in `src/test/scala/bio/algorithms/analysis/MultipleAlignmentSpec.scala` covering the canonical Rosalind sample (score `-18` + invariants — spec relaxed from pinned output because multiple optimal alignments exist), all-empty, all-identical, one-vs-three-empties, and an invariant-style check for mixed inputs.
- [x] 4.2 Add helpers `stripGaps`, `pairwiseScore(aug)` (sum of -1 per mismatch over all 6 pairs), and `assertValidAlignment` inside the spec to express the five `MultipleAlignment` invariants as reusable assertions.
- [x] 4.3 Create `src/main/scala/bio/algorithms/analysis/MultipleAlignment.scala` implementing the 4-D DP + traceback. Use a flat `Array[Int]` plus a flat `Array[Byte]` (chosen mask) of size `(n0+1)(n1+1)(n2+1)(n3+1)` with `idx(i0, i1, i2, i3)` arithmetic. Iterate `mask = 1..15` for each cell; skip illegal moves; record the max-achieving mask. Compute per-column score as sum over the 6 unordered pairs `(j, k)` with `j < k`.
- [x] 4.4 Run `sbt test` and confirm the algorithm tests pass.

## 5. Runner

- [x] 5.1 Create `src/main/scala/bio/problems/MULTProb.scala` mirroring the EDTAProb pattern (build `MultipleAlignmentProblem` from four `DnaString`s, call `MultipleAlignment.align`, `IO.println` the score on one line followed by each augmented string on its own line — matching the Rosalind output format).
- [x] 5.2 Do NOT modify `src/main/scala/bio/Main.scala` (it is user-managed).

## 6. Refactor & verify

- [x] 6.1 Read through the new files and refactor for clarity (rename locals, tighten Scaladoc, ensure imports are minimal). In particular, double-check the `idx` arithmetic and the 6-pair column-score loop.
- [x] 6.2 Run `sbt test` one final time and confirm all suites pass with zero regressions.
