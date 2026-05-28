## Context

Rosalind problem 32 (CSTR) asks: given an unrooted binary tree `T` in Newick format with `n ≤ 200` labelled leaves (taxa), return its *character table*. Each internal edge of `T`, when removed, splits the leaves into two non-empty disjoint subsets `S | S^c` — a *split*. The character table has one row per *nontrivial* split (where both sides have size `≥ 2`), with the `n` columns indexed by the lex-sorted taxa. Each row is a `0`/`1` string indicating which side of the split each taxon belongs to.

The sample input `(dog,((elephant,mouse),robot),cat);` has 5 leaves and 2 nontrivial internal edges (the edge above `((elephant,mouse),robot)` and the edge above `(elephant,mouse)`), yielding the 2-row output `00110\n00111`.

The framework already hosts `bio.{algorithms,domain}.graph` (overlap graphs, tree completion, phylogenetic ancestors, tree distances) and `bio.parsing.NewickParser` — the new feature slots in cleanly, reusing the parser and the `NewickTree` ADT exactly as-is.

## Goals / Non-Goals

**Goals:**
- Provide a validated `CharacterTableProblem` ADT wrapping a `NewickTree`, enforcing `leafCount ≤ 200` (the Rosalind cap) and exposing a pre-computed lexicographically-sorted `leafLabels: Vector[String]` so downstream code doesn't repeat the walk.
- Provide `bio.algorithms.graph.CharacterTable.compute` returning a `Vector[String]` of binary rows, one per nontrivial split, deterministically ordered.
- TDD coverage at both layers, including the canonical Rosalind sample and four structural edge cases (flat tree, balanced quartet, deeper nesting, single leaf).

**Non-Goals:**
- Generating the *trivial* splits (those isolating a single taxon). The spec explicitly excludes them.
- Newick parsing or tree-edge enumeration outside the existing API. Reuses `NewickParser.parse` and walks `NewickTree.children` directly.
- File ingestion. The Rosalind input is a single Newick string; if/when a `bio.problems.CSTRProb` runner is added, it can read a file with `IO.blocking` and feed the string to `NewickParser.parse`.
- Branch lengths, quoted labels, or any extended-Newick features — same scope as `NWCK`.

## Decisions

**1. Enumerate splits by walking non-root internal nodes.**

In a tree, removing any edge produces two components — i.e., one split. If we pick the Newick root, every non-root node has exactly one "edge up to its parent"; iterating over all non-root nodes therefore visits every edge exactly once with no double-counting. For each such edge, the split is `subtreeLeaves(node) | rest`. A leaf-edge (where `node` is a leaf) trivially produces a single-element side and is filtered out; an internal-edge produces a multi-element side and is kept iff both sides have size `≥ 2`. **Alternative considered:** enumerating splits over an explicit adjacency map (rejected: requires building the same undirected graph used by `NewickDistance`, then doing a connected-components walk after each edge removal — wasteful when the rooted Newick view already gives subtree-leaves directly).

**2. Deterministic row encoding: "lex-first-taxon side gets `0`s."**

For each split `S | S^c`, we need a rule that picks which side becomes the `1`s. The spec says "the particular subset of taxa to which 1s are assigned is arbitrary", but for tests to be deterministic we need *some* rule. We pick: **the side containing the lexicographically-smallest taxon gets `0`s; the other side gets `1`s.** This makes each row uniquely determined by its split, matches the Rosalind sample (where `cat` is first lex and is always on the `0`-side), and side-steps the spec's noted ambiguity. **Alternative considered:** smaller side gets `1`s (rejected: tied sizes are common — `((a,b),(c,d))` has a single split with equal sides — and the tie-breaker would still need to be lex-based, so we're just complicating the rule).

**3. Output row order: lexicographic.**

Spec allows any order, but tests need determinism. Sorting rows lexicographically is the simplest possible choice. **Alternative considered:** DFS / BFS ordering on the tree (rejected: brittle to refactors of the walk and harder to inspect by hand).

**4. Pre-compute `leafLabels` inside `CharacterTableProblem`.**

The leaf set is needed three times: for the validation upper bound, for the column order, and for the trivial-split filter (`subtree.size == 1` or `n - subtree.size == 1`). Computing it once in the smart constructor (and exposing it as a public field) avoids re-walking the tree and prevents the column-order code and the split-filter code from disagreeing about what counts as a leaf. Leaves are identified structurally: `children.isEmpty && label.isDefined` (unlabelled leaves are skipped — see "Risks").

**5. Place under `bio.{algorithms,domain}.graph`.**

Matches `tree-completion`, `phylogenetic-ancestors`, `tree-distances`. Same phylogenetics family.

## Risks / Trade-offs

- **[Risk]** An unlabelled leaf (rare but representable in Newick — e.g. `(,a)b;` has an unlabelled leaf) is silently skipped from the taxa list. → **Mitigation:** Scaladoc on `CharacterTableProblem.leafLabels` notes the convention. The Rosalind input never has unlabelled leaves, so this is purely a robustness consideration.
- **[Risk]** Duplicate labels in the tree would produce a smaller-than-expected taxa set and an incorrect column count. → **Mitigation:** the smart constructor's `leafLabels` collection uses a `Set` toggle for the lex-sorted output, and the rest of the algorithm keys off `leafLabels.distinct`. We do not currently error on duplicates (Rosalind never produces them), but the algorithm degrades gracefully — output columns reflect the *unique* labels, with all duplicates collapsed to a single column. A `DuplicateLabel` error case could be added if a real input demands it.
- **[Trade-off]** Walking the tree twice (once for `leafLabels` in the constructor, once for splits in the algorithm) over caching a parent-pointer map. At `n ≤ 200` the constant factor is negligible — the readability of two simple recursive walks beats a richer cached representation. Anyone needing a faster CSTR-on-huge-trees later can refactor.
- **[Trade-off]** Choosing lex-first-side-as-`0` over presenting both sides as separate rows. Rosalind explicitly notes the ambiguity; the convention picks one canonical form so that test assertions are checkable string-equals. If the user prefers the other convention, they invert each row in their problem runner.
