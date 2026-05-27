## Context

Rosalind problem 30 (NWCK) asks: given a tree in Newick format and a pair of labelled nodes `x` and `y`, return the number of edges on the unique path connecting them. The two canonical samples are `(cat)dog;` with query `dog cat` → `1`, and `(dog,cat);` with query `dog cat` → `2`.

Newick format is a small bracketed grammar:
```
tree     := node ";"
node     := ( "(" node ("," node)* ")" )? label?
label    := identifier (any printable except ",()" and ";")
```
The framework already hosts `bio.{algorithms,domain}.graph` (overlap graphs, tree completion, phylogenetic ancestors) and `bio.parsing` (the FASTA parser/file-reader pair). The new feature slots cleanly into those subdomains.

The Rosalind upstream of NWCK allows multiple tree+query pairs in one input; this change scopes the *algorithm* and *parser* surface only. A per-test fixture builds the tree and query inline. A higher-level I/O helper (analogous to `TreeAdjacencyFileReader`) can be layered later as a separate spec if/when a user wires NWCK into `bio.problems`.

## Goals / Non-Goals

**Goals:**
- Provide a structured `NewickTree` ADT — labelled or unlabelled internal nodes, labelled leaves, arbitrary branching.
- Provide a recursive-descent `NewickParser.parse` returning `Either[NewickParseError, NewickTree]`, with structured errors for the common malformed cases (mismatched parens, missing semicolon, empty input).
- Provide a validated `NewickDistanceProblem` input bundle (tree + two labels, both labels must exist in the tree).
- Provide `NewickDistance.between` computing the path-edge distance via BFS over the tree's undirected adjacency, with `between(same-node) == 0`.
- TDD coverage at parser, problem, and algorithm layers — canonical samples + edge cases.

**Non-Goals:**
- Branch lengths (the `:1.234` suffix in extended Newick). The Rosalind sample has none, and the spec asks for *edge count*, not weighted distance.
- Quoted labels (e.g. `'Homo sapiens'`). The Rosalind grammar uses bare identifiers; supporting quoted labels is a separate concern.
- Multi-tree file ingestion (the upstream Rosalind file format groups many tree/query blocks). That's an I/O concern — a separate `NewickDistanceFileReader` can be added later if needed.
- Lowest-common-ancestor analytical shortcuts. With ≤ 200 nodes per tree and ≤ 40 trees per input, BFS is trivially fast and far simpler to reason about.

## Decisions

**1. Tree representation: `NewickTree(label: Option[String], children: Vector[NewickTree])`.**

A single recursive case class covers leaves (empty children, label required for query lookups), labelled internal nodes (children + label), and unlabelled internal nodes (children + `None`). Children are ordered (`Vector`) even though distance computation is order-independent — preserves parser fidelity for any future serialization. **Alternative considered:** a separate `Leaf` / `Branch` sum type (rejected: introduces a node-kind dichotomy the Newick grammar doesn't make, and "leaf" is just "node with zero children" — the simpler representation lines up with the grammar).

**2. Parser style: hand-rolled recursive descent with a mutable position cursor.**

Mirrors the project's existing `FastaParser` style (no parser-combinator dependency, no external grammar tooling). The grammar is small (3 productions), so a recursive `parseNode` returning `Either[NewickParseError, (NewickTree, Int)]` (or equivalent state) is a 30-40 line method. The parser's *public* surface (`NewickParser.parse(String)`) is pure and returns `Either` — any internal mutation is local. **Alternative considered:** Scala's `scala.util.parsing.combinators` (rejected: removed from the stdlib in 2.13, would require a new dep, and the grammar is too small to justify).

**3. Distance algorithm: BFS over an undirected adjacency map keyed by label.**

Steps: (1) recursively walk the tree collecting an undirected edge set (each parent↔child link is one edge); since some internal nodes may be unlabelled, the BFS keys use synthetic node IDs assigned during the walk, with a label→ID lookup for the query endpoints. (2) BFS from the source ID to the target ID, returning the depth at which the target is reached. (3) Same-node short-circuit returns `0`. **Alternative considered:** Lowest-common-ancestor with depth precomputation (rejected: BFS is O(V+E) which for ≤ 200 nodes is microseconds; LCA's preprocessing cost only pays off across many queries on the same tree, which the spec doesn't require).

**4. Why synthetic node IDs (Int) instead of label-keyed adjacency.**

Two reasons: (a) internal nodes may be unlabelled (a `(dog,cat);` tree has an unlabelled root), so a label-keyed map can't represent the root vertex; (b) Newick has no uniqueness rule on labels — two leaves with the same label would collide in a label-keyed map. Synthetic IDs assigned during the parse walk are unambiguous; the label→ID map for query endpoints can carry `Either` semantics for "label not present" or "label ambiguous". For the simple Rosalind inputs we expect unique labels and don't need to surface the ambiguity case — but the design accommodates it.

**5. Validation surface for `NewickDistanceProblem`.**

The smart constructor checks: source label exists in the tree (`UnknownLabel(x)` otherwise), target label exists (`UnknownLabel(y)` otherwise). Order: source first, then target. The tree itself is presumed pre-validated (it came out of `NewickParser.parse`'s `Right`), so we don't re-validate structure.

**6. Newick parse error ADT.**

Cases: `EmptyInput`, `MissingTerminator`, `UnmatchedOpenParen(position)`, `UnmatchedCloseParen(position)`, `UnexpectedCharacter(char, position)`, `TrailingContent(remaining)`. Each error carries enough information to point at the offending location — useful in any future REPL/CLI surface.

## Risks / Trade-offs

- **[Risk]** Hand-rolled parser may miss an edge case in the Newick grammar (e.g. whitespace handling between tokens). → **Mitigation:** the design ignores whitespace inside the grammar except as label-internal characters (Rosalind samples have no whitespace); tests cover the trimmed-input case explicitly. If a future Rosalind problem demands whitespace tolerance, extend the parser.
- **[Risk]** Duplicate labels in the same tree could cause a `NewickDistanceProblem` query to be ambiguous → **Mitigation:** the label→ID map records *first* occurrence and the smart constructor's error is `UnknownLabel` (it's fine for Rosalind NWCK — labels in the samples are unique). A `DuplicateLabel` case can be added later if a real input needs it.
- **[Trade-off]** No branch lengths, no quoted labels — chose to scope tightly to what NWCK requires. Anyone adding branch-length-weighted distance later will need to extend `NewickTree` (e.g. with `edgeLengthToParent: Option[Double]`) and add a weighted-BFS variant. The current minimal surface keeps the change focused.
