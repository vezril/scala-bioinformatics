## Why

Rosalind problem 27 ("Wright-Fisher's Expected Behaviour") asks for the expected allele frequency of the next generation under the Wright-Fisher model — i.e., `E[Bin(n, p)] = n · p` for each input probability `p`. Math is trivial element-wise scalar multiplication; the value of this spec is integrating it cleanly into the existing `genetics` subdomain alongside `WrightFisher.atLeast` from spec 26.

This is the seventh `genetics` algorithm and the second method on the existing `WrightFisher` object — paired with `WrightFisher.atLeast` (probabilistic) to form the framework's first multi-method algorithm object.

## What Changes

- Add `WrightFisherExpectationProblem` in `bio.domain.genetics` as a `sealed abstract case class` validated parameter bundle wrapping `n: Int` and `p: Vector[Probability]`. The smart constructor enforces `1 ≤ n ≤ 1,000,000` and `|p| ≤ 20`.
- Add `WrightFisherExpectationProblemError` sealed ADT with cases `NonPositiveN`, `NExceedsMaximum`, `TooManyProbabilities`.
- Extend the existing `WrightFisher` object in `bio.algorithms.genetics` with a new method `expectedFrequencies(problem: WrightFisherExpectationProblem): Vector[Double]` returning `problem.p.map(prob => problem.n.toDouble * prob.value)`. Total over the validated input.

## Capabilities

### New Capabilities
- `wright-fisher-expectation`: The `WrightFisherExpectationProblem` validated parameter bundle, the `WrightFisherExpectationProblemError` ADT, and the `WrightFisher.expectedFrequencies` algorithm computing `E[Bin(n, p)] = n · p` element-wise.

### Modified Capabilities
<!-- none — the existing `wright-fisher-genetic-drift` capability's requirement on `WrightFisher.atLeast` is unchanged; adding a sibling method `expectedFrequencies` on the same object is strictly additive and doesn't modify any existing requirement -->

## Impact

- New files in `bio.domain.genetics`: `WrightFisherExpectationProblem.scala`, `WrightFisherExpectationProblemError.scala`.
- Modified file in `bio.algorithms.genetics`: `WrightFisher.scala` (adds one method, no changes to existing `atLeast`). This is an additive edit — the existing requirement on `WrightFisher.atLeast` is preserved verbatim.
- New test suites: `WrightFisherExpectationProblemSpec`, `WrightFisherExpectationProblemErrorSpec`, `WrightFisherExpectationSpec`. The existing `WrightFisherSpec` is untouched.
- No new SBT dependencies.
- No breaking changes — purely additive both at the API level and at the test level.
