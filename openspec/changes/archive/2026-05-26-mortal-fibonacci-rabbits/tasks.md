## 1. MortalRabbitProblemError ADT

- [x] 1.1 Create `src/main/scala/bio/domain/recurrence/MortalRabbitProblemError.scala` with `package bio.domain.recurrence`, `sealed trait MortalRabbitProblemError`, and cases `final case class NonPositiveMonths(value: Int)` and `final case class NonPositiveLifespan(value: Int)`
- [x] 1.2 Run `sbt compile` — confirm clean compile

## 2. MortalRabbitProblem domain type (TDD)

- [x] 2.1 Write failing tests in `src/test/scala/bio/domain/recurrence/MortalRabbitProblemSpec.scala` for `MortalRabbitProblem.from` covering: valid `(6, 3)` accepted with both fields exposed, minimum-bound `(1, 1)` accepted, zero months → `Left(NonPositiveMonths(0))`, negative months → `Left(NonPositiveMonths(-1))`, zero lifespan → `Left(NonPositiveLifespan(0))`, negative lifespan → `Left(NonPositiveLifespan(-2))`, and invariant tests: `assertDoesNotCompile("""bio.domain.recurrence.MortalRabbitProblem(1, 1)""")` plus `.copy(months = 99)` rejection
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `src/main/scala/bio/domain/recurrence/MortalRabbitProblem.scala` in `bio.domain.recurrence`: `sealed abstract case class MortalRabbitProblem(months: Int, lifespan: Int)`. Companion `from(months, lifespan)`: if `months < 1` return `Left(NonPositiveMonths(months))`; else if `lifespan < 1` return `Left(NonPositiveLifespan(lifespan))`; else `Right(new MortalRabbitProblem(months, lifespan) {})`. Months-first validation order
- [x] 2.4 Run `sbt test` — confirm `MortalRabbitProblemSpec` passes (Green)

## 3. MortalFibonacciRabbits algorithm (TDD)

- [x] 3.1 Write failing tests in `src/test/scala/bio/algorithms/recurrence/MortalFibonacciRabbitsSpec.scala` for `MortalFibonacciRabbits.population` covering: Rosalind sample `(6, 3) → 4`, single month `(1, 5) → 1`, lifespan-1 reproduction failure `(2, 1) → 0`, lifespan-1 stays zero `(10, 1) → 0`, lifespan-1 at month 1 still returns 1, lifespan-≥-n equals classic Fibonacci `(6, 100) → 8` and `(10, 100) → 55`, edge `(2, 2) → 1`. Use a `mrp` helper that calls `MortalRabbitProblem.from(...).getOrElse(sys.error(...))`
- [x] 3.2 Run `sbt test` — confirm tests fail (Red)
- [x] 3.3 Implement `src/main/scala/bio/algorithms/recurrence/MortalFibonacciRabbits.scala` in `bio.algorithms.recurrence`. Signature: `def population(problem: MortalRabbitProblem): BigInt`. Algorithm: build initial vector `BigInt(1) +: Vector.fill(problem.lifespan - 1)(BigInt(0))`; fold `(2 to problem.months)` advancing the state — each step computes `newborns = v.drop(1).sum` and yields `newborns +: v.dropRight(1)`. Return `finalState.sum`. No `var`, no mutable collections. Import `bio.domain.recurrence.MortalRabbitProblem`
- [x] 3.4 Run `sbt test` — confirm `MortalFibonacciRabbitsSpec` passes (Green)
- [x] 3.5 Refactor: confirm no `var`, single fold over months range, idiomatic Vector operations (`drop(1)`, `dropRight(1)`, `+:`)
- [x] 3.6 Run `sbt test` — confirm all tests still pass after refactor

## 4. Final Verification

- [x] 4.1 Run `sbt clean compile` — zero errors (warnings unrelated to this change are acceptable)
- [x] 4.2 Run `sbt test` — all tests pass (count higher than 226)
- [x] 4.3 Verify the new files reside at `src/main/scala/bio/domain/recurrence/MortalRabbitProblem.scala`, `src/main/scala/bio/domain/recurrence/MortalRabbitProblemError.scala`, `src/main/scala/bio/algorithms/recurrence/MortalFibonacciRabbits.scala` with the expected `package` declarations
- [x] 4.4 Verify `MortalFibonacciRabbits.population` uses `BigInt` end-to-end (initial vector, intermediate sums, return type) to match `FibonacciRabbits` style and avoid silent overflow
