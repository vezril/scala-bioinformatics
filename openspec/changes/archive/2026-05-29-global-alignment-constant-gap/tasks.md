## 1. Domain error ADT

- [x] 1.1 Create `src/main/scala/bio/domain/protein/ConstantGapAlignmentScoreProblemError.scala` with a `sealed trait ConstantGapAlignmentScoreProblemError` and companion `case class`es `LeftTooLong(length: Int, max: Int)` and `RightTooLong(length: Int, max: Int)`.

## 2. Input bundle (TDD)

- [x] 2.1 RED: Write `src/test/scala/bio/domain/protein/ConstantGapAlignmentScoreProblemSpec.scala` covering: accepts the canonical GCON sample (`PLEASANTLY`/`MEANLY`); accepts two empty strings; accepts empty left / non-empty right; accepts non-empty left / empty right; accepts both at 1000; rejects 1001-aa left as `LeftTooLong(1001, 1000)`; rejects 1001-aa right as `RightTooLong(1001, 1000)`; first-failure-wins reports `LeftTooLong` when both exceed; `assertDoesNotCompile` for companion `apply` and for `copy`.
- [x] 2.2 GREEN: Create `src/main/scala/bio/domain/protein/ConstantGapAlignmentScoreProblem.scala` as a `sealed abstract case class ConstantGapAlignmentScoreProblem(left: ProteinString, right: ProteinString)` with a `from` smart constructor enforcing the 1000-aa caps (left, then right) first-failure-wins.
- [x] 2.3 Run `sbt "testOnly bio.domain.protein.ConstantGapAlignmentScoreProblemSpec"` and confirm green.

## 3. Algorithm (TDD)

- [x] 3.1 RED: Write `src/test/scala/bio/algorithms/protein/ConstantGapAlignmentScoreSpec.scala` covering: canonical GCON sample `PLEASANTLY`/`MEANLY` → `13`; identical `MEANLY`/`MEANLY` → `31`; empty left + `MEANLY` → `-5`; `PLEASANTLY` + empty right → `-5`; two empty → `0`; single match `W`/`W` → `11`; single mismatch `A`/`R` → `-1`; gap-length independence (`A`/`AA` and `A`/`AAAAAAAAAA` both → `-1`); symmetry of the score under argument swap.
- [x] 3.2 GREEN: Create `src/main/scala/bio/algorithms/protein/ConstantGapAlignmentScore.scala` with `compute(problem): Int` implementing the three-state constant-gap DP (`M`/`X`/`Y` tables, BLOSUM62 substitution, gap-open `-5`, gap-extend `0`, gap-direction switch `-5`) with the `-∞` boundary sentinel; answer `max(M(m)(n), X(m)(n), Y(m)(n))`.
- [x] 3.3 Run `sbt "testOnly bio.algorithms.protein.ConstantGapAlignmentScoreSpec"` and confirm green.

## 4. Problem runner + wiring

- [x] 4.1 Create `src/main/scala/bio/problems/GCONProb.scala` (mirrors `GLOBProb`) that builds the two `ProteinString`s, calls `ConstantGapAlignmentScore.compute`, and prints the score; handle the `Either` from `ConstantGapAlignmentScoreProblem.from`.
- [x] 4.2 Wire `GCONProb.solve()` into `bio/Main.scala` following the existing commented-runner pattern.

## 5. Verify

- [x] 5.1 Run the full suite `sbt test` and confirm zero regressions across all suites.
