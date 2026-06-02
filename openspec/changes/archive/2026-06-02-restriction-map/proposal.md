## Why

Rosalind problem PDPL ("Creating a Restriction Map") is the classic Turnpike / Partial Digest problem: given the multiset `L` of all pairwise distances between restriction sites on a chromosome, reconstruct a set `X` of `n` nonnegative positions whose difference multiset `ΔX` equals `L`. It extends the project's combinatorial-reconstruction capabilities with a backtracking set-from-differences solver — the first problem that recovers point positions from pairwise gaps.

## What Changes

- Introduce a validated `RestrictionMapProblem` domain type wrapping the distance multiset `L` (a `Vector[Int]` of positive integers whose size is a triangular number `C(n,2)`).
- Introduce a `RestrictionMapProblemError` ADT for the new invariants (size not of the form `n(n-1)/2`, non-positive distance).
- Introduce a `RestrictionMap` result type holding the reconstructed positions (`Vector[Int]`, ascending), with a Rosalind-style space-separated `format`.
- Introduce a `RestrictionMapConstruction` algorithm — the Turnpike backtracking reconstruction — returning `Option[RestrictionMap]` (`None` when no set realises `L`).
- Add a `PDPLProb` runner reading `L` from `pdpl_data.txt` and printing a valid `X` through `IO`.
- Reuse existing infrastructure: none beyond the standard library (PDPL operates on integer multisets, not sequences).

## Capabilities

### New Capabilities
- `restriction-map`: Reconstruct a set `X` of `n` nonnegative integers whose difference multiset equals a given multiset `L` of `C(n,2)` pairwise distances (Rosalind PDPL / Turnpike problem).

### Modified Capabilities
<!-- None. PDPL adds a new capability and does not change any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.combinatorics`): `RestrictionMapProblem`, `RestrictionMapProblemError`, `RestrictionMap` (result).
- **New algorithm** (`bio.algorithms.combinatorics.RestrictionMapConstruction`).
- **New runner** (`bio.problems.PDPLProb`) reading `src/main/scala/resources/pdpl_data.txt`.
- **Tests**: new specs under `bio.domain.combinatorics` and `bio.algorithms.combinatorics`. No existing tests change.
