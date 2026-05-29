## 1. Domain error ADT

- [x] 1.1 Create `src/main/scala/bio/domain/protein/AffineGapAlignmentProblemError.scala` with a `sealed trait AffineGapAlignmentProblemError` and companion `case class`es `LeftTooLong(length: Int, max: Int)` and `RightTooLong(length: Int, max: Int)`.

## 2. Input bundle (TDD)

- [x] 2.1 RED: Write `src/test/scala/bio/domain/protein/AffineGapAlignmentProblemSpec.scala` covering: accepts the canonical GAFF sample (`PRTEINS`/`PRTWPSEIN`); accepts two empty strings; accepts empty left / non-empty right; accepts non-empty left / empty right; accepts both at 100; rejects 101-aa left as `LeftTooLong(101, 100)`; rejects 101-aa right as `RightTooLong(101, 100)`; first-failure-wins reports `LeftTooLong` when both exceed; `assertDoesNotCompile` for companion `apply` and for `copy`.
- [x] 2.2 GREEN: Create `src/main/scala/bio/domain/protein/AffineGapAlignmentProblem.scala` as a `sealed abstract case class AffineGapAlignmentProblem(left: ProteinString, right: ProteinString)` with a `from` smart constructor enforcing the 100-aa caps (left, then right) first-failure-wins.
- [x] 2.3 Run `sbt "testOnly bio.domain.protein.AffineGapAlignmentProblemSpec"` and confirm green.

## 3. Result ADT

- [x] 3.1 Create `src/main/scala/bio/domain/protein/AffineGapAlignment.scala` as a plain `final case class AffineGapAlignment(score: Int, augmentedLeft: String, augmentedRight: String)` with a scaladoc documenting the algorithm-guaranteed invariants (equal length, no double-gap column, gap-strip round-trips to inputs, recomputed affine score equals `score`).

## 4. Algorithm (TDD)

- [x] 4.1 RED: Write `src/test/scala/bio/algorithms/protein/AffineGapAlignmentSpec.scala` covering: canonical GAFF sample `PRTEINS`/`PRTWPSEIN` → `score` `8`; canonical sample alignment `PRT---EINS` / `PRTWPSEIN-`; structural validity of the returned alignment (equal length, no double-gap column, gap-strip round-trips, recomputed affine score equals `score` — assert via a test-local affine re-scorer); identical `PRTEINS`/`PRTEINS` → `36` with both augmented strings `PRTEINS`; empty left + `MEANLY` → `-16` with `augmentedLeft` six `-` and `augmentedRight` `MEANLY`; `PRTEINS` + empty right → `-17` with `augmentedRight` seven `-`; two empty → `0` with both augmented empty; single match `W`/`W` → `11`; single mismatch `A`/`R` → `-1`; affine length-dependence (`A`/`AA` → `-7`, `A`/`AAA` → `-8`); symmetry of `score` under argument swap.
- [x] 4.2 GREEN: Create `src/main/scala/bio/algorithms/protein/AffineGapAlignment.scala` with `compute(problem): bio.domain.protein.AffineGapAlignment` implementing the three-state affine-gap DP (`M`/`X`/`Y` tables, BLOSUM62 substitution, gap-open `a = 11`, gap-extend `b = 1`, gap-direction switch `-a`) with the `-∞` boundary sentinel and traceback (tie-break: match → extend → open/switch) producing the two augmented strings; `score = max(M(m)(n), X(m)(n), Y(m)(n))`.
- [x] 4.3 Run `sbt "testOnly bio.algorithms.protein.AffineGapAlignmentSpec"` and confirm green; if the canonical-sample alignment scenario fails on tie-break, adjust the traceback tie-break order until it reproduces `PRT---EINS` / `PRTWPSEIN-` (score and structural-validity scenarios must remain green throughout).

## 5. Problem runner + wiring

- [x] 5.1 Create `src/main/scala/bio/problems/GAFFProb.scala` (mirrors `GLOBProb`/`GCONProb`) that builds the two `ProteinString`s, calls `AffineGapAlignment.compute`, and prints the score on the first line followed by the two augmented strings; handle the `Either` from `AffineGapAlignmentProblem.from`.
- [x] 5.2 Wire `GAFFProb.solve()` into `bio/Main.scala` following the existing commented-runner pattern.

## 6. Verify

- [x] 6.1 Run the full suite `sbt test` and confirm zero regressions across all suites.
