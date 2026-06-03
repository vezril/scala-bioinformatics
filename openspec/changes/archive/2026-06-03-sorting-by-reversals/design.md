## Context

SORT ("Sorting by Reversals") extends REAR: for two length-10 permutations π and γ it returns `d_rev(π, γ)` **and** a concrete sequence of reversals that sorts π into γ, each reversal encoded by the 1-based endpoints `[i, j]` of the interval it inverts. The reversal graph is the same as REAR's (undirected, unit-weight, out-degree `C(10,2) = 45`), so the distance comes from the same bidirectional BFS; the new work is *path reconstruction*.

A crucial observation makes target-normalisation safe: a reversal is a **positional** operation. If applying interval `[i, j]` to a permutation `p` yields `p'`, then applying the *same* `[i, j]` to any relabelling `f(p)` yields `f(p')`. Therefore the sequence of position-intervals that sorts the relabelled source into the identity is exactly the sequence that sorts π into γ. We relabel γ to the identity (and π through γ⁻¹), search, and report the intervals unchanged.

The input bundle is identical to REAR's, so `ReversalDistanceProblem` (two equal-length `Permutation`s, length ≤ 10) and its `ReversalDistanceProblemError` are reused rather than duplicated.

## Goals / Non-Goals

**Goals:**
- A `Reversal` domain type (1-based `from < to`) with `format`.
- A `ReversalSorting` result type (distance + ordered `Reversal`s) with `format`.
- Pure, total `ReversalSortingSearch.sort(problem): ReversalSorting`.
- A returned reversal sequence whose length equals the distance and which actually sorts π into γ.

**Non-Goals:**
- A *canonical* reversal sequence — Rosalind accepts any valid one.
- The Hannenhalli–Pevzner signed theory — bidirectional BFS reconstruction is exact and sufficient at `n = 10`.
- A new input/error type — `ReversalDistanceProblem`/`ReversalDistanceProblemError` are reused.

## Decisions

**1. Bidirectional BFS with parent pointers on both frontiers.**
Relabel so γ becomes the identity (relabel π through γ⁻¹) → `start`; `goal` is the identity. Run two BFS frontiers (from `start`, from `goal`), each step expanding the smaller frontier; for every state, generate all 45 reversal-neighbours, recording for each newly-seen state its parent (the predecessor state and the interval used). When a generated neighbour already lives in the *other* side's visited map, that node is a meeting point; complete the level and keep the meeting with minimum total depth (optimality, as in REAR).

**2. Stitching the two halves.**
From the meeting node `m`: walking forward-parents `m → … → start` and reversing yields the intervals `F` that take `start` to `m`. Walking backward-parents `m → … → goal` yields the intervals `B` that take `m` to `goal` (a reversal is its own inverse, so the backward parent interval applied to a node moves it one step toward `goal`). The full sorting sequence is `F ++ B`, of length `|F| + |B| = d_rev`. These positional intervals (converted to 1-based) are the answer for the original π → γ.

**3. Pack states into a `Long`; store parents compactly.**
As in REAR, permutations (`n ≤ 10`, values `1..10`) pack into a `Long` (4 bits/element). Each side keeps `mutable.LongMap`s: depth, parent-state, and the packed interval used to reach the state. Intervals pack as a small `Int` (`i * n + j`, 0-based) and decode to 1-based `Reversal`s only at the end.

**4. Imperative BFS internals, pure signature (graph-search precedent).**
Frontiers, visited/parent maps, and array reversals use `var`/`while`/`Array`/`mutable.LongMap`; the public `sort` signature is pure and total, returning a `ReversalSorting`.

**5. Domain types.**
`Reversal(from: Int, to: Int)` is a plain `final case class` (algorithm-produced, always valid) with `format = s"$from $to"`. `ReversalSorting(distance: Int, reversals: Vector[Reversal])` has `format` = the distance on the first line followed by each reversal on its own line. The identity case (`start == goal`) yields distance 0 and an empty reversal list (formatting to just `"0"`).

**6. Naming and placement.**
`Reversal` and `ReversalSorting` live in `bio.domain.combinatorics` (beside `Permutation`/`ReversalDistance`); the algorithm `ReversalSortingSearch.sort` in `bio.algorithms.combinatorics`. Result (`ReversalSorting`) and algorithm (`ReversalSortingSearch`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Search cost]** → identical to REAR; bidirectional BFS handles `n = 10` (distance up to 9) in well under a second, bounded by `10!` reachable states.
- **[Reconstruction correctness]** → the relabelling-invariance of positional reversals guarantees the relabelled-space intervals sort the original pair; tests verify this empirically by *applying* the returned reversals to π and checking the result equals γ (rather than asserting specific intervals, since any valid sequence is accepted).
- **[Tie-breaking]** → many sorting sequences of length `d_rev` exist; the algorithm returns one. Scenarios assert the *length* equals the distance and that the sequence sorts π into γ.
- **[Edge cases]** → identical permutations → distance 0, no reversals; one reversal apart → distance 1, a single sorting reversal; covered by scenarios.
- **[Imperative internals]** → confined to the BFS core; the public signature stays pure/total.
