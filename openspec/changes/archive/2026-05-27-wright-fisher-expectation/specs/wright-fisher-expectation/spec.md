## ADDED Requirements

### Requirement: WrightFisherExpectationProblemError is a sealed ADT of WrightFisherExpectationProblem construction failures
The system SHALL provide a `sealed trait WrightFisherExpectationProblemError` with cases `final case class NonPositiveN(value: Int)`, `final case class NExceedsMaximum(value: Int, max: Int)`, and `final case class TooManyProbabilities(size: Int, max: Int)`. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: NonPositiveN carries the offending value
- **WHEN** `WrightFisherExpectationProblemError.NonPositiveN(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NExceedsMaximum carries the offending value and the maximum
- **WHEN** `WrightFisherExpectationProblemError.NExceedsMaximum(1000001, 1000000)` is constructed
- **THEN** the value's `value` field equals `1000001` and `max` equals `1000000`

#### Scenario: TooManyProbabilities carries the offending size and the maximum
- **WHEN** `WrightFisherExpectationProblemError.TooManyProbabilities(21, 20)` is constructed
- **THEN** the value's `size` field equals `21` and `max` equals `20`

### Requirement: WrightFisherExpectationProblem is a validated parameter bundle for the Wright-Fisher expectation algorithm
The system SHALL provide a `sealed abstract case class WrightFisherExpectationProblem(n: Int, p: Vector[bio.domain.stats.Probability])`. Construction SHALL be possible only through `WrightFisherExpectationProblem.from(n: Int, p: Vector[Probability]): Either[WrightFisherExpectationProblemError, WrightFisherExpectationProblem]` enforcing `1 <= n <= 1_000_000` and `p.size <= 20`. Validation SHALL apply in the order: `n` lower bound, `n` upper bound, then `p.size` upper bound. First failure wins. An empty `p` vector SHALL be accepted (produces an empty output). The synthesized `apply` and `copy` SHALL NOT be public — direct construction `WrightFisherExpectationProblem(17, Vector.empty)` MUST be a compile error. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: Rosalind sample parameters are accepted
- **WHEN** `WrightFisherExpectationProblem.from(17, Vector(0.1, 0.2, 0.3).map(Probability.from(_).toOption.get))` is called
- **THEN** the result is `Right(<WrightFisherExpectationProblem with n=17, p of length 3>)`

#### Scenario: Minimum-bound n (1) with single-element p is accepted
- **WHEN** `WrightFisherExpectationProblem.from(1, Vector(Probability.from(0.5).toOption.get))` is called
- **THEN** the result is `Right(<WrightFisherExpectationProblem with n=1>)`

#### Scenario: Upper-bound n (1,000,000) is accepted
- **WHEN** `WrightFisherExpectationProblem.from(1000000, Vector.empty)` is called
- **THEN** the result is `Right(<WrightFisherExpectationProblem with n=1000000, empty p>)`

#### Scenario: Upper-bound p size (20) is accepted
- **WHEN** `WrightFisherExpectationProblem.from(17, Vector.fill(20)(Probability.from(0.5).toOption.get))` is called
- **THEN** the result is `Right(<WrightFisherExpectationProblem with 20 probabilities>)`

#### Scenario: Empty p vector is accepted
- **WHEN** `WrightFisherExpectationProblem.from(17, Vector.empty)` is called
- **THEN** the result is `Right(<WrightFisherExpectationProblem with empty p>)`

#### Scenario: Non-positive n is rejected as NonPositiveN
- **WHEN** `WrightFisherExpectationProblem.from(0, Vector.empty)` is called
- **THEN** the result is `Left(WrightFisherExpectationProblemError.NonPositiveN(0))`

#### Scenario: n exceeding 1,000,000 is rejected as NExceedsMaximum
- **WHEN** `WrightFisherExpectationProblem.from(1000001, Vector.empty)` is called
- **THEN** the result is `Left(WrightFisherExpectationProblemError.NExceedsMaximum(1000001, 1000000))`

#### Scenario: p with more than 20 elements is rejected as TooManyProbabilities
- **WHEN** `WrightFisherExpectationProblem.from(17, Vector.fill(21)(Probability.from(0.5).toOption.get))` is called
- **THEN** the result is `Left(WrightFisherExpectationProblemError.TooManyProbabilities(21, 20))`

#### Scenario: Validation order — n lower bound is checked before n upper bound
- **WHEN** `WrightFisherExpectationProblem.from(0, Vector.fill(21)(Probability.from(0.5).toOption.get))` is called (n bad, p over)
- **THEN** the result is `Left(WrightFisherExpectationProblemError.NonPositiveN(0))`

#### Scenario: Validation order — n upper bound is checked before p.size check
- **WHEN** `WrightFisherExpectationProblem.from(1000001, Vector.fill(21)(Probability.from(0.5).toOption.get))` is called (n over, p over)
- **THEN** the result is `Left(WrightFisherExpectationProblemError.NExceedsMaximum(1000001, 1000000))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.genetics.WrightFisherExpectationProblem(17, Vector.empty)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: WrightFisher.expectedFrequencies computes E[Bin(n, p)] = n * p element-wise
The system SHALL provide `WrightFisher.expectedFrequencies(problem: WrightFisherExpectationProblem): Vector[Double]` returning a `Vector[Double]` of the same length as `problem.p` where each element equals `problem.n.toDouble * prob.value` for the corresponding probability `prob`. The algorithm SHALL be on the existing `WrightFisher` object in the `bio.algorithms.genetics` package (alongside the existing `atLeast` method from the `wright-fisher-genetic-drift` capability). The function SHALL be total — every valid `WrightFisherExpectationProblem` produces a defined `Vector[Double]`.

#### Scenario: Rosalind sample (n=17, P=[0.1, 0.2, 0.3]) produces [1.7, 3.4, 5.1] within 0.001 absolute error
- **WHEN** `WrightFisher.expectedFrequencies(problem)` is called with `problem = WrightFisherExpectationProblem.from(17, Vector(0.1, 0.2, 0.3).map(Probability.from(_).toOption.get)).toOption.get`
- **THEN** the result has length `3` and each element is within `0.001` of the corresponding expected value in `Vector(1.7, 3.4, 5.1)`

#### Scenario: An empty p vector yields an empty output
- **WHEN** `WrightFisher.expectedFrequencies(problem)` is called with `problem.p == Vector.empty`
- **THEN** the result is `Vector.empty`

#### Scenario: P containing 0.0 yields 0.0 (regardless of n)
- **WHEN** `WrightFisher.expectedFrequencies(problem)` is called with `n=1000, p=[0.0]`
- **THEN** `result(0)` equals `0.0`

#### Scenario: P containing 1.0 yields n exactly
- **WHEN** `WrightFisher.expectedFrequencies(problem)` is called with `n=1000, p=[1.0]`
- **THEN** `result(0)` equals `1000.0`

#### Scenario: Upper-bound n (1,000,000) and p=[0.5] yields 500000.0 exactly
- **WHEN** `WrightFisher.expectedFrequencies(problem)` is called with `n=1000000, p=[0.5]`
- **THEN** `result(0)` equals `500000.0`

#### Scenario: Result length always equals input p length
- **WHEN** `WrightFisher.expectedFrequencies(problem)` is called with `problem.p` of length 20
- **THEN** the result has length `20`

#### Scenario: Result is monotonically non-decreasing for a sorted ascending p
- **WHEN** `WrightFisher.expectedFrequencies(problem)` is called with `n=100, p=[0.1, 0.3, 0.5, 0.7, 0.9]`
- **THEN** for every consecutive pair `(result(i), result(i+1))` with `i in 0 until 4`, `result(i) <= result(i+1)`
