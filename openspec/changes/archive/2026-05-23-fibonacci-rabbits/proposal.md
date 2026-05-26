## Why

Spec 6 of the project brief is a pure recurrence-relation problem: count rabbit pairs after `n` months when each reproduction-age pair produces a litter of `k` pairs. It is the framework's second non-sequence algorithm and the first to take typed numeric inputs and return a count that may grow large (BigInt territory at the upper end of Rosalind's bounds).

## What Changes

- **NEW** `RabbitProblem` case class in `bio.domain` with smart constructor — validates `months ≥ 1` and `litterSize ≥ 0`
- **NEW** `RabbitProblemError` sealed ADT in `bio.domain` with cases `NonPositiveMonths(value: Int)` and `NegativeLitterSize(value: Int)`
- **NEW** `FibonacciRabbits.population(problem: RabbitProblem): BigInt` in `bio.algorithms` — pure, total, iterative closed-form using the recurrence `F(n) = F(n-1) + k * F(n-2)`, `F(1) = F(2) = 1`
- Returns `BigInt` (not `Long`) — counts grow exponentially with `k`; using `BigInt` future-proofs against any relaxation of Rosalind's `n ≤ 40, k ≤ 5` bound

## Capabilities

### New Capabilities

- `fibonacci-rabbits`: Domain type `RabbitProblem` (validated months + litter size) plus the algorithm `FibonacciRabbits.population` that computes the generalized Fibonacci recurrence `F(n) = F(n-1) + k * F(n-2)`, returning the rabbit pair count as `BigInt`

### Modified Capabilities

## Impact

- New files in `bio.domain`: `RabbitProblem.scala`, `RabbitProblemError.scala`
- New file in `bio.algorithms`: `FibonacciRabbits.scala`
- New test files mirroring each
- No changes to existing code, no new dependencies
- All existing 95 tests continue passing
