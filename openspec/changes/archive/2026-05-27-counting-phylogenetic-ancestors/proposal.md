## Why

Rosalind problem 29 ("Counting Phylogenetic Ancestors", INOD) asks for the number of internal nodes of any unrooted binary tree with `n` leaves. This is the next combinatorial primitive on the framework's roadmap and continues the phylogenetics track started by `tree-completion` (spec 20). Adding it keeps the Rosalind-derived feature set moving forward and gives downstream phylogeny work (rooted/unrooted tree construction, distance-based clustering) a validated formula to lean on.

## What Changes

- Add a new validated domain type `UnrootedBinaryTreeLeafCount` for the input `n` (smart-constructor on `Either`, `sealed abstract case class`), enforcing `3 <= n <= 10000` per the Rosalind constraint.
- Add a new algorithm object `bio.algorithms.graph.PhylogeneticAncestors` exposing `internalNodes(problem: UnrootedBinaryTreeLeafCount): Int`, implementing the closed-form identity `internalNodes = n - 2` for any unrooted binary tree with `n >= 3` leaves.
- Add ScalaTest coverage (Red-Green-Refactor): canonical Rosalind sample (`n=4 → 2`), boundary `n=3 → 1`, upper boundary `n=10000 → 9998`, plus validation failures for `n=2` (below minimum) and `n=10001` (above maximum).
- No changes to existing capabilities; no breaking changes.

## Capabilities

### New Capabilities
- `phylogenetic-ancestors`: Counts the internal nodes of an unrooted binary tree with `n` leaves, including the validated `UnrootedBinaryTreeLeafCount` input bundle and the `PhylogeneticAncestors.internalNodes` algorithm.

### Modified Capabilities
<!-- None — this is purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/graph/UnrootedBinaryTreeLeafCount.scala`
  - `src/main/scala/bio/domain/graph/UnrootedBinaryTreeLeafCountError.scala`
  - `src/main/scala/bio/algorithms/graph/PhylogeneticAncestors.scala`
  - `src/test/scala/bio/algorithms/graph/PhylogeneticAncestorsSpec.scala`
  - `src/test/scala/bio/domain/graph/UnrootedBinaryTreeLeafCountSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Builds on the established `bio.{algorithms,domain}.graph` subdomain conventions used by `tree-completion`.
