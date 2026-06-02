## Context

MREP ("Identifying Maximal Repeats") returns every maximal repeat of `s` with length ≥ 20. A *maximal repeat* is a substring `t` occurring ≥ 2 times whose occurrences cannot all be extended by one symbol in either direction and still agree.

Gusfield's theorem gives the suffix-tree characterisation (the problem's hint): the maximal repeats of `s` are exactly the path-labels of the **left-diverse internal nodes** of the suffix tree of `s$`:
- an internal node is **right-maximal** automatically — it branches, so its path-label is followed by ≥ 2 distinct characters;
- it is **left-diverse** when the leaves in its subtree are preceded by ≥ 2 distinct characters (treating the start-of-string as a distinct "character").

The project already builds suffix trees (SUFF) and has the BFS/fold tree-analysis machinery (LREP). MREP lives in `bio.{domain,algorithms}.graph` alongside them and reuses `bio.domain.nucleic.DnaString` for `s`.

## Goals / Non-Goals

**Goals:**
- Validated `MaximalRepeatProblem(dna, minLength)` (dna ≤ 1000 bp, minLength ≥ 1) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total, stack-safe `IdentifyMaximalRepeats.find(problem): MaximalRepeats`.
- Result type with `format: String` (one repeat per line).
- Functional implementation (immutable maps, tail recursion, folds); no `var`/`while`/mutable collections.

**Non-Goals:**
- A linear-time suffix tree (Ukkonen). The naive O(n²) builder is fine at `n ≤ 1000` and stays purely functional.
- Reporting occurrence positions — only the repeat strings are returned.
- Reusing SUFF's builder verbatim (it exposes only edge labels); MREP needs the tree structure plus leaf positions, so it builds its own structured tree.

## Decisions

**1. Maximal repeats = left-diverse internal nodes (Gusfield).**
Build the suffix tree of `text = s + "$"`. For each node:
- right-maximal ⇔ it has ≥ 2 children (internal, non-root);
- left-diverse ⇔ the set of preceding characters of the leaves beneath it has ≥ 2 elements, where a leaf for the suffix starting at position `i` contributes `text(i-1)` (or a distinct start-of-string marker when `i = 0`).
A node satisfying both, whose path-label has length ≥ `minLength`, is a maximal repeat. (The unique `$` keeps every internal node's path-label `$`-free, so path-labels are genuine substrings of `s`.) Verified against the problem's worked example: in `TAGTTAGCGAGA`, both `AG` and `TAG` are left-diverse internal nodes.

**2. A structured suffix tree (the SUFF builder augmented with leaf positions).**
Reuse the SUFF naive-insertion construction (immutable `Map[Int, Map[Char, Edge]]`, root id 0, edges as `(start, end)` offsets), but additionally record, for each leaf, the suffix start position it represents (each suffix insertion creates exactly one leaf because `$` is unique). This gives, per leaf, both its preceding character and a representative for reconstructing path-labels.

**3. Node analyses by BFS + decreasing-depth folds (the LREP pattern), all stack-safe.**
- `stringDepth` (path-label length) per node by a `@tailrec` BFS top-down (`parent + edge.length`).
- `leftChars` per node by a `foldLeft` over nodes in decreasing edge-depth: a leaf seeds `Set(precedingChar)`; each node unions its (now-complete) set into its parent's. A node is left-diverse iff its final set has ≥ 2 elements.
- `repLeafStart` per node (any leaf beneath) by the same fold, to reconstruct `pathLabel = text.substring(repLeafStart, repLeafStart + stringDepth)`.
No deep recursion: per-suffix insertion is tail-recursive and all aggregation is folds.

**4. Validation rules and order (first-failure-wins).**
`MaximalRepeatProblem.from(dna, minLength)` checks `dna.value.length <= 1000` → else `SequenceTooLong(length, 1000)`; then `minLength >= 1` → else `NonPositiveMinLength(minLength)`. Making `minLength` a field (rather than hard-coding 20) lets the algorithm be exercised on short strings with a small threshold; the runner passes 20.

**5. Naming and placement.**
`MaximalRepeatProblem`, `MaximalRepeatProblemError`, and the `MaximalRepeats` result live in `bio.domain.graph`; the algorithm `IdentifyMaximalRepeats.find` in `bio.algorithms.graph` (distinct from the result name, so no alias needed). Results are returned in a deterministic sorted order (Rosalind permits any order).

## Risks / Trade-offs

- **[O(n²) construction]** → at `n ≤ 1000` (`text ≤ 1001`) this is ~10⁶ char comparisons; instant.
- **[Deep suffix trees]** → construction descent and the depth/aggregation passes are tail-recursive / fold-based, so no stack overflow.
- **[Output order]** → Rosalind accepts any order; results are sorted for determinism, and tests compare as sets.
- **[Boundary occurrences]** → a start-of-string occurrence contributes a distinct left marker, so a repeat occurring at position 0 is correctly counted as left-diverse; the `$` leaf attaches only to the root (path-label empty) and never pollutes an internal node.
- **[Empty / no-repeat inputs]** → strings with no qualifying repeat yield an empty result; covered by scenarios.
