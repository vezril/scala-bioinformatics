## Why

Rosalind problem SORT ("Sorting by Reversals") is the constructive companion to REAR: given two length-10 permutations π and γ, return the reversal distance `d_rev(π, γ)` **and** an explicit collection of reversals (each encoded by the 1-based endpoints of the interval it inverts) that, applied successively to π, yields γ. The same bidirectional BFS used for REAR finds the distance; SORT additionally reconstructs the *path*, so it tracks parent pointers on both frontiers and stitches the forward and backward halves into a reversal sequence. Position-based reversals are invariant under value relabelling, so the intervals found in the target-normalised search space are the answer verbatim.

## What Changes

- Introduce a `Reversal` domain type encoding a single reversal by its 1-based interval endpoints (`from < to`), with a `format`.
- Introduce a `ReversalSorting` result type holding the distance and the ordered collection of reversals, with a `format` (distance on line 1, one reversal per following line).
- Introduce a `ReversalSortingSearch` algorithm computing both the distance and a sorting reversal sequence via bidirectional BFS with parent reconstruction.
- Add a `SORTProb` runner reading the two permutations from `sort_data.txt` and printing the distance and reversals through `IO`.
- Reuse existing infrastructure: `bio.domain.combinatorics.Permutation` and `bio.domain.combinatorics.ReversalDistanceProblem` (the same "two equal-length permutations, ≤ 10" input bundle introduced for REAR) with its `ReversalDistanceProblemError`.

## Capabilities

### New Capabilities
- `sorting-by-reversals`: Given two equal-length permutations, return the reversal distance together with an explicit sequence of interval reversals that sorts the first into the second (Rosalind SORT).

### Modified Capabilities
<!-- None. SORT adds a new capability and reuses Permutation/ReversalDistanceProblem without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.combinatorics`): `Reversal`, `ReversalSorting` (result).
- **New algorithm** (`bio.algorithms.combinatorics.ReversalSortingSearch`) — bidirectional BFS with parent pointers on both frontiers, stitching the forward and backward halves into a reversal sequence; permutations packed into a `Long` key.
- **New runner** (`bio.problems.SORTProb`) reading `src/main/scala/resources/sort_data.txt`.
- **Reused, unchanged**: `bio.domain.combinatorics.Permutation`, `bio.domain.combinatorics.ReversalDistanceProblem`, `bio.domain.combinatorics.ReversalDistanceProblemError`.
- **Tests**: new specs under `bio.domain.combinatorics` and `bio.algorithms.combinatorics`. No existing tests change.
