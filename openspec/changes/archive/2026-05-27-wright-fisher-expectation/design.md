## Context

Spec 27 generalizes spec 26's Wright-Fisher framing from a *probabilistic* question ("what's the chance of seeing at least `k` recessive after `g` generations?") to an *expected-value* question ("what's the expected count of dominant alleles after one generation?"). The math collapses to the textbook identity:

```
E[Bin(n, p)] = n · p
```

Element-wise: given a vector of allele frequencies `P`, the next-generation expected counts are `[n · P[0], n · P[1], ..., n · P[m-1]]`.

The framework already has `WrightFisher.atLeast(problem: WrightFisherProblem): Probability` in `bio.algorithms.genetics`. This spec extends that object with a sibling method `expectedFrequencies(problem: WrightFisherExpectationProblem): Vector[Double]`.

## Goals / Non-Goals

**Goals:**
- `WrightFisherExpectationProblem` validated bundle in `bio.domain.genetics` (two inputs: `n` and `p: Vector[Probability]`).
- `WrightFisherExpectationProblemError` ADT covering the three failure modes.
- Extend `WrightFisher` in `bio.algorithms.genetics` with `expectedFrequencies(problem): Vector[Double]`. Total over the validated input.
- Match Rosalind's sample (`n=17, P=[0.1, 0.2, 0.3] → [1.7, 3.4, 5.1]`) within absolute error `< 0.001`.

**Non-Goals:**
- Adding general binomial-distribution utilities (variance, skewness, etc.). Out of scope; if a future spec needs them we'd add a separate `Binomial` object in `bio.domain.stats`.
- Returning a `Vector[Probability]` or other wrapper output. The expected count is a *count*, not a probability — it can exceed 1 (the sample's `5.1` does). Bare `Vector[Double]` is the right output type.
- Caching across calls. Each call is `O(m)` multiplications at `m ≤ 20`; nothing to cache.

## Decisions

### Decision 1: `WrightFisherExpectationProblem` is a new bundle (not reusing `WrightFisherProblem`)

The two bundles serve different methods on the same object and have different shapes:

- `WrightFisherProblem(n: Int, m: Int, g: Int, k: Int)` — four parameters, four-stage validation, used by `WrightFisher.atLeast`.
- `WrightFisherExpectationProblem(n: Int, p: Vector[Probability])` — two parameters, two-stage validation, used by `WrightFisher.expectedFrequencies`.

Trying to unify them into one bundle would lose precision: `expectedFrequencies` doesn't care about `m`, `g`, or `k`, and `atLeast` doesn't care about a `Vector[Probability]`. Keeping them separate matches the framework's existing pattern (each method has the *minimal* validated input it needs).

```scala
sealed abstract case class WrightFisherExpectationProblem(n: Int, p: Vector[Probability])
object WrightFisherExpectationProblem {
  private val MaxN: Int = 1_000_000
  private val MaxP: Int = 20

  def from(n: Int, p: Vector[Probability]): Either[WrightFisherExpectationProblemError, WrightFisherExpectationProblem] = {
    if (n < 1)        Left(WrightFisherExpectationProblemError.NonPositiveN(n))
    else if (n > MaxN) Left(WrightFisherExpectationProblemError.NExceedsMaximum(n, MaxN))
    else if (p.size > MaxP) Left(WrightFisherExpectationProblemError.TooManyProbabilities(p.size, MaxP))
    else Right(new WrightFisherExpectationProblem(n, p) {})
  }
}
```

**Validation order:** `n` lower → `n` upper → `|p|` upper. First-failure-wins. (`|p| == 0` is allowed — produces an empty output vector. Rosalind's "array of length m" framing doesn't preclude `m = 0`.)

**Naming `TooManyProbabilities` (not `PExceedsMaximum`):** matches the descriptive style used by `MExceedsTotalAlleles` in spec 26 — the error name signals the constraint's meaning rather than just "exceeds max".

### Decision 2: Extend `WrightFisher`, don't create a new object

Two related-but-distinct methods on one object models the Wright-Fisher *family* cleanly:

```scala
object WrightFisher {
  def atLeast(problem: WrightFisherProblem): Probability = { /* spec 26 */ }
  def expectedFrequencies(problem: WrightFisherExpectationProblem): Vector[Double] = { /* spec 27 */ }
  private def binomialPmf(n: Int, p: Double): Vector[Double] = { /* spec 26 helper */ }
}
```

Each method takes its own validated input bundle — no ambiguity at the call site. Future Wright-Fisher methods (variance, drift simulation, etc.) would follow the same pattern.

**Alternative considered:** put `expectedFrequencies` in a new `WrightFisherExpectation` object. Rejected because it splits the Wright-Fisher family across two source files for no functional reason. The Rosalind problem statement itself frames spec 27 as continuing from spec 26 ("In 'The Wright-Fisher Model of Genetic Drift', we generalized..."), so the API should reflect that.

### Decision 3: Algorithm is one line of element-wise multiplication

```scala
def expectedFrequencies(problem: WrightFisherExpectationProblem): Vector[Double] =
  problem.p.map(prob => problem.n.toDouble * prob.value)
```

`n.toDouble` is exact for any `Int` (Double has 53 bits of mantissa; `Int.MaxValue ≈ 2^31`). The multiplication is one floating-point operation per element. Total cost: `O(m)` Doubles. At `m ≤ 20` this is microseconds.

**Float-precision note:** `17 * 0.1 = 1.7000000000000002` rather than exactly `1.7` due to `0.1`'s inexact binary representation. This is normal `Double` behavior and well within Rosalind's stated 0.001 absolute-error tolerance. Tested with abs-error comparisons.

### Decision 4: Output is bare `Vector[Double]` (not `Vector[Probability]`)

Expected counts are *counts*, not probabilities. At `n = 17, p = 0.3`, the expected count is `5.1` — not a valid `Probability`. The natural output type is bare `Vector[Double]`, mirroring `RandomMatch.logProbabilities` (also unbounded-range output, also bare Vector[Double]) and `IndependentSegregation.logProbs`.

## Risks / Trade-offs

- **Risk:** A bare `Vector[Double]` output gives the caller no type-system anchor on the meaning of the numbers. → **Mitigation:** the method name `expectedFrequencies` and the bundle name `WrightFisherExpectationProblem` carry the meaning. Wrapping in a domain type would require a non-existent `ExpectedCount` type that no other algorithm currently consumes — over-modeling.
- **Risk:** The `WrightFisher` object now has two methods with different validated-input shapes. Callers might confuse `WrightFisherProblem` with `WrightFisherExpectationProblem`. → **Mitigation:** distinct type names; the compiler enforces correct routing. Same-object multi-method APIs are common in Scala libraries and shouldn't surprise callers.
- **Trade-off:** Reusing the `WrightFisher` object means changes to spec 26's main spec (the existing `wright-fisher-genetic-drift` capability). → **Mitigation:** the change is strictly additive (one new method; existing `atLeast` unchanged); spec deltas can capture it cleanly as a MODIFIED requirement that adds the new method to the spec text. The change-proposal's "Modified Capabilities" section flags this.
- **Trade-off:** No new shared helper between `atLeast` and `expectedFrequencies` even though both deal with `Bin(n, p)`. → **Mitigation:** the existing `binomialPmf` helper computes the full PMF, while `expectedFrequencies` doesn't need the PMF at all (it only needs `n · p`). Forcing a shared helper would obscure each method's clarity.
