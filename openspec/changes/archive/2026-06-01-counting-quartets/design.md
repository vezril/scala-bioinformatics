## Context

Rosalind CNTQ: given a positive integer `n` (`4 ≤ n ≤ 5000`) and an unrooted
binary tree `T` on `n` taxa in Newick format, return `q(T) mod 1,000,000`, where
`q(T)` is the number of quartets `AB|CD` consistent with `T`.

A quartet is consistent with `T` when it can be inferred from one of `T`'s splits.
Since `T` is a fully resolved unrooted binary tree, **every** 4-element subset of
the `n` taxa is resolved by `T` into exactly one of its three possible quartet
topologies. Therefore `q(T)` counts the 4-subsets:
`q(T) = C(n, 4) = n(n−1)(n−2)(n−3) / 24`. The result is **topology-independent** —
it depends only on `n`. For the sample `n = 6`, `C(6, 4) = 15`, matching the
expected output.

The framework already provides `bio.parsing.NewickParser`
(`parse: String ⇒ Either[NewickParseError, NewickTree]`) and the recursive
`bio.domain.graph.NewickTree`. The established conventions are: validated input
bundles as `sealed abstract case class` with first-failure-wins smart constructors
returning `Either` (cf. `UnrootedBinaryTreeLeafCount`, `SplitDistanceProblem`),
pure algorithms in `bio.algorithms.*`, and `IO` runners reading from a resource
file (cf. `SPTDProb`, `CUNRProb`).

## Goals / Non-Goals

**Goals:**
- Validate the input `(n, tree)`: `n` within `[4, 5000]`, and the parsed tree's
  leaf count equal to `n` (a consistency check between the declared count and the
  actual tree).
- Compute `C(n, 4) mod 1,000,000` with overflow-safe arithmetic for `n ≤ 5000`.
- Reproduce the canonical sample output (`15`) exactly.

**Non-Goals:**
- Newick parsing itself (reuse `NewickParser`); the domain bundle takes an
  already-parsed `NewickTree`, so parse failures are handled in the runner.
- Verifying that `T` is actually a *binary* (fully resolved) tree; Rosalind
  guarantees this, and the closed form assumes it.
- Enumerating quartets individually (cf. the `quartets`/QRT capability) — CNTQ
  only needs the count, which the closed form gives in O(1).

## Decisions

### 1. Bundle `(n, tree)` in a validated `CountingQuartetsProblem`

Although the count needs only `n`, the input genuinely contains a tree, and
validating that the tree has `n` leaves makes the bundle meaningful and catches
malformed input. Following `SplitDistanceProblem`, the bundle is
`sealed abstract case class CountingQuartetsProblem(n: Int, tree: NewickTree)`
constructed via
`from(n: Int, tree: NewickTree): Either[CountingQuartetsProblemError, CountingQuartetsProblem]`.
First-failure-wins order:

1. `BelowMinimum(n, 4)` — `n < 4`.
2. `ExceedsMaximum(n, 5000)` — `n > 5000`.
3. `LeafCountMismatch(declared, actual)` — the tree's leaf count ≠ `n`.

Leaf count is the number of leaf nodes (nodes with empty `children`), computed by
a small recursive helper — no change to `NewickTree`.

- **Alternative considered:** validate only `n` (a bare `LeafCount` type, ignoring
  the tree). Rejected — it discards the tree the problem provides and skips a cheap
  consistency check; bundling matches the `SplitDistanceProblem` precedent.
- **Alternative considered:** parse inside `from` and add a parse-error case.
  Rejected to keep parsing (and its `NewickParseError` ADT) at the edge.

### 2. Closed-form count with overflow-safe `Long` arithmetic

`CountingQuartets.count(problem): Int` computes
`n·(n−1)·(n−2)·(n−3) / 24 mod 1,000,000`.

- The product of four consecutive integers is always divisible by 24, so the
  integer division is exact — divide **before** taking the modulus.
- For `n ≤ 5000` the product is at most `5000⁴ = 6.25×10¹⁴`, which fits in a
  `Long` (max `≈ 9.2×10¹⁸`); compute the product in `Long`, divide by 24, then
  `% 1000000`, returning the `Int` result (always in `[0, 999999]`).

- **Alternative considered:** per-factor modular arithmetic (as in CUNR's double
  factorial). Rejected — division by 24 is not directly modular-safe unless done
  before the mod, and the direct `Long` product is exact and simpler here.

## Risks / Trade-offs

- **Non-binary / unresolved tree** → the closed form `C(n, 4)` would overcount
  versus the true consistent-quartet count. Mitigation: documented Non-Goal;
  Rosalind guarantees a fully resolved binary tree.
- **Leaf-count check assumes distinct taxa** → a tree with a repeated leaf label
  still counts each leaf node, so `LeafCountMismatch` is by structural leaf count,
  not distinct labels. Mitigation: acceptable — Rosalind trees have distinct taxa;
  the check's purpose is catching a declared-`n` vs. tree-size mismatch.
- **Overflow if bounds were raised** → arithmetic is safe only because `n ≤ 5000`.
  Mitigation: the smart constructor enforces the upper bound before `count` runs.
