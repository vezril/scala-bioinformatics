## Why

Rosalind problem RNAS ("Wobble Bonding and RNA Secondary Structures") counts every valid noncrossing matching in the bonding graph of an RNA string, where edges connect `A`–`U`, `C`–`G`, **and** `U`–`G` (wobble), an edge may only join positions `j`, `k` with `k ≥ j+4`, and partial matchings (including the empty one) all count. It is the wobble-and-min-loop variant of MOTZ, and its counts overflow `Long`, so the answer is exact `BigInt`.

## What Changes

- Introduce a validated `WobbleMatchingProblem` domain type wrapping an `RnaString` of length ≤ 200 bp.
- Introduce a `WobbleMatchingProblemError` ADT for the new invariant (sequence too long).
- Introduce a `WobbleMatchings` result type holding the exact count (`BigInt`), with a `format` returning it.
- Introduce a `WobbleMatching` algorithm — an O(n³) interval dynamic program counting all valid noncrossing matchings, allowing wobble base pairs and enforcing the `k ≥ j+4` minimum separation, in exact `BigInt` arithmetic (no modulo).
- Add an `RNASProb` runner reading the RNA string from `rnas_data.txt` and printing the count through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.RnaString`.

## Capabilities

### New Capabilities
- `wobble-rna-matchings`: Count all distinct valid noncrossing matchings (with wobble `U`–`G` pairing and a `k ≥ j+4` separation rule) in the bonding graph of an RNA string (Rosalind RNAS).

### Modified Capabilities
<!-- None. RNAS adds a new capability and reuses RnaString without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.nucleic`): `WobbleMatchingProblem`, `WobbleMatchingProblemError`, `WobbleMatchings` (result).
- **New algorithm** (`bio.algorithms.nucleic.WobbleMatching`) — a `BigInt` interval-DP count, mirroring `MotzkinMatching` with wobble pairing and the `k ≥ j+4` rule.
- **New runner** (`bio.problems.RNASProb`) reading `src/main/scala/resources/rnas_data.txt`.
- **Reused, unchanged**: `bio.domain.nucleic.RnaString`.
- **Tests**: new specs under `bio.domain.nucleic` and `bio.algorithms.nucleic`. No existing tests change.
