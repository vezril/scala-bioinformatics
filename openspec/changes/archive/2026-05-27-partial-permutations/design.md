## Context

Spec 15 established the `combinatorics` subdomain with `Permutations.enumerate` (full enumeration, n ≤ 7 cap). Spec 16 asks a related but distinct question: not "list all permutations of length n" but "count partial permutations of length k drawn from n objects" — `P(n, k) = n × (n-1) × ... × (n-k+1)`. The result must be returned modulo `1,000,000` because the unmodded value can be astronomically large (e.g., `P(100, 10) = 100 × 99 × ... × 91 = 6.28 × 10^19`, which already exceeds `Long.MaxValue ≈ 9.2 × 10^18`).

The framework now has a precedent for modular arithmetic: `InferMRna.count` (spec 13). The same pattern applies here — `Int` accumulator with `% Modulus` after every multiplication.

## The math

`P(n, k) = n! / (n - k)!` — also called the *falling factorial*. Computed as:

```
P(n, k) = ∏_{i=0}^{k-1} (n - i)
```

### Sample trace (n=21, k=7)

| i | (n − i) | acc × (n − i) | acc mod 10⁶ |
|--:|--------:|--------------:|------------:|
| 0 | 21 | 21 | 21 |
| 1 | 20 | 420 | 420 |
| 2 | 19 | 7,980 | 7,980 |
| 3 | 18 | 143,640 | 143,640 |
| 4 | 17 | 2,441,880 | 441,880 |
| 5 | 16 | 7,070,080 | 70,080 |
| 6 | 15 | 1,051,200 | **51,200** ✓ |

Rosalind expects `51200` — match.

### Overflow analysis

After each modulo, `acc ∈ [0, 999_999]`. The maximum factor is `n = 100`. So intermediate `acc * (n - i) ≤ 999_999 * 100 = 99_999_900`, well within `Int.MaxValue ≈ 2.147e9`. `Int` is sufficient — no need for `Long` or `BigInt`.

## Goals / Non-Goals

**Goals:**
- Provide `PartialPermutations.count(problem): Int` returning `P(n, k) mod 1_000_000`
- Model the parameter bundle as a validated `PartialPermutationProblem` with smart-constructor validation enforcing both bounds (1 ≤ n ≤ 100, 1 ≤ k ≤ 10) and the cross-constraint (k ≤ n)
- Match the Rosalind sample `(21, 7)` → `51200`
- Be total once the input is a valid `PartialPermutationProblem`
- Use `Int` arithmetic with per-step modulo (no `Long`, no `BigInt`)

**Non-Goals:**
- A `count` that returns the unmodded value via `BigInt`. Rosalind explicitly asks for the modular result; an unmodded helper would be added if a future spec needs it.
- Computing `n!` or `(n - k)!` directly. The falling-factorial form avoids ever materializing a value larger than `n^k`.
- Generalizing the modulus. Rosalind fixes it at `1_000_000`. If a future spec wants a configurable modulus, refactor then.
- Enumerating the partial permutations (returning a list of `Vector[Int]`). That would be `O(P(n, k))` time and memory, infeasible at the upper bound where `P(100, 10) ≈ 6 × 10^19`. Counting is what Rosalind asks for.

## Decisions

### Domain type: `PartialPermutationProblem(n, k)` with smart constructor

**Decision**: `sealed abstract case class PartialPermutationProblem(n: Int, k: Int)` with companion `from(n, k)` enforcing the three constraints.

**Rationale**: Matches the framework's pattern (`RabbitProblem`, `MortalRabbitProblem`, `IndependentAllelesProblem`). The cross-constraint `k ≤ n` cannot live in either field alone, so a bundle with smart-constructor validation is the right shape.

### Five error cases, not one lumped case

**Decision**: `NonPositiveN`, `NExceedsMaximum`, `NonPositiveK`, `KExceedsMaximum`, `KExceedsN` — five distinct cases.

**Rationale**:
- Each is a semantically distinct failure mode; lumping them under `InvalidInput(field, value, reason)` would lose pattern-matching precision
- Matches the framework's precedent (`IndependentAllelesProblemError` has three cases for three distinct failures; `CouplePopulationError` has two cases for two distinct failures)
- The error carries the actionable information (the offending value, the cap, or both inputs for the cross-constraint) without requiring the caller to re-derive anything

**Trade-off**: Five cases is the most error-cases of any framework type so far. Acceptable — the input has two fields plus a cross-constraint, so five failure modes is genuinely the right number.

### Validation order: n bounds → k bounds → cross-constraint

**Decision**: `from(n, k)` checks `n < 1` first, then `n > 100`, then `k < 1`, then `k > 10`, then `k > n`. First failure wins.

**Rationale**:
- "Single-field" failures before "cross-field" failures — same as `IndependentAllelesProblem.from`
- Checking `n` before `k` is arbitrary but stable; documented order
- The cross-constraint `k > n` requires both individual values to be valid (positive, in their respective ranges) for the comparison to be meaningful

### Algorithm: incremental product with per-step modulo

**Decision**:
```scala
def count(problem: PartialPermutationProblem): Int = {
  val Modulus = 1_000_000
  (0 until problem.k).foldLeft(1) { (acc, i) =>
    (acc * (problem.n - i)) % Modulus
  }
}
```

**Rationale**:
- Single `foldLeft`; no `var`, no mutable state
- `Int` arithmetic suffices because `acc * (n - i) ≤ 999_999 * 100 = 99_999_900 < Int.MaxValue`
- Mirrors the pattern from `InferMRna.count` (spec 13), keeping the framework's modular-arithmetic style consistent

**Alternative considered**: Compute `P(n, k)` as `BigInt` then take `.mod(BigInt(1_000_000))`. Rejected — `BigInt` allocation per multiplication is overhead with no precision gain since `Int` doesn't overflow at our bounds.

**Alternative considered**: Recursive function. Rejected — `foldLeft` over a `Range` is the standard Scala idiom for "accumulate over a fixed iteration".

### Return bare `Int`, no wrapper

**Decision**: `def count(problem: PartialPermutationProblem): Int`.

**Rationale**: Matches `InferMRna.count` (spec 13), which also returns a modular count as `Int`. The result is bounded `[0, 999_999]`, but the framework's precedent is bare `Int` for this shape of output.

### `Modulus` is a private constant, not configurable

**Decision**: `private val Modulus: Int = 1_000_000` inside `PartialPermutations`.

**Rationale**: Same decision as `InferMRna`. Rosalind fixes the modulus; the framework doesn't need to parameterize until a real use case demands it.

### Lives in `bio.algorithms.combinatorics` (joining `Permutations`)

**Decision**: `PartialPermutations` and its domain types join `Permutations` and `PermutationLength` in the `combinatorics` subdomain.

**Rationale**: Permutations (full and partial) are the same conceptual family. The subdomain established by spec 15 now has its second algorithm.

## Risks / Trade-offs

- [Hard-coded `n ≤ 100` and `k ≤ 10` caps] If a future spec needs higher bounds, the smart constructor must change. → One-line edit. The bounds are explicit constants (`MaxN`, `MaxK`) in the companion.
- [`Int` arithmetic relies on the upper-bound argument] If anyone bumps `MaxN` past `Int.MaxValue / Modulus / 100 = ~21`, the `acc * (n - i)` step could overflow. At current `MaxN = 100`, the worst case is `999_999 * 100 = 99_999_900` — comfortable. → Mitigation: the upper-bound check in the smart constructor enforces the precondition; raising `MaxN` would require revisiting overflow.
- [Five error cases is a lot] Slight verbosity in callers' error handling. → Acceptable; each case carries unique diagnostic value. Lumping would erase that.
- [Same `Modulus = 1_000_000` as `InferMRna`] If a future spec uses a different modulus, we'd have two algorithms with different hard-coded values. → No issue; modular results live with their algorithm.
- [No enumeration of partial permutations] Only counting; no `enumerate(problem): Vector[Vector[Int]]`. → Per design's non-goals; enumerating `P(100, 10) ≈ 6 × 10^19` items is infeasible. If a small-bound enumeration is ever needed, add a separate function with its own cap.
