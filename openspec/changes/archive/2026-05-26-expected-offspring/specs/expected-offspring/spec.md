## ADDED Requirements

### Requirement: CouplePopulationError is a sealed ADT of CouplePopulation construction failures
The system SHALL provide a `sealed trait CouplePopulationError` with cases `final case class NegativeCount(index: Int, value: Int)` and `final case class ExceedsMaxCount(index: Int, value: Int)`. The `index` field SHALL be 1-based, matching the Rosalind input position (1=AA×AA, 2=AA×Aa, 3=AA×aa, 4=Aa×Aa, 5=Aa×aa, 6=aa×aa). The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: NegativeCount carries the 1-based index and offending value
- **WHEN** `CouplePopulationError.NegativeCount(3, -5)` is constructed
- **THEN** the value's `index` is `3` and `value` is `-5`

#### Scenario: ExceedsMaxCount carries the 1-based index and offending value
- **WHEN** `CouplePopulationError.ExceedsMaxCount(1, 20001)` is constructed
- **THEN** the value's `index` is `1` and `value` is `20001`

### Requirement: CouplePopulation is a validated domain type holding six pairing counts
The system SHALL provide a `sealed abstract case class CouplePopulation(homDomHomDom: Int, homDomHet: Int, homDomHomRec: Int, hetHet: Int, hetHomRec: Int, homRecHomRec: Int)` representing a population of couples grouped by genotype pairing. Construction SHALL be possible only through `CouplePopulation.from(c1: Int, c2: Int, c3: Int, c4: Int, c5: Int, c6: Int): Either[CouplePopulationError, CouplePopulation]` enforcing `0 <= each <= 20000`. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `CouplePopulation(0, 0, 0, 0, 0, 0)` MUST be a compile error. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: All-zero population is accepted
- **WHEN** `CouplePopulation.from(0, 0, 0, 0, 0, 0)` is called
- **THEN** the result is `Right(<CouplePopulation with all six counts equal to 0>)`

#### Scenario: Rosalind sample population is accepted
- **WHEN** `CouplePopulation.from(1, 0, 0, 1, 0, 1)` is called
- **THEN** the result is a `Right` containing a `CouplePopulation` whose `homDomHomDom == 1`, `homDomHet == 0`, `homDomHomRec == 0`, `hetHet == 1`, `hetHomRec == 0`, `homRecHomRec == 1`

#### Scenario: Population at the upper boundary is accepted
- **WHEN** `CouplePopulation.from(20000, 20000, 20000, 20000, 20000, 20000)` is called
- **THEN** the result is `Right(<CouplePopulation with all six counts equal to 20000>)`

#### Scenario: A negative count is rejected with the 1-based index
- **WHEN** `CouplePopulation.from(1, 2, -3, 4, 5, 6)` is called
- **THEN** the result is `Left(CouplePopulationError.NegativeCount(3, -3))`

#### Scenario: A count exceeding 20000 is rejected with the 1-based index
- **WHEN** `CouplePopulation.from(20001, 0, 0, 0, 0, 0)` is called
- **THEN** the result is `Left(CouplePopulationError.ExceedsMaxCount(1, 20001))`

#### Scenario: Validation short-circuits on the first invalid count
- **WHEN** `CouplePopulation.from(-1, 20001, 0, 0, 0, 0)` is called (two invalid inputs)
- **THEN** the result is `Left(CouplePopulationError.NegativeCount(1, -1))` — the first failure wins; the second invalid value (`c2 = 20001`) is not reported

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.genetics.CouplePopulation(0, 0, 0, 0, 0, 0)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: ExpectedOffspring.dominantPhenotype computes the expected count of dominant-phenotype offspring
The system SHALL provide `ExpectedOffspring.dominantPhenotype(pop: CouplePopulation): Double` returning the expected number of dominant-phenotype offspring in the next generation, assuming every couple produces exactly two offspring. The algorithm SHALL reside in the `bio.algorithms.genetics` package. The function SHALL be total — every `CouplePopulation` produces a defined `Double`.

The formula SHALL apply the standard Mendelian per-couple multipliers:
- AA × AA: 2.0
- AA × Aa: 2.0
- AA × aa: 2.0
- Aa × Aa: 1.5
- Aa × aa: 1.0
- aa × aa: 0.0

#### Scenario: Rosalind sample produces 3.5
- **WHEN** `ExpectedOffspring.dominantPhenotype(pop)` is called with `pop = CouplePopulation.from(1, 0, 0, 1, 0, 1).toOption.get`
- **THEN** the result equals `3.5` within tolerance `±1e-9`

#### Scenario: All-zero population produces 0.0
- **WHEN** `ExpectedOffspring.dominantPhenotype(pop)` is called with `pop = CouplePopulation.from(0, 0, 0, 0, 0, 0).toOption.get`
- **THEN** the result is `0.0`

#### Scenario: A single AA × AA couple produces expected value 2.0
- **WHEN** `ExpectedOffspring.dominantPhenotype(pop)` is called with `pop = CouplePopulation.from(1, 0, 0, 0, 0, 0).toOption.get`
- **THEN** the result equals `2.0` within tolerance `±1e-9`

#### Scenario: A single Aa × Aa couple produces expected value 1.5
- **WHEN** `ExpectedOffspring.dominantPhenotype(pop)` is called with `pop = CouplePopulation.from(0, 0, 0, 1, 0, 0).toOption.get`
- **THEN** the result equals `1.5` within tolerance `±1e-9`

#### Scenario: A single Aa × aa couple produces expected value 1.0
- **WHEN** `ExpectedOffspring.dominantPhenotype(pop)` is called with `pop = CouplePopulation.from(0, 0, 0, 0, 1, 0).toOption.get`
- **THEN** the result equals `1.0` within tolerance `±1e-9`

#### Scenario: Any number of aa × aa couples contributes 0.0
- **WHEN** `ExpectedOffspring.dominantPhenotype(pop)` is called with `pop = CouplePopulation.from(0, 0, 0, 0, 0, 100).toOption.get`
- **THEN** the result is `0.0`

#### Scenario: Upper-boundary population produces 170000.0
- **WHEN** `ExpectedOffspring.dominantPhenotype(pop)` is called with `pop = CouplePopulation.from(20000, 20000, 20000, 20000, 20000, 20000).toOption.get`
- **THEN** the result equals `170000.0` within tolerance `±1e-6` (= 3 × 2.0 × 20000 + 1.5 × 20000 + 1.0 × 20000 + 0.0 × 20000)
