## 1. Error type

- [x] 1.1 RED: write `RootedTreeLeafCountErrorSpec` asserting `NonPositive(value)` and `ExceedsMaximum(value, max)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.combinatorics.RootedTreeLeafCountError` — `sealed trait` + `NonPositive(value: Int)`, `ExceedsMaximum(value: Int, max: Int)`; run the spec green.

## 2. RootedTreeLeafCount domain type

- [x] 2.1 RED: write `RootedTreeLeafCountSpec` — accepts `4` (Right, `value` = 4), accepts `1`, accepts `1000`, rejects `0` (`NonPositive(0)`), rejects `1001` (`ExceedsMaximum(1001, 1000)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.combinatorics.RootedTreeLeafCount` as `sealed abstract case class RootedTreeLeafCount(value: Int)` with `MaxN = 1000` and `from(value)` applying first-failure-wins (`value < 1` → `NonPositive`; `value > 1000` → `ExceedsMaximum`), building via `Right(new RootedTreeLeafCount(value) {})`. Run green.

## 3. RootedBinaryTrees algorithm

- [x] 3.1 RED: write `RootedBinaryTreesSpec` — `n = 4` → `15`; `n = 1` → `1`; `n = 2` → `1`; `n = 3` → `3`; `n = 10` → `459425` (modulo). Build inputs via `RootedTreeLeafCount.from`. Confirm RED.
- [x] 3.2 GREEN: create `bio.algorithms.combinatorics.RootedBinaryTrees` with `count(leaves: RootedTreeLeafCount): Int = (3 to (2 * leaves.value - 3) by 2).foldLeft(1)((acc, k) => (acc * k) % 1000000)`. Run green.
- [x] 3.3 REFACTOR: review for `var`/`while`/mutable collections (none — single `foldLeft`); confirm `Int` overflow safety (worst intermediate `999_999 × 1997 < Int.MaxValue`) and that the empty range for `n ≤ 2` yields `1`; add the `Modulus` constant. Run full `sbt test` green.

## 4. Runner

- [x] 4.1 Create `bio.problems.ROOTProb` reading `n` from `root_data.txt` (single integer line via `toIntOption`), build `RootedTreeLeafCount.from`, run `RootedBinaryTrees.count`, print the result via `IO.println`; all errors printed (never thrown).
- [x] 4.2 Wire `ROOTProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
