## Context

The `bio.{domain,algorithms}.graph` packages host the project's tree/graph problems (de Bruijn graph, overlap graph, the pattern-matching trie with `TrieEdge`/`PatternTrie`). LREP ("Finding the Longest Multiple Repeat") fits here: it consumes a pre-built **suffix tree** of `s$` (given as an edge list) and finds the longest substring occurring at least `k` times. It depends only on the standard library (the text is `s$`, including the `$` terminator — not a pure `DnaString`).

The governing fact (the problem's hint): in a suffix tree, the number of leaves beneath an internal node equals the number of times the substring spelled from the root to that node occurs in `s`. So the answer is the **string-deepest internal node whose subtree has ≥ k leaves**.

## Goals / Non-Goals

**Goals:**
- `SuffixTreeEdge(parent, child, start, length)` — a plain ADT edge; `start` is the 1-based offset into `s$`.
- Validated `LongestRepeatProblem(text, k, edges)` via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage.
- Pure, total, **stack-safe** `LongestMultipleRepeat.find(problem): LongestRepeat`.
- Result type with `format: String` returning the answer substring verbatim.
- `LREPProb` runner reading text / `k` / edges from `lrep_data.txt`, printing via `IO`.

**Non-Goals:**
- *Building* a suffix tree from `s` (the tree is given). No Ukkonen/McCreight construction.
- Returning all longest repeats — Rosalind accepts any single solution; ties are broken arbitrarily.
- Validating that the edge list forms a well-formed suffix tree beyond substring-bounds and `k` checks (the input is trusted to be a valid tree).

## Decisions

**1. Leaf-count = occurrence-count; answer = deepest internal node with leaf-count ≥ k.**
Each node's path-string (concatenated edge labels from the root) occurs in `s` exactly as many times as there are leaves in its subtree. Internal nodes (≥ 2 children) have `$`-free path-strings (a shared prefix of ≥ 2 suffixes cannot contain the unique terminator), so any internal node with leaf-count ≥ k yields a valid substring of `s`. The algorithm returns the path-string of the internal node maximising string-depth among those with leaf-count ≥ k (empty string if none qualifies). Verified on the sample (`CATACATAC$`, `k = 2` → node "CATAC", 2 leaves, depth 5).

**2. Stack-safe traversal (no deep recursion).**
`s` may be up to 20 kbp, so a degenerate suffix tree can be ~20 000 deep — naive recursive DFS would overflow the stack. Instead the algorithm uses:
  - a `@tailrec` breadth-first sweep from the root to compute each node's **string-depth** (`parent + edge.length`, top-down) and **edge-depth** (for ordering);
  - a `foldLeft` over nodes in **decreasing edge-depth** to accumulate **leaf-counts** bottom-up into an immutable `Map` (a child is always deeper than its parent, so every child contributes before its parent is processed);
  - a `@tailrec` walk up `parentEdge` pointers to reconstruct only the winning node's path-string (labels collected then `mkString`).
All structures are immutable; there is no `var`, `while`, or mutable collection, and recursion is constant-stack.

**3. `Map`-based tree representation.**
From the edge list: `childrenOf: Map[String, Vector[SuffixTreeEdge]]`, `parentEdge: Map[String, SuffixTreeEdge]` (each child has exactly one parent edge), and the root = the unique node never appearing as a child. Leaves are nodes absent from `childrenOf`. Edge labels are `text.substring(start - 1, start - 1 + length)`.

**4. Total function returning a (possibly empty) substring.**
Rather than `Option`, `find` returns `LongestRepeat(substring)`; when no internal node has leaf-count ≥ k the substring is `""` (no nonempty substring repeats ≥ k times — e.g. `k` exceeds every occurrence count). This keeps the function total and the result type simple.

**5. Validation rules and order (first-failure-wins).**
`LongestRepeatProblem.from(text, k, edges)` checks, in order: `k >= 1`, else `NonPositiveK(k)`; `text.length <= 20001` (20 kbp + terminator), else `TextTooLong(length, 20001)`; then each edge's label must lie within the text — `start >= 1` and `start - 1 + length <= text.length` (index order), else `EdgeOutOfBounds(index, start, length, textLength)`. An empty edge list is accepted (root-only tree → answer `""`).

**6. Naming and placement.**
`SuffixTreeEdge`, `LongestRepeatProblem`, `LongestRepeatProblemError`, and the `LongestRepeat` result live in `bio.domain.graph`; the algorithm `LongestMultipleRepeat.find` in `bio.algorithms.graph`. Result (`LongestRepeat`) and algorithm (`LongestMultipleRepeat`) names are distinct, so no `=> Result` alias is needed.

## Risks / Trade-offs

- **[Deep suffix trees overflowing the stack]** → mitigated by the tail-recursive BFS + fold + upward-walk design (Decision 2); no traversal recurses on tree depth non-tail-recursively.
- **[Output differs from sample on ties]** → Rosalind accepts any longest repeat; the algorithm's `maxBy(string-depth)` breaks ties by node ordering, which is deterministic for a given input. The sample's unique deepest node is `CATAC`.
- **[`k` exceeds all occurrence counts]** → returns `""` (covered by an explicit scenario, `k = 5` on the sample where the most frequent symbol occurs 4 times).
- **[Edge label out of bounds / malformed tree]** → bounds are validated up front (`EdgeOutOfBounds`); broader structural validity is trusted, consistent with the "given a valid suffix tree" framing.
- **[Path-string reconstruction cost]** → labels are collected up the path and `mkString`-ed once (linear in the answer length), avoiding quadratic string concatenation.
