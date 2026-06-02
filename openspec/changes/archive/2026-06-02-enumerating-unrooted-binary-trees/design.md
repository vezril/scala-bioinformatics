## Context

The `bio.{domain,algorithms}.graph` packages host the project's phylogeny work: `NewickTree` (a parsed tree with a `render` method producing canonical Newick), `UnrootedBinaryTreeLeafCount` (CUNR, *counting* unrooted trees), and the Newick distance/parse problems. EUBT ("Enumerating Unrooted Binary Trees") *generates* every unrooted binary tree on `n` taxa and renders each in Newick. The number of such trees is the double factorial `(2n−5)!!`.

The Rosalind output roots each unrooted tree at the first taxon, e.g. for `dog cat mouse elephant`: `(((mouse,cat),elephant))dog;` — a node labelled `dog` with a single child that is the rest of the tree. `NewickTree(Some("dog"), Vector(rest)).render` produces exactly this shape, so the existing renderer is reused.

## Goals / Non-Goals

**Goals:**
- Validated `UnrootedBinaryTreesProblem` wrapping `taxa: Vector[String]` (≥ 3 distinct taxa, with an upper bound) via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage.
- Pure, total `EnumerateUnrootedBinaryTrees.enumerate(problem): UnrootedBinaryTrees` generating all `(2n−5)!!` trees.
- Result type with `format: String` rendering one Newick tree per line.
- Reuse `NewickTree.render` for output; functional implementation (recursion + `flatMap`), no `var`/`while`/mutable collections.

**Non-Goals:**
- De-duplicating Newick representations — Rosalind accepts any valid representation and any tree order; the enumeration produces each distinct topology exactly once.
- Linear-time generation — `(2n−5)!!` trees must be materialised regardless; an upper bound on `n` guards memory.
- A general k-ary tree ADT — internal nodes are strictly binary.

## Decisions

**1. Incremental edge-insertion enumeration, rooted at the first taxon.**
Root every tree at `taxa(0)`. The structure *below* the root is a binary tree whose leaves are the remaining taxa. Start from the base tree on the first three taxa: `rest = Node(Leaf(taxa(1)), Leaf(taxa(2)))` (the unique unrooted tree on 3 leaves, rooted at `taxa(0)`). Then insert each subsequent taxon `taxa(3..n−1)` onto **every edge** of every current tree. Inserting taxon `x` by subdividing the edge above a subtree `S` replaces `S` with `Node(S, Leaf(x))`. A tree whose `rest` has `N` nodes has exactly `N` edges (the root edge plus one above each node of `rest`), so each insertion yields `N` new trees; the running product is `∏_{k=3}^{n−1}(2k−3) = (2n−5)!!`. Verified on the sample (`n = 4 → 3` trees, matching the three distinct sibling-pairings of the non-root taxa).

**2. A private `BinaryTree` ADT for enumeration; `NewickTree` for rendering.**
Enumeration uses a dedicated, strictly-binary ADT — `sealed trait BinaryTree`, `Leaf(name)`, `Node(left, right)` — so the recursion is exhaustive and the binary invariant is structural (no general-arity escape hatch). Insertion is a pure recursion returning, for each node position, a new tree with that subtree wrapped in `Node(_, Leaf(x))`. For output, each tree is converted to `NewickTree(Some(taxa(0)), Vector(toNewick(rest)))` and rendered with the existing `NewickTree.render` — reusing the canonical Newick formatter (single source of truth) and producing the `(rest)root;` shape exactly.

**3. Validation rules and order (first-failure-wins).**
`UnrootedBinaryTreesProblem.from(taxa)` checks, in order: `taxa.size >= 3` (an unrooted binary tree needs ≥ 3 leaves), else `TooFewTaxa(size, 3)`; `taxa.size <= 10`, else `TooManyTaxa(size, 10)`; then the first duplicated name → `DuplicateTaxon(name)`. The upper bound of 10 guards against the factorial blow-up (`(2·10−5)!! = 2 027 025` trees); realistic EUBT inputs are far smaller (the sample is 4).

**4. Total function; recursion depth.**
`enumerate` always succeeds for a valid problem. The insertion recursion depth is bounded by the tree height (≤ ~`2n`), tiny for `n ≤ 10`, so plain recursion (not tail) is safe; the cross-product is built with `flatMap`. All structures immutable.

**5. Naming and placement.**
`UnrootedBinaryTreesProblem`, `UnrootedBinaryTreesProblemError`, and the `UnrootedBinaryTrees` result live in `bio.domain.graph`; the algorithm `EnumerateUnrootedBinaryTrees.enumerate` in `bio.algorithms.graph` (alongside the CUNR counter and Newick types). Result and algorithm names are distinct, so no `=> Result` alias is needed. The private `BinaryTree` ADT lives inside the algorithm object (an implementation detail, like `Tree`/`Edge` in `SuffixTreeConstruction`).

## Risks / Trade-offs

- **[Factorial blow-up]** → `(2n−5)!!` grows fast; the `MaxTaxa = 10` bound caps materialised output at ~2M trees. Realistic Rosalind EUBT datasets are small.
- **[Output differs from the sample strings]** → Rosalind accepts any valid Newick representation and any order; the enumeration yields each distinct unrooted topology once. The sample's three trees and the algorithm's three trees are the same set of topologies (verified by the {sibling-pair} partition).
- **[n = 3 minimal case]** → one tree, `((taxa1,taxa2))taxa0;`; covered by a scenario.
- **[Duplicate / too-few / too-many taxa]** → rejected by validation; covered by scenarios.
