## Context

The project already builds DNA graphs under `bio.{domain,algorithms}.graph`:
- `DeBruijnEdge`/`OverlapEdge` (plain case-class edges), `DeBruijnGraph` (result with `format`), `DeBruijnGraphProblem` (`sealed abstract case class` wrapping `Vector[DnaString]`, first-failure-wins validation).
- `bio.domain.nucleic.DnaString` (validated, `.value`, MaxLength 100000) and `DnaNucleotide` (sealed ADT with `fromChar`/`toChar`).

TRIE ("Introduction to Pattern Matching") is the first trie/prefix-tree capability. It fits the graph package and follows the de Bruijn precedents for problem/edge/result/error shapes.

## Goals / Non-Goals

**Goals:**
- `TrieEdge(parent: Int, child: Int, symbol: DnaNucleotide)` — symbol uses the `DnaNucleotide` ADT (not a raw `Char`), per the framework's ADT requirement.
- Validated `PatternTrieProblem` (≤ 100 patterns, each `DnaString` ≤ 100 bp, none a prefix of another) via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage.
- Pure, total `TrieConstruction.construct(problem): PatternTrie`, root labelled `1`, new node integers assigned in edge-creation order.
- Result type with `format: String` rendering `parent child symbol` per line; the empty trie (no patterns) renders as the empty string.
- `TRIEProb` runner reading patterns from `trie_data.txt`, printing via `IO`.

**Non-Goals:**
- Matching patterns against a text (this problem only constructs the trie).
- A canonical node numbering — Rosalind permits "any order"; we choose the deterministic insertion/creation order (which reproduces the sample output).
- Suffix tries/suffix trees or compressed (radix) tries.

## Decisions

**1. Node numbering by edge-creation order.**
The root is `1`; the next id starts at `2`. Patterns are inserted in input order, each walked symbol-by-symbol from the root. When the current node already has a child edge for the symbol, the walk reuses it; otherwise a new node is created with the next integer id and a new edge is appended. The output edge order is the creation order. Verified against the canonical sample (`ATAGA, ATC, GAT` → the 10-node, 9-edge adjacency list). Rosalind allows any labelling, so this deterministic scheme is sufficient and testable.

**2. Pure functional construction via threaded immutable state.**
Although trie construction is classically imperative (a mutable counter + node map), the framework mandates pure FP for non-alignment algorithms. Construction folds over the patterns (and, within each, over its nucleotides) threading an immutable `State(nextId, children, edges)`, where `children: Map[(Int, DnaNucleotide), Int]` records existing edges for O(1) reuse lookup and `edges: Vector[TrieEdge]` accumulates in creation order. No `var`/`while`/mutable collection. Nucleotides are obtained via `DnaNucleotide.fromChar` (ADT dispatch, no raw `Char`s).

**3. Validation rules and order (first-failure-wins).**
`PatternTrieProblem.from(patterns)` checks, in order: `patterns.size > 100` → `TooManyPatterns(size, 100)`; then (per pattern, index order) `length > 100` → `PatternTooLong(index, length, 100)`; then the prefix-freedom invariant → `PrefixConflict(prefixIndex, ofIndex)` for the first pair where one pattern is a prefix of another (string equality counts, so duplicates are conflicts). DNA-character validity is owned upstream by `DnaString`. The empty pattern list is accepted (it yields a root-only trie with no edges).

**4. Prefix-conflict detection.**
A conflict is the first `(i, j)` with `i ≠ j` and `patterns(j).value.startsWith(patterns(i).value)` — i.e. pattern `i` is a prefix of pattern `j`. Scanned with `i` outer, `j` inner so the first-found pair is deterministic. This both enforces the Rosalind precondition and prevents a pattern terminating at an internal node (which the bare adjacency-list output could not distinguish).

**5. `DnaNucleotide` for edge symbols.**
Unlike `DeBruijnEdge` (whose `from`/`to` are k-mer *strings*), a trie edge is labelled by a single nucleotide, so the ADT-correct representation is `DnaNucleotide`. `format` lowers it with `DnaNucleotide.toChar`. This keeps the symbol type-safe and matches the "dispatch on ADT, not raw `Char`" convention.

**6. Naming and placement.**
`TrieEdge`, `PatternTrieProblem`, `PatternTrieProblemError`, and the `PatternTrie` result live in `bio.domain.graph`; the algorithm `TrieConstruction.construct` in `bio.algorithms.graph` (mirroring `DeBruijnGraphConstruction.construct`). The result type (`PatternTrie`) and algorithm (`TrieConstruction`) have distinct names, so no `=> Result` alias is needed.

## Risks / Trade-offs

- **[Deterministic numbering vs. "any order"]** → Rosalind accepts any valid labelling; the insertion/creation-order scheme is deterministic, reproduces the sample exactly, and is straightforward to test.
- **[Prefix validation rejects inputs the bare algorithm could still process]** → Intentional: the adjacency-list output cannot mark a pattern that ends at an internal node, so prefix-freedom is a genuine domain invariant; covered by explicit scenarios (proper prefix and duplicate).
- **[Empty pattern list / empty pattern string]** → Empty list → root-only trie, `format` is `""` (explicit scenario). A lone empty pattern likewise adds no edges; combined with any other pattern it is a prefix of everything and is rejected by `PrefixConflict`.
- **[Large inputs]** → ≤ 100 patterns × ≤ 100 bp is tiny; the `Map`-backed construction is linear in total characters.
