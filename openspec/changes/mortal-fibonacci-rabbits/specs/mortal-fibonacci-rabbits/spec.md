## ADDED Requirements

### Requirement: MortalRabbitProblemError is a sealed ADT of MortalRabbitProblem construction failures
The system SHALL provide a `sealed trait MortalRabbitProblemError` with cases `final case class NonPositiveMonths(value: Int)` and `final case class NonPositiveLifespan(value: Int)`. The type SHALL reside in the `bio.domain.recurrence` package.

#### Scenario: NonPositiveMonths carries the offending value
- **WHEN** `MortalRabbitProblemError.NonPositiveMonths(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NonPositiveLifespan carries the offending value
- **WHEN** `MortalRabbitProblemError.NonPositiveLifespan(-3)` is constructed
- **THEN** the value's `value` field equals `-3`

### Requirement: MortalRabbitProblem is a validated parameter bundle for the mortal-Fibonacci recurrence
The system SHALL provide a `sealed abstract case class MortalRabbitProblem(months: Int, lifespan: Int)`. Construction SHALL be possible only through `MortalRabbitProblem.from(months: Int, lifespan: Int): Either[MortalRabbitProblemError, MortalRabbitProblem]` enforcing `months >= 1` and `lifespan >= 1`. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `MortalRabbitProblem(1, 1)` MUST be a compile error. The type SHALL reside in the `bio.domain.recurrence` package.

#### Scenario: Valid parameters are accepted
- **WHEN** `MortalRabbitProblem.from(6, 3)` is called
- **THEN** the result is `Right(<MortalRabbitProblem with months=6, lifespan=3>)`

#### Scenario: Minimum-bound parameters are accepted
- **WHEN** `MortalRabbitProblem.from(1, 1)` is called
- **THEN** the result is `Right(<MortalRabbitProblem with months=1, lifespan=1>)`

#### Scenario: Zero months is rejected
- **WHEN** `MortalRabbitProblem.from(0, 3)` is called
- **THEN** the result is `Left(MortalRabbitProblemError.NonPositiveMonths(0))`

#### Scenario: Negative months is rejected
- **WHEN** `MortalRabbitProblem.from(-1, 3)` is called
- **THEN** the result is `Left(MortalRabbitProblemError.NonPositiveMonths(-1))`

#### Scenario: Zero lifespan is rejected
- **WHEN** `MortalRabbitProblem.from(5, 0)` is called
- **THEN** the result is `Left(MortalRabbitProblemError.NonPositiveLifespan(0))`

#### Scenario: Negative lifespan is rejected
- **WHEN** `MortalRabbitProblem.from(5, -2)` is called
- **THEN** the result is `Left(MortalRabbitProblemError.NonPositiveLifespan(-2))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.recurrence.MortalRabbitProblem(1, 1)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: MortalFibonacciRabbits.population computes the live pair count after n months
The system SHALL provide `MortalFibonacciRabbits.population(problem: MortalRabbitProblem): BigInt` returning the number of rabbit pairs alive at the end of month `problem.months`, under the rule that each pair produces one offspring pair per month from its second month onward and dies after exactly `problem.lifespan` months. The algorithm SHALL reside in the `bio.algorithms.recurrence` package. The result SHALL be non-negative.

#### Scenario: Rosalind sample produces 4
- **WHEN** `MortalFibonacciRabbits.population(problem)` is called with `problem = MortalRabbitProblem.from(6, 3).toOption.get`
- **THEN** the result is `BigInt(4)`

#### Scenario: A single month with any lifespan returns 1
- **WHEN** `MortalFibonacciRabbits.population(problem)` is called with `problem = MortalRabbitProblem.from(1, 5).toOption.get`
- **THEN** the result is `BigInt(1)`

#### Scenario: Lifespan of 1 produces 0 from month 2 onward (pair dies before reproducing)
- **WHEN** `MortalFibonacciRabbits.population(problem)` is called with `problem = MortalRabbitProblem.from(2, 1).toOption.get`
- **THEN** the result is `BigInt(0)`

#### Scenario: Lifespan of 1 stays at 0 for any month >= 2
- **WHEN** `MortalFibonacciRabbits.population(problem)` is called with `problem = MortalRabbitProblem.from(10, 1).toOption.get`
- **THEN** the result is `BigInt(0)`

#### Scenario: Lifespan of exactly 1 at month 1 still returns 1
- **WHEN** `MortalFibonacciRabbits.population(problem)` is called with `problem = MortalRabbitProblem.from(1, 1).toOption.get`
- **THEN** the result is `BigInt(1)`

#### Scenario: When lifespan >= n, the result equals classic Fibonacci (no deaths within the window)
- **WHEN** `MortalFibonacciRabbits.population(problem)` is called with `problem = MortalRabbitProblem.from(6, 100).toOption.get`
- **THEN** the result is `BigInt(8)` (= F(6))

#### Scenario: Classic Fibonacci sanity check at n=10 with non-restrictive lifespan
- **WHEN** `MortalFibonacciRabbits.population(problem)` is called with `problem = MortalRabbitProblem.from(10, 100).toOption.get`
- **THEN** the result is `BigInt(55)` (= F(10))

#### Scenario: Two months with lifespan 2 returns 1 (pair matured but did not yet reproduce that month)
- **WHEN** `MortalFibonacciRabbits.population(problem)` is called with `problem = MortalRabbitProblem.from(2, 2).toOption.get`
- **THEN** the result is `BigInt(1)`
