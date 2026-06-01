## Why

Rosalind spec 55 (QRT — "Quartets") asks, given a *partial* character table `C`
over `n` named taxa (each row a string of `0`/`1`/`x`), for the collection of
all *quartets* that can be inferred from the partial splits of its characters. A
quartet is a partial split `{a,b} | {c,d}` with exactly two taxa on each side; a
character whose `1`-side is `S₁` and `0`-side is `S₀` (the `x` taxa being
excluded) infers every quartet formed by choosing two taxa from `S₁` and two
from `S₀`. This is a new phylogenetics capability with no existing analog.

## What Changes

- Add a validated input bundle for the partial character table: a vector of
  distinct taxon names plus a vector of `0`/`1`/`x` character rows, each row's
  width equal to the taxon count.
- Add a `Quartet` ADT representing a canonicalised `{a,b} | {c,d}` partial split
  (sorted within and across its two pairs) so equal quartets compare equal and
  can be deduplicated.
- Add an algorithm enumerating, for each character, every quartet from the
  cross-product of 2-combinations of its `1`-side and its `0`-side, collected
  and deduplicated across all characters.
- Add a problem runner and wire it into `Main`, reproducing the canonical sample
  (4 quartets from the cat/dog/elephant/… table).

## Capabilities

### New Capabilities
- `quartets`: A validated partial-character-table input bundle, a canonicalised
  `Quartet` ADT, and an algorithm inferring all quartets from the table's
  partial splits.

### Modified Capabilities
<!-- None: this is a standalone new capability. -->

## Impact

- New domain types under `bio.domain.graph`
  (`QuartetsProblem`, `QuartetsProblemError`, `Quartet`).
- New algorithm under `bio.algorithms.graph.Quartets`.
- New runner `bio.problems.QRTProb`, wired into `bio.Main`.
- No changes to existing capabilities or shared infrastructure.
