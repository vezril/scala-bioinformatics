## Why

Rosalind problem 22 ("Counting Subsets") asks for the total number of subsets of `{1, 2, ..., n}` modulo `1,000,000`, where `1 ≤ n ≤ 1000`. The math is elementary — there are `2^n` subsets — and the modulo is needed because `2^1000` is enormous (~301 decimal digits). This is the third algorithm in the `combinatorics` subdomain after `Permutations` (spec 15) and `PartialPermutations` (spec 16), and it follows their two established patterns exactly: a validated single-parameter value type and a per-step-modulo integer computation under the same `1,000,000` modulus.

## What Changes

- Add `SubsetUniverseSize` in `bio.domain.combinatorics` as a `sealed abstract case class` validated value-type wrapping a positive integer `n` with `1 ≤ n ≤ 1000`. Mirrors `PermutationLength` (single-parameter input → single value-type wrapper).
- Add `SubsetUniverseSizeError` sealed ADT with cases `NonPositive(value: Int)` and `ExceedsMaximum(value: Int, max: Int)`.
- Add `Subsets.count(size: SubsetUniverseSize): Int` in `bio.algorithms.combinatorics` returning `2^n mod 1,000,000`. Computed via a per-step-modulo fold (mirrors the `PartialPermutations.count` idiom). Total over the validated input.

## Capabilities

### New Capabilities
- `counting-subsets`: The `SubsetUniverseSize` validated wrapper and its error ADT, and the `Subsets.count` algorithm computing `2^n mod 1,000,000` via an incremental product with per-step modulo.

### Modified Capabilities
<!-- none — purely additive -->

## Impact

- New files in `bio.domain.combinatorics`: `SubsetUniverseSize.scala`, `SubsetUniverseSizeError.scala`.
- New file in `bio.algorithms.combinatorics`: `Subsets.scala`.
- New test suites: `SubsetUniverseSizeSpec`, `SubsetUniverseSizeErrorSpec`, `SubsetsSpec`.
- No new SBT dependencies.
- No breaking changes — purely additive. The `combinatorics` subdomain grows to host three algorithms (`Permutations`, `PartialPermutations`, `Subsets`).
