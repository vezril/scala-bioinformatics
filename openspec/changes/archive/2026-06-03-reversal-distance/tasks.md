## 1. ReversalDistanceProblemError ADT

- [x] 1.1 RED: write `bio.domain.combinatorics.ReversalDistanceProblemErrorSpec` asserting `LengthMismatch(5, 8)` carries `sourceLength == 5` / `targetLength == 8`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.combinatorics.ReversalDistanceProblemError` as a sealed trait with `LengthMismatch(sourceLength, targetLength)` and `LengthExceedsMax(length, max)`. Confirm the test passes.

## 2. ReversalDistanceProblem domain type

- [x] 2.1 RED: write `bio.domain.combinatorics.ReversalDistanceProblemSpec` covering: accepts two length-10 permutations, accepts two empty permutations, rejects length 3 vs 4 (`LengthMismatch(3,4)`), rejects two length-11 (`LengthExceedsMax(11,10)`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.combinatorics.ReversalDistanceProblem` as `sealed abstract case class ReversalDistanceProblem(source: Permutation, target: Permutation)` with `from(...)` enforcing (first-failure-wins) equal length then MaxLength=10, returning `Right(new ReversalDistanceProblem(...) {})` or the appropriate `Left`. Confirm GREEN.

## 3. ReversalDistance result type

- [x] 3.1 RED: write `bio.domain.combinatorics.ReversalDistanceSpec` asserting `ReversalDistance(9).format == "9"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.combinatorics.ReversalDistance(distance: Int)` with `def format: String = distance.toString`. Confirm GREEN.

## 4. ReversalDistanceSearch algorithm

- [x] 4.1 RED: write `bio.algorithms.combinatorics.ReversalDistanceSearchSpec` covering: identical length-10 permutations → 0; `1 2 3 4 5` vs `1 4 3 2 5` → 1; equal empty → 0; the five canonical sample pairs → `Vector(9,4,5,7,0)`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.combinatorics.ReversalDistanceSearch.distance(problem): ReversalDistance`. Relabel target to identity and apply to source; bidirectional BFS over the 45 interval reversals, permutations packed into a `Long` key, `mutable.LongMap[Int]` visited maps, expanding the smaller frontier and returning the per-level minimum meeting `dS + dG`. Imperative internals, pure signature. Confirm GREEN.
- [x] 4.3 REFACTOR: extract private helpers (`encode`/`decode`, `neighbours`, `relabel`, `bidirectional`); keep `distance` pure/total; rerun the spec to confirm still GREEN.

## 5. REARProb runner

- [x] 5.1 Add `bio.problems.REARProb` reading `src/main/scala/resources/rear_data.txt` (blank-line-separated blocks, each two permutation lines), parsing each line into a `Vector[Int]`, building `Permutation`s and a `ReversalDistanceProblem` per block, running `ReversalDistanceSearch.distance`, and printing the distances space-separated on one line through `IO`; errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `REARProb.solve()` and confirm `sbt run` prints `9 4 5 7 0` for the canonical dataset.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
