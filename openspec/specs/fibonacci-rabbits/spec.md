## Purpose
Define the Fibonacci rabbits capability that computes rabbit population growth under a generalized Fibonacci recurrence, where each mature pair produces a fixed-size litter of offspring pairs every month. This is Rosalind problem FIB. The capability provides a validated `RabbitProblem` input (months and litter size) and a pure, iterative function `FibonacciRabbits.population` that returns the total number of rabbit pairs alive after the requested number of months as a `BigInt`, supporting arbitrarily large counts without overflow.

## Requirements
### Requirement: RabbitProblem is a validated recurrence input
The system SHALL provide a `final case class RabbitProblem` representing the parameters of the Fibonacci-rabbits recurrence (`months: Int`, `litterSize: Int`), constructable only through a smart constructor `RabbitProblem.from(months: Int, litterSize: Int): Either[RabbitProblemError, RabbitProblem]`. The smart constructor MUST reject `months < 1` and `litterSize < 0`.

#### Scenario: Valid input is accepted
- **WHEN** `RabbitProblem.from(5, 3)` is called
- **THEN** the result is `Right(RabbitProblem(5, 3))`

#### Scenario: Zero litter size is accepted
- **WHEN** `RabbitProblem.from(10, 0)` is called
- **THEN** the result is `Right(RabbitProblem(10, 0))`

#### Scenario: Single month is accepted
- **WHEN** `RabbitProblem.from(1, 3)` is called
- **THEN** the result is `Right(RabbitProblem(1, 3))`

#### Scenario: Zero months is rejected
- **WHEN** `RabbitProblem.from(0, 3)` is called
- **THEN** the result is `Left(RabbitProblemError.NonPositiveMonths(0))`

#### Scenario: Negative months is rejected
- **WHEN** `RabbitProblem.from(-5, 3)` is called
- **THEN** the result is `Left(RabbitProblemError.NonPositiveMonths(-5))`

#### Scenario: Negative litter size is rejected
- **WHEN** `RabbitProblem.from(5, -1)` is called
- **THEN** the result is `Left(RabbitProblemError.NegativeLitterSize(-1))`

### Requirement: FibonacciRabbits.population computes the generalized recurrence
The system SHALL provide a pure, total function `FibonacciRabbits.population(problem: RabbitProblem): BigInt` that returns the number of rabbit pairs alive after `problem.months` months, given the recurrence `F(n) = F(n-1) + k * F(n-2)` with base cases `F(1) = F(2) = 1` and `k = problem.litterSize`. The implementation MUST be iterative (`O(n)` time, `O(1)` space) — naive exponential recursion SHALL NOT be used.

#### Scenario: Rosalind sample (n=5, k=3)
- **WHEN** `population` is called with `RabbitProblem(5, 3)`
- **THEN** the result is `BigInt(19)`

#### Scenario: Base case n=1 returns 1 regardless of litter size
- **WHEN** `population` is called with `RabbitProblem(1, 3)`
- **THEN** the result is `BigInt(1)`

#### Scenario: Base case n=2 returns 1 regardless of litter size
- **WHEN** `population` is called with `RabbitProblem(2, 3)`
- **THEN** the result is `BigInt(1)`

#### Scenario: Standard Fibonacci with k=1 at n=10
- **WHEN** `population` is called with `RabbitProblem(10, 1)`
- **THEN** the result is `BigInt(55)` (standard Fibonacci F(10))

#### Scenario: Zero litter size yields constant 1
- **WHEN** `population` is called with `RabbitProblem(40, 0)`
- **THEN** the result is `BigInt(1)` (no offspring, original pair persists)

#### Scenario: Larger Rosalind-scale input produces correct count
- **WHEN** `population` is called with `RabbitProblem(5, 1)`
- **THEN** the result is `BigInt(5)` (standard Fibonacci F(5))

#### Scenario: Third-month derivation
- **WHEN** `population` is called with `RabbitProblem(3, 3)`
- **THEN** the result is `BigInt(4)` (F(3) = F(2) + 3*F(1) = 1 + 3 = 4)

#### Scenario: Fourth-month derivation
- **WHEN** `population` is called with `RabbitProblem(4, 3)`
- **THEN** the result is `BigInt(7)` (F(4) = F(3) + 3*F(2) = 4 + 3 = 7)
