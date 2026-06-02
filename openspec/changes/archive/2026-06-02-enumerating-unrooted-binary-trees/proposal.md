## Why

Rosalind problem EUBT ("Enumerating Unrooted Binary Trees") asks, given `n` taxa, for every unrooted binary tree whose leaves are those taxa, in Newick format. The number of such trees is `(2n−5)!!`. It rounds out the project's phylogeny family (which so far counts unrooted trees in CUNR and parses/measures Newick trees) by actually *generating* the trees, reusing the existing `NewickTree` renderer.

## What Changes

- Introduce a validated `UnrootedBinaryTreesProblem` domain type wrapping the taxa (`Vector[String]`), requiring at least 3 distinct taxa (and a sane upper bound, since the tree count grows factorially).
- Introduce an `UnrootedBinaryTreesProblemError` ADT for the new invariants (too few taxa, too many taxa, duplicate taxon).
- Introduce an `UnrootedBinaryTrees` result type holding the Newick strings (`Vector[String]`), with a `format` of one tree per line.
- Introduce an `EnumerateUnrootedBinaryTrees` algorithm that builds all trees by incremental edge insertion (root at the first taxon, start from 3 taxa, insert each remaining taxon onto every edge), rendering each via `NewickTree`.
- Add an `EUBTProb` runner reading the taxa from `eubt_data.txt` and printing the trees through `IO`.
- Reuse existing infrastructure: `bio.domain.graph.NewickTree` (Newick rendering).

## Capabilities

### New Capabilities
- `enumerating-unrooted-binary-trees`: Generate every unrooted binary tree on a given set of `n` taxa, rendered in Newick format (Rosalind EUBT).

### Modified Capabilities
<!-- None. EUBT adds a new capability and reuses NewickTree without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.graph`): `UnrootedBinaryTreesProblem`, `UnrootedBinaryTreesProblemError`, `UnrootedBinaryTrees` (result).
- **New algorithm** (`bio.algorithms.graph.EnumerateUnrootedBinaryTrees`).
- **New runner** (`bio.problems.EUBTProb`) reading `src/main/scala/resources/eubt_data.txt`.
- **Reused, unchanged**: `bio.domain.graph.NewickTree`.
- **Tests**: new specs under `bio.domain.graph` and `bio.algorithms.graph`. No existing tests change.
