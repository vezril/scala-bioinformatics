## 1. Error type

- [x] 1.1 RED: write `RestrictionMapProblemErrorSpec` asserting `InvalidSize(size)` and `NonPositiveDistance(index, value)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.combinatorics.RestrictionMapProblemError` — `sealed trait` + `InvalidSize(size: Int)`, `NonPositiveDistance(index: Int, value: Int)`; run the spec green.

## 2. RestrictionMapProblem domain type

- [x] 2.1 RED: write `RestrictionMapProblemSpec` — accepts `[2,2,3,3,4,5,6,7,8,10]` (Right, distances preserved), accepts empty multiset, accepts `[5]`, rejects `[2,3]` (`InvalidSize(2)`), rejects `[2,-1,3]` (`NonPositiveDistance(1,-1)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.combinatorics.RestrictionMapProblem` as `sealed abstract case class RestrictionMapProblem(distances: Vector[Int])` with `from(distances)` applying first-failure-wins: size not triangular (no `n >= 1` with `n(n-1)/2 == size`) → `InvalidSize(size)`; else first non-positive element (index order) → `NonPositiveDistance(index, value)`; else `Right(new RestrictionMapProblem(distances) {})`. Triangular check via `n = round((1 + sqrt(1 + 8*size)) / 2)` then confirm `n(n-1)/2 == size`. Run green.

## 3. RestrictionMap result type

- [x] 3.1 RED: write `RestrictionMapResultSpec` — `points` field exposure (`[0,2,5]`), `format` of `[0,2,4,7,10]` → `"0 2 4 7 10"`, `format` of `[0]` → `"0"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.combinatorics.RestrictionMap` result — `final case class RestrictionMap(points: Vector[Int])` with `format: String = points.mkString(" ")`. Run green.

## 4. RestrictionMapConstruction algorithm

- [x] 4.1 RED: write `RestrictionMapConstructionSpec` with scenarios — canonical `[2,2,3,3,4,5,6,7,8,10]` → `Some` whose positions expand (all positive pairwise differences) to the sorted input, head `0`, size 5; `[5]` → `Some([0,5])`; empty → `Some([0])`; `[1,1,1]` → `None`. Use a test helper `deltaX(points)` computing sorted pairwise differences. Build inputs via `RestrictionMapProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.combinatorics.RestrictionMapConstruction` with `solve(problem: RestrictionMapProblem): Option[RestrictionMap]`. Empty distances → `Some(RestrictionMap(Vector(0)))`. Else `width = max`, multiset as `Map[Int,Int]` counts, remove one `width`, seed `X = Set(0, width)`, run pure-recursive `place`: if multiset empty → `Some(X)`; else `y = keys.max`, `tryPoint(y) orElse tryPoint(width - y)`. `tryPoint(p)`: skip if `X` contains `p`; compute distances `|p - xi|` for all `xi in X`; `removeAll` them from the multiset (fold, decrement one occurrence each, `None` if unavailable), then recurse. Return positions ascending in `RestrictionMap`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — pure recursion + immutable `Map`/`Set`); extract `place`/`tryPoint`/`removeAll`/`decrement` private helpers. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.PDPLProb` reading `pdpl_data.txt` (a single whitespace-separated line of integers). Parse: `split("\\s+")` + `toIntOption` folded into `Either`; build `RestrictionMapProblem.from`; run `RestrictionMapConstruction.solve`; on `Some` print `format`, on `None` print a "no restriction map exists" message, via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `PDPLProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
