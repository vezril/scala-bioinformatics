## Why

Rosalind problem SETO ("Introduction to Set Operations") asks, given a universe size `n`
and two subsets `A` and `B` of `{1, …, n}`, for the six derived sets `A∪B`, `A∩B`,
`A−B`, `B−A`, `Aᶜ`, and `Bᶜ` (complements taken with respect to `{1, …, n}`). It rounds
out the combinatorics cluster of the suite (alongside SSET) with the foundational
set-algebra primitives.

## What Changes

- Add a validated **set-operations** capability that ingests a universe size `n`
  (`1 ≤ n ≤ 20000`) and two subsets `A`, `B` of `{1, …, n}`, then produces the six
  result sets.
- Introduce a `SetOperationsProblem` domain bundle (smart-constructed, invariant-bearing)
  with a dedicated `SetOperationsProblemError` ADT covering a non-positive universe, a
  universe over the maximum, and elements outside `{1, …, n}`.
- Introduce a `SetOperationsResult` ADT holding the six sets with a `format` method that
  renders each on its own line as `{e1, e2, …}` in ascending order.
- Add a pure `SetOperations.compute` algorithm that derives the six sets via standard
  set algebra over the universe `{1, …, n}`.
- Add a `SETOProb` IO runner that reads `seto_data.txt` (an `n` line followed by two
  brace-delimited set lines) and prints the six result lines.

## Capabilities

### New Capabilities
- `set-operations`: validate a universe size and two subsets, then compute union,
  intersection, both directed differences, and both complements.

### Modified Capabilities
<!-- None: this introduces a new capability in the existing combinatorics package and adds no requirement changes elsewhere. -->

## Impact

- New domain: `bio.domain.combinatorics.SetOperationsProblem`,
  `SetOperationsProblemError`, `SetOperationsResult`.
- New algorithm: `bio.algorithms.combinatorics.SetOperations`.
- New runner: `bio.problems.SETOProb` (+ `Main.scala` wiring, `resources/seto_data.txt`).
- No changes to existing capabilities or shared types.
