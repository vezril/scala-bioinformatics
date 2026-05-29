## 1. Error ADT (Red → Green)

- [x] 1.1 Write failing tests for `OptimalAlignmentCountProblemError` in `src/test/scala/bio/domain/protein/OptimalAlignmentCountProblemErrorSpec.scala` covering `LeftTooLong(length, max)` and `RightTooLong(length, max)` shapes and equality.
- [x] 1.2 Create `src/main/scala/bio/domain/protein/OptimalAlignmentCountProblemError.scala` as a `sealed trait` with two case classes carrying `length: Int` and `max: Int`.
- [x] 1.3 Run `sbt test` and confirm the new error-ADT tests pass.

## 2. Domain bundle (Red → Green)

- [x] 2.1 Write failing tests for `OptimalAlignmentCountProblem.from` in `src/test/scala/bio/domain/protein/OptimalAlignmentCountProblemSpec.scala` covering every scenario (canonical PLEASANTLY/MEANLY sample, empties, both-at-cap, 1001-char left/right rejections in first-failure-wins order).
- [x] 2.2 Add no-leak compile-time guards for `apply` and `copy` via `assertDoesNotCompile`.
- [x] 2.3 Create `src/main/scala/bio/domain/protein/OptimalAlignmentCountProblem.scala` as `sealed abstract case class OptimalAlignmentCountProblem(left: ProteinString, right: ProteinString)` with a smart constructor `from(left, right)` enforcing the 1000-cap checks in documented order.
- [x] 2.4 Run `sbt test` and confirm all domain-level tests pass.

## 3. Algorithm (Red → Green)

- [x] 3.1 Write failing tests for `OptimalAlignmentCount.compute` in `src/test/scala/bio/algorithms/protein/OptimalAlignmentCountSpec.scala` covering every scenario in the spec (canonical PLEASANTLY/MEANLY → 4, identical MEANLY/MEANLY → 1, all empty edge cases, distinct single chars → 1, `A` vs `AA` → 2, symmetry).
- [x] 3.2 Create `src/main/scala/bio/algorithms/protein/OptimalAlignmentCount.scala` implementing the parallel Levenshtein cost + count DP with `private val Modulus = 134_217_727`. Apply mod after each addition in the count recurrence.
- [x] 3.3 Run `sbt test` and confirm the algorithm tests pass.

## 4. Runner

- [x] 4.1 Create `src/main/scala/bio/problems/CTEAProb.scala` mirroring the EDITProb pattern (build `OptimalAlignmentCountProblem` from two `ProteinString`s, call `OptimalAlignmentCount.compute`, `IO.println` the integer count).
- [x] 4.2 Do NOT modify `src/main/scala/bio/Main.scala` (it is user-managed).

## 5. Refactor & verify

- [x] 5.1 Read through the new files and refactor for clarity (rename locals, tighten Scaladoc, ensure imports are minimal). Confirm the inner-loop arithmetic and modulo-application points.
- [x] 5.2 Run `sbt test` one final time and confirm all suites pass with zero regressions.
