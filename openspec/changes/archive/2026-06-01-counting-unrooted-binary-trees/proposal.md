## Why

Rosalind spec 54 (CUNR — "Counting Unrooted Binary Trees") asks for the number
of distinct unrooted binary trees on `n` labeled leaves, modulo 1,000,000. This
count is the double factorial `b(n) = (2n − 5)!! = 1·3·5···(2n − 5)` for `n ≥ 3`
(and `1` for `n ≤ 2`). It is a new combinatorics capability — structurally a
twin of the existing SSET ("Counting Subsets"): a single validated positive
integer `≤ 1000` mapped to a value modulo 1,000,000 via a per-step-modulo
product.

## What Changes

- Add a validated value-type wrapper for the leaf count `n` (`1 ≤ n ≤ 1000`)
  with a smart constructor and a sealed error ADT.
- Add an algorithm computing `b(n) = (2n − 5)!! mod 1,000,000` via an
  incremental product with per-step modulo (`Int`-safe at the `n ≤ 1000` cap).
- Add a problem runner and wire it into `Main`.
- Reproduce the canonical sample `5 → 15`.

## Capabilities

### New Capabilities
- `counting-unrooted-binary-trees`: A validated leaf-count value type
  (`1 ≤ n ≤ 1000`), its error ADT, and an algorithm computing the number of
  distinct unrooted binary trees on `n` labeled leaves modulo 1,000,000.

### Modified Capabilities
<!-- None: this is a standalone new capability. -->

## Impact

- New domain types under `bio.domain.combinatorics`
  (`LeafCount`, `LeafCountError`).
- New algorithm under `bio.algorithms.combinatorics.UnrootedBinaryTrees`.
- New runner `bio.problems.CUNRProb`, wired into `bio.Main`.
- No changes to existing capabilities or shared infrastructure; follows the
  SSET single-value-type + per-step-modulo pattern.
