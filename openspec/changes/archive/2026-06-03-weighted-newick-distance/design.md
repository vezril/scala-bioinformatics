## Context

NKEW ("Newick Format with Edge Weights") gives `n` weighted Newick trees (≤ 40, each ≤ 200 nodes), each followed by a query pair `x y`, and asks for the distance between `x` and `y` in each tree — the sum of edge weights on the unique path between them.

The project's `bio.parsing.NewickParser` and `bio.domain.graph.NewickTree` (used by NWCK/CTBL/MEND/EUBT) are **unweighted**: a label is any run of non-`(),;` characters, so a `:weight` would be folded into the label, and the tree has no weight field. NKEW therefore needs its own weighted parser and tree. The runner mirrors NWCK's multi-block input handling.

## Goals / Non-Goals

**Goals:**
- A `WeightedNewickTree` ADT (`label`, `children: Vector[WeightedChild]`, each `WeightedChild` carrying an edge `weight`).
- A `WeightedNewickParser` reading `label:weight` edges, returning `Either[WeightedNewickParseError, WeightedNewickTree]`.
- A validated `WeightedTreeDistanceProblem(tree, x, y)` requiring both labels to occur in the tree.
- Pure, total `WeightedNewickDistance.between(problem): Double` (sum of edge weights on the `x`–`y` path).
- An `NKEWProb` runner producing the space-separated per-tree distances.

**Non-Goals:**
- Modifying the unweighted NWCK parser/tree.
- Quoted labels or scientific-notation edge cases beyond plain decimals (Rosalind weights are plain numbers).

## Decisions

**1. Dedicated weighted parser and tree.**
Grammar: `node := ("(" child ("," child)* ")")? label?`, `child := node (":" weight)?`. A label is a maximal run of characters not in `(),;:` (the colon now begins a weight). Edge weights attach to children (the edge to the parent); the root has none. The parser is recursive descent returning `(WeightedNewickTree, position)`, validating paren balance up front and a terminating `;`, mirroring `NewickParser`'s structure. A `:weight` that fails to parse as a `Double` yields `InvalidWeight(text, position)`. A child without an explicit weight defaults to `0.0`.

**2. Distance = sum of edge weights on the unique tree path.**
The parsed tree is indexed into an undirected weighted adjacency map with integer node ids (assigned by a pure DFS that threads a counter and records `labelToId` and parent→child edges). The distance between `x` and `y` is found by a depth-first search from `x`'s id to `y`'s id, accumulating edge weights; because the graph is a tree, the first path found is the unique one. Recursion depth is bounded by node count (≤ 200), so plain recursion is safe.

**3. Validation rules and order (first-failure-wins).**
`WeightedTreeDistanceProblem.from(tree, x, y)` checks that `x` occurs in the tree's labels, then that `y` does, failing with `NodeNotFound(label)` for the first missing one.

**4. Pure functional throughout.**
Parsing, indexing (DFS threading an immutable counter/maps), and the path search are all immutable recursion / folds — no `var`, `while`, or mutable collection. The algorithm returns a bare `Double` (mirroring NWCK's bare-`Int` `NewickDistance.between`); the runner formats each distance, rendering whole numbers without a decimal point, and joins them with spaces.

**5. Naming and placement.**
`WeightedNewickTree`/`WeightedChild`, `WeightedTreeDistanceProblem`, and `WeightedTreeDistanceProblemError` live in `bio.domain.graph`; `WeightedNewickParser`/`WeightedNewickParseError` in `bio.parsing`; the algorithm `WeightedNewickDistance.between` in `bio.algorithms.graph`. Mirrors the unweighted NWCK layout.

## Risks / Trade-offs

- **[Weighted parsing correctness]** → verified on the sample (`(dog:42,cat:33);` → cat–dog = 75; `((dog:4,cat:3):74,robot:98,elephant:58);` → dog–elephant = 136). Edge cases (single edge, deep path) covered by scenarios.
- **[Output formatting]** → integer-valued distances render without a trailing `.0` (the sample shows `75 136`); non-integer weights would render as decimals. Documented.
- **[Recursion depth]** → bounded by ≤ 200 nodes per tree; plain recursion is safe.
- **[Missing query node]** → rejected by `WeightedTreeDistanceProblem.from` with `NodeNotFound`; covered by a scenario.
- **[Not reusing the unweighted parser]** → necessary: it cannot represent or read weights; the duplication is intentional and isolated.
