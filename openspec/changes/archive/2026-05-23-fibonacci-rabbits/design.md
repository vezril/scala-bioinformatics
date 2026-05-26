## Context

This is the framework's sixth capability and second non-sequence problem (after `mendelian-inheritance`). The pattern established there — validated input case class with smart constructor, error ADT, pure algorithm in `bio.algorithms` — applies directly. The differences are in the output type (a count, not a probability) and the algorithmic technique (iterative recurrence, not a closed-form arithmetic expression).

Current state of relevant code: nothing exists. This change introduces an entirely new domain.

## The math

Let `F(n)` denote the rabbit-pair count after month `n` starting from 1 pair, where each pair of reproduction-age rabbits produces `k` new pairs each month. The standard derivation:

```
F(1) = 1                          (initial pair)
F(2) = 1                          (not yet reproduction-age)
F(n) = F(n-1) + k * F(n-2)        (n ≥ 3)
```

The `F(n-1)` term is the existing population from the previous month; the `k * F(n-2)` term is the offspring born this month — equal to `k` times the number of reproduction-age pairs, which is the population from two months ago.

Verification with Rosalind sample `n=5, k=3`:
- F(1) = 1
- F(2) = 1
- F(3) = 1 + 3*1 = 4
- F(4) = 4 + 3*1 = 7
- F(5) = 7 + 3*4 = 19 ✓

For `k=5, n=40`, the value grows to approximately `2.79^38 ≈ 10^17` — within `Long` range but uncomfortably close. Using `BigInt` makes overflow impossible regardless of future bound relaxation.

## Goals / Non-Goals

**Goals:**
- Model the problem inputs with a validated case class (`RabbitProblem`) — months ≥ 1, litterSize ≥ 0
- Implement `FibonacciRabbits.population` as a pure, total function using the iterative recurrence
- Return `BigInt` to avoid overflow concerns

**Non-Goals:**
- Closed-form (Binet-style) computation — the iterative form is `O(n)` time, `O(1)` space, and trivially correct; closed-form has floating-point precision concerns
- Memoization / caching across invocations — each call is independent, no shared state
- Mortal rabbits (Rosalind FIBD) — that is a separate problem with a different recurrence; future change
- Matrix exponentiation — `O(log n)` is overkill for `n ≤ 40`

## Decisions

### `RabbitProblem` enforces algorithmically necessary bounds, not Rosalind's

**Decision**: Smart constructor enforces `months ≥ 1` and `litterSize ≥ 0`. Rosalind's `n ≤ 40, k ≤ 5` upper bounds are **not** enforced.

**Rationale**: Same logic as `Population.from` in `mendelian-inheritance` — validate what the math actually requires. The algorithm works correctly for any valid input within these bounds. Rosalind's caps are a problem-statement convention, not a mathematical requirement. With `BigInt` output, even very large inputs produce correct (if slow) answers.

**Bounds rationale:**
- `months ≥ 1` → the recurrence is defined starting at `F(1) = 1`. `F(0)` is undefined in this problem framing.
- `litterSize ≥ 0` → zero is acceptable (rabbits exist but don't reproduce, so `F(n) = 1` for all n). Negative is biologically meaningless.

### Return type is bare `BigInt`, not a wrapper value class

**Decision**: `population` returns `BigInt` directly, not `RabbitPairCount(BigInt)` or similar.

**Rationale**: A count of rabbit pairs has only the trivial invariant "non-negative" (guaranteed by the math). There is no domain operation that benefits from a wrapper (no `format`, no arithmetic that needs constraint). `BigInt` already supports comparison, equality, and arithmetic. Wrapping would be pure ceremony.

**Contrast with `Probability`**: `Probability` has the non-trivial invariant `[0.0, 1.0]` and may be reused across statistical algorithms — wrapping pays off. A rabbit count has no analogous payoff.

### Iterative recurrence via `foldLeft`, not naive recursion or `@tailrec`

**Decision**: Implement the recurrence as a `foldLeft` over `3 to n`, accumulating the pair `(F(n-2), F(n-1))` and yielding `F(n)`.

**Rationale**: `foldLeft` is the most idiomatic Scala for state-evolving iteration over a range. It expresses the algorithm declaratively (an accumulator, a step function) without needing `@tailrec` annotations or recursion-style boilerplate. The `O(n)` time and `O(1)` space characteristics match the requirement.

**Alternative considered**: `@tailrec` recursion. Equally correct but more verbose for this case. `foldLeft` reads more directly as "fold a series of months into a population count."

**Alternative considered**: Naive recursion. `O(2^n)` time. Rejected — exponential blowup even for moderate `n`.

### Base cases handled before the fold

**Decision**: `n == 1 || n == 2` returns `BigInt(1)` directly. The fold only runs for `n >= 3`.

**Rationale**: The recurrence is only defined for `n ≥ 3`. Handling base cases explicitly avoids special-casing inside the fold.

## Risks / Trade-offs

- [`BigInt` is slower than `Long`] For `n ≤ 40, k ≤ 5`, the difference is microseconds — negligible. → Accept; correctness over micro-optimization.
- [`RabbitProblem` may need additional constraints if Rosalind variants emerge] Mortal-rabbits (FIBD) adds a `lifespan` parameter. → Acceptable: when that change lands, either add a field to `RabbitProblem` or introduce a sibling type. Not a current concern.
- [Smart constructor accepts inputs Rosalind would call invalid] e.g., `months = 100`. → Acceptable: the function generalizes naturally. Callers consuming Rosalind input can enforce their own caps.
