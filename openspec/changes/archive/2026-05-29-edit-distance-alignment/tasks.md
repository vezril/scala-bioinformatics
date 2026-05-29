## 1. Error ADT (Red → Green)

- [x] 1.1 Write failing tests for `EditDistanceAlignmentProblemError` in `src/test/scala/bio/domain/protein/EditDistanceAlignmentProblemErrorSpec.scala` covering `LeftTooLong(length, max)` and `RightTooLong(length, max)` shapes and equality.
- [x] 1.2 Create `src/main/scala/bio/domain/protein/EditDistanceAlignmentProblemError.scala` as a `sealed trait` with two case classes carrying `length: Int` and `max: Int`.
- [x] 1.3 Run `sbt test` and confirm the new error-ADT tests pass.

## 2. EditAlignment output ADT (Red → Green)

- [x] 2.1 Write failing tests for `EditAlignment` in `src/test/scala/bio/domain/protein/EditAlignmentSpec.scala` covering named-field construction, value equality, and structural sharing (`copy`).
- [x] 2.2 Create `src/main/scala/bio/domain/protein/EditAlignment.scala` as a plain `final case class` carrying `distance: Int`, `augmentedLeft: String`, `augmentedRight: String`.
- [x] 2.3 Run `sbt test` and confirm the `EditAlignment` tests pass.

## 3. Domain bundle (Red → Green)

- [x] 3.1 Write failing tests for `EditDistanceAlignmentProblem.from` in `src/test/scala/bio/domain/protein/EditDistanceAlignmentProblemSpec.scala` covering every scenario in the spec (canonical sample, empties, both-at-cap, 1001-char left/right rejections in first-failure-wins order, both-over-cap reports left first).
- [x] 3.2 Add no-leak compile-time guards for `apply` and `copy` via `assertDoesNotCompile`.
- [x] 3.3 Create `src/main/scala/bio/domain/protein/EditDistanceAlignmentProblem.scala` as `sealed abstract case class EditDistanceAlignmentProblem(left: ProteinString, right: ProteinString)` with a smart constructor `from(left, right)` enforcing the 1000-cap checks in documented order.
- [x] 3.4 Run `sbt test` and confirm all domain-level tests pass.

## 4. Algorithm (Red → Green)

- [x] 4.1 Write failing tests for `EditDistanceAlignment.align` in `src/test/scala/bio/algorithms/protein/EditDistanceAlignmentSpec.scala` covering every scenario in the spec (canonical PRETTY/PRTTEIN → 4 / "PRETTY--" / "PR-TTEIN", identical, empty-left, empty-right, both-empty, single sub/ins/del, and an invariant-style PLEASANTLY/MEANLY check).
- [x] 4.2 Add a helper `private def stripGaps(s: String): String` and `private def hamming(a: String, b: String): Int` inside the spec to express the four `EditAlignment` invariants as reusable assertions.
- [x] 4.3 Create `src/main/scala/bio/algorithms/protein/EditDistanceAlignment.scala` implementing the classical `O(m · n)` Levenshtein DP + greedy traceback with the documented tie-break order (diagonal-match > up > left > diagonal-sub) — spec and design corrected from the original draft after hand-tracing the canonical sample.
- [x] 4.4 Run `sbt test` and confirm the algorithm tests pass.

## 5. Runner

- [x] 5.1 Create `src/main/scala/bio/problems/EDTAProb.scala` mirroring the EDITProb pattern (build `EditDistanceAlignmentProblem` from two `ProteinString`s, call `EditDistanceAlignment.align`, `IO.println` the distance on one line followed by `augmentedLeft` and `augmentedRight` on separate lines — matching the Rosalind output format).
- [x] 5.2 Do NOT modify `src/main/scala/bio/Main.scala` (it is user-managed).

## 6. Refactor & verify

- [x] 6.1 Read through the new files and refactor for clarity (rename locals, tighten Scaladoc, ensure imports are minimal). In particular, confirm the traceback branches are documented inline.
- [x] 6.2 Run `sbt test` one final time and confirm all suites pass with zero regressions.
