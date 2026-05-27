## Why

Rosalind problem 23 ("Introduction to Alternative Splicing") asks for the sum of binomial coefficients `Σ_{k=m}^{n} C(n, k) mod 1,000,000` for `0 ≤ m ≤ n ≤ 2000`. This is the fourth algorithm in the `combinatorics` subdomain after `Permutations` (spec 15), `PartialPermutations` (spec 16), and `Subsets` (spec 22), and the first one whose lower bound on the integer parameters is `0` rather than `1` (both `m = 0` and `n = 0` are valid inputs).

The math is a sum of binomial coefficients, which is most cleanly computed via a single modular Pascal's-triangle row build (`O(n²)` integer additions, all `mod 1,000,000`). Modular inverse / Fermat's little theorem doesn't apply here because `1,000,000 = 2^6 × 5^6` isn't prime, so the row-by-row recurrence is the natural choice.

## What Changes

- Add `CombinationSumProblem` in `bio.domain.combinatorics` as a `sealed abstract case class` validated parameter bundle wrapping `n: Int` and `m: Int`. The smart constructor enforces `0 ≤ n ≤ 2000`, `0 ≤ m`, and the cross-constraint `m ≤ n`.
- Add `CombinationSumProblemError` sealed ADT with cases `NegativeN(value)`, `NExceedsMaximum(value, max)`, `NegativeM(value)`, and `MExceedsN(m, n)`.
- Add `Combinations.sumFrom(problem): Int` in `bio.algorithms.combinatorics` returning `Σ_{k=m}^{n} C(n, k) mod 1,000,000` via a single modular Pascal's-triangle row build. Total over the validated input.

## Capabilities

### New Capabilities
- `alternative-splicing-counting`: The `CombinationSumProblem` validated parameter bundle, the `CombinationSumProblemError` ADT, and the `Combinations.sumFrom` algorithm computing the modular tail-sum of binomial coefficients from row `n` of Pascal's triangle.

### Modified Capabilities
<!-- none — purely additive -->

## Impact

- New files in `bio.domain.combinatorics`: `CombinationSumProblem.scala`, `CombinationSumProblemError.scala`.
- New file in `bio.algorithms.combinatorics`: `Combinations.scala`.
- New test suites: `CombinationSumProblemSpec`, `CombinationSumProblemErrorSpec`, `CombinationsSpec`.
- No new SBT dependencies.
- No breaking changes — purely additive. The `combinatorics` subdomain grows to host four algorithms (`Permutations`, `PartialPermutations`, `Subsets`, `Combinations`).
