## Why

Rosalind problem ROOT ("Counting Rooted Binary Trees") asks for `B(n)`, the number of distinct rooted binary trees on `n` labeled taxa, modulo 1,000,000. It is the direct rooted counterpart of CUNR ("Counting Unrooted Binary Trees", already implemented): where CUNR counts `(2n−5)!!` unrooted trees, ROOT counts `(2n−3)!!` rooted trees. It extends the combinatorics counting family with one more closed-form modular product.

## What Changes

- Introduce a validated `RootedTreeLeafCount` domain type wrapping `n` (positive integer ≤ 1000), mirroring CUNR's `LeafCount`.
- Introduce a `RootedTreeLeafCountError` ADT for the invariants (non-positive, exceeds maximum).
- Introduce a `RootedBinaryTrees` algorithm computing `B(n) = (2n−3)!! mod 1,000,000` via a per-step-modulo product over the odd factors `3, 5, …, (2n−3)`.
- Add a `ROOTProb` runner reading `n` from `root_data.txt` and printing `B(n) mod 1,000,000` through `IO`.
- Reuse existing infrastructure: none beyond the standard library (the algorithm mirrors CUNR's `UnrootedBinaryTrees.count`).

## Capabilities

### New Capabilities
- `counting-rooted-binary-trees`: Compute `B(n) = (2n−3)!! mod 1,000,000`, the number of distinct rooted binary trees on `n` labeled taxa (Rosalind ROOT).

### Modified Capabilities
<!-- None. ROOT adds a new capability and does not change any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.combinatorics`): `RootedTreeLeafCount`, `RootedTreeLeafCountError`.
- **New algorithm** (`bio.algorithms.combinatorics.RootedBinaryTrees`) returning the count as an `Int` (mirroring CUNR's `UnrootedBinaryTrees.count`).
- **New runner** (`bio.problems.ROOTProb`) reading `src/main/scala/resources/root_data.txt`.
- **Tests**: new specs under `bio.domain.combinatorics` and `bio.algorithms.combinatorics`. No existing tests change.
