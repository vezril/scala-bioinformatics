## Why

Rosalind problem 25 ("Counting Disease Carriers") asks for the carrier probability `B[k]` of a recessive allele given the homozygous-recessive proportion `A[k]` under Hardy-Weinberg equilibrium. Math: `A[k] = q²` (allele frequency squared), `q = √A[k]`, `B[k] = 1 − (1 − q)² = 2q − A[k]`. Element-wise transform over an input vector — the cleanest algorithm in the `genetics` subdomain so far.

This becomes the fifth `genetics` algorithm after `MendelianInheritance` (spec 5), `ExpectedOffspring` (spec 11), `IndependentAlleles` (spec 14), and `IndependentSegregation` (spec 24). Like `IndependentSegregation`, it operates on a vector of probabilities and produces a vector of derived values — but here both sides are `Vector[Probability]`, because the output is mathematically guaranteed to lie in `[0, 1]` and benefits from the type-system anchor.

## What Changes

- Add `DiseaseCarriers.frequencies(homozygousRecessive: Vector[Probability]): Vector[Probability]` in `bio.algorithms.genetics`. Element-wise computes `2 * sqrt(p.value) - p.value` and wraps each result as a `Probability` via `Probability.unsafeFrom` (the math is structurally constrained to `[0, 1]`).
- No new domain types: the input element type (`Probability`) already exists, and there is no collection-level invariant to enforce (Rosalind specifies no length bound; an empty input vector produces an empty output).
- No new error type: the algorithm is total — every valid input produces a defined output.

## Capabilities

### New Capabilities
- `disease-carrier-frequencies`: The `DiseaseCarriers.frequencies` algorithm computing, under Hardy-Weinberg equilibrium, the carrier probability for each factor given its homozygous-recessive proportion.

### Modified Capabilities
<!-- none — purely additive -->

## Impact

- New file in `bio.algorithms.genetics`: `DiseaseCarriers.scala`.
- New test suite: `DiseaseCarriersSpec`.
- No new SBT dependencies.
- No new domain types — reuses the existing `Probability` from `bio.domain.stats`.
- No breaking changes — purely additive. The `genetics` subdomain grows to host five algorithms.
