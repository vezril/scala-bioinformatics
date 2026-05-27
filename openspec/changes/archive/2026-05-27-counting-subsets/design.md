## Context

Spec 22 takes a positive integer `n` (with `1 ≤ n ≤ 1000`) and returns `2^n mod 1,000,000`. The framework already has near-identical algorithms in `bio.algorithms.combinatorics`:

- `Permutations.enumerate(length: PermutationLength)` — single value-type input, list output.
- `PartialPermutations.count(problem: PartialPermutationProblem): Int` — bundled input (two integers with a cross-constraint), modular integer output via per-step modulo (mod 1,000,000).

`Subsets` reuses both patterns: the single-parameter value-type wrapper (from `Permutations`) plus the per-step-modulo computation (from `PartialPermutations`).

## Goals / Non-Goals

**Goals:**
- `SubsetUniverseSize` validated wrapper in `bio.domain.combinatorics` (smart constructor enforces `1 ≤ n ≤ 1000`).
- `SubsetUniverseSizeError` ADT with `NonPositive` and `ExceedsMaximum` cases.
- `Subsets.count(size): Int` in `bio.algorithms.combinatorics` returning `2^n mod 1,000,000`. Total. Computed via an `Int`-only per-step-modulo fold (no `BigInt`).

**Non-Goals:**
- Enumerating the actual subsets (`Permutations.enumerate` does its analog, but here `2^n` subsets is up to `2^1000` distinct lists — not feasible to materialize and not what the problem asks).
- Generalizing the modulus. The `1,000,000` modulus is fixed by the Rosalind problem; matching `PartialPermutations`.
- A `BigInt`-returning variant for unbounded outputs. Out of scope; if a future caller wants `2^n` without modulo we can add it separately.

## Decisions

### Decision 1: Single value-type wrapper (`SubsetUniverseSize`), not a bundle

```scala
sealed abstract case class SubsetUniverseSize(value: Int)
object SubsetUniverseSize {
  private val MaxN: Int = 1000
  def from(value: Int): Either[SubsetUniverseSizeError, SubsetUniverseSize] =
    if (value < 1) Left(SubsetUniverseSizeError.NonPositive(value))
    else if (value > MaxN) Left(SubsetUniverseSizeError.ExceedsMaximum(value, MaxN))
    else Right(new SubsetUniverseSize(value) {})
}
```

The algorithm takes one integer parameter. The framework's established pattern for one-parameter algorithms is a validated value type (`PermutationLength`, `OverlapLength`) — not a `Problem` bundle. Bundles are for multi-input algorithms or single-input algorithms with cross-constraints; neither applies here.

**Validation order:** lower bound, then upper bound. First-failure-wins.

**Naming:** `SubsetUniverseSize` describes what the integer means — the size `n` of the universe set `{1, 2, ..., n}` whose subsets we're counting. More precise than `n` and less generic than `Size`.

### Decision 2: Per-step modulo with `Int` arithmetic (no `BigInt`)

```scala
def count(size: SubsetUniverseSize): Int = {
  val Modulus = 1_000_000
  (0 until size.value).foldLeft(1) { (acc, _) => (acc * 2) % Modulus }
}
```

Mirrors `PartialPermutations.count` exactly. After each step `acc ∈ [0, 999_999]`, and the worst intermediate is `999_999 * 2 = 1_999_998`, well within `Int.MaxValue ≈ 2.15 × 10^9`. Cost: `O(n)` integer multiplies = ≤ 1000 multiplies at the upper bound. Trivially fast.

**Alternative considered — modular fast exponentiation (square-and-multiply):** O(log n) instead of O(n). At `n ≤ 1000` the difference is `10` vs `1000` multiplies — both microseconds. The fold form is simpler to read and matches the existing `PartialPermutations` idiom.

**Alternative considered — `BigInt(2).modPow(BigInt(n), BigInt(1_000_000)).toInt`:** correct and arguably cleaner. Rejected because it pulls in `BigInt` allocation when an `Int` fold suffices, and the framework's existing convention (`PartialPermutations`) is to use `Int` arithmetic with per-step modulo.

### Decision 3: Output is bare `Int`

The result is in `[0, 999_999]`, fits trivially in `Int`, and the Rosalind output format is a single integer. Mirrors `PartialPermutations.count` (also `Int`). No wrapper type — the value carries no further invariant the caller can usefully exploit.

### Decision 4: Algorithm name `Subsets.count`

The framework's combinatorics naming uses plural-noun-objects with verb methods: `Permutations.enumerate`, `PartialPermutations.count`. The analogous form for this spec is `Subsets.count` — consistent with the family.

## Risks / Trade-offs

- **Trade-off:** `O(n)` fold instead of `O(log n)` fast exponentiation. → **Mitigation:** the upper bound is `n ≤ 1000`; the practical difference is unmeasurable. The simpler form matches the framework's existing pattern and is easier to read.
- **Trade-off:** `1 ≤ n ≤ 1000` strictly enforced (no `n = 0` for the empty set). → **Mitigation:** the Rosalind problem explicitly says "positive integer", so we match that bound. A future spec wanting `n = 0` would just relax the lower bound — purely additive.
- **Risk:** Sharing the magic constant `1_000_000` with `PartialPermutations`. → **Mitigation:** the constant is defined locally inside `Subsets` (as it is in `PartialPermutations`); both algorithms happen to use the same modulus because Rosalind specifies it for both. Extracting a shared constant would couple two otherwise-independent capabilities for no real benefit.
