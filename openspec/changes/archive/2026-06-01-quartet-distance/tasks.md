## 1. Error ADT (`QuartetDistanceProblemError`)

- [x] 1.1 RED: write `QuartetDistanceProblemErrorSpec` asserting the sealed ADT exposes
  `EmptyTaxa`, `DuplicateTaxon(name)`, and `TreeTaxaMismatch(treeIndex, missing, extra)`;
  confirm it fails to compile ("not found: ...")
- [x] 1.2 GREEN: create `bio.domain.graph.QuartetDistanceProblemError` sealed trait with
  the three cases; confirm `sbt test` green
- [x] 1.3 REFACTOR: review naming/fields against `SplitDistanceProblemError`; rerun tests

## 2. Validated bundle (`QuartetDistanceProblem`)

- [x] 2.1 RED: write `QuartetDistanceProblemSpec` covering valid construction, empty taxa,
  duplicate taxon (first wins), tree-1 mismatch, tree-2 mismatch, first-failure ordering,
  and `assertDoesNotCompile` for public `apply`/`copy`; confirm RED
- [x] 2.2 GREEN: create `bio.domain.graph.QuartetDistanceProblem` as a
  `sealed abstract case class(taxa, tree1, tree2)` with `from(...)` returning
  `Either[QuartetDistanceProblemError, QuartetDistanceProblem]`; validation order
  empty → first duplicate → tree-1 mismatch → tree-2 mismatch → `Right(new ... {})`;
  reuse a recursive `leafLabels` helper; confirm `sbt test` green
- [x] 2.3 REFACTOR: deduplicate mismatch logic; rerun tests

## 3. Algorithm (`QuartetDistance.compute`)

- [x] 3.1 RED: write `QuartetDistanceSpec` with the canonical sample (→ 4), identical
  trees (→ 0), an `n = 4` differing-topology case (→ 2), and symmetry; confirm RED
- [x] 3.2 GREEN: implement `bio.algorithms.graph.QuartetDistance.compute(problem): Long`:
  per tree build an `O(n²)` all-pairs-LCA node-id table + depth array; for each leaf pair
  `{a,b}` label every other leaf `x` by its median `median(a,b,x)` (deepest of the three
  pairwise LCAs) in each tree; sum `C(cellSize,2)` over the common refinement of the two
  labellings; halve to get `shared`; return `2 * (C(n,4) - shared)` as `Long`; confirm
  `sbt test` green
- [x] 3.3 REFACTOR: tighten the hot loop to primitive `int` arrays with intrusive-bucket
  grouping (no hashmaps), run the build/compute on a large-stack thread for deep trees,
  add Scaladoc documenting the median-labelling counting argument and `O(n³)` complexity;
  add a cross-check test against an independent `O(n⁴)` four-point reference; rerun tests

## 4. IO runner and wiring

- [x] 4.1 Create `bio.problems.QRTDProb` reading `resources/qrtd_data.txt` (taxa line +
  two Newick lines), parsing trees via `NewickParser`, chaining Eithers, printing the
  `Long` distance (mirror `SPTDProb` structure)
- [x] 4.2 Wire `QRTDProb.solve()` into `Main.scala` (comment out the prior runner)
- [x] 4.3 Run `Main` against the sample `qrtd_data.txt` and confirm it prints `4`

## 5. Verification

- [x] 5.1 Run full `sbt test`; confirm all suites green and the new tests included
