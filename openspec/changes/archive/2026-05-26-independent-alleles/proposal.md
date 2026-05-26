## Why

Spec 14 of the project brief — "Independent Alleles" — extends the genetics subdomain with a *binomial-tail* probability over a family tree. Tom (Aa Bb) in generation 0 has descendants whose genotypes evolve under repeated crossing with Aa Bb mates. The remarkable insight: regardless of any descendant's specific genotype, when crossed with an Aa Bb mate, its offspring is Aa Bb with probability exactly `1/4`. So generation `k` contains `2^k` organisms each independently Aa Bb with `p = 1/4`, and the question reduces to a binomial tail: `P(X >= N)` where `X ~ Binomial(2^k, 1/4)`.

Beyond the Rosalind problem, this change introduces the framework's first explicit binomial-distribution computation, returning the framework's existing `Probability` type — extending the genetics subdomain with a probability-bearing algorithm to complement the expected-count algorithms from specs 5 and 11.

## What Changes

- **NEW** `IndependentAllelesProblem` domain type in `bio.domain.genetics` — `sealed abstract case class IndependentAllelesProblem(generations: Int, atLeast: Int)` with `populationSize: Long = 1L << generations` accessor, constructed via `from(generations: Int, atLeast: Int): Either[IndependentAllelesProblemError, IndependentAllelesProblem]` enforcing `generations >= 1`, `atLeast >= 1`, and `atLeast <= 2^generations`
- **NEW** `IndependentAllelesProblemError` sealed ADT in `bio.domain.genetics` — cases `NonPositiveGenerations(value: Int)`, `NonPositiveAtLeast(value: Int)`, `AtLeastExceedsPopulation(atLeast: Int, generations: Int)`
- **NEW** `IndependentAlleles.probability(problem: IndependentAllelesProblem): Probability` algorithm in `bio.algorithms.genetics` — returns `P(X >= atLeast)` for `X ~ Binomial(2^generations, 1/4)`, computed via an incremental PMF recurrence in `O(2^generations)` time and space
- **NO** modifications to existing capabilities; `MendelianInheritance` (spec 5) and `ExpectedOffspring` (spec 11) remain untouched. The three Mendelian algorithms coexist with distinct parameter types.

## Capabilities

### New Capabilities

- `independent-alleles`: The `IndependentAllelesProblem` validated parameter bundle (generations + atLeast, with `populationSize = 2^generations`), the `IndependentAllelesProblemError` ADT, and the `IndependentAlleles.probability` algorithm returning `P(at least atLeast Aa Bb organisms in generation generations | Tom is Aa Bb in gen 0, every organism mates with Aa Bb)` as a `Probability` value.

### Modified Capabilities

None.

## Impact

- New files in `bio.domain.genetics`: `IndependentAllelesProblem.scala`, `IndependentAllelesProblemError.scala`
- New file in `bio.algorithms.genetics`: `IndependentAlleles.scala`
- New test files mirroring each new source file
- No changes to existing files
- No new external dependencies
- All existing 254 tests continue passing
