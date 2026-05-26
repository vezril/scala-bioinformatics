## ADDED Requirements

### Requirement: IndependentAllelesProblemError is a sealed ADT of IndependentAllelesProblem construction failures
The system SHALL provide a `sealed trait IndependentAllelesProblemError` with cases `final case class NonPositiveGenerations(value: Int)`, `final case class NonPositiveAtLeast(value: Int)`, and `final case class AtLeastExceedsPopulation(atLeast: Int, generations: Int)`. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: NonPositiveGenerations carries the offending value
- **WHEN** `IndependentAllelesProblemError.NonPositiveGenerations(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NonPositiveAtLeast carries the offending value
- **WHEN** `IndependentAllelesProblemError.NonPositiveAtLeast(-2)` is constructed
- **THEN** the value's `value` field equals `-2`

#### Scenario: AtLeastExceedsPopulation carries both offending inputs
- **WHEN** `IndependentAllelesProblemError.AtLeastExceedsPopulation(5, 2)` is constructed
- **THEN** the value's `atLeast` field equals `5` and `generations` equals `2`

### Requirement: IndependentAllelesProblem is a validated parameter bundle for the independent-alleles algorithm
The system SHALL provide a `sealed abstract case class IndependentAllelesProblem(generations: Int, atLeast: Int)` exposing `populationSize: Long = 1L << generations`. Construction SHALL be possible only through `IndependentAllelesProblem.from(generations: Int, atLeast: Int): Either[IndependentAllelesProblemError, IndependentAllelesProblem]` enforcing `generations >= 1`, `atLeast >= 1`, and `atLeast <= 2^generations`. Validation SHALL apply in that order (generations first, then atLeast, then the cross-constraint). The synthesized `apply` and `copy` SHALL NOT be public — direct construction `IndependentAllelesProblem(2, 1)` MUST be a compile error. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: Rosalind sample parameters are accepted
- **WHEN** `IndependentAllelesProblem.from(2, 1)` is called
- **THEN** the result is `Right(<IndependentAllelesProblem with generations=2, atLeast=1>)` and `problem.populationSize` is `4`

#### Scenario: Boundary parameters atLeast = 2^generations are accepted
- **WHEN** `IndependentAllelesProblem.from(3, 8)` is called (atLeast equals 2^3 = 8)
- **THEN** the result is `Right(<IndependentAllelesProblem with generations=3, atLeast=8>)`

#### Scenario: Zero generations is rejected
- **WHEN** `IndependentAllelesProblem.from(0, 1)` is called
- **THEN** the result is `Left(IndependentAllelesProblemError.NonPositiveGenerations(0))`

#### Scenario: Negative generations is rejected
- **WHEN** `IndependentAllelesProblem.from(-1, 1)` is called
- **THEN** the result is `Left(IndependentAllelesProblemError.NonPositiveGenerations(-1))`

#### Scenario: Zero atLeast is rejected
- **WHEN** `IndependentAllelesProblem.from(2, 0)` is called
- **THEN** the result is `Left(IndependentAllelesProblemError.NonPositiveAtLeast(0))`

#### Scenario: Negative atLeast is rejected
- **WHEN** `IndependentAllelesProblem.from(2, -3)` is called
- **THEN** the result is `Left(IndependentAllelesProblemError.NonPositiveAtLeast(-3))`

#### Scenario: atLeast exceeding 2^generations is rejected
- **WHEN** `IndependentAllelesProblem.from(2, 5)` is called (2^2 = 4 < 5)
- **THEN** the result is `Left(IndependentAllelesProblemError.AtLeastExceedsPopulation(5, 2))`

#### Scenario: Validation order — generations checked before atLeast
- **WHEN** `IndependentAllelesProblem.from(0, 0)` is called (both invalid)
- **THEN** the result is `Left(IndependentAllelesProblemError.NonPositiveGenerations(0))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.genetics.IndependentAllelesProblem(2, 1)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: IndependentAlleles.probability computes the binomial-tail probability
The system SHALL provide `IndependentAlleles.probability(problem: IndependentAllelesProblem): Probability` returning `P(X >= problem.atLeast)` where `X ~ Binomial(2^problem.generations, 1/4)`. The algorithm SHALL reside in the `bio.algorithms.genetics` package. The function SHALL be total — every valid `IndependentAllelesProblem` produces a defined `Probability` in `[0, 1]`.

#### Scenario: Rosalind sample (k=2, N=1) produces ~0.684
- **WHEN** `IndependentAlleles.probability(problem)` is called with `problem = IndependentAllelesProblem.from(2, 1).toOption.get`
- **THEN** the result's value equals `0.684` within tolerance `±1e-3`

#### Scenario: One generation, at-least-1 organisms produces 7/16
- **WHEN** `IndependentAlleles.probability(problem)` is called with `problem = IndependentAllelesProblem.from(1, 1).toOption.get`
- **THEN** the result's value equals `7.0 / 16.0` within tolerance `±1e-9`

#### Scenario: One generation, at-least-2 organisms produces 1/16
- **WHEN** `IndependentAlleles.probability(problem)` is called with `problem = IndependentAllelesProblem.from(1, 2).toOption.get`
- **THEN** the result's value equals `1.0 / 16.0` within tolerance `±1e-9`

#### Scenario: All 4 must be Aa Bb at k=2 yields 1/256
- **WHEN** `IndependentAlleles.probability(problem)` is called with `problem = IndependentAllelesProblem.from(2, 4).toOption.get`
- **THEN** the result's value equals `1.0 / 256.0` within tolerance `±1e-9`

#### Scenario: Large population with at-least-1 approaches 1.0
- **WHEN** `IndependentAlleles.probability(problem)` is called with `problem = IndependentAllelesProblem.from(7, 1).toOption.get` (2^7 = 128 organisms)
- **THEN** the result's value equals `1.0 - math.pow(0.75, 128.0)` within tolerance `±1e-12` (extremely close to 1.0)

#### Scenario: All-must-match in a large population is positive but tiny
- **WHEN** `IndependentAlleles.probability(problem)` is called with `problem = IndependentAllelesProblem.from(7, 128).toOption.get`
- **THEN** the result's value is positive (`> 0.0`) and equals `math.pow(0.25, 128.0)` within tolerance `±1e-80` (about 10^-77)

#### Scenario: Result is wrapped as Probability, value in [0, 1]
- **WHEN** `IndependentAlleles.probability(problem)` is called with any valid `IndependentAllelesProblem`
- **THEN** the result is a `Probability` value (not a bare `Double`), with `value` in `[0.0, 1.0]`
