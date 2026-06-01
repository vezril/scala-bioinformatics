## Why

Rosalind problem QRTD ("Quartet Distance") asks for the quartet distance between two
unrooted binary trees over the same taxa: `dq(T1, T2) = q(T1) + q(T2) − 2·q(T1, T2)`,
the number of 4-taxon subsets whose induced quartet topology differs between the two
trees. It extends the existing quartet/Newick tooling (QRT, SPTD, CNTQ) to a pairwise
comparison metric and completes the cluster of tree-comparison problems in the suite.

## What Changes

- Add a validated **quartet-distance** capability that ingests a shared taxon list plus
  two unrooted binary trees in Newick format and produces the quartet distance.
- Introduce a `QuartetDistanceProblem` domain bundle (smart-constructed, invariant-bearing)
  with a dedicated `QuartetDistanceProblemError` ADT covering empty taxa, duplicate taxa,
  and tree/taxa mismatch (per tree).
- Add a pure `QuartetDistance.compute` algorithm that counts shared quartets without
  enumerating 4-subsets: for each leaf pair `{a, b}` it labels every other leaf by its
  median node `median(a, b, x)` in each tree, and a quartet `ab|cd` is shared iff `c`
  and `d` share that median label in both trees. Summing `C(cellSize, 2)` over the
  common refinement of the two labellings (then halving) yields `shared`, and
  `dq = 2·(C(n, 4) − shared)`. Runs in `O(n³)` overall (`O(n²)` all-pairs-LCA setup per
  tree, `O(n)` per leaf pair). Returns `Long` (max `dq` for `n = 2000` is ~`1.3×10¹²`,
  beyond `Int` range).
- Reuse the existing `bio.parsing.NewickParser` and `bio.domain.graph.NewickTree`.
- Add a `QRTDProb` IO runner that reads `qrtd_data.txt` (taxa line + two Newick lines)
  and prints the distance.
- Cross-check correctness against an independent `O(n⁴)` four-point-condition reference
  over random tree pairs, and confirm the full Rosalind ceiling (`n ≤ 2000`) completes
  well within the submission time limit.

## Capabilities

### New Capabilities
- `quartet-distance`: validate a two-tree quartet-distance input bundle and compute the
  quartet distance `dq = 2·(C(n, 4) − shared)` via per-leaf-pair median-label counting.

### Modified Capabilities
<!-- None: this introduces a new capability and reuses existing Newick parsing/domain types unchanged. -->

## Impact

- New domain: `bio.domain.graph.QuartetDistanceProblem`, `QuartetDistanceProblemError`.
- New algorithm: `bio.algorithms.graph.QuartetDistance`.
- New runner: `bio.problems.QRTDProb` (+ `Main.scala` wiring, `resources/qrtd_data.txt`).
- Reuses `NewickParser` / `NewickTree` with no changes.
- No changes to existing capabilities or shared error types.
