## Context

The recurrence subdomain currently contains `RabbitProblem` (months + litterSize) and `FibonacciRabbits` (closed-form generalized Fibonacci recurrence). Spec 12 needs a different state shape — tracking the population *by age* — so the existing two-term `(prev, curr)` fold cannot be reused. We introduce a parallel domain type (`MortalRabbitProblem`) and a parallel algorithm (`MortalFibonacciRabbits`), keeping spec 6 untouched.

## The math / state machine

A rabbit pair born in month `b` is:
- **Newborn** at month `b` (age 0): present but cannot reproduce
- **Adult** at months `b+1`, `b+2`, ..., `b+lifespan-1` (ages 1..lifespan-1): reproduces 1 pair per month
- **Dead** at month `b+lifespan` (never appears in the population at age `lifespan`)

State: a vector `v` of length `lifespan` where `v(k)` is the number of pairs currently at age `k`. Initial state at month 1 is `v = [1, 0, 0, ..., 0]` (one newborn pair).

Per-month transition:
- `newborns_{n+1}` = sum of all adult pairs at month n = `v.drop(1).sum`
- pairs at age `k+1` at month n+1 = pairs at age `k` at month n (ageing up)
- pairs at age `lifespan-1` at month n die at end of month n (never advance to age `lifespan`)

Equivalently:
```
v_{n+1} = newborns_{n+1} +: v_n.dropRight(1)
```

The total at month n is `v.sum`.

### Walking the Rosalind sample (n=6, m=3)

| Month | v | total |
|------:|---|------:|
| 1 | [1, 0, 0] | 1 |
| 2 | [0, 1, 0] | 1 |
| 3 | [1, 0, 1] | 2 |
| 4 | [1, 1, 0] | 2 |
| 5 | [1, 1, 1] | 3 |
| 6 | [2, 1, 1] | 4 ✓ |

### Sanity check against classic Fibonacci

If `lifespan >= n`, nothing dies before month `n`, and the recurrence collapses to the classic Fibonacci sequence. For `(n=6, m=100)` the total is `8` (= F(6)). Tests verify this equivalence.

## Goals / Non-Goals

**Goals:**
- Model `MortalRabbitProblem` as a `sealed abstract case class` with smart-constructor validation, matching the framework's existing pattern for parameter bundles
- Reject `months < 1` and `lifespan < 1` (negative or zero values have no biological / mathematical meaning)
- Compute the population in `O(n × m)` time and `O(m)` space using a `Vector[BigInt]` of length `lifespan`
- Use `BigInt` for the population count (consistent with `FibonacciRabbits` and necessary for large `n`)
- Match the Rosalind sample exactly: `(6, 3)` → `4`
- Coexist with `FibonacciRabbits` without sharing code or types — they're different recurrences over different parameter bundles

**Non-Goals:**
- A closed-form / matrix-exponentiation algorithm. The age-vector simulation is `O(n × m)` which is trivially fast for Rosalind's `n ≤ 100, m ≤ 20` (≤ 2000 ops).
- Stochastic mortality (probabilistic lifespan). Fixed lifespan only.
- Litter size other than 1. Combining mortality with multi-offspring litters is a future extension; if needed, a `MortalRabbitProblem` would gain a `litterSize` field and `newborns` would be multiplied by `litterSize`. Out of scope here.
- Per-age reproduction differences (e.g., first-month adult produces less). The spec treats all adults (ages 1..lifespan-1) identically.
- Reusing or generalizing `RabbitProblem`. The parameter sets differ semantically; merging would force one type to carry an unused field.

## Decisions

### Distinct `MortalRabbitProblem` domain type

**Decision**: Introduce `MortalRabbitProblem(months: Int, lifespan: Int)` separately from the existing `RabbitProblem(months: Int, litterSize: Int)`.

**Rationale**:
- The two recurrences take *different* parameters (`litterSize` vs `lifespan`) with different meanings
- A unified `RabbitProblem(months, litterSize, lifespan)` would force callers of spec 6 to pass a meaningless lifespan, and callers of spec 12 to pass a meaningless litter size. Each call site would carry the lie that the unused parameter is meaningful.
- Two narrow types > one wide type for distinct problems. Same principle as why `Population` (spec 5) and `CouplePopulation` (spec 11) are distinct.

**Alternative considered**: Merge into a `GeneralizedRabbitProblem(months, litterSize, lifespan)` with `litterSize = 1, lifespan = Int.MaxValue` defaulting to classic Fibonacci. Rejected — the smart constructor would need to permit the "infinite lifespan" sentinel value, the algorithm would conditionally pick the closed-form vs the simulation, and the API would obscure intent. Better to keep them separate.

### Algorithm: explicit age-vector simulation

**Decision**: Maintain a `Vector[BigInt]` of length `lifespan` indexed by age `0..lifespan-1`. Each month:
1. Compute newborns as the sum of pairs at ages 1..lifespan-1
2. Prepend newborns, drop the oldest age — `newborns +: v.dropRight(1)`

After `n - 1` transitions starting from `Vector(BigInt(1)) ++ Vector.fill(lifespan - 1)(BigInt(0))`, sum the final vector.

**Rationale**:
- One-liner per transition; idiomatic functional code; clearly mirrors the math
- `Vector.dropRight(1)` is O(1) amortized for vectors; the per-month cost is dominated by the sum, which is O(m). Total cost O(n × m). For Rosalind's bounds this is ≤ 2000 ops.
- `BigInt` everywhere prevents overflow; even at the upper bound `n=100, m=20` the population stays well-bounded but the discipline of `BigInt` matches `FibonacciRabbits` and future-proofs against larger inputs.
- No `var`, no mutable collections — purely functional

**Alternative considered**: Manage the state with two scalar accumulators (similar to the spec-6 fold). Rejected — the mortality rule requires knowing the *age* of each pair, not just the previous two months' totals. A vector is the right state shape.

**Alternative considered**: Use a fixed-size `Array[BigInt]` with in-place updates. Rejected — `Vector[BigInt]` is the framework's idiom (functional, immutable); the constant-factor difference is irrelevant at this scale.

### Initial state is `[1, 0, 0, ..., 0]` (one newborn pair at month 1)

**Decision**: At month 1, the single seed pair is treated as newborn (age 0). The transition to month 2 ages it to age 1 (now an adult, but no offspring yet that month since newborns are produced by adults *during* the transition — and at month 1 there were no adults).

**Rationale**: This matches the classic Fibonacci interpretation where `F(1) = F(2) = 1` (the seed pair appears at month 1, still alone at month 2, first offspring at month 3). The age-vector simulation reproduces this exactly when `lifespan >= 2`.

**Edge case**: With `lifespan = 1`, the pair dies at end of month 1 before ever reproducing. From month 2 onwards the population is 0. The simulation reflects this naturally:
- Month 1: `v = [1]` → total 1
- Month 2: newborns = sum(`v.drop(1)`) = sum([]) = 0; new v = `[0]` ++ `v.dropRight(1)` = `[0]` ++ `[]` = `[0]` → total 0
- Subsequent months stay at `[0]`

### Lower-bound validation only

**Decision**: Validate `months >= 1` and `lifespan >= 1`. No upper bound checks on either.

**Rationale**:
- Matches `RabbitProblem.from` (which checks `months >= 1` and `litterSize >= 0` — no upper bound)
- Rosalind caps `n ≤ 100, m ≤ 20`, but the framework has no reason to enforce that — `BigInt` handles any `n`; the simulation runs `O(n × m)` for any sized inputs.
- Negative or zero values have no biological / mathematical meaning. The error messages name the offending value so callers can diagnose.

**Alternative considered**: Add upper bounds matching Rosalind. Rejected — adds noise and ties the framework to a specific problem's constraints. Document Rosalind's bounds in the proposal, not in the validation.

### `lifespan` of 0 or negative is `NonPositiveLifespan`, not `NegativeLifespan`

**Decision**: `MortalRabbitProblemError.NonPositiveLifespan(value: Int)` rejects both `0` and negative values.

**Rationale**:
- A lifespan of 0 is degenerate (a pair would be dead at birth before being counted). Rejecting it is principled.
- Same shape as `RabbitProblem`'s `NonPositiveMonths(value)`; consistent naming pattern within the recurrence subdomain.

### Lives in `bio.algorithms.recurrence` and `bio.domain.recurrence`

**Decision**: `MortalRabbitProblem` and `MortalRabbitProblemError` go in `bio.domain.recurrence` (joining `RabbitProblem` and `RabbitProblemError`). `MortalFibonacciRabbits` goes in `bio.algorithms.recurrence` (joining `FibonacciRabbits`).

**Rationale**: Both algorithms are discrete-time population recurrences on rabbit populations. They are conceptually siblings under the same subdomain.

## Risks / Trade-offs

- [Two distinct rabbit-problem types could confuse callers] A caller might use `RabbitProblem` when they want mortality, or vice versa. → Mitigation: explicit type names (`MortalRabbitProblem` vs `RabbitProblem`), clear scaladoc on both. Type system enforces the distinction at compile time.
- [Vector-based state has constant-factor overhead vs Array] At Rosalind's bounds (≤ 2000 ops) this is invisible. → If a future problem pushes the bounds, swap to a mutable `Array[BigInt]` inside a function-local scope (still pure FP from the outside).
- [`Vector(0)(0) = BigInt(1)`-style initialization can be unclear] → Use explicit `Vector.fill(lifespan - 1)(BigInt(0))` prefixed with `BigInt(1)` for clarity, or `Vector.tabulate(lifespan)(i => if (i == 0) BigInt(1) else BigInt(0))`. Pick whichever reads best in the implementation.
- [Floating-point not used anywhere — `BigInt` everywhere] No precision concerns. → No risk.
- [`lifespan = 1` produces `0` for `n >= 2` — could surprise unprepared callers] → Documented in spec scenarios and design. The math is unambiguous.
- [The `+:` (prepend) on Vector is O(n)] For `Vector[BigInt]` of length ≤ 20, irrelevant; the dominant cost is the sum. → Not optimizing prematurely.
