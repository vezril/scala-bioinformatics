## Context

The suite already models unrooted binary trees via `bio.domain.graph.NewickTree`
(parsed by `bio.parsing.NewickParser`) and has solved adjacent problems: QRT (inferring
consistent quartets), SPTD (split distance), and CNTQ (counting quartets, where
`q(T) = C(n, 4)` because every 4-subset is resolved in a fully binary tree). QRTD asks
for the quartet distance between two such trees:

`dq(T1, T2) = q(T1) + q(T2) − 2·q(T1, T2)`

Because both inputs are fully resolved unrooted binary trees, `q(T1) = q(T2) = C(n, 4)`,
so `dq = 2·(C(n, 4) − shared)` where `shared` is the number of 4-taxon subsets whose
induced quartet topology is identical in both trees. The Rosalind constraint is `n ≤ 2000`.

## Goals / Non-Goals

**Goals:**
- A validated `QuartetDistanceProblem` bundle (taxa + two trees) with first-failure-wins
  validation, following the established `sealed abstract case class` + smart-constructor
  pattern (no leaked `apply`/`copy`).
- A pure `QuartetDistance.compute(problem): Long` that matches the canonical sample (→ 4)
  and completes the full Rosalind ceiling (`n ≤ 2000`) within the submission time limit.
- Reuse `NewickParser` / `NewickTree` unchanged.

**Non-Goals:**
- The asymptotically optimal `O(n log n)` Brodal et al. counter. An `O(n³)` shared-quartet
  count is fast enough for `n ≤ 2000` (seconds) and far simpler to verify.
- Support for non-binary / multifurcating trees or rooted-tree semantics.

## Decisions

**Decision 1 — Count shared quartets by leaf-pair median labelling (no subset enumeration).**
A resolved quartet has topology `ab|cd` exactly when, along the `a`–`b` tree path, both
`c` and `d` branch off at the same internal node. So for each leaf pair `{a, b}`, label
every other leaf `x` by its *median* node `median(a, b, x)` (where the path to `x` meets
the `a`–`b` path); `{c, d}` is an `ab|cd` quartet iff `c` and `d` share that label. For a
fixed pair, a quartet is shared by both trees iff `c, d` agree on the median label in `T1`
*and* in `T2`, so summing `C(cellSize, 2)` over the common refinement of the two
labellings counts shared quartets attributed to the cherry `{a, b}`. Each quartet is
attributed to both its cherries, so the grand total over all leaf pairs is halved:
`shared = ½ · Σ_{ {a,b} } Σ_cells C(cellSize, 2)`; then `dq = 2·(C(n, 4) − shared)`.
*Rationale:* `O(n³)` overall, finishing `n ≤ 2000` in seconds, while remaining simple to
reason about. *Alternative considered:* enumerating all `C(n, 4)` subsets (`O(n⁴)`) — the
original approach, replaced because it is intractable at `n ≈ 1600` (hours).
*Alternative considered:* the Brodal et al. `O(n log n)` counter — fastest but high
implementation/bug risk for no practical benefit at this scale.

**Decision 2 — Resolve median nodes via the deepest pairwise LCA.**
Root each tree at its parsed Newick root (no re-rooting needed). The median of three
leaves is rooting-independent and equals the deepest of their three pairwise LCAs, so
`median(a, b, x) = argmax_depth { LCA(a,b), LCA(a,x), LCA(b,x) }`. Because two leaves in a
binary tree branch off the `a`–`b` path at the same node iff they share an off-path
subtree (each internal path node has a single off-path edge), the median node id is a
sufficient grouping label, and labels are compared only *within* a tree.
*Implementation:* precompute, per tree, a flat `n × n` all-pairs-LCA node-id table in
`O(n²)` (one DFS gathering per-subtree leaf lists, filling each cross-child leaf pair with
its LCA), plus a per-node depth array. The hot loop uses primitive `int` arrays and an
intrusive-bucket grouping (no hashmaps); it runs on a thread with a 64 MB stack to
tolerate deep/caterpillar trees.

**Decision 3 — Return `Long`.**
Max `dq` for `n = 2000` is `2·C(2000, 4) ≈ 1.3×10¹²`, which overflows `Int`. The algorithm
returns `Long`; the runner prints it directly.

**Decision 4 — Error ADT mirrors SPTD.**
`QuartetDistanceProblemError`: `EmptyTaxa`, `DuplicateTaxon(name)`,
`TreeTaxaMismatch(treeIndex, missing, extra)`. Validation order: empty → first duplicate →
tree-1 mismatch → tree-2 mismatch → success. Consistent with `SplitDistanceProblem`.

## Risks / Trade-offs

- **`O(n³)` time** → for `n = 2000` this is ~10¹⁰ cache-friendly primitive operations,
  completing in seconds; verified on an `n = 1614` sample (~19 s end-to-end including JVM
  startup). Not asymptotically optimal, but well within the submission time limit.
- **`O(n²)` all-pairs-LCA tables** (two `int[n²]` arrays ≈ 21 MB at `n = 2000`) →
  acceptable within default heap; the transient per-subtree leaf lists are bounded by
  `O(n·height)`.
- **Median mis-resolution / counting bug** → mitigated by the deepest-LCA correctness
  argument and by cross-checking against an independent `O(n⁴)` four-point-condition
  reference over random tree pairs (`n = 5..12`), plus the `n = 4` differing-topology case
  (`dq = 2`) and the canonical sample (`dq = 4`).
- **Deep recursion during table build** → mitigated by running the build/compute on a
  dedicated large-stack thread.
