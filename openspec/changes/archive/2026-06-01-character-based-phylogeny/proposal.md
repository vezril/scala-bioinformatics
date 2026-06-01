## Why

The framework can already derive a character table from a tree (CSTR —
`CharacterTable.compute`) and reason about splits (SPTD, QRTD). The inverse
operation — reconstructing the tree from a consistent character table — is the
Rosalind **CHBP ("Character-Based Phylogeny")** problem and the natural next
capability: it closes the split ↔ tree round trip and reuses the split machinery
already in `bio.domain.graph`.

## What Changes

- Add a validated `CharacterBasedPhylogenyProblem` domain bundle (taxa list +
  0/1 character rows) built through a smart constructor returning
  `Either[CharacterBasedPhylogenyProblemError, _]`, with the constructor
  rejecting empty/duplicate taxa, more than 80 taxa, mis-sized rows, non-`0/1`
  characters, and **conflicting characters** (an inconsistent table).
- Add a `CharacterBasedPhylogenyProblemError` sealed ADT enumerating those
  failure cases.
- Add `bio.algorithms.graph.CharacterBasedPhylogeny.build` — a pure, total
  function turning a validated problem into an unrooted binary tree
  (`NewickTree`) that models the table, via reference-oriented splits assembled
  into a laminar (nested) cluster hierarchy.
- Add a `render: String` method to `NewickTree` producing canonical Newick
  (`(a,b,(c,d));`) so the result (and future tree-producing problems) can be
  printed. This is an additive method on an existing type — no behavior change
  to existing callers.
- Add a `bio.problems.CHBPProb` IO runner that reads `resources/chbp_data.txt`
  (a taxa line followed by character rows), builds the tree, and prints its
  Newick form; wire it into `Main`.

## Capabilities

### New Capabilities
- `character-based-phylogeny`: validating a consistent 0/1 character table over
  a taxa set and reconstructing an unrooted binary tree (Newick) that models it.

### Modified Capabilities
<!-- None. NewickTree gains an additive render method but no existing spec
     describes NewickTree behavior, so no requirement changes. -->

## Impact

- **New domain types**: `bio.domain.graph.CharacterBasedPhylogenyProblem`,
  `bio.domain.graph.CharacterBasedPhylogenyProblemError`.
- **New algorithm**: `bio.algorithms.graph.CharacterBasedPhylogeny`.
- **Modified (additive)**: `bio.domain.graph.NewickTree` gains `render: String`.
- **New runner**: `bio.problems.CHBPProb`; one commented-line swap in
  `bio.Main`.
- **Data**: reads `src/main/scala/resources/chbp_data.txt` (already present).
- **Dependencies**: none new — reuses `NewickTree` and existing patterns.
