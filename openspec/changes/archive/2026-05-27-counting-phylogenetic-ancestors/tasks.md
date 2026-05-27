## 1. Domain ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/graph/UnrootedBinaryTreeLeafCountSpec.scala` covering: accepts `n = 3`, `n = 4` (Rosalind sample), `n = 10000`; rejects `n = 2` with `BelowMinimum(2, 3)`, `n = 0` with `BelowMinimum(0, 3)`, `n = -5` with `BelowMinimum(-5, 3)`, `n = 10001` with `ExceedsMaximum(10001, 10000)`. Run `sbt test` and confirm the suite fails to compile / fails to find the type (Red).
- [x] 1.2 Create `src/main/scala/bio/domain/graph/UnrootedBinaryTreeLeafCountError.scala` as a `sealed trait` with cases `BelowMinimum(n: Int, min: Int)` and `ExceedsMaximum(n: Int, max: Int)`.
- [x] 1.3 Create `src/main/scala/bio/domain/graph/UnrootedBinaryTreeLeafCount.scala` as `sealed abstract case class UnrootedBinaryTreeLeafCount(n: Int)` with companion `from(n: Int): Either[UnrootedBinaryTreeLeafCountError, UnrootedBinaryTreeLeafCount]` enforcing `3 <= n <= 10000`, lower-bound checked first.
- [x] 1.4 Run `sbt test` and confirm the `UnrootedBinaryTreeLeafCountSpec` suite is now Green.

## 2. Algorithm (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/algorithms/graph/PhylogeneticAncestorsSpec.scala` covering: `n = 4 → 2` (Rosalind canonical sample), `n = 3 → 1` (lower boundary), `n = 10000 → 9998` (upper boundary), `n = 100 → 98` (mid-range). Use a private `problem(n: Int)` helper that constructs `UnrootedBinaryTreeLeafCount.from(n)` and `getOrElse(sys.error(...))`. Run `sbt test` and confirm the suite fails to compile (Red).
- [x] 2.2 Create `src/main/scala/bio/algorithms/graph/PhylogeneticAncestors.scala` exposing `def internalNodes(problem: UnrootedBinaryTreeLeafCount): Int = problem.n - 2`. Include Scaladoc citing the unrooted-binary degree-sum identity (`3I + L = 2(I + L - 1) ⇒ I = L - 2`) and the Rosalind problem code (INOD).
- [x] 2.3 Run `sbt test` and confirm the `PhylogeneticAncestorsSpec` suite is now Green.

## 3. Refactor & Final Verification

- [x] 3.1 Re-read both new source files and tests; tighten Scaladoc, ensure naming and validation order match the rest of the framework, ensure no stray imports.
- [x] 3.2 Run `sbt test` one more time and confirm the full suite (including the new tests) passes with zero regressions.
