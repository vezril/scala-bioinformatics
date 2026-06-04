## 1. SignedPermutationProblemError ADT

- [x] 1.1 RED: write `bio.domain.combinatorics.SignedPermutationProblemErrorSpec` asserting `ExceedsMaximum(9, 6)` carries `n == 9` / `max == 6`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.combinatorics.SignedPermutationProblemError` as a sealed trait with `NonPositive(n)` and `ExceedsMaximum(n, max)`. Confirm the test passes.

## 2. SignedPermutationProblem domain type

- [x] 2.1 RED: write `bio.domain.combinatorics.SignedPermutationProblemSpec` covering: accepts 2, rejects 0 (`NonPositive(0)`), rejects 7 (`ExceedsMaximum(7,6)`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.combinatorics.SignedPermutationProblem` as `sealed abstract case class SignedPermutationProblem(n: Int)` with `from(n)` enforcing (first-failure-wins) `n >= 1` then `n <= 6` (MaxLength=6). Confirm GREEN.

## 3. SignedPermutations result type

- [x] 3.1 RED: write `bio.domain.combinatorics.SignedPermutationsSpec` asserting that a `SignedPermutations` of 8 permutations has `count == 8`, and `SignedPermutations(Vector(Vector(1,2),Vector(-1,2))).format == "2\n1 2\n-1 2"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.combinatorics.SignedPermutations(permutations: Vector[Vector[Int]])` with `def count: Int = permutations.size` and `def format: String = (count.toString +: permutations.map(_.mkString(" "))).mkString("\n")`. Confirm GREEN.

## 4. SignedPermutationEnumeration algorithm

- [x] 4.1 RED: write `bio.algorithms.combinatorics.SignedPermutationEnumerationSpec` covering: `n = 2` → count 8 and the set of 8 expected permutations; `n = 1` → count 2 and set `{[-1],[1]}`; `n = 3` → count 48; `n = 3` → each permutation length 3 with absolute values a permutation of `{1,2,3}`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.combinatorics.SignedPermutationEnumeration.enumerate(problem): SignedPermutations`. Get the base permutations via `Permutations.enumerate(PermutationLength.from(n))` (n pre-validated); generate the `2^n` sign combinations from bitmasks; `flatMap` each base permutation over the sign combinations, multiplying element-wise. Confirm GREEN.
- [x] 4.3 REFACTOR: extract private helpers (`basePermutations`, `signCombinations`); keep `enumerate` pure/total; rerun the spec to confirm still GREEN.

## 5. SIGNProb runner

- [x] 5.1 Add `bio.problems.SIGNProb` reading `src/main/scala/resources/sign_data.txt` (a single integer `n`), building the `SignedPermutationProblem`, running `SignedPermutationEnumeration.enumerate`, and printing `result.format` through `IO`; errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `SIGNProb.solve()` and confirm `sbt run` prints `8` followed by the eight signed permutations for the canonical dataset.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
