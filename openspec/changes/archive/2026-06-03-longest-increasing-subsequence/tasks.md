## 1. PermutationError ADT

- [x] 1.1 RED: write `bio.domain.combinatorics.PermutationErrorSpec` asserting `TooLong(12000, 10000)` carries `length == 12000` / `max == 10000`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.combinatorics.PermutationError` as a sealed trait with `TooLong(length, max)` and `NotAPermutation(values: Vector[Int])`. Confirm the test passes.

## 2. Permutation domain type

- [x] 2.1 RED: write `bio.domain.combinatorics.PermutationSpec` covering: accepts `Vector(5,1,4,2,3)`, accepts empty, rejects length 10001 (`TooLong(10001,10000)`), rejects `Vector(1,2,2)` (`NotAPermutation(Vector(1,2,2))`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.combinatorics.Permutation` as `sealed abstract case class Permutation(values: Vector[Int])` with `from(...)` enforcing (first-failure-wins) MaxLength=10000 then "is a permutation of 1..length" (sorted equals `1 to length`), returning `Right(new Permutation(...) {})` or the appropriate `Left`. Confirm GREEN.

## 3. MonotonicSubsequences result type

- [x] 3.1 RED: write `bio.domain.combinatorics.MonotonicSubsequencesSpec` asserting `MonotonicSubsequences(Vector(1,2,3), Vector(5,4,2)).format == "1 2 3\n5 4 2"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.combinatorics.MonotonicSubsequences(increasing: Vector[Int], decreasing: Vector[Int])` with `def format: String = Seq(increasing, decreasing).map(_.mkString(" ")).mkString("\n")`. Confirm GREEN.

## 4. LongestSubsequences algorithm

- [x] 4.1 RED: write `bio.algorithms.combinatorics.LongestSubsequencesSpec` with a helper validating a result subsequence (strict monotonicity + genuine subsequence of the input + expected length). Cover: canonical `5 1 4 2 3` → increasing valid length 3, decreasing valid length 3; sorted `1 2 3 4` → increasing `Vector(1,2,3,4)`, decreasing length 1; single `1` → both `Vector(1)`; empty → both empty. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.combinatorics.LongestSubsequences.find(permutation): MonotonicSubsequences`. Implement an O(n log n) patience-sorting LIS core parameterised by a `lessThan` comparison with predecessor reconstruction; run with `_ < _` for increasing and `_ > _` for decreasing. Imperative `Array[Int]` internals, pure signature. Confirm GREEN.
- [x] 4.3 REFACTOR: extract the LIS core as a private helper taking the value vector and comparison; keep `find` pure/total; rerun the spec to confirm still GREEN.

## 5. LGISProb runner

- [x] 5.1 Add `bio.problems.LGISProb` reading `src/main/scala/resources/lgis_data.txt` (line 1 = n, line 2 = space-separated permutation), parsing into a `Vector[Int]`, building the `Permutation`, running `LongestSubsequences.find`, and printing `result.format` through `IO`; errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `LGISProb.solve()` and confirm `sbt run` prints two valid subsequences for the canonical dataset.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
