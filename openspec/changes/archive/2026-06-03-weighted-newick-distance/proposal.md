## Why

Rosalind problem NKEW ("Newick Format with Edge Weights") is the weighted generalisation of NWCK: given trees in Newick format whose edges carry weights (`label:weight`), the distance between two nodes is the sum of edge weights along the unique path between them. The project's existing `NewickParser`/`NewickTree` are unweighted (a colon would be swallowed into a label), so NKEW needs a weighted parser and tree plus a weighted-distance algorithm.

## What Changes

- Introduce a `WeightedNewickTree` domain type (a node label plus child subtrees each carrying an edge weight, via `WeightedChild`).
- Introduce a `WeightedNewickParser` (and `WeightedNewickParseError` ADT) that parses weighted Newick, reading `:weight` on each edge.
- Introduce a validated `WeightedTreeDistanceProblem` wrapping a parsed weighted tree and two query node labels `x`, `y` (both must occur in the tree).
- Introduce a `WeightedTreeDistanceProblemError` ADT for the new invariant (node not found).
- Introduce a `WeightedNewickDistance` algorithm returning the summed-edge-weight distance between `x` and `y` (the unique tree path).
- Add an `NKEWProb` runner reading the multi-block input from `nkew_data.txt` (each tree followed by a query pair, blocks blank-line separated) and printing the per-tree distances space-separated through `IO`.
- Reuse existing infrastructure: none beyond the standard library (the unweighted Newick types cannot carry weights).

## Capabilities

### New Capabilities
- `weighted-newick-distance`: Parse weighted Newick trees and compute the distance (sum of edge weights) between two queried nodes (Rosalind NKEW).

### Modified Capabilities
<!-- None. NKEW adds a new capability; the unweighted NWCK parser/tree are left unchanged. -->

## Impact

- **New domain types** (`bio.domain.graph`): `WeightedNewickTree`, `WeightedChild`, `WeightedTreeDistanceProblem`, `WeightedTreeDistanceProblemError`.
- **New parser** (`bio.parsing`): `WeightedNewickParser`, `WeightedNewickParseError`.
- **New algorithm** (`bio.algorithms.graph.WeightedNewickDistance`).
- **New runner** (`bio.problems.NKEWProb`) reading `src/main/scala/resources/nkew_data.txt`.
- **Tests**: new specs under `bio.parsing`, `bio.domain.graph`, and `bio.algorithms.graph`. No existing tests change.
