## Why

Rosalind problem 32 ("Creating a Character Table", CSTR) takes an unrooted binary tree in Newick format and asks for its *character table* — one binary row per *nontrivial* edge-split of the tree, with columns indexed by the lexicographically-ordered taxa. This is a foundational phylogenetics primitive: an edge in an unrooted binary tree partitions the taxa into two non-empty sets, and that partition (the "split") is exactly the character that the edge encodes. Adding it builds directly on the now-archived `tree-distances` (NWCK, spec 30) and `phylogenetic-ancestors` (INOD, spec 29) capabilities, reusing the existing `NewickTree` ADT and parser, and completing Rosalind problem 32 — the next problem after the now-archived KMP (spec 31).

## What Changes

- Add a new validated domain type `bio.domain.graph.CharacterTableProblem` wrapping a `NewickTree`. The smart constructor enforces the Rosalind cap of `leafCount <= 200` and exposes `leafLabels: Vector[String]` (lexicographically sorted), so the algorithm doesn't have to re-walk the tree to discover them.
- Add a new algorithm object `bio.algorithms.graph.CharacterTable` exposing `compute(problem: CharacterTableProblem): Vector[String]`. Walks the tree, enumerates each internal-edge split (the subtree-leaves on one side vs the rest on the other), filters out trivial splits (where either side has size `< 2`), encodes each remaining split as a `0`/`1` row using the **lex-first-taxon-gets-0** convention (deterministic, matches Rosalind sample), deduplicates, and sorts the rows lexicographically for deterministic output. Empty result when there are no nontrivial splits.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `CharacterTableProblemSpec`: accepts canonical Rosalind sample tree; accepts edge sizes 1 and 200 leaves; rejects 201 leaves as `TooManyTaxa`; companion `apply`/`copy` leak-proofness.
  - `CharacterTableSpec`: canonical Rosalind sample yields `Vector("00110", "00111")`; flat tree `(a,b,c,d);` yields `Vector.empty` (no internal edges); balanced quartet `((a,b),(c,d));` yields a single row `"0011"` (one nontrivial split); deeper nesting `((a,b),((c,d),(e,f)));` yields three nontrivial split rows; single-leaf `a;` yields `Vector.empty`.
- No changes to existing capabilities; no breaking changes. `NewickTree` and `NewickParser` are reused as-is.

## Capabilities

### New Capabilities
- `character-table`: Computes the character-table representation of an unrooted binary tree given in Newick format. Includes the validated `CharacterTableProblem` input bundle and the `CharacterTable.compute` algorithm.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/graph/CharacterTableProblem.scala`
  - `src/main/scala/bio/domain/graph/CharacterTableProblemError.scala`
  - `src/main/scala/bio/algorithms/graph/CharacterTable.scala`
  - `src/test/scala/bio/domain/graph/CharacterTableProblemSpec.scala`
  - `src/test/scala/bio/algorithms/graph/CharacterTableSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Builds on the established `bio.{algorithms,domain}.graph` subdomain (alongside `overlap-graphs`, `tree-completion`, `phylogenetic-ancestors`, `tree-distances`) and reuses the existing `bio.parsing.NewickParser` for any I/O wiring later.
