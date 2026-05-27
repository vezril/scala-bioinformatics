## Why

Rosalind problem 20 ("Completing a Tree") asks us to compute the minimum number of edges that must be added to an acyclic undirected graph on `n` nodes to turn it into a tree. The math is elementary: a forest with `c` connected components and `n` nodes has `n − c` edges, so the number of edges to add equals `c − 1 = n − m − 1` where `m` is the input edge count.

This problem is the framework's *second* graph algorithm (after `OverlapGraph` from spec 17). With two graph algorithms now in flight — and two different edge vocabularies (directed/String-id for overlap graphs, undirected/Int-id for tree completion) — it's the right moment to promote graph types out of the `analysis` subdomain into a dedicated `bio.{domain,algorithms}.graph` subdomain. We migrate the existing `OverlapGraph` capability at the same time so the framework stays consistent rather than scattering graph code across two subdomains.

## What Changes

### New: tree-completion capability
- Add `UndirectedEdge(u: Int, v: Int)` in `bio.domain.graph` as a `sealed abstract case class` with smart constructor `from(u, v): Either[UndirectedEdgeError, UndirectedEdge]` enforcing `u ≠ v` (no self-loops) and `u ≥ 1`, `v ≥ 1` (1-indexed node labels).
- Add `UndirectedEdgeError` sealed ADT with cases `SelfLoop(node: Int)`, `NonPositiveU(value: Int)`, `NonPositiveV(value: Int)`.
- Add `TreeCompletionProblem(n: Int, edges: Vector[UndirectedEdge])` in `bio.domain.graph` as a validated bundle with smart constructor enforcing `1 ≤ n ≤ 1000` and `1 ≤ u, v ≤ n` for every edge (endpoints must reference real nodes).
- Add `TreeCompletionProblemError` ADT: `NonPositiveN(value: Int)`, `NExceedsMaximum(value: Int, max: Int)`, `EdgeEndpointOutOfRange(edge: UndirectedEdge, n: Int)`.
- Add `TreeCompletion.edgesToAdd(problem): Int` in `bio.algorithms.graph` returning `n − edges.size − 1`. Total over the validated input. Trusts the Rosalind precondition that the input is acyclic — does not run cycle detection.

### Modified: overlap-graphs capability
- **BREAKING (refactor only — no behavior change):** Relocate the following types from `bio.{domain,algorithms}.analysis` → `bio.{domain,algorithms}.graph`:
  - `OverlapEdge`
  - `OverlapLength`
  - `OverlapLengthError`
  - `OverlapGraph`
- Update the main spec at `openspec/specs/overlap-graphs/spec.md` to reflect the new package paths. All behavior, validation, and scenario semantics remain identical.
- Move the four matching test files into the new package layout.

## Capabilities

### New Capabilities
- `tree-completion`: The `UndirectedEdge` validated edge type and its error ADT, the `TreeCompletionProblem` validated parameter bundle and its error ADT, and the `TreeCompletion.edgesToAdd` algorithm computing the minimum number of edges to add to make an acyclic undirected graph into a tree.

### Modified Capabilities
- `overlap-graphs`: Package paths change from `bio.{domain,algorithms}.analysis` to `bio.{domain,algorithms}.graph` for `OverlapEdge`, `OverlapLength`, `OverlapLengthError`, and `OverlapGraph`. No semantic changes; this is a pure refactor to consolidate graph types into a dedicated subdomain.

## Impact

- New packages populated: `bio.domain.graph` (new — first occupants will be the relocated overlap types and the new tree-completion types), `bio.algorithms.graph` (new — first occupants will be the relocated `OverlapGraph` and the new `TreeCompletion`).
- Existing `bio.domain.analysis` subdomain will have `OverlapEdge`, `OverlapLength`, `OverlapLengthError` removed.
- Existing `bio.algorithms.analysis` subdomain will have `OverlapGraph` removed.
- New tests: `UndirectedEdgeSpec`, `UndirectedEdgeErrorSpec`, `TreeCompletionProblemSpec`, `TreeCompletionProblemErrorSpec`, `TreeCompletionSpec`.
- Moved tests (no behavior change): `OverlapEdgeSpec`, `OverlapLengthSpec`, `OverlapLengthErrorSpec`, `OverlapGraphSpec` — package declarations updated to `bio.{domain,algorithms}.graph`.
- No new SBT dependencies.
- Downstream code that imports the relocated types from `bio.{domain,algorithms}.analysis` must update imports to `bio.{domain,algorithms}.graph`. (Internal-only break — there are no published consumers.)
