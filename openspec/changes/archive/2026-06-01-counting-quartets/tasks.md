## 1. Error ADT

- [x] 1.1 Create `bio.domain.graph.CountingQuartetsProblemError` (sealed trait + `BelowMinimum(value, min)`, `ExceedsMaximum(value, max)`, `LeafCountMismatch(declared, actual)`)

## 2. Validated input bundle (TDD)

- [x] 2.1 Write `CountingQuartetsProblemSpec` covering: accepts the canonical sample (`n = 6`, sample tree); rejects `n = 3` → `BelowMinimum(3, 4)`, `n = 5001` → `ExceedsMaximum(5001, 5000)`, leaf-count mismatch (`n = 5`, 6-leaf tree) → `LeafCountMismatch(5, 6)`; first-failure-wins (`n = 3` + mismatched tree → `BelowMinimum`); `assertDoesNotCompile` for `apply`/`copy` (RED)
- [x] 2.2 Implement `bio.domain.graph.CountingQuartetsProblem` as a `sealed abstract case class` with first-failure-wins `from(n, tree)` using a leaf-count helper (GREEN)
- [x] 2.3 Run `sbt test` and confirm the input-bundle spec passes

## 3. Algorithm (TDD)

- [x] 3.1 Write `CountingQuartetsSpec` covering: sample `n = 6` → `15`; `n = 4` → `1`; `n = 100` → `921225` (modulus wrap); `n = 5000` → value in `[0, 999999]` (RED)
- [x] 3.2 Implement `bio.algorithms.graph.CountingQuartets.count` (`Long` product `n(n-1)(n-2)(n-3)`, exact `/24`, then `% 1000000`) (GREEN)
- [x] 3.3 Run `sbt test` and confirm the algorithm spec passes

## 4. Runner and wiring

- [x] 4.1 Create `bio.problems.CNTQProb` (`solve(): IO[Unit]`) reading `src/main/scala/resources/cntq_data.txt` (`n` line + Newick line), parsing `n` and the tree via `NewickParser`, building the problem, and printing the count
- [x] 4.2 Wire `CNTQProb.solve()` into `bio.Main.run`

## 5. Verification

- [x] 5.1 Run the full `sbt test` suite and confirm zero regressions
