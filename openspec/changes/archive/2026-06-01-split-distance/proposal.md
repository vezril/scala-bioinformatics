## Why

Rosalind SPTD ("Phylogeny Comparison with Split Distance") asks for the split
distance between two unrooted binary trees on the same taxa — a standard measure
of how much two phylogenies disagree. The framework already parses Newick trees
(NWCK) and computes tree distances, but has no way to compare two trees by their
sets of nontrivial splits.

## What Changes

- Add a new `split-distance` capability that, given a taxa list and two unrooted
  binary trees in Newick format on those taxa, returns their split distance
  `d_split(T1, T2) = 2(n − 3) − 2·s(T1, T2)`, where `s` is the number of
  nontrivial splits shared by both trees.
- Introduce a canonicalised `Split` ADT (a partition of the taxa into two sides,
  orientation-invariant so equal splits compare equal) used to count shared
  splits via ordinary set intersection.
- Introduce a validated `SplitDistanceProblem` input bundle and its
  `SplitDistanceProblemError` ADT (empty taxa, duplicate taxon, per-tree taxa
  mismatch), constructed only through a first-failure-wins smart constructor.
- Add `SplitDistance.compute` that extracts each tree's nontrivial splits,
  intersects them, and applies the distance formula.
- Add a `SPTDProb` runner that reads the sample dataset from
  `src/main/scala/resources/sptd_data.txt` (taxa on line 1, two Newick trees on
  the following lines), parses both trees, and prints the distance; wire it into
  `bio.Main`.

## Capabilities

### New Capabilities
- `split-distance`: validated two-tree comparison input, a canonicalised split
  ADT, and the algorithm computing the split distance between two unrooted binary
  trees per Rosalind SPTD.

### Modified Capabilities
<!-- None — reuses the existing Newick parser/tree types without changing their requirements. -->

## Impact

- New domain types: `bio.domain.graph.SplitDistanceProblem`,
  `bio.domain.graph.SplitDistanceProblemError`, `bio.domain.graph.Split`.
- New algorithm: `bio.algorithms.graph.SplitDistance`.
- New runner: `bio.problems.SPTDProb`; one line changed in `bio.Main`.
- Reuses existing `bio.parsing.NewickParser` and `bio.domain.graph.NewickTree`
  (no changes to either).
- New tests under `bio.domain.graph` and `bio.algorithms.graph`.
