## 1. RabbitProblemError ADT

- [x] 1.1 Create `RabbitProblemError.scala` with `sealed trait RabbitProblemError` and two cases: `NonPositiveMonths(value: Int)` and `NegativeLitterSize(value: Int)`
- [x] 1.2 Run `sbt compile` — confirm clean compile

## 2. RabbitProblem (TDD)

- [x] 2.1 Write failing tests for `RabbitProblem.from`: valid input `(5, 3)` accepted, zero litter size `(10, 0)` accepted, single month `(1, 3)` accepted, zero months `(0, 3)` rejected with `NonPositiveMonths(0)`, negative months `(-5, 3)` rejected with `NonPositiveMonths(-5)`, negative litter `(5, -1)` rejected with `NegativeLitterSize(-1)`
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `RabbitProblem` as `final case class RabbitProblem private (months: Int, litterSize: Int)` with companion `RabbitProblem.from` returning `Either[RabbitProblemError, RabbitProblem]`
- [x] 2.4 Run `sbt test` — confirm RabbitProblem tests pass (Green)

## 3. FibonacciRabbits algorithm (TDD)

- [x] 3.1 Write failing tests for `FibonacciRabbits.population`: Rosalind sample `(5, 3) → 19`, base case `(1, 3) → 1`, base case `(2, 3) → 1`, standard Fibonacci `(10, 1) → 55`, zero litter `(40, 0) → 1`, `(5, 1) → 5`, third month `(3, 3) → 4`, fourth month `(4, 3) → 7`
- [x] 3.2 Run `sbt test` — confirm tests fail (Red)
- [x] 3.3 Implement `FibonacciRabbits.population(problem: RabbitProblem): BigInt` using a `foldLeft` over `3 to months` accumulating `(BigInt, BigInt)` representing `(F(n-2), F(n-1))`; handle base cases `months == 1 || months == 2` explicitly
- [x] 3.4 Run `sbt test` — confirm all FibonacciRabbits tests pass (Green)
- [x] 3.5 Refactor: verify no `var`, no mutable collections, no imperative loops; return type is `BigInt`; algorithm is `O(n)` time and `O(1)` space
- [x] 3.6 Run `sbt test` — confirm all tests still pass after refactor

## 4. Final Verification

- [x] 4.1 Run `sbt compile` — zero errors, zero warnings
- [x] 4.2 Run `sbt test` — all tests pass (count higher than 95)
- [x] 4.3 Review `FibonacciRabbits.scala`: pure function, total, returns `BigInt` directly, iterative not recursive (or `@tailrec` if recursive), no naive exponential implementation
