## Context

REAR ("Reversal Distance") computes `d_rev(π, σ)`, the minimum number of interval reversals transforming π into σ, for at most 5 pairs of length-10 permutations. A *reversal* picks an interval `[i, j]` (`i < j`) and reverses that contiguous block. The reversal graph is undirected (applying the same reversal twice is the identity), unit-weight, with `10!` ≈ 3.6 M nodes and out-degree `C(10,2) = 45`. Distances reach up to `n − 1 = 9`. A single-source BFS to depth 9 is infeasible (`45⁹`), but **bidirectional BFS** meeting in the middle keeps each side near depth ~4–5 and is bounded overall by the `10!` reachable states.

Reversal distance is invariant under relabelling, so the target can be normalised to the identity: build `σ⁻¹` (value → 1-based position) and apply it to π, giving `π'` with `d_rev(π, σ) = d_rev(π', id)`. The search then runs between `π'` and the identity permutation. The framework's `Permutation` type (from LGIS) is reused for the validated inputs.

## Goals / Non-Goals

**Goals:**
- Validated `ReversalDistanceProblem(source, target)` (equal length, ≤ 10) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `ReversalDistanceSearch.distance(problem): ReversalDistance`.
- Result type with `format: String`.
- Exact distances matching the canonical sample (`9 4 5 7 0`).

**Non-Goals:**
- Producing the actual reversal *sequence* (only the distance is required).
- The O(n) Hannenhalli–Pevzner signed-reversal theory — bidirectional BFS is exact and sufficient at `n = 10`.
- Permutations longer than 10 (the BFS would blow up; rejected by validation).

## Decisions

**1. Bidirectional BFS over the reversal graph.**
Normalise the target to the identity (relabel π through `σ⁻¹`). If `π' == id`, distance is 0. Otherwise run two BFS frontiers — one from `π'`, one from the identity — each step expanding the side with the *smaller* current frontier by one level: for every state, generate all 45 reversal-neighbours; if a neighbour is already in the *other* side's visited map, a meeting is found. The distance is `min over meetings this level of (expandingDepth + otherSideDepth)`. Completing the level before returning (taking the minimum contact) guarantees the optimal `d_rev`, since `dS + dG` only grows across levels.

**2. Pack each permutation into a `Long` key.**
With `n ≤ 10` and values `1..10`, each value fits in 4 bits, so a whole permutation packs into ≤ 40 bits of a `Long`. Visited maps are `mutable.LongMap[Int]` (state → depth) for fast, low-overhead lookup; neighbour generation decodes to an `Array[Int]`, reverses an interval, and re-encodes. This keeps the multi-million-state search memory- and time-efficient.

**3. Imperative BFS internals, pure signature (graph-search precedent).**
The frontiers, visited maps, and array reversals use `var`/`while`/`Array`/`mutable.LongMap`, consistent with the alignment/DP/graph-search families; the public `distance` signature is pure and total, returning a `ReversalDistance`.

**4. Validation and first-failure-wins ordering.**
`ReversalDistanceProblem.from(source, target)` enforces, in order: equal lengths (`LengthMismatch(sourceLength, targetLength)`), then length `≤ 10` (`LengthExceedsMax(length, 10)`). Both inputs are already valid permutations of `1..n` by virtue of being `Permutation`s. Equal empty permutations are accepted (distance 0).

**5. Result rendering.**
`ReversalDistance(distance: Int)` with `format = distance.toString`. The runner computes one `ReversalDistance` per pair and joins them with single spaces on one line (the Rosalind output shape).

**6. Naming and placement.**
`ReversalDistanceProblem`, `ReversalDistanceProblemError`, and the `ReversalDistance` result live in `bio.domain.combinatorics` (beside `Permutation`); the algorithm `ReversalDistanceSearch.distance` in `bio.algorithms.combinatorics`. Result (`ReversalDistance`) and algorithm (`ReversalDistanceSearch`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Search cost at distance 9]** → the hardest sample pair has `d_rev = 9`; bidirectional BFS keeps each side to depth ~4–5 (`≈ 45⁴`–`45⁵` before dedup, bounded by `10!`), completing in well under the per-pair budget. Single-source BFS would be intractable; the `Long` packing keeps per-state overhead minimal.
- **[Memory]** → visited maps may hold a few million `Long → Int` entries for the deepest pair; `LongMap` keeps this compact, and only one pair is searched at a time.
- **[Correctness of first-meeting]** → returning the per-level *minimum* contact (not the first) guarantees optimality under the smaller-frontier expansion; covered by the canonical-sample scenario (`9 4 5 7 0`) and a single-reversal scenario.
- **[Edge cases]** → identical permutations → 0; one reversal apart → 1; equal empty permutations → 0; covered by scenarios.
- **[Imperative internals]** → confined to the BFS core; the public signature stays pure/total.
