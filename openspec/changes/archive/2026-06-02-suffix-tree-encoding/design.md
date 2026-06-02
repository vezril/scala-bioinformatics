## Context

The `bio.{domain,algorithms}.graph` packages host the project's tree/graph problems, including the LREP suffix-tree *consumer* (`SuffixTreeEdge`, `LongestRepeatProblem`, `LongestMultipleRepeat`). SUFF ("Encoding Suffix Trees") is the complementary *producer*: given a DNA string `s` (≤ 1 kbp), build the suffix tree of `s$` and return the substrings of `s$` labelling its edges. It reuses `bio.domain.nucleic.DnaString` for `s`; the `$` terminator is appended internally.

## Goals / Non-Goals

**Goals:**
- Validated `SuffixTreeProblem` wrapping a `DnaString` `s` (≤ 1000 bp) via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage.
- Pure, total, stack-safe `SuffixTreeConstruction.encode(problem): SuffixTreeEncoding`.
- Result type with `format: String` rendering one edge label per line (any order).
- Functional implementation (immutable maps + tail recursion), no `var`/`while`/mutable collections.

**Non-Goals:**
- A linear-time construction (Ukkonen/McCreight). The naive O(n²) insertion is more than adequate for `n ≤ 1000` and far simpler to keep purely functional.
- Any prescribed edge ordering — Rosalind accepts any order; tests compare the edge-label multiset.
- Modelling internal node identities in the result — only the edge-label substrings are returned.

## Decisions

**1. Naive suffix-tree construction by insertion with edge splitting.**
Form `text = s + "$"` (length `n`). Insert each suffix `text[i..]` (`i = 0..n-1`) into an initially root-only tree. Walking down from the root: at the current node, look up the child edge whose label begins with the next character. If none, add a leaf edge labelled with the rest of the suffix. If one exists, match the suffix against the edge label: a full match descends to the child and continues; a partial match (divergence at offset `m`) **splits** the edge — a new internal node at `m`, re-parenting the old child and adding a new leaf. Because `s$` ends in the unique terminator `$`, every suffix is distinct and none is a prefix of another, so each insertion ends by creating a leaf (never terminating at an internal node) and never over-runs the text. The resulting compressed tree is unique, so the **multiset** of edge labels is an invariant of `s` regardless of insertion or traversal order.

**2. Edges stored as `(start, end)` offsets into `text`; labels materialised on output.**
A node is an `Int` id (root = 0). `children: Map[Int, Map[Char, Edge]]` keys each node's out-edges by their first character (the suffix-tree invariant that sibling edges begin with distinct symbols), giving O(1) edge lookup. `Edge(start, end, child)` references `text`; the label is `text.substring(start, end)`. This keeps memory O(number of edges) rather than storing substrings during construction.

**3. Pure FP with tail-recursive descent.**
Construction folds over the suffixes threading an immutable `Tree(children, nextId)`. Each suffix insertion is a `@tailrec` descent: the full-match case recurses in tail position (descend); the add-leaf and split cases return the updated `Tree` terminally. The common-prefix match is itself a `@tailrec` counter. No `var`, `while`, or mutable collection; descent is constant-stack (bounded by tree depth but tail-recursive).

**4. Total function; result holds the raw labels.**
`encode` always succeeds (a validated `DnaString` plus `$` always has a well-defined suffix tree). The result `SuffixTreeEncoding(edges: Vector[String])` collects every edge's label; `format` joins them with newlines. The empty result (only possible for a hypothetical empty tree) renders as `""`; in practice even `s = ""` yields the single edge `"$"`.

**5. Validation.**
`SuffixTreeProblem.from(dna)` rejects `dna.value.length > 1000` with `SequenceTooLong(length, 1000)` (mirroring `RestrictionSiteProblem`). Character validity is owned upstream by `DnaString`. The empty string is accepted (`s$ = "$"` → one leaf edge `"$"`).

**6. Naming and placement.**
`SuffixTreeProblem`, `SuffixTreeProblemError`, and the `SuffixTreeEncoding` result live in `bio.domain.graph`; the algorithm `SuffixTreeConstruction.encode` in `bio.algorithms.graph`. Result and algorithm names are distinct, so no `=> Result` alias is needed. The runner strips a single trailing `$` from the input line (the sample data includes it) before building the `DnaString`, since the algorithm re-appends the terminator.

## Risks / Trade-offs

- **[O(n²) construction]** → For `n ≤ 1000` (`text ≤ 1001`) this is at most ~10⁶ character comparisons — instant. Acceptable in exchange for a simple, purely functional implementation.
- **[Output order differs from the sample]** → Rosalind accepts any order; the edge-label multiset is invariant, so tests compare sorted labels.
- **[Deep trees]** → Per-suffix descent is tail-recursive (constant stack); the suffix fold is iterative. No stack-overflow risk at `n ≤ 1000`.
- **[Input may or may not include `$`]** → The runner strips one trailing `$` if present; the algorithm always appends exactly one `$`, so both input forms behave identically.
- **[Empty / single-character / repeated-character inputs]** → `""` → `{"$"}`; `"A"` → `{"A$","$"}`; `"AAA"` → six edges — all covered as scenarios.
