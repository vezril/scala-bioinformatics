## Why

Spec 16 of the project brief — "Partial Permutations" — counts the number of ordered selections of `k` items from `n` distinct objects: `P(n, k) = n × (n-1) × ... × (n-k+1) = n! / (n-k)!`. Result is returned modulo `1,000,000`. This sits naturally beside spec 15's `Permutations` (which enumerates *all* full permutations) — same `combinatorics` subdomain, but counting instead of enumerating, and with a cap-driven modular result. It's the framework's second modular-arithmetic algorithm after `InferMRna`.

## What Changes

- **NEW** `PartialPermutationProblem` domain type in `bio.domain.combinatorics` — `sealed abstract case class PartialPermutationProblem(n: Int, k: Int)` with smart constructor `from(n, k): Either[PartialPermutationProblemError, PartialPermutationProblem]` enforcing `1 <= n <= 100`, `1 <= k <= 10`, and `k <= n`
- **NEW** `PartialPermutationProblemError` sealed ADT in `bio.domain.combinatorics` — cases `NonPositiveN(value: Int)`, `NExceedsMaximum(value: Int, max: Int)`, `NonPositiveK(value: Int)`, `KExceedsMaximum(value: Int, max: Int)`, `KExceedsN(k: Int, n: Int)`
- **NEW** `PartialPermutations.count(problem: PartialPermutationProblem): Int` algorithm in `bio.algorithms.combinatorics` — returns `P(n, k) mod 1_000_000` via an incremental product with per-step modulo, in `O(k)` time and `O(1)` space
- **NO** modifications to existing capabilities. The `combinatorics` subdomain established by spec 15 grows from two files per package to three.

## Capabilities

### New Capabilities

- `partial-permutations`: The `PartialPermutationProblem` validated parameter bundle (n + k with cross-constraint `k <= n`), the `PartialPermutationProblemError` ADT, and the `PartialPermutations.count(problem): Int` algorithm computing `P(n, k) mod 1,000,000` for Rosalind's bounds `n ∈ [1, 100]` and `k ∈ [1, 10]`.

### Modified Capabilities

None.

## Impact

- New files in `bio.domain.combinatorics`: `PartialPermutationProblem.scala`, `PartialPermutationProblemError.scala`
- New file in `bio.algorithms.combinatorics`: `PartialPermutations.scala`
- New test files mirroring each new source file
- No changes to existing files
- No new external dependencies
- All existing 286 tests continue passing
