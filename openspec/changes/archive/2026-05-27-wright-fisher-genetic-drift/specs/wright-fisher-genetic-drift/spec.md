## ADDED Requirements

### Requirement: WrightFisherProblemError is a sealed ADT of WrightFisherProblem construction failures
The system SHALL provide a `sealed trait WrightFisherProblemError` with cases `final case class NonPositiveN(value: Int)`, `final case class NExceedsMaximum(value: Int, max: Int)`, `final case class NonPositiveM(value: Int)`, `final case class MExceedsTotalAlleles(value: Int, max: Int)`, `final case class NonPositiveG(value: Int)`, `final case class GExceedsMaximum(value: Int, max: Int)`, `final case class NonPositiveK(value: Int)`, and `final case class KExceedsTotalAlleles(value: Int, max: Int)`. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: NonPositiveN carries the offending value
- **WHEN** `WrightFisherProblemError.NonPositiveN(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NExceedsMaximum carries the offending value and the maximum
- **WHEN** `WrightFisherProblemError.NExceedsMaximum(8, 7)` is constructed
- **THEN** the value's `value` field equals `8` and `max` equals `7`

#### Scenario: NonPositiveM carries the offending value
- **WHEN** `WrightFisherProblemError.NonPositiveM(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: MExceedsTotalAlleles carries the offending value and the computed maximum
- **WHEN** `WrightFisherProblemError.MExceedsTotalAlleles(9, 8)` is constructed (for n=4, max = 2*4 = 8)
- **THEN** the value's `value` field equals `9` and `max` equals `8`

#### Scenario: NonPositiveG carries the offending value
- **WHEN** `WrightFisherProblemError.NonPositiveG(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: GExceedsMaximum carries the offending value and the maximum
- **WHEN** `WrightFisherProblemError.GExceedsMaximum(7, 6)` is constructed
- **THEN** the value's `value` field equals `7` and `max` equals `6`

#### Scenario: NonPositiveK carries the offending value
- **WHEN** `WrightFisherProblemError.NonPositiveK(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: KExceedsTotalAlleles carries the offending value and the computed maximum
- **WHEN** `WrightFisherProblemError.KExceedsTotalAlleles(9, 8)` is constructed (for n=4, max = 2*4 = 8)
- **THEN** the value's `value` field equals `9` and `max` equals `8`

### Requirement: WrightFisherProblem is a validated parameter bundle for the Wright-Fisher genetic-drift algorithm
The system SHALL provide a `sealed abstract case class WrightFisherProblem(n: Int, m: Int, g: Int, k: Int)`. Construction SHALL be possible only through `WrightFisherProblem.from(n: Int, m: Int, g: Int, k: Int): Either[WrightFisherProblemError, WrightFisherProblem]` enforcing `1 <= n <= 7`, `1 <= m <= 2*n`, `1 <= g <= 6`, and `1 <= k <= 2*n`. Validation SHALL apply in the order: `n` lower bound, `n` upper bound, `m` lower bound, `m` upper bound (cross-constraint on `n`), `g` lower bound, `g` upper bound, `k` lower bound, `k` upper bound (cross-constraint on `n`). First failure wins. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `WrightFisherProblem(4, 6, 2, 1)` MUST be a compile error. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: Rosalind sample parameters are accepted
- **WHEN** `WrightFisherProblem.from(4, 6, 2, 1)` is called
- **THEN** the result is `Right(<WrightFisherProblem with n=4, m=6, g=2, k=1>)`

#### Scenario: Minimum-bound parameters (n=1, m=1, g=1, k=1) are accepted
- **WHEN** `WrightFisherProblem.from(1, 1, 1, 1)` is called
- **THEN** the result is `Right(<WrightFisherProblem with n=1, m=1, g=1, k=1>)`

#### Scenario: Upper-bound parameters (n=7, m=14, g=6, k=14) are accepted
- **WHEN** `WrightFisherProblem.from(7, 14, 6, 14)` is called
- **THEN** the result is `Right(<WrightFisherProblem with n=7, m=14, g=6, k=14>)`

#### Scenario: Equal m at the 2n boundary is accepted
- **WHEN** `WrightFisherProblem.from(4, 8, 1, 1)` is called (m = 2n = 8)
- **THEN** the result is `Right(<WrightFisherProblem with n=4, m=8, g=1, k=1>)`

#### Scenario: Non-positive n is rejected as NonPositiveN
- **WHEN** `WrightFisherProblem.from(0, 1, 1, 1)` is called
- **THEN** the result is `Left(WrightFisherProblemError.NonPositiveN(0))`

#### Scenario: n exceeding 7 is rejected as NExceedsMaximum
- **WHEN** `WrightFisherProblem.from(8, 1, 1, 1)` is called
- **THEN** the result is `Left(WrightFisherProblemError.NExceedsMaximum(8, 7))`

#### Scenario: Non-positive m is rejected as NonPositiveM
- **WHEN** `WrightFisherProblem.from(4, 0, 1, 1)` is called
- **THEN** the result is `Left(WrightFisherProblemError.NonPositiveM(0))`

#### Scenario: m exceeding 2n is rejected as MExceedsTotalAlleles
- **WHEN** `WrightFisherProblem.from(4, 9, 1, 1)` is called (m=9 > 2*4=8)
- **THEN** the result is `Left(WrightFisherProblemError.MExceedsTotalAlleles(9, 8))`

#### Scenario: Non-positive g is rejected as NonPositiveG
- **WHEN** `WrightFisherProblem.from(4, 1, 0, 1)` is called
- **THEN** the result is `Left(WrightFisherProblemError.NonPositiveG(0))`

#### Scenario: g exceeding 6 is rejected as GExceedsMaximum
- **WHEN** `WrightFisherProblem.from(4, 1, 7, 1)` is called
- **THEN** the result is `Left(WrightFisherProblemError.GExceedsMaximum(7, 6))`

#### Scenario: Non-positive k is rejected as NonPositiveK
- **WHEN** `WrightFisherProblem.from(4, 1, 1, 0)` is called
- **THEN** the result is `Left(WrightFisherProblemError.NonPositiveK(0))`

#### Scenario: k exceeding 2n is rejected as KExceedsTotalAlleles
- **WHEN** `WrightFisherProblem.from(4, 1, 1, 9)` is called (k=9 > 2*4=8)
- **THEN** the result is `Left(WrightFisherProblemError.KExceedsTotalAlleles(9, 8))`

#### Scenario: Validation order — n lower bound first
- **WHEN** `WrightFisherProblem.from(0, 0, 0, 0)` is called (all invalid)
- **THEN** the result is `Left(WrightFisherProblemError.NonPositiveN(0))`

#### Scenario: Validation order — n upper bound before m checks
- **WHEN** `WrightFisherProblem.from(8, 0, 0, 0)` is called (n over, m/g/k all bad)
- **THEN** the result is `Left(WrightFisherProblemError.NExceedsMaximum(8, 7))`

#### Scenario: Validation order — m upper bound before g and k checks
- **WHEN** `WrightFisherProblem.from(4, 9, 0, 0)` is called (n valid, m over, g/k bad)
- **THEN** the result is `Left(WrightFisherProblemError.MExceedsTotalAlleles(9, 8))`

#### Scenario: Validation order — g upper bound before k checks
- **WHEN** `WrightFisherProblem.from(4, 1, 7, 0)` is called (n/m valid, g over, k bad)
- **THEN** the result is `Left(WrightFisherProblemError.GExceedsMaximum(7, 6))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.genetics.WrightFisherProblem(4, 6, 2, 1)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: WrightFisher.atLeast computes the Wright-Fisher tail probability after g generations
The system SHALL provide `WrightFisher.atLeast(problem: WrightFisherProblem): bio.domain.stats.Probability` returning the probability that, after `problem.g` generations of the Wright-Fisher Markov chain starting from `problem.m` dominant alleles in a population with `2*problem.n` chromosomes, at least `problem.k` recessive alleles are present. The algorithm SHALL reside in the `bio.algorithms.genetics` package. The function SHALL be total — every valid `WrightFisherProblem` produces a defined `Probability`.

#### Scenario: Rosalind sample (n=4, m=6, g=2, k=1) produces ≈ 0.772 within 0.001 absolute error
- **WHEN** `WrightFisher.atLeast(problem)` is called with `problem = WrightFisherProblem.from(4, 6, 2, 1).toOption.get`
- **THEN** the result's `.value` is within `0.001` of `0.7717925`

#### Scenario: All-dominant absorbing state (m=2n) with g=1 yields 0 — no recessive can ever appear
- **WHEN** `WrightFisher.atLeast(problem)` is called with `problem = WrightFisherProblem.from(4, 8, 1, 1).toOption.get` (m = 2n, all dominant)
- **THEN** the result's `.value` equals `0.0` (the all-dominant state is absorbing — every subsequent generation samples with p = 1, producing 0 recessive with probability 1)

#### Scenario: Single-generation, single recessive needed, m=2n-1 yields 1 − (3/4)^4
- **WHEN** `WrightFisher.atLeast(problem)` is called with `problem = WrightFisherProblem.from(2, 3, 1, 1).toOption.get` (n=2, 2n=4, m=3 dominant, g=1, k=1)
- **THEN** the result's `.value` is within `1e-9` of `1.0 - Math.pow(0.75, 4.0)` (≈ `0.68359375`)

#### Scenario: Result is monotonically non-increasing in k (the tail shrinks as the threshold grows)
- **WHEN** `WrightFisher.atLeast` is called twice with `problem1 = WrightFisherProblem.from(4, 6, 2, 1).toOption.get` and `problem2 = WrightFisherProblem.from(4, 6, 2, 2).toOption.get`
- **THEN** `result1.value >= result2.value`

#### Scenario: Upper-bound parameters (n=7, m=7, g=6, k=7) produce a defined value within [0, 1]
- **WHEN** `WrightFisher.atLeast(problem)` is called with `problem = WrightFisherProblem.from(7, 7, 6, 7).toOption.get`
- **THEN** the result's `.value` is within `0.001` of `0.5385700` and satisfies `0.0 <= result.value <= 1.0`

#### Scenario: Output is always a valid Probability
- **WHEN** `WrightFisher.atLeast(problem)` is called with any valid `WrightFisherProblem`
- **THEN** the result's `.value` satisfies `0.0 <= value <= 1.0` (structurally guaranteed: the tail-sum of a probability distribution is itself a probability)
