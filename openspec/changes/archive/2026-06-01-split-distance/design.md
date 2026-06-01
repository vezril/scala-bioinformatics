## Context

Rosalind SPTD compares two unrooted binary trees `T1`, `T2` on the same `n` taxa
(up to 3,000) and returns their split distance
`d_split = 2(n − 3) − 2·s`, where `s` is the count of nontrivial splits shared by
both trees.

A *split* is the partition of the taxa into two sides induced by removing one
edge of the tree. Removing a **leaf edge** isolates a single taxon (trivial);
removing an **internal edge** yields a *nontrivial* split with ≥ 2 taxa on each
side. An unrooted binary tree on `n` taxa has exactly `n − 3` internal edges, so
each tree has `n − 3` nontrivial splits; that is why the maximum distance is
`2(n − 3)`.

The framework already provides `bio.parsing.NewickParser`
(`parse: String ⇒ Either[NewickParseError, NewickTree]`) and the recursive
`bio.domain.graph.NewickTree` (with a `labels: Set[String]` accessor). These are
reused unchanged. The established conventions are: validated input bundles as
`sealed abstract case class` with first-failure-wins smart constructors returning
`Either`, canonicalised output/value ADTs (cf. `Quartet`), pure algorithms in
`bio.algorithms.*`, and `IO`-based runners reading from a resource file (cf.
`QRTProb`, `CSTRProb`).

## Goals / Non-Goals

**Goals:**
- Model a canonicalised `Split` ADT so equal splits compare equal and shared
  splits are counted by ordinary `Set` intersection.
- Validate the two-tree input (non-empty distinct taxa; each tree's leaf labels
  equal the taxa set) via a first-failure-wins smart constructor.
- Compute the split distance with a pure, functional traversal — efficient enough
  for `n ≤ 3,000`.
- Reproduce the canonical sample output (`2`) exactly.

**Non-Goals:**
- Newick parsing itself (reuse `NewickParser`); the domain bundle takes
  already-parsed `NewickTree`s, so parse failures are handled in the runner.
- Validating that a tree is actually unrooted-binary (Rosalind guarantees valid
  trees); the formula assumes `n − 3` nontrivial splits per tree.
- Branch lengths, internal-node labels, or rooted-tree semantics.

## Decisions

### 1. Represent a split as a canonicalised `Split` over taxon **indices**

The taxa are indexed `0 .. n−1`. Each split is the pair `(A, complement)` of
taxon-index sets. To make `{A | B}` equal to `{B | A}`, `Split` stores a single
canonical `side: BitSet` — the side that does **not** contain the reference index
`0`. Because index `0` lies in exactly one side of any split, this orients every
split uniquely; and because both trees share the same indexing, canonical
`BitSet`s from different trees compare equal iff they denote the same split.

- Smart constructor `Split.of(sideA: BitSet, sideB: BitSet): Split` picks the side
  lacking index `0` as the canonical `side`. Construction is via
  `new Split(...) {}`; synthesized `apply`/`copy` are not public
  (`sealed abstract case class`), matching the `Quartet` precedent.
- **Alternative considered:** `Set[String]` per side — simpler but heavier for
  `n = 3,000` and slower to intersect; `BitSet` is compact and fast.
- **Alternative considered:** canonicalise by "lexicographically smaller side" —
  works but requires comparing two sets; the "side without index 0" rule is O(1)
  to decide and equally total.

### 2. `SplitDistanceProblem` validates parsed trees, not raw strings

Following `NewickDistanceProblem`, the bundle is
`sealed abstract case class SplitDistanceProblem(taxa: Vector[String], tree1: NewickTree, tree2: NewickTree)`
constructed via
`from(taxa, tree1, tree2): Either[SplitDistanceProblemError, SplitDistanceProblem]`.
First-failure-wins order:

1. `EmptyTaxa` — taxa is empty.
2. `DuplicateTaxon(name)` — first repeated taxon name.
3. `TreeTaxaMismatch(1, missing, extra)` — `tree1`'s leaf labels ≠ taxa set
   (`missing` = taxa absent as leaves, `extra` = leaf labels not in taxa).
4. `TreeTaxaMismatch(2, missing, extra)` — same check for `tree2`.

`treeIndex` is **1-based** (`T1`/`T2`). Leaf labels are gathered by a small
recursive helper (a node with empty `children` whose `label` is defined); this
avoids counting unlabelled internal nodes and needs no change to `NewickTree`.

- **Alternative considered:** parse inside `from` and add a `TreeParseError`
  case. Rejected to keep parsing (and its existing `NewickParseError` ADT) at the
  edge, consistent with `NewickDistanceProblem`.

### 3. Split extraction by a single functional traversal

`SplitDistance.compute(problem): Int`:

- Build `index: Map[String, Int]` from `taxa.zipWithIndex` and
  `universe: BitSet = BitSet(0 until n)`.
- For each tree, a recursive `go(node): (BitSet, Set[Split])` returns the leaf-index
  set beneath `node` and the splits collected so far. A leaf contributes its
  singleton index set and no split; an internal node unions its children's sets
  and, when its leaf set is **nontrivial** (`2 ≤ |A| ≤ n − 2`), adds
  `Split.of(A, universe diff A)`. The root's full set is trivial and excluded.
- `s = splits(T1) ∩ splits(T2)` size; return `2 * (n − 3) − 2 * s`.

Using a `Set[Split]` per tree both deduplicates and gives the intersection
directly. Complexity is `O(n²)` worst case for the union/diff work (bitset ops
over `n` bits at up to `n` nodes) — well within limits for `n ≤ 3,000`.

## Risks / Trade-offs

- **Malformed tree (leaf label repeated but still within taxa set)** → not caught
  by the set-equality check. Mitigation: out of scope — Rosalind guarantees valid
  trees on the given taxa; the equality check covers missing/extra labels, which
  are the realistic failure modes.
- **Trees that are not strictly unrooted-binary** → the formula `2(n − 3)` could
  mismatch the actual split counts. Mitigation: documented Non-Goal; the
  algorithm still returns `2(n − 3) − 2·s` faithfully for whatever splits exist.
- **`n < 3`** makes `2(n − 3)` negative and there are no nontrivial splits.
  Mitigation: mathematically the formula yields the (possibly negative/zero)
  value with `s = 0`; the smart constructor still accepts any non-empty distinct
  taxa, leaving the degenerate-`n` interpretation to the caller (Rosalind inputs
  have `n ≥ 4`).
