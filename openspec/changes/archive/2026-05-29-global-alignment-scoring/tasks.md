## 1. Error ADT (Red → Green)

- [x] 1.1 Write failing tests for `GlobalAlignmentScoreProblemError` in `src/test/scala/bio/domain/protein/GlobalAlignmentScoreProblemErrorSpec.scala` covering `LeftTooLong(length, max)` and `RightTooLong(length, max)` shapes and equality.
- [x] 1.2 Create `src/main/scala/bio/domain/protein/GlobalAlignmentScoreProblemError.scala` as a `sealed trait` with two case classes carrying `length: Int` and `max: Int`.
- [x] 1.3 Run `sbt test` and confirm the new error-ADT tests pass.

## 2. Domain bundle (Red → Green)

- [x] 2.1 Write failing tests for `GlobalAlignmentScoreProblem.from` in `src/test/scala/bio/domain/protein/GlobalAlignmentScoreProblemSpec.scala` covering every scenario in the spec (canonical PLEASANTLY/MEANLY sample, empties, both-at-cap, 1001-char left/right rejections in first-failure-wins order, both-over-cap reports left first).
- [x] 2.2 Add no-leak compile-time guards for `apply` and `copy` via `assertDoesNotCompile`.
- [x] 2.3 Create `src/main/scala/bio/domain/protein/GlobalAlignmentScoreProblem.scala` as `sealed abstract case class GlobalAlignmentScoreProblem(left: ProteinString, right: ProteinString)` with a smart constructor `from(left, right)` enforcing the 1000-cap checks in documented order.
- [x] 2.4 Run `sbt test` and confirm all domain-level tests pass.

## 3. BLOSUM62 matrix (Red → Green)

- [x] 3.1 Write failing tests for `Blosum62.score` in `src/test/scala/bio/algorithms/protein/Blosum62Spec.scala` covering canonical reference values (`A/A=4`, `W/W=11`, `C/C=9`, `M/M=5`, `G/G=6`, `A/R=-1`, `W/C=-2`, `L/M=2`, `P/Y=-3`, `N/D=1`) and a symmetry check over every pair of amino acids in `AminoAcid.all`.
- [x] 3.2 Create `src/main/scala/bio/algorithms/protein/Blosum62.scala` exposing `score(a: AminoAcid, b: AminoAcid): Int`. Encode the 20 × 20 NCBI BLOSUM62 matrix as a private `Array[Array[Int]]` indexed by amino-acid ordinal (built once at object init via `AminoAcid.all`).
- [x] 3.3 Run `sbt test` and confirm all `Blosum62` tests pass.

## 4. Algorithm (Red → Green)

- [x] 4.1 Write failing tests for `GlobalAlignmentScore.compute` in `src/test/scala/bio/algorithms/protein/GlobalAlignmentScoreSpec.scala` covering every scenario in the spec (canonical PLEASANTLY/MEANLY → 8, identical MEANLY/MEANLY → 31, empty/empty, single-letter self- and cross-substitution, single-insertion edge case, symmetry).
- [x] 4.2 Create `src/main/scala/bio/algorithms/protein/GlobalAlignmentScore.scala` implementing the classical Needleman-Wunsch `O(m · n)` DP with BLOSUM62 substitution scoring (via `Blosum62.score`) and a hardcoded `private val Gap = -5` linear gap penalty. Recurrence: `dp(0)(j) = -5j`, `dp(i)(0) = -5i`, `dp(i)(j) = max(diag + Blosum62(...), up - 5, left - 5)`.
- [x] 4.3 Run `sbt test` and confirm the algorithm tests pass.

## 5. Runner

- [x] 5.1 Create `src/main/scala/bio/problems/GLOBProb.scala` mirroring the EDITProb pattern (build `GlobalAlignmentScoreProblem` from two `ProteinString`s, call `GlobalAlignmentScore.compute`, `IO.println` the integer).
- [x] 5.2 Do NOT modify `src/main/scala/bio/Main.scala` (it is user-managed).

## 6. Refactor & verify

- [x] 6.1 Read through the new files and refactor for clarity (rename locals, tighten Scaladoc, ensure imports are minimal). In particular, double-check the BLOSUM62 matrix entries against the NCBI canonical table.
- [x] 6.2 Run `sbt test` one final time and confirm all suites pass with zero regressions.
