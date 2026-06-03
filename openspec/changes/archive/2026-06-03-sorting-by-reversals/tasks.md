## 1. Reversal domain type

- [x] 1.1 RED: write `bio.domain.combinatorics.ReversalSpec` asserting `Reversal(4, 9).format == "4 9"`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.combinatorics.Reversal(from: Int, to: Int)` (plain `final case class`) with `def format: String = s"$from $to"`. Confirm the test passes.

## 2. ReversalSorting result type

- [x] 2.1 RED: write `bio.domain.combinatorics.ReversalSortingSpec` asserting `ReversalSorting(2, Vector(Reversal(4,9), Reversal(2,5))).format == "2\n4 9\n2 5"` and that distance `0` with no reversals formats to `"0"`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.combinatorics.ReversalSorting(distance: Int, reversals: Vector[Reversal])` with `format` = distance line followed by each reversal's `format`, all joined by `\n`. Confirm GREEN.

## 3. ReversalSortingSearch algorithm

- [x] 3.1 RED: write `bio.algorithms.combinatorics.ReversalSortingSearchSpec` with a helper that applies a `Vector[Reversal]` (1-based intervals) to a `Vector[Int]`. Cover: canonical sample (`1..10` → `1 8 9 3 2 7 6 5 4 10`) → distance 2 and the reversals sort source into target; identical perms → distance 0 and empty; `1 2 3 4 5` vs `1 4 3 2 5` → distance 1 and the reversal sorts it; and that every returned reversal satisfies `1 ≤ from < to ≤ n`. Confirm RED.
- [x] 3.2 GREEN: create `bio.algorithms.combinatorics.ReversalSortingSearch.sort(problem: ReversalDistanceProblem): ReversalSorting`. Relabel target to identity and apply to source; bidirectional BFS over the 45 interval reversals with parent-state and parent-interval maps (`mutable.LongMap`); on meeting, stitch the forward half (start→meet, reversed) and backward half (meet→goal) into the reversal sequence; convert packed 0-based intervals to 1-based `Reversal`s. Imperative internals, pure signature. Confirm GREEN.
- [x] 3.3 REFACTOR: extract private helpers (`encode`/`decode`, `neighbours` returning `(state, packedInterval)`, `relabel`, `reconstruct`); keep `sort` pure/total; rerun the spec to confirm still GREEN.

## 4. SORTProb runner

- [x] 4.1 Add `bio.problems.SORTProb` reading `src/main/scala/resources/sort_data.txt` (two permutation lines), parsing into `Vector[Int]`, building `Permutation`s and a `ReversalDistanceProblem`, running `ReversalSortingSearch.sort`, and printing `result.format` through `IO`; errors print a message rather than throw.
- [x] 4.2 Point `Main.scala` at `SORTProb.solve()` and confirm `sbt run` prints `2` followed by two reversal lines for the canonical dataset.

## 5. Full suite

- [x] 5.1 Run `sbt test` and confirm the whole suite is green.
