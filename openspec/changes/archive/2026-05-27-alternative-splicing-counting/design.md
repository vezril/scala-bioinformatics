## Context

Spec 23 computes `Σ_{k=m}^{n} C(n, k) mod 1,000,000` for `0 ≤ m ≤ n ≤ 2000`. The framework already has three combinatorics algorithms in `bio.algorithms.combinatorics`:

- `Permutations.enumerate(length: PermutationLength)` — single-parameter, enumeration output.
- `PartialPermutations.count(problem: PartialPermutationProblem): Int` — two-parameter bundle with `k ≤ n` cross-constraint, modular integer output.
- `Subsets.count(size: SubsetUniverseSize): Int` — single-parameter wrapper, modular integer output.

This spec inherits `PartialPermutations`' bundle pattern (two integers with a cross-constraint) and the per-step modulo pattern, with one fresh twist: lower bound `0` rather than `1`.

## Goals / Non-Goals

**Goals:**
- `CombinationSumProblem(n, m)` validated parameter bundle in `bio.domain.combinatorics`.
- `CombinationSumProblemError` ADT covering `NegativeN`, `NExceedsMaximum`, `NegativeM`, `MExceedsN`.
- `Combinations.sumFrom(problem): Int` in `bio.algorithms.combinatorics` — builds row `n` of Pascal's triangle modulo `1,000,000` and sums entries from index `m` onward.

**Non-Goals:**
- Computing arbitrary `C(n, k)` exactly without modulo. Out of scope. If a future spec needs a non-modular version we'd add it separately (likely returning `BigInt`).
- Modular inverse / Fermat's little theorem approach. The modulus `1,000,000 = 2^6 × 5^6` isn't prime, so the closed-form division-based approach can't be used safely without Lucas's theorem. Pascal's triangle is simpler and faster at this scale.
- Caching Pascal rows across calls. Each call computes one row from scratch. Memoization would be an optimization for a workload not represented by Rosalind problems.

## Decisions

### Decision 1: `CombinationSumProblem(n, m)` validated bundle

```scala
sealed abstract case class CombinationSumProblem(n: Int, m: Int)

object CombinationSumProblem {
  private val MaxN: Int = 2000

  def from(n: Int, m: Int): Either[CombinationSumProblemError, CombinationSumProblem] =
    if (n < 0)       Left(CombinationSumProblemError.NegativeN(n))
    else if (n > MaxN) Left(CombinationSumProblemError.NExceedsMaximum(n, MaxN))
    else if (m < 0)  Left(CombinationSumProblemError.NegativeM(m))
    else if (m > n)  Left(CombinationSumProblemError.MExceedsN(m, n))
    else Right(new CombinationSumProblem(n, m) {})
}
```

Two integers with a cross-constraint (`m ≤ n`) — classic bundle territory, mirroring `PartialPermutationProblem`. Validation order: `n` lower bound, `n` upper bound, `m` lower bound, then `m ≤ n` cross-constraint. First-failure-wins.

**`Negative*` vs `NonPositive*` naming:** because `0` is a valid value here (not the case for `PartialPermutationProblem` where the bound was `1`), the error case name is `NegativeN` / `NegativeM` rather than `NonPositiveN`. Each case carries the offending value so callers can report it.

**No upper bound on `m`:** `m` is implicitly bounded above by `n` (which is bounded by 2000) via the cross-constraint, so a separate `MExceedsMaximum(2000)` case would be redundant.

### Decision 2: Algorithm = single Pascal's-triangle row build, then sum

```scala
def sumFrom(problem: CombinationSumProblem): Int = {
  val Modulus: Int = 1_000_000
  val row = buildModRow(problem.n, Modulus)  // Vector[Int] of length n+1
  (problem.m to problem.n).foldLeft(0) { (acc, k) =>
    (acc + row(k)) % Modulus
  }
}

private def buildModRow(n: Int, modulus: Int): Vector[Int] =
  (1 to n).foldLeft(Vector(1)) { (prev, _) =>
    Vector(1) ++ (1 until prev.size).map(i => (prev(i - 1) + prev(i)) % modulus) ++ Vector(1)
  }
```

Build row `n` of Pascal's triangle via the recurrence `C(n, k) = C(n-1, k-1) + C(n-1, k)`, all values reduced mod `1,000,000`. Then sum the entries from index `m` to `n`, again accumulating modulo.

**Complexity:** `O(n²)` integer additions = up to 4M operations at `n = 2000`. Each addition is bounded by `999_999 + 999_999 = 1_999_998`, well within `Int.MaxValue`. Trivially fast.

**Why Pascal's triangle (not Fermat's little theorem):** `1,000,000 = 2^6 × 5^6` is composite, so `k!` doesn't have a modular inverse mod `10^6` whenever `gcd(k!, 10^6) > 1` (which happens as soon as `k ≥ 2` for the factor of 2, and `k ≥ 5` for the factor of 5). The closed-form `n! / (k! (n-k)!)` approach via modular inverse simply doesn't apply. We'd need Lucas's theorem with two separate prime-power factorizations and CRT recombination — much more code for the same result. Pascal's triangle is the natural, simple choice at `n ≤ 2000`.

**Why row-by-row (not a 2D matrix):** we only need the final row to sum from. Keeping the full triangle would use `O(n²)` memory unnecessarily. The fold over rows holds at most one row at a time.

### Decision 3: Algorithm name `Combinations.sumFrom`

Combinatorics naming convention: plural-noun object with verb method.
- `Permutations.enumerate(length)`
- `PartialPermutations.count(problem)`
- `Subsets.count(size)`
- `Combinations.sumFrom(problem)` ← new

`sumFrom` (rather than `count` or `tailSum`) emphasizes that this is a sum starting from index `m`, which is the distinguishing parameter of the algorithm. The alternative name `tailSum` is also defensible but less descriptive of what `m` does.

**Alternative considered:** name the algorithm `AlternativeSplicing.count(problem)` after the Rosalind problem framing. Rejected because the math is purely combinatorial — the "alternative splicing" framing is just motivational hand-waving. Future combinatorics algorithms operating on binomial coefficients would benefit from finding their kin under a `Combinations` object.

### Decision 4: Output is bare `Int`

Result is in `[0, 999_999]`, fits in `Int`. No wrapper. Mirrors `PartialPermutations.count` and `Subsets.count`.

### Decision 5: `n = 0` and `m = 0` edge semantics

- `n = 0, m = 0`: row 0 is `[1]`, sum from index 0 is `1`. Correct: `C(0, 0) = 1`.
- `n = k, m = 0` (any valid `k`): sum is `2^k` mod `1,000,000` (because the full row sums to `2^n`). Useful sanity check: matches `Subsets.count` for the same `n`.
- `n = k, m = k`: sum is `C(k, k) = 1`.

These three edge points are tested explicitly.

## Risks / Trade-offs

- **Trade-off:** `O(n²)` time and `O(n)` memory per call. → **Mitigation:** at `n ≤ 2000` this is ~4M cheap integer adds and a 2001-element `Vector[Int]`. Practical cost is microseconds; nothing to optimize.
- **Risk:** `Vector` append-and-rebuild allocates per row. → **Mitigation:** measured cost is still microseconds; using `Array[Int]` in-place would be a 2× speedup at most, not worth the imperative leak. Keeping the functional style consistent with the rest of the framework.
- **Trade-off:** `Negative*` naming differs from earlier specs' `NonPositive*`. → **Mitigation:** the lower-bound semantics differ (this spec allows 0; earlier specs did not), and the error name needs to reflect that. Documented in scaladoc.
- **Risk:** The constant `1_000_000` is now defined locally in *four* places (`PartialPermutations`, `InferMRna`, `Subsets`, `Combinations`). → **Mitigation:** these are independent capabilities that happen to share Rosalind's chosen modulus. Extracting a shared constant would couple unrelated specs for no real benefit — same call we made for `Subsets`.
