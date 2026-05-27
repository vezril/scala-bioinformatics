## Why

Rosalind problem 26 ("The Wright-Fisher Model of Genetic Drift") asks for the probability that after `g` generations of the Wright-Fisher model — starting from `m` dominant alleles in a population of `N` diploid individuals (`2N` chromosomes total) — at least `k` copies of the recessive allele will be present. Each generation re-samples all `2N` chromosomes independently, with the dominant-allele probability proportional to the current dominant count.

This is the sixth `genetics` algorithm (after `MendelianInheritance`, `ExpectedOffspring`, `IndependentAlleles`, `IndependentSegregation`, `DiseaseCarriers`) and the framework's first **Markov-chain** computation: the state space is the dominant-allele count `0..2N`, transitions are `Bin(2N, d/2N)` PMFs, and after `g` generations we sum the probability mass over states corresponding to "at least `k` recessive alleles".

## What Changes

- Add `WrightFisherProblem` in `bio.domain.genetics` as a `sealed abstract case class` validated parameter bundle wrapping four positive integers `n, m, g, k`. The smart constructor enforces `1 ≤ n ≤ 7`, `1 ≤ m ≤ 2n`, `1 ≤ g ≤ 6`, and `1 ≤ k ≤ 2n`.
- Add `WrightFisherProblemError` sealed ADT with 8 cases: `NonPositiveN`, `NExceedsMaximum`, `NonPositiveM`, `MExceedsTotalAlleles`, `NonPositiveG`, `GExceedsMaximum`, `NonPositiveK`, `KExceedsTotalAlleles`.
- Add `WrightFisher.atLeast(problem: WrightFisherProblem): Probability` in `bio.algorithms.genetics` returning the at-least-`k`-recessive tail probability after `g` Wright-Fisher generations starting from `m` dominant alleles. Total over the validated input.

## Capabilities

### New Capabilities
- `wright-fisher-genetic-drift`: The `WrightFisherProblem` validated parameter bundle, the `WrightFisherProblemError` ADT, and the `WrightFisher.atLeast` algorithm computing the Wright-Fisher tail-probability after `g` generations via repeated vector-by-transition-matrix multiplication.

### Modified Capabilities
<!-- none — purely additive -->

## Impact

- New files in `bio.domain.genetics`: `WrightFisherProblem.scala`, `WrightFisherProblemError.scala`.
- New file in `bio.algorithms.genetics`: `WrightFisher.scala`.
- New test suites: `WrightFisherProblemSpec`, `WrightFisherProblemErrorSpec`, `WrightFisherSpec`.
- No new SBT dependencies.
- No breaking changes — purely additive. The `genetics` subdomain grows to host six algorithms.
