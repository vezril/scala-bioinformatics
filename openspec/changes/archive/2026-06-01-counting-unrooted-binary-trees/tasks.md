## 1. Error ADT

- [x] 1.1 Create `bio.domain.combinatorics.LeafCountError` (sealed trait + `NonPositive(value)` / `ExceedsMaximum(value, max)` cases)

## 2. Validated leaf-count value type (TDD)

- [x] 2.1 Write `LeafCountSpec` covering: accepts n = 1 / 5 / 1000; rejects n = 0 → `NonPositive(0)`, negative → `NonPositive(-5)`, n = 1001 → `ExceedsMaximum(1001, 1000)`; lower-bound-first ordering; `assertDoesNotCompile` for `apply`/`copy` (RED)
- [x] 2.2 Implement `bio.domain.combinatorics.LeafCount` as a `sealed abstract case class` with first-failure-wins `from` and `MaxN = 1000` (GREEN)
- [x] 2.3 Run `sbt test` and confirm the value-type spec passes

## 3. Algorithm (TDD)

- [x] 3.1 Write `UnrootedBinaryTreesSpec` covering: n = 5 → 15, n = 4 → 3, n = 3 → 1, n = 2 → 1, n = 1 → 1, n = 6 → 105, and n = 1000 result in `[0, 999_999]` (RED)
- [x] 3.2 Implement `bio.algorithms.combinatorics.UnrootedBinaryTrees.count` (per-step-modulo product over odd factors `3..(2n-5) by 2`, `Modulus = 1_000_000`, empty product = 1) (GREEN)
- [x] 3.3 Run `sbt test` and confirm the algorithm spec passes

## 4. Runner and wiring

- [x] 4.1 Create `bio.problems.CUNRProb` (`solve(): IO[Unit]`) following the established runner pattern
- [x] 4.2 Wire `CUNRProb.solve()` into `bio.Main.run`

## 5. Verification

- [x] 5.1 Run the full `sbt test` suite and confirm zero regressions
