## Context

Rosalind problem 29 (INOD) asks: given a positive integer `n` with `3 <= n <= 10000`, return the number of internal nodes of any unrooted binary tree with `n` leaves. By definition (and a classic phylogenetics result), every unrooted binary tree has exactly two more leaves than internal nodes, so the answer is simply `n - 2`.

The framework already hosts a phylogenetics-adjacent subdomain at `bio.{algorithms,domain}.graph` (the home of `tree-completion`, spec 20). The new feature slots cleanly there. The work is purely additive: a validated input wrapper plus a tiny algorithm object with a single closed-form method.

## Goals / Non-Goals

**Goals:**
- Provide a validated `UnrootedBinaryTreeLeafCount` ADT (smart-constructor returning `Either`, `sealed abstract case class`) enforcing the `[3, 10000]` Rosalind range.
- Provide `bio.algorithms.graph.PhylogeneticAncestors.internalNodes` returning the exact internal-node count.
- Cover the canonical Rosalind sample (`n = 4 → 2`), both boundaries (`n = 3 → 1`, `n = 10000 → 9998`), and the two out-of-range validation failures (`n = 2`, `n = 10001`) with ScalaTest (Red-Green-Refactor).

**Non-Goals:**
- Tree construction or enumeration. The problem is purely a counting identity — no graph data structure is needed.
- Generalisation to rooted binary trees, non-binary phylogenies, or multifurcating internal nodes.
- I/O helpers. Rosalind input for INOD is one integer; the existing `bio.problems` skeleton (or whatever the user adds later) can read it inline.

## Decisions

**1. Use the closed-form `n - 2`, not a tree-walk.**

For any unrooted binary tree, every internal node has degree exactly 3 and every leaf has degree exactly 1. Sum of degrees equals `2 * (edges)`. With `L` leaves and `I` internal nodes: `3I + L = 2(I + L - 1)` (an `n`-node tree has `n - 1` edges) ⇒ `I = L - 2`. There is no reason to construct a tree, walk it, or simulate anything — the identity is exact. **Alternative considered:** building a sample tree and counting internal nodes (rejected: pointless overhead for a closed-form formula and would invite questions about which tree was constructed when the spec says "any" unrooted binary tree).

**2. Place the algorithm under `bio.algorithms.graph`.**

This is where `tree-completion` already lives, and the problem is phylogenetics-flavoured. **Alternative considered:** `bio.algorithms.combinatorics` (rejected: the answer counts a graph-theoretic quantity, not a combinatorial enumeration like `Combinations`).

**3. Place the validated input bundle under `bio.domain.graph`.**

Mirrors the conventions established by the rest of the codebase (`bio.domain.<subdomain>.<Problem>` paired with `<Problem>Error`). **Alternative considered:** inline `Int` parameter (rejected: violates the project-wide "validated ADTs for all domain inputs" rule).

**4. Return type is `Int`, not a wrapped count.**

The result is a plain integer count, not a probability or a constrained domain value (and `n - 2` for `n <= 10000` fits comfortably in `Int`). Mirrors `tree-completion`'s `Int` return for "minimum edges to add". **Alternative considered:** a `NonNegativeInt` wrapper (rejected: the framework doesn't have one, and introducing it for a single use site is premature).

**5. Validation order: lower-bound (`n < 3`) → upper-bound (`n > 10000`).**

Consistent with the validation-order pattern used in `WrightFisherFixationProblem` and elsewhere (lower before upper, first failure wins, deterministic for tests).

## Risks / Trade-offs

- **[Risk]** Someone might assume the formula generalises to rooted binary trees → **Mitigation:** the type is named `UnrootedBinaryTreeLeafCount` and the algorithm object is `PhylogeneticAncestors` with Scaladoc citing the unrooted-binary identity explicitly.
- **[Risk]** `n - 2` is so small that someone might want to skip the validated wrapper → **Mitigation:** the wrapper is the project convention; skipping it would create the only un-wrapped algorithm input in the framework. The 5-line ADT is cheap insurance.
- **[Trade-off]** Closed-form formula vs. educational tree-walk — chose closed-form for correctness and simplicity. Anyone wanting the tree-walk derivation can read the Scaladoc.
