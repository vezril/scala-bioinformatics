## Why

Rosalind problem REAR ("Reversal Distance") asks for the reversal distance `d_rev(π, σ)` — the minimum number of interval reversals needed to transform permutation π into σ — for at most 5 pairs of length-10 permutations. With permutations this short the exact distance is found by **bidirectional breadth-first search** over the reversal graph (each node a permutation, each edge a single reversal), which meets in the middle and avoids enumerating the full `10!` state space from one side. The framework already has a validated `Permutation` type (from LGIS) to reuse for the inputs.

## What Changes

- Introduce a validated `ReversalDistanceProblem` domain type wrapping a `source` and `target` `Permutation` of equal length (≤ 10, the cap that keeps the BFS tractable).
- Introduce a `ReversalDistanceProblemError` ADT for the new invariants (length mismatch; length exceeds the BFS-tractable cap).
- Introduce a `ReversalDistance` result type holding the integer distance, with a `format`.
- Introduce a `ReversalDistanceSearch` algorithm computing `d_rev` via bidirectional BFS (after relabelling the target to the identity), expanding the smaller frontier and meeting in the middle.
- Add a `REARProb` runner reading the blank-line-separated permutation pairs from `rear_data.txt` and printing the per-pair distances space-separated on one line through `IO`.
- Reuse existing infrastructure: `bio.domain.combinatorics.Permutation`.

## Capabilities

### New Capabilities
- `reversal-distance`: Compute the reversal distance (minimum number of interval reversals) between two equal-length permutations via bidirectional BFS (Rosalind REAR).

### Modified Capabilities
<!-- None. REAR adds a new capability and reuses Permutation without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.combinatorics`): `ReversalDistanceProblem`, `ReversalDistanceProblemError`, `ReversalDistance` (result).
- **New algorithm** (`bio.algorithms.combinatorics.ReversalDistanceSearch`) — bidirectional BFS over the reversal graph, permutations packed into a `Long` key for fast visited-set lookup.
- **New runner** (`bio.problems.REARProb`) reading `src/main/scala/resources/rear_data.txt`.
- **Reused, unchanged**: `bio.domain.combinatorics.Permutation`.
- **Tests**: new specs under `bio.domain.combinatorics` and `bio.algorithms.combinatorics`. No existing tests change.
