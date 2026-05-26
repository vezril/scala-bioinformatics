## Why

Spec 12 of the project brief — "Mortal Fibonacci Rabbits" — extends the recurrence subdomain with mortality. Where spec 6 (`FibonacciRabbits`) modeled an immortal generalized-Fibonacci population (each pair lives forever, produces `k` offspring per month from maturity), spec 12 returns to the classic 1-offspring rule but adds a *fixed lifespan*: pairs die after `m` months. This requires a fundamentally different state representation — tracking population *by age*, not just a two-term recurrence — and produces the framework's first algorithm whose state space grows linearly in a problem parameter.

## What Changes

- **NEW** `MortalRabbitProblem` domain type in `bio.domain.recurrence` — `sealed abstract case class MortalRabbitProblem(months: Int, lifespan: Int)`, with `from(months: Int, lifespan: Int): Either[MortalRabbitProblemError, MortalRabbitProblem]` enforcing `months >= 1` and `lifespan >= 1`
- **NEW** `MortalRabbitProblemError` sealed ADT in `bio.domain.recurrence` — cases `NonPositiveMonths(value: Int)` and `NonPositiveLifespan(value: Int)`
- **NEW** `MortalFibonacciRabbits.population(problem: MortalRabbitProblem): BigInt` algorithm in `bio.algorithms.recurrence` — returns the total number of rabbit pairs alive after `problem.months` months under the standard 1-offspring rule with a fixed `problem.lifespan`-month lifetime
- **NO** modified capabilities; `RabbitProblem`/`RabbitProblemError` and `FibonacciRabbits` (spec 6) are untouched. The two problems coexist with distinct domain types since their parameters (`litterSize` vs `lifespan`) are semantically different.

## Capabilities

### New Capabilities

- `mortal-fibonacci-rabbits`: The `MortalRabbitProblem` validated domain type (enforcing positive months and lifespan), the `MortalRabbitProblemError` ADT, and the `MortalFibonacciRabbits.population` algorithm computing the total pair count after `n` months when every pair lives exactly `m` months.

### Modified Capabilities

None.

## Impact

- New files in `bio.domain.recurrence`: `MortalRabbitProblem.scala`, `MortalRabbitProblemError.scala`
- New file in `bio.algorithms.recurrence`: `MortalFibonacciRabbits.scala`
- New test files mirroring each new source file
- No changes to existing files
- No new external dependencies
- All existing 226 tests continue passing
