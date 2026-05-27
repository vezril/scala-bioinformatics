## 1. PartialPermutationProblemError ADT

- [x] 1.1 Create `src/main/scala/bio/domain/combinatorics/PartialPermutationProblemError.scala` with `package bio.domain.combinatorics`, `sealed trait PartialPermutationProblemError`, and five cases: `NonPositiveN(value: Int)`, `NExceedsMaximum(value: Int, max: Int)`, `NonPositiveK(value: Int)`, `KExceedsMaximum(value: Int, max: Int)`, `KExceedsN(k: Int, n: Int)`
- [x] 1.2 Run `sbt compile` — confirm clean compile

## 2. PartialPermutationProblem domain type (TDD)

- [x] 2.1 Write failing tests in `src/test/scala/bio/domain/combinatorics/PartialPermutationProblemSpec.scala` for `PartialPermutationProblem.from` covering: Rosalind sample `(21, 7)` accepted, lower bound `(1, 1)` accepted, upper bound `(100, 10)` accepted, equal `n = k` boundary `(5, 5)` accepted, zero n → `Left(NonPositiveN(0))`, n=101 → `Left(NExceedsMaximum(101, 100))`, zero k → `Left(NonPositiveK(0))`, k=11 → `Left(KExceedsMaximum(11, 10))`, k > n → `Left(KExceedsN(5, 3))`, validation-order tests (both invalid → n first, n-over before k-under). Plus invariant tests: `assertDoesNotCompile("""bio.domain.combinatorics.PartialPermutationProblem(21, 7)""")` and `.copy(n = 99)` rejection
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `src/main/scala/bio/domain/combinatorics/PartialPermutationProblem.scala` in `bio.domain.combinatorics`: `sealed abstract case class PartialPermutationProblem(n: Int, k: Int)`. Define `private val MaxN: Int = 100` and `private val MaxK: Int = 10`. Companion `from(n, k)` checks in order: `n < 1` → `NonPositiveN`; `n > MaxN` → `NExceedsMaximum(n, MaxN)`; `k < 1` → `NonPositiveK`; `k > MaxK` → `KExceedsMaximum(k, MaxK)`; `k > n` → `KExceedsN(k, n)`; otherwise `Right(new PartialPermutationProblem(n, k) {})`
- [x] 2.4 Run `sbt test` — confirm `PartialPermutationProblemSpec` passes (Green)

## 3. PartialPermutations algorithm (TDD)

- [x] 3.1 Write failing tests in `src/test/scala/bio/algorithms/combinatorics/PartialPermutationsSpec.scala` for `PartialPermutations.count` covering: Rosalind sample `(21, 7) → 51200`, `(5, 1) → 5`, `(1, 1) → 1`, `(5, 5) → 120` (= 5!), `(7, 7) → 5040` (= 7!), `(10, 3) → 720`, upper-bound `(100, 10) → 472000` (= P(100,10) mod 1,000,000). Use a `pp(n, k)` helper that calls `PartialPermutationProblem.from(n, k).getOrElse(sys.error(...))`
- [x] 3.2 Run `sbt test` — confirm tests fail (Red)
- [x] 3.3 Implement `src/main/scala/bio/algorithms/combinatorics/PartialPermutations.scala` in `bio.algorithms.combinatorics`. Define `private val Modulus: Int = 1_000_000`. Body: `(0 until problem.k).foldLeft(1) { (acc, i) => (acc * (problem.n - i)) % Modulus }`. Import `bio.domain.combinatorics.PartialPermutationProblem`. No `var`, no mutable collections, no `Long`/`BigInt`
- [x] 3.4 Run `sbt test` — confirm `PartialPermutationsSpec` passes (Green)
- [x] 3.5 Refactor: confirm single-`foldLeft` body, no `var`, modulo applied after each multiplication, no intermediate overflow (max intermediate = `999_999 * 100 = 99_999_900 < Int.MaxValue`)
- [x] 3.6 Run `sbt test` — confirm all tests still pass after refactor

## 4. Final Verification

- [x] 4.1 Run `sbt clean compile` — zero errors (warnings unrelated to this change are acceptable)
- [x] 4.2 Run `sbt test` — all tests pass (count higher than 286)
- [x] 4.3 Verify the new files reside at `src/main/scala/bio/domain/combinatorics/PartialPermutationProblem.scala`, `src/main/scala/bio/domain/combinatorics/PartialPermutationProblemError.scala`, `src/main/scala/bio/algorithms/combinatorics/PartialPermutations.scala` with the expected `package` declarations
- [x] 4.4 Verify the algorithm uses `Int` arithmetic with per-step modulo (no `Long`, no `BigInt`) — open the file and confirm
