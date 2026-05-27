## Why

Rosalind problem 28 ("The Founder Effect and Genetic Drift") asks for an `m × k` matrix where `B[i, j]` is `log10(P(after i generations of Wright-Fisher, the recessive allele for factor j is no longer present))` — i.e., the log-probability of *dominant fixation* by generation `i`, computed independently for each factor. The math reuses spec 26's Wright-Fisher Markov chain: start from a one-hot at state `2N − A[j]` (initial dominant count), apply the transition `g` times for each `g = 1..m`, and at each step record `log10` of the mass at the all-dominant absorbing state `2N`.

This becomes the **fourth method** on the existing `WrightFisher` object (after `atLeast` from spec 26 and `expectedFrequencies` from spec 27), demonstrating that the framework's multi-method algorithm-object pattern scales cleanly past two siblings. It is also the seventh `genetics` algorithm overall — the family continues to be the framework's most populous subdomain.

## What Changes

- Add `WrightFisherFixationProblem` in `bio.domain.genetics` as a `sealed abstract case class` validated parameter bundle wrapping `n: Int`, `m: Int`, `recessiveCounts: Vector[Int]`. The smart constructor enforces `1 ≤ n ≤ 100`, `1 ≤ m ≤ 100`, `|recessiveCounts| ≤ 100`, and `0 ≤ recessiveCounts(j) ≤ 2n` for every element.
- Add `WrightFisherFixationProblemError` sealed ADT with cases `NonPositiveN`, `NExceedsMaximum`, `NonPositiveM`, `MExceedsMaximum`, `TooManyRecessiveCounts`, and `RecessiveCountOutOfRange(index, value, max)`.
- Extend the existing `WrightFisher` object in `bio.algorithms.genetics` with a new method `fixationLogProbs(problem: WrightFisherFixationProblem): Vector[Vector[Double]]` — returns an `m × k` matrix (outer vector = generations `1..m`, inner vectors = one value per factor). Total over the validated input. Reuses the existing `binomialPmf` helper.

## Capabilities

### New Capabilities
- `founder-effect-genetic-drift`: The `WrightFisherFixationProblem` validated parameter bundle, the `WrightFisherFixationProblemError` ADT, and the `WrightFisher.fixationLogProbs` algorithm computing the per-generation per-factor log10 fixation probability under the Wright-Fisher model.

### Modified Capabilities
<!-- none — adding a third sibling method to the WrightFisher object is strictly additive, matching the spec-27 precedent -->

## Impact

- New files in `bio.domain.genetics`: `WrightFisherFixationProblem.scala`, `WrightFisherFixationProblemError.scala`.
- Modified file in `bio.algorithms.genetics`: `WrightFisher.scala` (adds one method; existing `atLeast`, `expectedFrequencies`, and `binomialPmf` unchanged).
- New test suites: `WrightFisherFixationProblemSpec`, `WrightFisherFixationProblemErrorSpec`, `WrightFisherFixationSpec`.
- No new SBT dependencies.
- No breaking changes — purely additive both at the API level and at the test level. The existing `WrightFisherSpec` and `WrightFisherExpectationSpec` are untouched.
