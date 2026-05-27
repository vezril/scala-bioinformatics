## 1. PermutationLengthError ADT

- [x] 1.1 Create `src/main/scala/bio/domain/combinatorics/PermutationLengthError.scala` with `package bio.domain.combinatorics`, `sealed trait PermutationLengthError`, and cases `final case class NonPositive(value: Int)` and `final case class ExceedsMaximum(value: Int, max: Int)`
- [x] 1.2 Run `sbt compile` — confirm clean compile

## 2. PermutationLength domain type (TDD)

- [x] 2.1 Write failing tests in `src/test/scala/bio/domain/combinatorics/PermutationLengthSpec.scala` for `PermutationLength.from` covering: lower-bound `1` accepted, Rosalind sample `3` accepted, upper-bound `7` accepted, zero → `Left(NonPositive(0))`, negative → `Left(NonPositive(-1))`, `8` → `Left(ExceedsMaximum(8, 7))`. Plus invariant tests: `assertDoesNotCompile("""bio.domain.combinatorics.PermutationLength(3)""")` and `.copy(value = 99)` rejection
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `src/main/scala/bio/domain/combinatorics/PermutationLength.scala` in `bio.domain.combinatorics`: `sealed abstract case class PermutationLength(value: Int)`. Companion `from(value)`: validate `value >= 1` (else `NonPositive`), then `value <= 7` (else `ExceedsMaximum(value, 7)`), then `Right(new PermutationLength(value) {})`. Define `private val MaxLength: Int = 7`
- [x] 2.4 Run `sbt test` — confirm `PermutationLengthSpec` passes (Green)

## 3. Permutations algorithm (TDD)

- [x] 3.1 Write failing tests in `src/test/scala/bio/algorithms/combinatorics/PermutationsSpec.scala` for `Permutations.enumerate` covering: `n=1 → Vector(Vector(1))`, `n=2` → size 2 contains both orderings, Rosalind sample `n=3` → size 6 contains all six permutations, `n=7` → size 5040, every permutation has length n, every permutation's `.toSet == Set(1..n)`, no duplicate permutations (`.distinct.size == .size`). Use a `pl(n)` helper that calls `PermutationLength.from(n).getOrElse(sys.error(...))`
- [x] 3.2 Run `sbt test` — confirm tests fail (Red) because `Permutations` does not yet exist
- [x] 3.3 Implement `src/main/scala/bio/algorithms/combinatorics/Permutations.scala` in `bio.algorithms.combinatorics`. Signature: `def enumerate(length: PermutationLength): Vector[Vector[Int]]`. Body: `(1 to length.value).toVector.permutations.toVector`. Import `bio.domain.combinatorics.PermutationLength`. No `var`, no mutable collections
- [x] 3.4 Run `sbt test` — confirm `PermutationsSpec` passes (Green)
- [x] 3.5 Refactor: confirm single-expression body, uses Scala stdlib `Iterable.permutations`, returns `Vector[Vector[Int]]` (no wrapper type)
- [x] 3.6 Run `sbt test` — confirm all tests still pass after refactor

## 4. Final Verification

- [x] 4.1 Run `sbt clean compile` — zero errors (warnings unrelated to this change are acceptable)
- [x] 4.2 Run `sbt test` — all tests pass (count higher than 271)
- [x] 4.3 Verify the new files reside at `src/main/scala/bio/domain/combinatorics/PermutationLength.scala`, `src/main/scala/bio/domain/combinatorics/PermutationLengthError.scala`, `src/main/scala/bio/algorithms/combinatorics/Permutations.scala` with the expected `package` declarations
- [x] 4.4 Verify the new `combinatorics` subdomain has been created under both `bio.domain` and `bio.algorithms` (directory listings show the new subdirectory in each)
