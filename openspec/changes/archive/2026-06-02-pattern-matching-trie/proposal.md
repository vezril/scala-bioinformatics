## Why

Rosalind problem TRIE ("Introduction to Pattern Matching") asks, given a collection of DNA strings (none a prefix of another), for the adjacency list of the trie that encodes them. The trie is the foundational data structure for multiple-pattern matching, and this is the project's first trie capability — extending the existing graph family (de Bruijn, overlap graphs) with a rooted prefix tree over DNA patterns.

## What Changes

- Introduce a `TrieEdge` domain type — one directed, symbol-labelled edge `(parent, child, symbol)` where `symbol` is a `DnaNucleotide`.
- Introduce a validated `PatternTrieProblem` domain type wrapping the collection of pattern `DnaString`s (≤ 100 patterns, each ≤ 100 bp, none a prefix of another).
- Introduce a `PatternTrieProblemError` ADT for the new invariants (too many patterns, pattern too long, prefix conflict).
- Introduce a `PatternTrie` result type holding the trie edges in creation order, with a Rosalind-style `format` (one `parent child symbol` triple per line).
- Introduce a `TrieConstruction` algorithm that builds the trie by inserting each pattern symbol-by-symbol, labelling the root `1` and assigning new node integers in edge-creation order, reusing existing edges where prefixes overlap.
- Add a `TRIEProb` runner reading the patterns from `trie_data.txt` and printing the adjacency list through `IO`.
- Reuse existing infrastructure unchanged: `bio.domain.nucleic.{DnaString, DnaNucleotide}`.

## Capabilities

### New Capabilities
- `pattern-matching-trie`: Construct the trie (symbol-labelled adjacency list) encoding a collection of DNA patterns, with the root labelled `1` (Rosalind TRIE).

### Modified Capabilities
<!-- None. TRIE adds a new capability and reuses existing nucleic types without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.graph`): `TrieEdge`, `PatternTrieProblem`, `PatternTrieProblemError`, `PatternTrie` (result).
- **New algorithm** (`bio.algorithms.graph.TrieConstruction`).
- **New runner** (`bio.problems.TRIEProb`) reading `src/main/scala/resources/trie_data.txt`.
- **Reused, unchanged**: `bio.domain.nucleic.{DnaString, DnaNucleotide}`.
- **Tests**: new specs under `bio.domain.graph` and `bio.algorithms.graph`. No existing tests change.
