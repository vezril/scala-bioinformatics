## Context

Rosalind RSUB (spec 45) sits one step downstream of ALPH (spec 44):
- *ALPH* assigned DNA strings to internal nodes by minimising per-edge Hamming distance.
- *RSUB* takes a tree where *all* node strings (leaves and internals) are already given — typically the output of a parsimony algorithm — and asks "which positions exhibit a reverting substitution along some root-down path?"

**Definition (paraphrased from the spec).** A *reversing substitution* at position `i` is two parent-child edges `(s, t)` and `(v, w)` along with `i`, satisfying:

1. There is a path in `T` from `s` down to `w` (so `s` is an ancestor of `w`; the edge `(s, t)` precedes `(v, w)` on that path).
2. `s[i] = w[i] ≠ v[i] = t[i]` — the original symbol at `s[i]` matches the reverted symbol at `w[i]`, and the substituted symbol at `t[i]` matches the parent of the reversion `v[i]`.
3. For every node `u` on the path from `t` to `v` (both endpoints included), `t[i] = u[i]` — the substituted value is preserved along the entire intermediate sub-path, with no other substitution at position `i` in between.

Canonical sample (verified by hand):
```
Tree: (((ostrich,cat)rat,mouse)dog,elephant)robot;
       robot=AATTG, dog=GGGCA, mouse=AAGAC, rat=GTTGT,
       cat=GAGGC, ostrich=GTGTC, elephant=AATTC

Expected output (any order):
  dog mouse 1 A->G->A     (robot->dog at pos 1 is A->G; dog->mouse reverts to A)
  dog mouse 2 A->G->A     (same pair, position 2)
  rat ostrich 3 G->T->G   (dog->rat at pos 3 is G->T; rat->ostrich reverts to G)
  rat cat 3 G->T->G       (same first edge, dog->rat; rat->cat reverts to G)
  dog rat 3 T->G->T       (robot->dog at pos 3 is T->G; dog->rat reverts to T)
```

Notice the *nested* nature at position 3: the chain `robot[2]=T → dog[2]=G → rat[2]=T` is one reversion (T→G→T), AND simultaneously the sub-chain `dog[2]=G → rat[2]=T → cat[2]=G` is *another* (G→T→G), and also `dog[2]=G → rat[2]=T → ostrich[2]=G`. All three are independent reversions and all must be reported.

## Goals / Non-Goals

**Goals:**
- Validated `ReversingSubstitutionsProblem(tree, alignment)` smart constructor with full pre-flight checks.
- Output record `ReversingSubstitution(firstChangeSpecies, reversionSpecies, position, originalSymbol, substitutedSymbol, revertedSymbol)`.
- Algorithm `ReversingSubstitutions.findAll(problem): Vector[ReversingSubstitution]` enumerating every reversion in the tree.
- Position numbering is **1-indexed** (matches Rosalind's output).
- Output ordering is deterministic but unspecified by Rosalind — we use *position-ascending, then tree-pre-order on the first edge*. Tests verify membership rather than order when ordering would be brittle.
- File-based input via `RSUBProb`, mirroring the ALPHProb pattern (inline FASTA parser allowing only `{A, C, G, T}` here).
- TDD: Red → Green → Refactor with `sbt test` green at the end.

**Non-Goals:**
- No support for "gap" symbol `-` (RSUB inputs are pure DNA).
- No support for unrooted trees, polytomies (non-binary internal nodes), or unlabeled internal nodes — all rejected by validation.
- No automatic inference of internal-node sequences (that's ALPH's job — RSUB takes them as given).
- No "find shortest" or "filter by minimum-path-length" variants. Just enumerate every reversion.
- The algorithm is `O(L · n · n)` in the worst case (per position, per edge, full subtree DFS). With `L ≤ 400` and `n ≤ 100`, that's `4M` ops — trivially fast. No optimisation needed.

## Decisions

**1. Per-position, per-edge DFS algorithm.**
- Iterate `i ∈ [0, L)`.
- Iterate every directed edge `(s, t)` of the tree (parent-to-child).
- If `s[i] == t[i]`: skip (no first substitution).
- Else: let `X = s[i]`, `Y = t[i]`. DFS from `t` into its descendants. At each descendant `u` already known to satisfy `u[i] == Y`, examine each child `c`:
  - if `c[i] == Y`: continue DFS into `c`.
  - if `c[i] == X`: emit `ReversingSubstitution(t.label, c.label, i+1, X, Y, X)`. **Do not recurse into `c`** — the reversion ends here; a "reverted" symbol at `c` cannot be on the "intermediate Y-preserving path" of any further reversion seeded at `(s, t)`.
  - else (`c[i]` is neither `X` nor `Y`): stop this branch — the path no longer carries `Y`, so condition 3 fails.
- Rationale: this is a direct translation of the spec's definition. The DFS naturally handles nested reversions because *every* edge is iterated as a candidate first-substitution edge.

**2. Output record exposes all six fields.**
- `firstChangeSpecies: String` — `t.label`.
- `reversionSpecies: String` — `w.label` (the descendant where the reversion completes).
- `position: Int` — 1-indexed.
- `originalSymbol: Char` — `s[i]`.
- `substitutedSymbol: Char` — `t[i]`.
- `revertedSymbol: Char` — `w[i]` (always equal to `originalSymbol` by construction, but kept explicit so the output formatter matches the Rosalind `X->Y->X` text exactly).
- Plain `final case class` — no smart constructor. The algorithm guarantees `originalSymbol == revertedSymbol`.

**3. Validation in priority order.**
1. `EmptyAlignment` — `alignment.isEmpty`.
2. `LengthMismatch(rowIndex, length, expectedLength)` — first row whose length differs from row 0's length.
3. `SequenceTooLong(rowIndex, length, 400)` — first row exceeding the cap.
4. `InvalidCharacter(rowIndex, position, ch)` — first non-`{A, C, G, T}` character (row-major scan; *no* gap support, unlike ALPH).
5. `TooManyStrings(actual, 100)` — more than 100 alignment rows.
6. `InternalNodeMissingLabel` — at least one internal node has `label == None`.
7. `LeafMissingLabel` — at least one leaf has `label == None` (Newick allows unlabeled leaves; RSUB does not).
8. `NonBinaryInternalNode(label, degree)` — first internal node with `children.size != 2`.
9. `NodeLabelMismatch(treeOnly, alignmentOnly)` — set of *all* node labels in the tree (internal + leaf) must equal the alignment's row-label set. Each side is the symmetric-difference set.

**4. Reuse `NamedSequence` from the ALPH change.**
- Rationale: identical shape, same semantics. Already a plain `final case class` with `label: String` and `sequence: String`.

**5. Place under `bio.{domain,algorithms}.analysis`.**
- Rationale: matches ALPH (`bio.algorithms.analysis.AlignmentBasedPhylogeny`) and the broader comparative-analysis grouping.

**6. Runner reads from `src/main/scala/resources/rsub_data.txt`.**
- Format identical to `alph_data.txt`: line 1 = Newick tree, then FASTA-style records.
- Inline parser allows only `{A, C, G, T}` characters in sequences (no `-`).
- Output: one line per reversion as `"firstChangeSpecies reversionSpecies position O->S->R"`.

## Risks / Trade-offs

- **Output ordering is implementation-defined; Rosalind permits any order but our tests use a deterministic ordering for predictability.** → For the canonical sample, we assert that the result *set* contains exactly the five expected reversions; we don't pin the order.
- **The DFS could in principle revisit nodes if the tree has cycles, but `NewickTree` is by construction acyclic.** → No mitigation needed.
- **Algorithm is O(L · n²) worst case.** → Fine at the cap (`L ≤ 400`, `n ≤ 100` ⇒ `4M` ops).
- **Reverted symbol is structurally equal to original — keeping both fields is mildly redundant.** → Justified for output-formatter clarity; the explicit storage costs nothing.
