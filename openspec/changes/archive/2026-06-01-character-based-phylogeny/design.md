## Context

Rosalind **CHBP** gives a list of `n ≤ 80` taxa and an `n`-column 0/1 character
table `C` (one character per row; column `j` is taxon `j`) and asks for an
unrooted binary tree in Newick format that *models* `C`. A character row is a
split `S | Sᶜ` of the taxa (the `1`-side vs the `0`-side). The table is
**consistent** when no two characters conflict — two splits `S₁|S₁ᶜ` and
`S₂|S₂ᶜ` conflict iff all four intersections `S₁∩S₂`, `S₁∩S₂ᶜ`, `S₁ᶜ∩S₂`,
`S₁ᶜ∩S₂ᶜ` are non-empty. CHBP is the inverse of CSTR
(`bio.algorithms.graph.CharacterTable.compute`, which derives the table from a
tree), so it can reuse the framework's existing split conventions and the
`NewickTree` type.

Current relevant state:
- `NewickTree(label, children)` models a parsed tree; it has `labels` but no
  rendering-to-Newick method.
- `Split` canonicalises a bipartition against reference index `0`.
- `CharacterTable` already encodes/decodes splits with the "reference taxon's
  side" convention — the same orientation trick CHBP needs.
- Runner/parse conventions are established (see `SETOProb`, `QRTDProb`).

## Goals / Non-Goals

**Goals:**
- A validated `CharacterBasedPhylogenyProblem` (taxa + 0/1 rows) via a smart
  constructor returning `Either`, following the `sealed abstract case class`
  pattern (no leaking `apply`/`copy`).
- Detect an inconsistent table (conflicting characters) at construction and
  report the first conflicting pair, so the algorithm is total over accepted
  input.
- A pure, total `CharacterBasedPhylogeny.build(problem): NewickTree` that
  reproduces the canonical Rosalind sample (up to unrooted-tree equivalence).
- Canonical Newick rendering via an additive `NewickTree.render`.
- An IO runner reading `chbp_data.txt` and printing the Newick string.

**Non-Goals:**
- No branch lengths or internal-node labels in output (Rosalind grades the
  unrooted topology / split set, not labels or order).
- No artificial resolution of polytomies. The Rosalind inputs are fully
  resolving consistent tables; the induced tree is binary for them. If a table
  under-determines the tree, we emit the (possibly multifurcating) tree the
  splits induce rather than inventing extra edges.
- Not changing CSTR or any existing split/tree code beyond the additive
  `render` method.

## Decisions

### 1. Reference-oriented splits → laminar cluster family

Pick taxon index `0` as a fixed reference. Orient every non-trivial character
split so the reference is **excluded** from the retained side `S` (replace `S`
with `Sᶜ` if it contains the reference). For a *consistent* table, all the
reference-oriented `S` sets form a **laminar family**: any two are nested or
disjoint. A laminar family of subsets of the taxa is exactly the set of
internal clusters of a tree rooted "at the reference edge", so the tree can be
read straight off the nesting.

This mirrors the orientation `Split.of` and `CharacterTable.encode` already use,
keeping the whole graph package consistent. *Alternative considered:* iterative
split-insertion / star-decomposition (refine a star tree one split at a time).
Rejected as more stateful and harder to keep purely functional; the laminar
construction is a direct, side-effect-free fold.

### 2. Which splits to keep

After orienting away from the reference, keep a split only if **both sides have
≥ 2 taxa** (`2 ≤ |S| ≤ n−2`). Singleton sides correspond to leaf edges, already
represented by the leaves themselves; all-equal rows are trivial splits. Keeping
only proper internal clusters keeps the family clean and dedup-friendly.

### 3. Tree assembly from the cluster family

Build the cluster set `= { kept oriented S sets } ∪ { {t} : t ∈ taxa } ∪ { U }`
where `U` is the full taxa set (the root). For each cluster `c ≠ U`, its parent
is the **smallest cluster strictly containing it**; children of a node are the
clusters whose parent is that node. Recurse from `U`: a singleton `{t}` renders
as a leaf `NewickTree(Some(t), Vector.empty)`; any larger cluster renders as an
unlabelled internal node over its children. Children are ordered by ascending
smallest-member index for determinism (output order is irrelevant to grading but
determinism aids testing). This is `O(k²)` in the number of clusters (`k ≤ n +
#characters`), trivially fast at `n ≤ 80`.

### 4. Consistency validated in the smart constructor

The constructor checks each pair of non-trivial character splits for the
four-intersection conflict and fails with `ConflictingCharacters(i, j)` for the
first conflicting pair. This makes `build` total. *Alternative considered:*
trusting the "consistent" guarantee and skipping the check. Rejected — the
framework's contract is fail-fast validation at the boundary; an `Either` that
can surface inconsistency is strictly safer and gives a clean edge-case test.

### 5. Result type = `NewickTree` + additive `render`

`build` returns a `NewickTree` rather than a bespoke result type — the tree *is*
the answer, and reusing it avoids a redundant wrapper. Newick text comes from a
new `render: String` on `NewickTree` (`leaf → label`, `internal →
"(" + children.render-joined-by-"," + ")"`, top level appends `;`). Additive and
reusable by other tree-producing problems. *Alternative considered:* a
`CharacterBasedPhylogenyResult` wrapper with `format`. Rejected as redundant
indirection around a type that already models the answer.

## Risks / Trade-offs

- [A consistent but under-resolving table yields a multifurcating tree, not a
  strictly *binary* one] → Out of scope per Non-Goals; Rosalind's CHBP inputs
  fully resolve. The induced tree still models `C`. Documented as a known limit.
- [Reference-orientation correctness depends on the table truly being
  consistent] → The constructor's conflict check guarantees laminarity before
  `build` runs, so the family is always nested/disjoint when `build` executes.
- [`render` added to a widely-referenced type] → Purely additive method; no
  existing call site changes, existing `NewickTree` tests stay green. Verified
  by the full suite during apply.
- [Ambiguous output ordering vs. the sample] → Grading is on unrooted topology
  (split set), so any consistent ordering is accepted; tests assert the split
  set / topological equivalence, not byte-equality with the sample.
