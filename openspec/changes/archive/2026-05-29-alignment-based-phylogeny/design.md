## Context

Rosalind spec 44 (ALPH) is the *small-parsimony* problem with gap-as-symbol:

- *Given* a rooted binary tree `T` (every internal node and every leaf labeled) and a multiple alignment of the leaf species' sequences (equal-length DNA strings over `{A, C, G, T, -}`),
- *Find* DNA strings for the internal nodes that minimise `d_H(T) = Σ_{edges (u, v)} d_H(s_u, s_v)`, where `d_H` is the per-column Hamming distance (counting gap-vs-non-gap as a mismatch, gap-vs-gap as a match).

The classical solution is the **Sankoff algorithm**, a per-column dynamic program on the tree. For each column independently:

1. **Bottom-up pass.** At each leaf, the cost vector indexed by symbol `c ∈ {A, C, G, T, -}` is `0` if `c == leafChar`, else `+∞`. At each internal node `u` with children `L` and `R`, the cost for symbol `c` is `min_{c_L} (cost_L(c_L) + δ(c, c_L)) + min_{c_R} (cost_R(c_R) + δ(c, c_R))`, where `δ(a, b) = 0` if `a == b` else `1`.
2. **Top-down traceback.** At the root, pick `c` minimising `cost_root(c)`. At each child, pick the symbol `c'` that achieved the minimum in step 1, given the parent's chosen `c`.

Sum the per-column root-min costs to get the total `d_H(T)`. Concatenate the per-column traceback choices at each internal node to recover that node's full DNA string.

Canonical sample (hand-verified):
```
Tree: (((ostrich,cat)rat,(duck,fly)mouse)dog,(elephant,pikachu)hamster)robot;
Leaves: ostrich=AC, cat=CA, duck=T-, fly=GC, elephant=-T, pikachu=AA
One optimum internal assignment: rat=AC, mouse=TC, dog=AC, hamster=AT, robot=AC
Per-edge Hamming costs: 0+2+0+1+1+1+0+1+1+1 = 8 ✓
```

## Goals / Non-Goals

**Goals:**
- Validated `AlignmentBasedPhylogenyProblem(tree, alignment)` smart constructor with comprehensive validation (caps, label coverage, alphabet, length agreement).
- `NamedSequence(label, sequence)` reusable input/output record.
- Algorithm `AlignmentBasedPhylogeny.solve(problem)` returning `AlignmentBasedPhylogeny(totalDistance, internalAssignments)` via per-column Sankoff DP + traceback over the 5-symbol alphabet.
- Internal-node labels in the output are in *tree-traversal* order (post-order during the DP fill, but returned as a fixed traversal so tests can pin them).
- Empty-tree edge case ruled out by the validation (Rosalind constrains `n ≥ 2`, and our smart constructor will require *at least 1 internal node* — i.e., at least 2 leaves).
- TDD: Red → Green → Refactor with `sbt test` green at the end.

**Non-Goals:**
- No general N-ary tree support — Sankoff generalises trivially, but Rosalind ALPH pins rooted *binary* trees so we enforce exactly 2 children per internal node.
- No support for *unlabeled* internal nodes in the input — Rosalind's sample has every internal node labeled (`rat`, `mouse`, `dog`, `hamster`, `robot`). The validation rejects the unlabeled-internal-node case.
- No protein-alphabet variant; alphabet is hardcoded `{A, C, G, T, -}`.
- No FASTA parsing inside the algorithm or domain (the runner under `bio.problems.ALPHProb` handles I/O — same convention as MULT).
- No streaming or sparse optimisation. With `n ≤ 500` internal nodes, `m ≤ 500` leaves, length `L ≤ 300`, alphabet size `K = 5`: the DP runs in `O(L · n · K²) = 300 · 500 · 25 ≈ 3.75M` ops — trivially fast.
- `NamedSequence` and `AlignmentBasedPhylogeny` are *not* `sealed abstract` — they're plain value carriers with no invariants beyond what their components provide.

## Decisions

**1. Reuse `NewickTree` and `NewickParser`.**
- Rationale: already exists, already tested, already handles labelled internal nodes (`label: Option[String]`) and arbitrary-arity children (`Vector[NewickTree]`). We add a *validation step* in the smart constructor that the tree is rooted binary with all internals labeled.

**2. New `NamedSequence(label: String, sequence: String)` plain `final case class`.**
- Rationale: a tiny re-usable record for FASTA-style entries. Used both for the input alignment and for output internal-node assignments. Plain case class — no smart constructor, no invariants beyond `label.nonEmpty` and a valid alignment alphabet (`A`, `C`, `G`, `T`, `-`). Those constraints are enforced by the *containing* `AlignmentBasedPhylogenyProblem.from` validation, not by `NamedSequence` itself.

**3. Sankoff with 5-symbol alphabet `{A, C, G, T, -}`.**
- Rationale: gap-vs-gap is a *match* (cost 0), gap-vs-non-gap is a *mismatch* (cost 1). This matches the Rosalind d_H definition. Treating `-` as a regular fifth symbol with unit substitution cost handles this exactly.

**4. Per-column DP table: `Array[Array[Int]]` keyed by `(nodeOrdinal, symbolOrdinal)`.**
- Rationale: at each column we walk the tree post-order, populating `cost(node, sym)` per the recurrence. Numeric ordinals (`0..K-1` for symbol, `0..NumNodes-1` for node) avoid boxing. Total memory per column: `numNodes × 5 × 4 bytes ≈ 10 KB` at the cap — trivially small.

**5. Pre-compute a post-order node ordering once and reuse it across all columns.**
- Rationale: the tree topology doesn't change column-to-column, so the traversal order can be cached. Storing each node's `(parentOrdinal, leftChildOrdinal, rightChildOrdinal, isLeaf, leafCharByColumn?)` lets the per-column inner loop be a tight array walk.

**6. Symbol encoding: `A=0, C=1, G=2, T=3, -=4`.**
- Rationale: any consistent ordering works; this matches the canonical `DnaString` convention extended with gap as the highest ordinal. Inner-loop `δ(a, b)` is just `if (a == b) 0 else 1`.

**7. Traceback: top-down per column, recording the chosen symbol for every internal node.**
- Rationale: at each internal node, during the bottom-up pass, also store *which* child symbol achieved the inner `min` for each parent symbol. Then traceback picks the root-min and walks down. Memory: `numInternal × K × 2 (children) × Byte ≈ 5 KB` per column at the cap.

**8. Output `internalAssignments` ordered by a deterministic pre-order traversal of the tree.**
- Rationale: pre-order gives a predictable, root-first ordering that's easy to test. Rosalind's published sample output orders by topology (rat, mouse, dog, hamster, robot — a depth-first left-then-right pre-order). We match that convention.

**9. Validation: validate everything strictly before running the DP.**
- Rationale: the algorithm assumes a well-formed input. Caching the error checks up-front keeps the algorithm pure and predictable. Errors (in priority order):
  1. `EmptyAlignment` — alignment has 0 rows.
  2. `LengthMismatch(rowIndex, length, expectedLength)` — first row whose length differs from row 0's length.
  3. `SequenceTooLong(length, max = 300)` — first sequence (by row index) whose length exceeds 300.
  4. `InvalidCharacter(rowIndex, position, ch)` — first non-`A`/`C`/`G`/`T`/`-` character encountered (row-major scan).
  5. `TooManyLeaves(actual, max = 500)` — tree has > 500 leaves.
  6. `InternalNodeMissingLabel(path)` — tree has at least one internal node with no label (Newick `(a,b);` would trigger this for the root).
  7. `NonBinaryInternalNode(label, degree)` — first internal node found whose `children.size != 2`.
  8. `LeafLabelMismatch(treeLeafLabels, alignmentLabels)` — the leaf labels in the tree and the alignment names do not coincide (set equality). Reported as the symmetric-difference set.

**10. Place under `bio.{domain,algorithms}.analysis`.**
- Rationale: the algorithm operates on a multiple alignment + tree; the output is a collection of DNA strings (alignment-style). Mirrors MULT and other comparative-analysis algorithms.

## Risks / Trade-offs

- **Multiple optimal solutions exist for the same input.** Rosalind says "multiple solutions will exist; you need only output one". Our deterministic traceback (left-child symbol preferred on ties, then alphabetic order of the symbol ordinal `A < C < G < T < -`) produces *one* valid optimum, which may differ from Rosalind's published sample. → For the canonical sample we verify `totalDistance == 8` and the five invariants on the returned internal assignments (leaves match input, each column matches the per-column DP optimum, total cost reproduces).
- **Per-column traceback memory.** At the cap (300 columns × 500 internal nodes × 5 symbols × 2 children), the traceback pointer table is ~3 MB. Acceptable. → If we ever lift the cap, switch to a streaming per-column build that flushes pointers after each column.
- **`NewickTree` allows `Vector` of arbitrary children, but ALPH requires exactly 2.** → The smart constructor checks every internal node's child count and rejects non-binary trees with `NonBinaryInternalNode`.
- **`NewickTree` allows missing internal labels — Rosalind ALPH always supplies them.** → The validation rejects any internal node with `label == None`. The runner pre-processes inputs to drop internal-label-stripped sample variants if needed.
