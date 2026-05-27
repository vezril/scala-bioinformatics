## Why

Rosalind problem 24 ("Independent Segregation of Chromosomes") asks for an array of length `2n` where the `k`-th element is the common logarithm of the probability that two diploid siblings share at least `k` of their `2n` chromosomes, modeled as a `Bin(2n, 1/2)` random variable. This is the fourth algorithm in the `genetics` subdomain after `MendelianInheritance` (spec 5), `ExpectedOffspring` (spec 11), and `IndependentAlleles` (spec 14) — all probability-of-genetic-inheritance algorithms — and the natural extension of Mendel's law of independent assortment from individual factors to whole chromosomes.

The math: compute the binomial PMF for `Bin(2n, 1/2)` via the multiplicative recurrence `pmf[k+1] = pmf[k] × (2n − k) / (k + 1)`, accumulate upper-tail sums from right to left, then take `log10` of each. The output is `Vector(log10(P(X ≥ 1)), log10(P(X ≥ 2)), ..., log10(P(X ≥ 2n)))`. All values land in `Double` precision comfortably: at `n = 50`, the smallest PMF value is `0.5^100 ≈ 7.89 × 10^-31`, well above `Double`'s underflow threshold.

## What Changes

- Add `ChromosomePairs` in `bio.domain.genetics` as a `sealed abstract case class` validated value-type wrapping `n: Int` with `1 ≤ n ≤ 50`. The wrapper name reflects the biological meaning — `n` is the haploid chromosome-pair count, so the total chromosome count is `2n`.
- Add `ChromosomePairsError` sealed ADT with cases `NonPositive(value)` and `ExceedsMaximum(value, max)`.
- Add `IndependentSegregation.logProbs(pairs: ChromosomePairs): Vector[Double]` in `bio.algorithms.genetics` returning a length-`2n` vector where `result(k - 1) = log10(P(X ≥ k))` for `X ~ Bin(2n, 1/2)`. Total over the validated input. Computed via PMF recurrence → upper-tail accumulation → element-wise `Math.log10`.

## Capabilities

### New Capabilities
- `independent-chromosome-segregation`: The `ChromosomePairs` validated value-type wrapper and its error ADT, and the `IndependentSegregation.logProbs` algorithm computing the per-`k` common logarithm of the upper-tail probability for a `Bin(2n, 1/2)` distribution.

### Modified Capabilities
<!-- none — purely additive -->

## Impact

- New files in `bio.domain.genetics`: `ChromosomePairs.scala`, `ChromosomePairsError.scala`.
- New file in `bio.algorithms.genetics`: `IndependentSegregation.scala`.
- New test suites: `ChromosomePairsSpec`, `ChromosomePairsErrorSpec`, `IndependentSegregationSpec`.
- No new SBT dependencies.
- No breaking changes — purely additive. The `genetics` subdomain grows to host four algorithms (`MendelianInheritance`, `ExpectedOffspring`, `IndependentAlleles`, `IndependentSegregation`).
