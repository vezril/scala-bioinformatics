## Context

Spec 25 takes a vector of homozygous-recessive proportions and computes, for each, the probability that a randomly selected diploid individual carries at least one copy of the recessive allele under Hardy-Weinberg equilibrium. Math:

- Given `A[k] = q²` (homozygous-recessive proportion).
- Allele frequency: `q = √A[k]`.
- Dominant-allele frequency: `p = 1 − q`.
- Carrier probability (at least one recessive allele): `B[k] = 1 − P(both alleles dominant) = 1 − p² = 1 − (1 − q)² = 2q − q²`.
- Substituting `q² = A[k]`: `B[k] = 2 · √A[k] − A[k]`.

The framework already has a `Probability` value type in `bio.domain.stats` (validated `Double` in `[0, 1]`). No new domain types are needed here.

## Goals / Non-Goals

**Goals:**
- `DiseaseCarriers.frequencies(homozygousRecessive: Vector[Probability]): Vector[Probability]` in `bio.algorithms.genetics`.
- Total over any `Vector[Probability]` input — the math preserves the `[0, 1]` range structurally (proved in Decision 2 below), so `Probability.unsafeFrom` is justified at every output position.
- Match Rosalind's sample within absolute error `< 0.001` (Rosalind's standard tolerance).

**Non-Goals:**
- A new domain wrapper for the input or output vectors. The element type (`Probability`) already enforces the per-element invariant; nothing useful happens at the collection level.
- A bundle / problem type. Bundles exist to enforce cross-input constraints; this algorithm has only one input vector with no cross-element constraints.
- Length cap. Rosalind doesn't specify one, and the algorithm is `O(n)` with negligible constants.
- Generalizing to non-Hardy-Weinberg assumptions. Out of scope; if a future spec relaxes the equilibrium assumption it can add a separate algorithm.

## Decisions

### Decision 1: Element-wise `Math.sqrt`-based formula

```scala
def frequencies(homozygousRecessive: Vector[Probability]): Vector[Probability] =
  homozygousRecessive.map { p =>
    val a = p.value
    val carrier = 2.0 * Math.sqrt(a) - a
    Probability.unsafeFrom(carrier)
  }
```

Direct application of the closed-form `B = 2√A − A`. `Math.sqrt` on a `Double` in `[0, 1]` is well-defined and stable — no special cases for `A = 0` or `A = 1` (`sqrt(0) = 0`, `sqrt(1) = 1`, both give the right answer for the carrier probability).

### Decision 2: `Probability.unsafeFrom` is justified — output is always in `[0, 1]`

Let `f(a) = 2√a − a` on `a ∈ [0, 1]`.

- `f(0) = 0`
- `f(1) = 2 − 1 = 1`
- `f'(a) = 1/√a − 1`, which is `> 0` for `a < 1`, `= 0` at `a = 1`. So `f` is monotonically non-decreasing on `(0, 1]`.
- Therefore `f(a) ∈ [f(0), f(1)] = [0, 1]` for every `a ∈ [0, 1]`.

The output is structurally a valid `Probability`. `Probability.unsafeFrom` (the framework's `private[bio]` trusted constructor) is the right tool — same pattern as `MendelianInheritance` and `IndependentAlleles`, both of which also produce probabilities via `unsafeFrom`.

### Decision 3: Bare `Vector[Probability]` in and out — no wrapper, no bundle

The element type already enforces the per-element invariant (`p ∈ [0, 1]`). There is no collection-level invariant to enforce — Rosalind specifies no length bound, and an empty vector is a valid input (produces an empty output). Wrapping in `DiseaseCarriersProblem(values: Vector[Probability])` would be ceremonial; the algorithm would still just do `problem.values.map(...)`.

The output type `Vector[Probability]` (rather than `Vector[Double]`) preserves the type-system anchor: a downstream caller iterating the output can rely on each element being a valid probability without re-checking. This is the same choice we made for the input. Mirrors the framework's `MendelianInheritance.dominantProbability` which returns a `Probability`.

**Alternative considered:** return `Vector[Double]` to mirror `RandomMatch.logProbabilities`. Rejected because *log* probabilities are unbounded below (so a `Probability` wrapper wouldn't fit there), but raw carrier probabilities are in `[0, 1]` and benefit from the wrapper.

### Decision 4: Algorithm name `DiseaseCarriers.frequencies`

- Object: `DiseaseCarriers` — matches the Rosalind problem name ("Counting Disease Carriers") and the biological concept being computed.
- Method: `frequencies` — plural noun, returns the collection of carrier frequencies. Consistent with the framework's noun/verb pattern but uses a noun for the method because the output IS the named concept (frequencies). Compare `Permutations.enumerate(length)`: object is plural noun, method is verb. Here the output IS plural, so the method is the plural noun.

**Alternative considered:** `CarrierFrequency.compute(...)` or `HardyWeinbergCarriers.frequencies(...)`. Both defensible. Chose `DiseaseCarriers` because the Rosalind problem framing — disease carriers — is the clearest biological hook and is consistent with the existing pattern of naming objects after the *concept being counted* (e.g., `Permutations`, `Subsets`).

## Risks / Trade-offs

- **Risk:** `Math.sqrt` introduces floating-point error. → **Mitigation:** Rosalind's stated tolerance is `< 0.001`; the algorithm's actual error is at the `Double` precision floor (`~10^-16`). Comfortable margin.
- **Risk:** `unsafeFrom` could underflow/overflow at the bounds if a future caller passed an unvalidated `Double`. → **Mitigation:** the input is `Probability`, not raw `Double` — the per-element wrapper protects against that.
- **Trade-off:** No `DiseaseCarriersProblem` wrapper means callers can pass any `Vector[Probability]` directly. → **Mitigation:** that's the intent — the algorithm is total over any such vector. Adding a wrapper would only add ceremony.
- **Trade-off:** Re-using `Math.sqrt` in scaladoc as a primitive — different from `IndependentSegregation`'s `Math.pow(0.5, n)`. → **Mitigation:** both are standard JDK numeric primitives; documenting their use in scaladoc is sufficient.
