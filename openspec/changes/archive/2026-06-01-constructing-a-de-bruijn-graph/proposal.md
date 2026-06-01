## Why

Rosalind's "Constructing a De Bruijn Graph" (DBRU) builds the de Bruijn graph
`B_k` from a set of (k+1)-mers together with their reverse complements. This is the
graph that underpins modern short-read assembly, and the framework already has the
pieces it needs: a validated `DnaString`, the total `DnaReverseComplement`
algorithm (REVC), and an established `bio.*.graph` package with a directed-edge
precedent (`OverlapEdge`). DBRU composes these into a single graph-construction pass.

## What Changes

- Add a `DeBruijnGraphProblemError` ADT capturing the ways the (k+1)-mer collection
  can be invalid (empty, too many, a k-mer too short to split, a k-mer too long, or
  k-mers of unequal length).
- Add a validated, invariant-bearing `DeBruijnGraphProblem` wrapping the collection
  of (k+1)-mer `DnaString`s, constructed only via a smart constructor `from(kmers)`
  returning `Either`.
- Add a `DeBruijnEdge(from, to)` directed-edge type (mirroring `OverlapEdge`) and a
  `DeBruijnGraph` result type holding the sorted, de-duplicated edges, with a
  `format` rendering each edge as `(from, to)` one per line.
- Add a `DeBruijnGraphConstruction.construct` algorithm: union the (k+1)-mer set
  with its reverse complements (reusing `DnaReverseComplement`), emit a
  `(prefix, suffix)` edge per (k+1)-mer, then de-duplicate and sort lexicographically.
- Add a `DBRUProb` IO runner reading `resources/dbru_data.txt` (one k-mer per line),
  constructing the graph, and printing the adjacency list; wire it into `Main`.

## Capabilities

### New Capabilities
- `constructing-a-de-bruijn-graph`: building the de Bruijn graph `B_k` from a set of
  (k+1)-mers and their reverse complements, reported as a sorted adjacency list.

### Modified Capabilities
<!-- None: this is a new capability that reuses existing types without changing their requirements. -->

## Impact

- New domain types under `bio.domain.graph` (`DeBruijnGraphProblemError`,
  `DeBruijnGraphProblem`, `DeBruijnEdge`, `DeBruijnGraph`).
- New algorithm `bio.algorithms.graph.DeBruijnGraphConstruction`, reusing
  `bio.algorithms.nucleic.DnaReverseComplement`.
- New IO runner `bio.problems.DBRUProb`; one-line wiring change in `bio.Main`.
- Reads the existing `src/main/scala/resources/dbru_data.txt` dataset.
- No changes to existing types, APIs, or specs.
