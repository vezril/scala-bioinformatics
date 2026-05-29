## 1. Error ADT

- [x] 1.1 Create `bio.domain.protein.LocalAffineAlignmentProblemError` (sealed trait + `LeftTooLong(length, max)` / `RightTooLong(length, max)` cases)

## 2. Validated input bundle (TDD)

- [x] 2.1 Write `LocalAffineAlignmentProblemSpec` covering: accepts canonical sample, accepts empties, accepts strings at the 10,000 cap, rejects 10,001 left → `LeftTooLong(10001, 10000)`, rejects 10,001 right → `RightTooLong(10001, 10000)`, both over-length → `LeftTooLong` first, and `assertDoesNotCompile` for `apply`/`copy` (RED)
- [x] 2.2 Implement `bio.domain.protein.LocalAffineAlignmentProblem` as a `sealed abstract case class` with first-failure-wins `from` and `MaxLength = 10000` (GREEN)
- [x] 2.3 Run `sbt test` and confirm the input-bundle spec passes

## 3. Result ADT

- [x] 3.1 Create `bio.domain.protein.LocalAffineAlignment` as a plain `final case class (score: Int, leftSubstring: String, rightSubstring: String)`

## 4. Algorithm (TDD)

- [x] 4.1 Write `LocalAffineAlignmentSpec` covering: canonical `PLEASANTLY`/`MEANLY` → `12` / `LEAS` / `MEAN`, contiguous-infix invariant, `MEANLY`/`MEANLY` → `31`, `W`/`W` → `11`, `A`/`R` → `0` with empties, `A`/`AA` → `4` with `A`/`A`, empty left → `(0,"","")`, empty right → `(0,"","")`, score symmetry (RED)
- [x] 4.2 Implement `bio.algorithms.protein.LocalAffineAlignment.compute` (local affine three-state DP: 0-clamped `M`, `NegInf` boundaries for `X`/`Y`, open 11 / extend 1, BLOSUM62; track running max over `M`; traceback to substrings stopping at a 0-clamped match; empty input → `(0,"","")`) (GREEN)
- [x] 4.3 Run `sbt test` and confirm the algorithm spec passes

## 5. Runner and wiring

- [x] 5.1 Create `bio.problems.LAFFProb` (`solve(): IO[Unit]`) following the established runner pattern
- [x] 5.2 Wire `LAFFProb.solve()` into `bio.Main.run`

## 6. Verification

- [x] 6.1 Run the full `sbt test` suite and confirm zero regressions
