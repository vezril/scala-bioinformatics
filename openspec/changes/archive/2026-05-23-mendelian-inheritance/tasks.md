## 1. Genotype ADT (TDD)

- [x] 1.1 Write failing tests for `Genotype`: all three case objects exist; exhaustive pattern match compiles without warning
- [x] 1.2 Run `sbt test` — confirm tests fail (Red)
- [x] 1.3 Implement `Genotype` sealed trait with `HomozygousDominant`, `Heterozygous`, `HomozygousRecessive` case objects
- [x] 1.4 Run `sbt test` — confirm Genotype tests pass (Green)

## 2. PopulationError ADT

- [x] 2.1 Create `PopulationError.scala` with `sealed trait PopulationError` and two cases: `NegativeCount` and `InsufficientPopulation`
- [x] 2.2 Run `sbt compile` — confirm clean compile

## 3. Population (TDD)

- [x] 3.1 Write failing tests for `Population.from`: valid input `(2,2,2)` accepted with `total == 6`, zero counts accepted when total ≥ 2 `(2,0,0)`, negative count rejected, total < 2 rejected `(1,0,0)`, all-zero `(0,0,0)` rejected
- [x] 3.2 Run `sbt test` — confirm tests fail (Red)
- [x] 3.3 Implement `Population` as `final case class Population private (homozygousDominant: Int, heterozygous: Int, homozygousRecessive: Int)` with a `total: Int` derived field and a companion `Population.from` returning `Either[PopulationError, Population]`
- [x] 3.4 Run `sbt test` — confirm Population tests pass (Green)

## 4. ProbabilityError ADT

- [x] 4.1 Create `ProbabilityError.scala` with `sealed trait ProbabilityError` and cases `OutOfRange(value: Double)` and `NotFinite`
- [x] 4.2 Run `sbt compile` — confirm clean compile

## 5. Probability (TDD)

- [x] 5.1 Write failing tests for `Probability.from`: `0.5` accepted, `0.0` accepted, `1.0` accepted, `1.5` rejected as `OutOfRange`, `-0.1` rejected as `OutOfRange`, `NaN` rejected as `NotFinite`, `Double.PositiveInfinity` rejected as `NotFinite`
- [x] 5.2 Run `sbt test` — confirm tests fail (Red)
- [x] 5.3 Implement `Probability` as `final class Probability private (val value: Double) extends AnyVal` with companion `from` returning `Either[ProbabilityError, Probability]` and a `private[bio] unsafeFrom` for trusted internal use
- [x] 5.4 Run `sbt test` — confirm Probability tests pass (Green)

## 6. MendelianInheritance algorithm (TDD)

- [x] 6.1 Write failing tests for `MendelianInheritance.probabilityOfDominantPhenotype`: Rosalind sample `(2,2,2) → 0.78333 ±1e-5`, all-AA `(2,0,0) → 1.0`, all-aa `(0,0,2) → 0.0`, all-Aa `(0,2,0) → 0.75`, one-of-each `(1,1,1) → 5/6`, Aa+aa only `(0,1,1) → 0.5`, AA+aa only `(1,0,1) → 1.0`
- [x] 6.2 Run `sbt test` — confirm tests fail (Red)
- [x] 6.3 Implement `MendelianInheritance.probabilityOfDominantPhenotype(pop: Population): Probability` using the closed-form formula `1 - [m(m-1)/4 + m*n + n(n-1)] / (N*(N-1))`, wrapping the result via `Probability.unsafeFrom`
- [x] 6.4 Run `sbt test` — confirm all MendelianInheritance tests pass (Green)
- [x] 6.5 Refactor: verify no `var`, no mutable state, no imperative loops; ensure return type is `Probability` (not `Either` or raw `Double`)
- [x] 6.6 Run `sbt test` — confirm all tests still pass after refactor

## 7. Final Verification

- [x] 7.1 Run `sbt compile` — zero errors, zero warnings
- [x] 7.2 Run `sbt test` — all tests pass (count higher than 71)
- [x] 7.3 Review `MendelianInheritance.scala`: pure function, total, returns `Probability` directly via `unsafeFrom`, no floating-point operations leak outside the algorithm
