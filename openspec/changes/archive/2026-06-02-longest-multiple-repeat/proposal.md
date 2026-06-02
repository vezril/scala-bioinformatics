## Why

Rosalind problem LREP ("Finding the Longest Multiple Repeat") gives the suffix tree of a string `s$` and asks for the longest substring of `s` that occurs at least `k` times. It extends the project's graph/tree capabilities (de Bruijn, overlap graphs, the pattern-matching trie) with suffix-tree analysis — the first capability to consume a pre-built suffix tree and exploit the leaf-count = occurrence-count property.

## What Changes

- Introduce a `SuffixTreeEdge` domain type — one edge `(parent, child, start, length)` of the suffix tree, where `start` is the 1-based position in `s$` of the edge's substring label and `length` its length.
- Introduce a validated `LongestRepeatProblem` domain type wrapping the text `s$`, the repeat threshold `k`, and the list of suffix-tree edges.
- Introduce a `LongestRepeatProblemError` ADT for the new invariants (non-positive `k`, text too long, edge substring out of bounds).
- Introduce a `LongestRepeat` result type holding the answer substring, with a `format` returning it verbatim.
- Introduce a `LongestMultipleRepeat` algorithm: compute each node's subtree leaf-count (= occurrence count) and string-depth, then return the path-string of the deepest internal node whose leaf-count is at least `k`.
- Add an `LREPProb` runner reading the text, `k`, and edges from `lrep_data.txt` and printing the longest multiple repeat through `IO`.
- Reuse existing infrastructure: none beyond the standard library (LREP consumes a generic suffix tree over `s$`).

## Capabilities

### New Capabilities
- `longest-multiple-repeat`: Given the suffix tree of `s$` and an integer `k`, return the longest substring of `s` occurring at least `k` times (Rosalind LREP).

### Modified Capabilities
<!-- None. LREP adds a new capability and does not change any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.graph`): `SuffixTreeEdge`, `LongestRepeatProblem`, `LongestRepeatProblemError`, `LongestRepeat` (result).
- **New algorithm** (`bio.algorithms.graph.LongestMultipleRepeat`).
- **New runner** (`bio.problems.LREPProb`) reading `src/main/scala/resources/lrep_data.txt`.
- **Tests**: new specs under `bio.domain.graph` and `bio.algorithms.graph`. No existing tests change.
