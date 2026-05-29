## 1. Error ADT (Red → Green)

- [x] 1.1 Write failing tests for `EditDistanceProblemError` in `src/test/scala/bio/domain/protein/EditDistanceProblemErrorSpec.scala` covering `LeftTooLong(length, max)` and `RightTooLong(length, max)` shapes and equality.
- [x] 1.2 Create `src/main/scala/bio/domain/protein/EditDistanceProblemError.scala` as a `sealed trait` with two case classes carrying `length: Int` and `max: Int`.
- [x] 1.3 Run `sbt test` and confirm the new error-ADT tests pass.

## 2. Domain bundle (Red → Green)

- [x] 2.1 Write failing tests for `EditDistanceProblem.from` in `src/test/scala/bio/domain/protein/EditDistanceProblemSpec.scala` covering every scenario in the spec (canonical sample, empties, both-at-cap, 1001-char left/right rejections in first-failure-wins order).
- [x] 2.2 Add a no-leak compile-time guard test (e.g. an `illTyped` style check or a comment + manual review) that documents `apply` and `copy` cannot leak.
- [x] 2.3 Create `src/main/scala/bio/domain/protein/EditDistanceProblem.scala` as `sealed abstract case class EditDistanceProblem(left: ProteinString, right: ProteinString)` with a smart constructor `from(left, right)` enforcing the 1000-cap checks in the documented order.
- [x] 2.4 Run `sbt test` and confirm all domain-level tests pass.

## 3. Algorithm (Red → Green)

- [x] 3.1 Write failing tests for `EditDistance.compute` in `src/test/scala/bio/algorithms/protein/EditDistanceSpec.scala` covering every scenario in the spec (canonical PLEASANTLY/MEANLY → 5, identical, empty/empty, single sub/ins/del, fully-disjoint AAA/MMM → 3).
- [x] 3.2 Create `src/main/scala/bio/algorithms/protein/EditDistance.scala` implementing the classical `O(m · n)` Levenshtein DP with the standard recurrence (`dp(0)(j) = j`, `dp(i)(0) = i`, match ⇒ diagonal, mismatch ⇒ 1 + min of three neighbours).
- [x] 3.3 Run `sbt test` and confirm the algorithm tests pass.

## 4. Runner

- [x] 4.1 Create `src/main/scala/bio/problems/EDITProb.scala` mirroring the LCSQProb pattern (build `EditDistanceProblem` from two `ProteinString`s, call `EditDistance.compute`, `IO.println` the integer).
- [x] 4.2 Do NOT modify `src/main/scala/bio/Main.scala` (it is user-managed) — note in the PR summary that the user may want to wire it manually.

## 5. Refactor & verify

- [x] 5.1 Read through the new files and refactor for clarity (rename locals, tighten Scaladoc, ensure imports are minimal).
- [x] 5.2 Run `sbt test` one final time and confirm all suites pass with zero regressions.
