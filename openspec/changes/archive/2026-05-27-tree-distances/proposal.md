## Why

Rosalind problem 30 ("Distances in Trees", NWCK) is the next phylogenetics primitive in the framework's roadmap. It introduces *Newick format* — the de-facto serialization for phylogenetic trees — and computes the path-edge distance between two named nodes in a parsed tree. Adding it gives the framework both a Newick parser (broadly reusable for any future phylogenetics feature) and a graph-distance algorithm grounded in real biological tree representations, building naturally on the `phylogenetic-ancestors` (INOD, spec 29) and `tree-completion` (TREE, spec 20) work that already lives under `bio.{algorithms,domain}.graph`.

## What Changes

- Add a new validated tree ADT `bio.domain.graph.NewickTree` modelling a parsed Newick tree (recursive case class with a label, children, and helpers to enumerate labelled descendants). Includes a smart-constructor surface for clean test wiring.
- Add a new Newick parser `bio.parsing.NewickParser` exposing `parse(input: String): Either[NewickParseError, NewickTree]`. Handles labelled and unlabelled internal nodes, leaf-only labels, nested groups, the terminating semicolon, and rejects malformed input (mismatched parens, empty input, etc.).
- Add a new validated input bundle `bio.domain.graph.NewickDistanceProblem` packaging a `NewickTree`, source label `x`, and target label `y`. Smart constructor verifies that both labels exist in the tree.
- Add a new algorithm object `bio.algorithms.graph.NewickDistance` exposing `between(problem: NewickDistanceProblem): Int` — the number of edges on the unique path between `x` and `y`, computed by BFS over the tree's undirected adjacency.
- Add ScalaTest coverage (Red-Green-Refactor) at three layers:
  - `NewickParserSpec`: canonical samples `(cat)dog;` and `(dog,cat);`; unlabelled internal node `(dog,cat);`; deeply nested `((a,b)c,(d,e)f)g;`; rejection cases for mismatched parens, missing terminator, and empty input.
  - `NewickDistanceSpec`: both canonical Rosalind sample distances (`1` and `2`); same-node distance `0`; multi-level path through internal nodes.
  - `NewickDistanceProblemSpec`: accepts valid problem; rejects unknown source / target.
- No changes to existing capabilities; no breaking changes.

## Capabilities

### New Capabilities
- `newick-parsing`: Parses Newick-format tree strings into a structured `NewickTree` ADT. Handles labelled and unlabelled internal nodes, nested groups, and the terminating semicolon; reports structured errors for malformed input.
- `tree-distances`: Computes the number of edges between two labelled nodes in a parsed `NewickTree`, including the validated `NewickDistanceProblem` input bundle and the `NewickDistance.between` BFS-based algorithm.

### Modified Capabilities
<!-- None — this is purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/graph/NewickTree.scala`
  - `src/main/scala/bio/domain/graph/NewickDistanceProblem.scala`
  - `src/main/scala/bio/domain/graph/NewickDistanceProblemError.scala`
  - `src/main/scala/bio/parsing/NewickParser.scala`
  - `src/main/scala/bio/parsing/NewickParseError.scala`
  - `src/main/scala/bio/algorithms/graph/NewickDistance.scala`
  - `src/test/scala/bio/domain/graph/NewickTreeSpec.scala`
  - `src/test/scala/bio/domain/graph/NewickDistanceProblemSpec.scala`
  - `src/test/scala/bio/parsing/NewickParserSpec.scala`
  - `src/test/scala/bio/algorithms/graph/NewickDistanceSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies (Newick is a small grammar — hand-rolled recursive-descent parser fits the project's "no parser-generator" pattern, mirroring `FastaParser`).
- Builds on the established `bio.{algorithms,domain}.graph` and `bio.parsing` conventions.
