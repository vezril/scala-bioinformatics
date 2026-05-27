## Context

Spec 20 takes a positive integer `n` (up to 1000) and an adjacency list of an acyclic undirected graph on `n` nodes and returns the minimum number of edges to add to make it a tree. The mathematics is elementary: a forest on `n` nodes with `m` edges has `c = n − m` connected components, and merging them into a single tree requires exactly `c − 1 = n − m − 1` additional edges. The graph structure itself does not matter beyond `n` and `m`.

This is the framework's second graph-shaped algorithm (after `OverlapGraph` from spec 17). Two graph algorithms with two distinct edge vocabularies (directed/String-id for overlap graphs, undirected/Int-id for tree completion) is enough mass to justify promoting graph types into their own subdomain. The migration is bundled into this change so the framework stays consistent at every commit.

## Goals / Non-Goals

**Goals:**
- A new `bio.{domain,algorithms}.graph` subdomain housing both the existing graph types (relocated) and the new tree-completion types.
- `UndirectedEdge` as a smart-constructed value type — every individual edge is independently validated (`u ≠ v`, `u ≥ 1`, `v ≥ 1`). Different from the existing `OverlapEdge`'s "public ctor, no further invariant" pattern because *here* we have real per-edge invariants worth enforcing at the value level.
- `TreeCompletionProblem` as a validated parameter bundle enforcing `1 ≤ n ≤ 1000` and `1 ≤ u, v ≤ n` per edge.
- `TreeCompletion.edgesToAdd(problem): Int` — one-line algorithm (`n − edges.size − 1`), total over the validated bundle.
- A clean migration of `OverlapEdge`, `OverlapLength`, `OverlapLengthError`, and `OverlapGraph` from `analysis` → `graph` with identical behavior.

**Non-Goals:**
- Cycle detection on the input. Rosalind guarantees the input is acyclic; we trust that precondition. If a future spec needs cycle detection it can add it as a separate capability (likely a `UnionFind` or `ConnectedComponents` helper).
- Generic `Graph[V, E]` abstraction. Still overkill — two algorithms with different edge types don't motivate a unified interface. The two stay as separate concrete domain types until a third forces the issue.
- Deduplication of duplicate edges in the input. If the input contains the same edge twice the algorithm produces a wrong answer (it would over-count `m`). This is fine because Rosalind's adjacency list does not duplicate edges; documented in scaladoc and left to the input source to guarantee.
- Validating that `(u, v)` and `(v, u)` are not both supplied. Same reasoning as above — Rosalind canonicalizes its input and we trust it.

## Decisions

### Decision 1: `UndirectedEdge` is smart-constructed, unlike `OverlapEdge`

```scala
sealed abstract case class UndirectedEdge(u: Int, v: Int)

object UndirectedEdge {
  def from(u: Int, v: Int): Either[UndirectedEdgeError, UndirectedEdge] =
    if (u < 1) Left(UndirectedEdgeError.NonPositiveU(u))
    else if (v < 1) Left(UndirectedEdgeError.NonPositiveV(v))
    else if (u == v) Left(UndirectedEdgeError.SelfLoop(u))
    else Right(new UndirectedEdge(u, v) {})
}
```

The framework's previous edge type (`OverlapEdge(from: String, to: String)`) is a public-ctor case class because string ids carry no enforceable invariant. *Here* there is a real invariant (`u ≠ v`, positive endpoints), so the smart constructor pattern earns its keep.

**Validation order:** `u` lower bound, `v` lower bound, then self-loop. First-failure-wins. Consistent with the framework's `from`-pattern across `PartialPermutationProblem`, `RandomMatchProblem`, etc.

**Alternative considered:** A public-ctor `UndirectedEdge` and let `TreeCompletionProblem` do all the per-edge checks. Rejected because invalid edges can be constructed in isolation (e.g. by a test fixture), and "this Edge value is always well-formed" is a more useful invariant to carry at the type level than "trust the bundle".

### Decision 2: `TreeCompletionProblem` validates endpoint references against `n`

```scala
sealed abstract case class TreeCompletionProblem(n: Int, edges: Vector[UndirectedEdge])

object TreeCompletionProblem {
  private val MaxN: Int = 1000

  def from(n: Int, edges: Vector[UndirectedEdge]): Either[TreeCompletionProblemError, TreeCompletionProblem] =
    if (n < 1) Left(TreeCompletionProblemError.NonPositiveN(n))
    else if (n > MaxN) Left(TreeCompletionProblemError.NExceedsMaximum(n, MaxN))
    else edges.find(e => e.u > n || e.v > n) match {
      case Some(edge) => Left(TreeCompletionProblemError.EdgeEndpointOutOfRange(edge, n))
      case None       => Right(new TreeCompletionProblem(n, edges) {})
    }
}
```

`UndirectedEdge.from` already enforces `u, v ≥ 1` and `u ≠ v`, so the bundle only needs to check the upper bound (endpoints reference real nodes in `[1, n]`). This division of responsibility — per-value invariants on the value type, cross-input constraints on the bundle — matches the framework's existing layering (`Probability` validates element-wise, `RandomMatchProblem` validates the array as a whole).

**Validation order:** `n` lower bound, `n` upper bound, then endpoint scan. First-failure-wins.

**Error case `EdgeEndpointOutOfRange` carries the full edge plus `n`:** so callers can report exactly which edge violated the contract without re-scanning.

### Decision 3: The algorithm is `n − edges.size − 1`, full stop

```scala
def edgesToAdd(problem: TreeCompletionProblem): Int =
  problem.n - problem.edges.size - 1
```

A forest on `n` nodes with `m` edges has exactly `n − m` connected components. Connecting `c` components into one tree takes `c − 1` edges. Therefore the answer is `n − m − 1`. The graph topology beyond `(n, m)` is irrelevant.

The function is total over the validated bundle. The minimum value the result can take is `−1` if you somehow had `m = n` edges — but `m = n` is impossible in an acyclic graph on `n` nodes (a tree has `n−1`, anything more has a cycle), so under the Rosalind precondition the result is always ≥ 0. We don't enforce this with a separate type because the precondition is documented in the bundle's scaladoc and Rosalind guarantees it.

**Alternative considered:** Run union-find while constructing the bundle, count components, return `components − 1`. Rejected because (a) the math is identical, (b) Rosalind guarantees the input is a forest, and (c) the simpler form is easier to reason about and faster.

### Decision 4: Migrate overlap-graphs into the new subdomain

The four files to relocate (with their tests):
- `bio.domain.analysis.OverlapEdge` → `bio.domain.graph.OverlapEdge`
- `bio.domain.analysis.OverlapLength` → `bio.domain.graph.OverlapLength`
- `bio.domain.analysis.OverlapLengthError` → `bio.domain.graph.OverlapLengthError`
- `bio.algorithms.analysis.OverlapGraph` → `bio.algorithms.graph.OverlapGraph`

The behavior, validation rules, smart-constructor semantics, and scaladoc are all preserved verbatim — only the `package` declaration changes. The matching main spec at `openspec/specs/overlap-graphs/spec.md` is updated via a `## MODIFIED Requirements` block to reflect the new package paths.

`OverlapLength` and `OverlapLengthError` are pulled along with the algorithm because they are *only* consumed by `OverlapGraph` — leaving them in `analysis` would be an awkward residue.

**Alternative considered:** Leave `OverlapGraph` in `analysis` and only put `TreeCompletion` in `graph`. Rejected because it splits graph algorithms across two subdomains — a worse state than either "both in analysis" or "both in graph".

### Decision 5: No shared `Edge` supertype across `OverlapEdge` and `UndirectedEdge`

The two edge types differ in three ways:
- Direction: `OverlapEdge` is directed (`from → to`); `UndirectedEdge` is undirected (unordered pair).
- Node identifier: `OverlapEdge` uses `String` (FASTA record ids); `UndirectedEdge` uses `Int` (1-indexed node labels from the adjacency list).
- Invariants: `OverlapEdge` has none; `UndirectedEdge` has three (`u ≥ 1`, `v ≥ 1`, `u ≠ v`).

No useful operation works across both. A `sealed trait Edge` would be a marker without methods. Skip it.

## Risks / Trade-offs

- **Risk:** Migrating `OverlapGraph` creates a "modified capability" delta — a more complex archive than purely additive changes. → **Mitigation:** the modification is mechanical (package paths only). The main spec's `## MODIFIED Requirements` block lists each requirement with its updated package, scenarios unchanged.
- **Risk:** Internal callers of `OverlapEdge`/`OverlapLength`/`OverlapGraph` (e.g., the `bio.problems.GRPHProb` file the user maintains) will need import updates. → **Mitigation:** confirm by grep before moving; update any in-tree consumers as part of the migration tasks. The user maintains those files; they are pre-existing and we update them carefully without changing their behavior.
- **Risk:** A `private[bio]` accessor exists on `OverlapEdge`/`OverlapLength` that could leak past the move. → **Mitigation:** scoped access stays at `bio` package level so the move within `bio` preserves visibility.
- **Trade-off:** Trusting "input is acyclic" without verification. → **Mitigation:** the Rosalind problem explicitly states the precondition; the documentation calls it out; if a future caller needs verification they can add a separate `Forest` validator capability.
- **Trade-off:** `UndirectedEdge(1, 2)` and `UndirectedEdge(2, 1)` are distinct values even though they represent the same undirected edge mathematically. → **Mitigation:** the algorithm doesn't care (it just counts), and canonicalization (always-`u ≤ v`) would surprise callers who expect input order preserved. If a future graph algorithm needs canonical edges it can wrap with an `UnorderedEdge` type.
