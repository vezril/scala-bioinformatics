## Context

The suite already models combinatorics problems with the same shape: a validated
universe size `n` over `{1, …, n}` (see `bio.domain.combinatorics.SubsetUniverseSize`
for SSET). SETO extends this with two explicit subsets and standard set algebra. The
result is six sets; Rosalind grades set membership, not element order, so the canonical
sample's complement lines are not in sorted order.

## Goals / Non-Goals

**Goals:**
- A validated `SetOperationsProblem` bundle (`n`, `A`, `B`) with first-failure-wins
  validation, following the established `sealed abstract case class` + smart-constructor
  pattern (no leaked `apply`/`copy`).
- A pure `SetOperations.compute(problem): SetOperationsResult` producing the six sets.
- Deterministic, testable output rendering.

**Non-Goals:**
- Arbitrary element domains — elements are integers drawn from `{1, …, n}`.
- Preserving the sample's incidental element ordering (the grader is order-insensitive).

## Decisions

**Decision 1 — Render every set in ascending order.**
The result type stores each set as a `SortedSet[Int]` and `format` renders it as
`{e1, e2, …}` (comma-space separated, ascending; empty set → `{}`). *Rationale:*
deterministic and unit-testable; Rosalind accepts any element order, so ascending is a
safe canonical choice. *Alternative considered:* reproducing the sample's exact
(unsorted) complement order — impossible to derive from set logic and not required.

**Decision 2 — Validate `n` then element ranges, first failure wins.**
`SetOperationsProblem.from(n, a, b)` checks, in order: `n ≥ 1`
(`NonPositiveUniverse`), `n ≤ 20000` (`ExceedsMaximum`), every element of `A` within
`{1, …, n}` (`ElementOutOfRange("A", value, n)`), then every element of `B`
(`ElementOutOfRange("B", value, n)`). For determinism the reported out-of-range element
is the smallest offending value in the offending set. Consistent with
`SubsetUniverseSize`'s lower-then-upper ordering.

**Decision 3 — Place the capability in the `combinatorics` package.**
Domain in `bio.domain.combinatorics`, algorithm in `bio.algorithms.combinatorics`,
alongside `SubsetUniverseSize` / `Subsets` (SSET), which shares the `{1, …, n}` universe
concept.

**Decision 4 — Set algebra over the explicit universe.**
`universe = SortedSet(1 to n: _*)`; `union = a | b`, `intersection = a & b`,
`aMinusB = a &~ b`, `bMinusA = b &~ a`, `aComplement = universe &~ a`,
`bComplement = universe &~ b`. Pure, total, no side effects.

**Decision 5 — Runner parses brace-delimited set lines.**
`SETOProb` reads `seto_data.txt`: line 1 is `n`, lines 2–3 are sets formatted `{1, 2, 3}`.
Parsing strips the braces, splits on commas, trims, and parses integers; `{}` parses to
the empty set. Parse/validation failures are reported via the `Either` chain, mirroring
the existing runners.

## Risks / Trade-offs

- **Output order differs from the sample's complement lines** → acceptable: Rosalind is
  order-insensitive for set answers; ascending order is documented and tested.
- **Large `n` (up to 20000)** → trivial: integer sets of at most 20000 elements; rendering
  is `O(n)`. No performance concern.
