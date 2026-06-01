## Why

The framework can derive a character table from a tree (CSTR), reconstruct a
tree from a *consistent* table (CHBP), and already knows when two characters'
splits **conflict**. The remaining Rosalind **CSET ("Fixing an Inconsistent
Character Set")** problem closes the loop: given an *inconsistent* table, repair
it by deleting a single character (row) so the rest become consistent. It reuses
the split-compatibility rule introduced for CHBP.

## What Changes

- Add a validated `InconsistentCharacterSetProblem` domain bundle (the `0/1`
  character rows, columns = taxa) built through a smart constructor returning
  `Either[InconsistentCharacterSetProblemError, _]`, rejecting an empty table,
  ragged rows, non-`0/1` characters, and more than 100 taxa (columns).
- Add an `InconsistentCharacterSetProblemError` sealed ADT for those failures.
- Add a `ConsistentCharacterTable` result type (the retained rows) with a
  `format: String` (one row per line).
- Add `bio.algorithms.graph.FixInconsistentCharacterSet.fix` — a pure function
  returning `Option[ConsistentCharacterTable]`: the table with one conflicting
  row removed so no two remaining characters conflict, or `None` when no single
  deletion suffices.
- Add a `bio.problems.CSETProb` IO runner that reads `resources/cset_data.txt`,
  computes the repaired table, and prints it (or a no-solution message); wire it
  into `Main`.

## Capabilities

### New Capabilities
- `fixing-inconsistent-character-set`: validating a `0/1` character table over
  taxa columns and repairing inconsistency by deleting a single conflicting
  character row.

### Modified Capabilities
<!-- None. The split-conflict rule is re-implemented locally; no existing spec
     describes it as shared behavior, so no requirement changes. -->

## Impact

- **New domain types**: `bio.domain.graph.InconsistentCharacterSetProblem`,
  `bio.domain.graph.InconsistentCharacterSetProblemError`,
  `bio.domain.graph.ConsistentCharacterTable`.
- **New algorithm**: `bio.algorithms.graph.FixInconsistentCharacterSet`.
- **New runner**: `bio.problems.CSETProb`; one commented-line swap in `bio.Main`.
- **Data**: reads `src/main/scala/resources/cset_data.txt` (already present).
- **Dependencies**: none new — same split-compatibility rule as CHBP, applied to
  rows-as-characters.
