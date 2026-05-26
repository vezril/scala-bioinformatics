## ADDED Requirements

### Requirement: Genotype is a sealed ADT with three case objects
The system SHALL provide a `sealed trait Genotype` with exactly three case objects: `HomozygousDominant` (AA), `Heterozygous` (Aa), and `HomozygousRecessive` (aa). No other values of type `Genotype` SHALL be constructable.

#### Scenario: All three genotypes exist as case objects
- **WHEN** the `Genotype` sealed trait is inspected
- **THEN** it has exactly three subtypes: `Genotype.HomozygousDominant`, `Genotype.Heterozygous`, `Genotype.HomozygousRecessive`

#### Scenario: Pattern match on Genotype is exhaustive
- **WHEN** a `match` expression covers all three case objects
- **THEN** the compiler reports no non-exhaustive match warning

### Requirement: Population is a validated count of genotypes
The system SHALL provide a `final case class Population` representing counts of each genotype, constructable only through a smart constructor `Population.from(k: Int, m: Int, n: Int): Either[PopulationError, Population]`. Counts MUST be non-negative. Total population (`k + m + n`) MUST be at least 2 (the algorithm requires two distinct mating individuals). `Population` SHALL expose `total: Int` as a derived value equal to `k + m + n`.

#### Scenario: Valid population is accepted
- **WHEN** `Population.from(2, 2, 2)` is called
- **THEN** the result is `Right(Population(2, 2, 2))` with `total == 6`

#### Scenario: Zero counts are accepted when total is at least 2
- **WHEN** `Population.from(2, 0, 0)` is called
- **THEN** the result is `Right(Population(2, 0, 0))` with `total == 2`

#### Scenario: Negative count is rejected
- **WHEN** `Population.from(-1, 2, 2)` is called
- **THEN** the result is `Left(PopulationError.NegativeCount)`

#### Scenario: Insufficient population is rejected
- **WHEN** `Population.from(1, 0, 0)` is called (total < 2)
- **THEN** the result is `Left(PopulationError.InsufficientPopulation)`

#### Scenario: All-zero population is rejected
- **WHEN** `Population.from(0, 0, 0)` is called
- **THEN** the result is `Left(PopulationError.InsufficientPopulation)`

### Requirement: Probability is a validated value in [0, 1]
The system SHALL provide a `final class Probability` value class wrapping a `Double` in the range `[0.0, 1.0]`. The class SHALL be constructable through a smart constructor `Probability.from(d: Double): Either[ProbabilityError, Probability]` that rejects values outside `[0, 1]`, `NaN`, and infinities. An internal `private[bio] def unsafeFrom(d: Double): Probability` SHALL exist for trusted internal callers.

#### Scenario: Valid probability in range is accepted
- **WHEN** `Probability.from(0.5)` is called
- **THEN** the result is `Right(Probability(0.5))`

#### Scenario: Probability equal to 0.0 is accepted
- **WHEN** `Probability.from(0.0)` is called
- **THEN** the result is `Right(Probability(0.0))`

#### Scenario: Probability equal to 1.0 is accepted
- **WHEN** `Probability.from(1.0)` is called
- **THEN** the result is `Right(Probability(1.0))`

#### Scenario: Probability greater than 1 is rejected
- **WHEN** `Probability.from(1.5)` is called
- **THEN** the result is `Left(ProbabilityError.OutOfRange(1.5))`

#### Scenario: Negative probability is rejected
- **WHEN** `Probability.from(-0.1)` is called
- **THEN** the result is `Left(ProbabilityError.OutOfRange(-0.1))`

#### Scenario: NaN is rejected
- **WHEN** `Probability.from(Double.NaN)` is called
- **THEN** the result is `Left(ProbabilityError.NotFinite)`

#### Scenario: Positive infinity is rejected
- **WHEN** `Probability.from(Double.PositiveInfinity)` is called
- **THEN** the result is `Left(ProbabilityError.NotFinite)`

### Requirement: Probability of dominant phenotype is computed analytically
The system SHALL provide a pure, total function `MendelianInheritance.probabilityOfDominantPhenotype(pop: Population): Probability` that returns the probability that two uniformly randomly selected distinct mating organisms from `pop` produce offspring carrying at least one dominant allele (i.e., displaying the dominant phenotype). The implementation SHALL use the closed-form formula derived from Mendelian inheritance — Monte Carlo simulation SHALL NOT be used.

#### Scenario: Rosalind sample
- **WHEN** `probabilityOfDominantPhenotype` is called with `Population(2, 2, 2)`
- **THEN** the result equals `0.78333` within tolerance `±1e-5`

#### Scenario: All homozygous dominant always produces dominant offspring
- **WHEN** `probabilityOfDominantPhenotype` is called with `Population(2, 0, 0)`
- **THEN** the result equals `1.0` within tolerance `±1e-9`

#### Scenario: All homozygous recessive never produces dominant offspring
- **WHEN** `probabilityOfDominantPhenotype` is called with `Population(0, 0, 2)`
- **THEN** the result equals `0.0` within tolerance `±1e-9`

#### Scenario: All heterozygous yields three quarters dominant
- **WHEN** `probabilityOfDominantPhenotype` is called with `Population(0, 2, 0)`
- **THEN** the result equals `0.75` within tolerance `±1e-9`

#### Scenario: One of each genotype
- **WHEN** `probabilityOfDominantPhenotype` is called with `Population(1, 1, 1)`
- **THEN** the result equals `5.0 / 6.0` within tolerance `±1e-9`

#### Scenario: Heterozygous and recessive only
- **WHEN** `probabilityOfDominantPhenotype` is called with `Population(0, 1, 1)`
- **THEN** the result equals `0.5` within tolerance `±1e-9`

#### Scenario: Dominant and recessive only (no heterozygous)
- **WHEN** `probabilityOfDominantPhenotype` is called with `Population(1, 0, 1)`
- **THEN** the result equals `1.0` within tolerance `±1e-9` (offspring is always Aa)
