## ADDED Requirements

### Requirement: ChromosomePairsError is a sealed ADT of ChromosomePairs construction failures
The system SHALL provide a `sealed trait ChromosomePairsError` with cases `final case class NonPositive(value: Int)` (the supplied value was less than 1) and `final case class ExceedsMaximum(value: Int, max: Int)` (the supplied value exceeded the per-problem maximum). The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: NonPositive carries the offending value
- **WHEN** `ChromosomePairsError.NonPositive(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NonPositive accepts a negative value
- **WHEN** `ChromosomePairsError.NonPositive(-5)` is constructed
- **THEN** the value's `value` field equals `-5`

#### Scenario: ExceedsMaximum carries the offending value and the maximum
- **WHEN** `ChromosomePairsError.ExceedsMaximum(51, 50)` is constructed
- **THEN** the value's `value` field equals `51` and `max` equals `50`

### Requirement: ChromosomePairs is a validated positive integer wrapper enforcing 1 <= n <= 50
The system SHALL provide a `sealed abstract case class ChromosomePairs(value: Int)` representing the haploid chromosome-pair count `n` (such that the total chromosome count being modeled is `2n`). Construction SHALL be possible only through `ChromosomePairs.from(value: Int): Either[ChromosomePairsError, ChromosomePairs]` enforcing `1 <= value <= 50`. Validation SHALL apply in the order: lower bound, then upper bound (first failure wins). The synthesized `apply` and `copy` SHALL NOT be public — direct construction `ChromosomePairs(5)` MUST be a compile error. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: n = 1 (minimum) is accepted
- **WHEN** `ChromosomePairs.from(1)` is called
- **THEN** the result is `Right(<ChromosomePairs with value=1>)`

#### Scenario: n = 5 (Rosalind sample) is accepted
- **WHEN** `ChromosomePairs.from(5)` is called
- **THEN** the result is `Right(<ChromosomePairs with value=5>)`

#### Scenario: n = 50 (upper bound) is accepted
- **WHEN** `ChromosomePairs.from(50)` is called
- **THEN** the result is `Right(<ChromosomePairs with value=50>)`

#### Scenario: n = 0 is rejected as NonPositive
- **WHEN** `ChromosomePairs.from(0)` is called
- **THEN** the result is `Left(ChromosomePairsError.NonPositive(0))`

#### Scenario: A negative n is rejected as NonPositive
- **WHEN** `ChromosomePairs.from(-3)` is called
- **THEN** the result is `Left(ChromosomePairsError.NonPositive(-3))`

#### Scenario: n exceeding 50 is rejected as ExceedsMaximum
- **WHEN** `ChromosomePairs.from(51)` is called
- **THEN** the result is `Left(ChromosomePairsError.ExceedsMaximum(51, 50))`

#### Scenario: Validation order — lower bound is checked before upper bound
- **WHEN** `ChromosomePairs.from(0)` is called (which is both `<1` and `<50`)
- **THEN** the result is `Left(ChromosomePairsError.NonPositive(0))` (the lower-bound failure wins)

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.genetics.ChromosomePairs(5)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: IndependentSegregation.logProbs computes log10(P(X >= k)) for k=1..2n under X ~ Bin(2n, 1/2)
The system SHALL provide `IndependentSegregation.logProbs(pairs: ChromosomePairs): Vector[Double]` returning a `Vector[Double]` of length `2 * pairs.value` whose entry at index `k - 1` equals `log10(P(X >= k))` for `X ~ Bin(2 * pairs.value, 1/2)`. The algorithm SHALL reside in the `bio.algorithms.genetics` package. The function SHALL be total — every valid `ChromosomePairs` produces a defined `Vector[Double]`.

#### Scenario: Rosalind sample (n=5) produces the canonical 10-element vector within 0.001 absolute error
- **WHEN** `IndependentSegregation.logProbs(pairs)` is called with `pairs = ChromosomePairs.from(5).toOption.get`
- **THEN** the result has length `10` and each element is within `0.001` of the corresponding expected value in `Vector(0.000, -0.004, -0.024, -0.082, -0.206, -0.424, -0.765, -1.262, -1.969, -3.010)`

#### Scenario: n=1 yields log10(3/4) and log10(1/4)
- **WHEN** `IndependentSegregation.logProbs(pairs)` is called with `pairs = ChromosomePairs.from(1).toOption.get`
- **THEN** the result has length `2`; `result(0)` is within `1e-9` of `log10(0.75)` (≈ `-0.124939`); `result(1)` is within `1e-9` of `log10(0.25)` (≈ `-0.602060`)

#### Scenario: Result length equals 2 * pairs.value
- **WHEN** `IndependentSegregation.logProbs(pairs)` is called with `pairs = ChromosomePairs.from(7).toOption.get`
- **THEN** the result has length `14`

#### Scenario: Last entry equals log10(0.5^(2n)) — sharing all 2n chromosomes
- **WHEN** `IndependentSegregation.logProbs(pairs)` is called with `pairs = ChromosomePairs.from(50).toOption.get`
- **THEN** `result(99)` is within `1e-6` of `-100 * log10(2.0)` (≈ `-30.10300`)

#### Scenario: First entry approaches 0 — sharing at least 1 chromosome is nearly certain
- **WHEN** `IndependentSegregation.logProbs(pairs)` is called with `pairs = ChromosomePairs.from(10).toOption.get`
- **THEN** `result(0)` is within `1e-6` of `Math.log10(1.0 - Math.pow(0.5, 20.0))` (the probability of sharing at least one is `1 - (1/2)^20`)

#### Scenario: Upper-bound n=50 produces a 100-element vector with all entries <= 0
- **WHEN** `IndependentSegregation.logProbs(pairs)` is called with `pairs = ChromosomePairs.from(50).toOption.get`
- **THEN** the result has length `100` and every element is `<= 0` (log of a probability is non-positive)

#### Scenario: Entries are monotonically non-increasing — upper-tail probabilities only shrink as k grows
- **WHEN** `IndependentSegregation.logProbs(pairs)` is called with `pairs = ChromosomePairs.from(5).toOption.get`
- **THEN** for every consecutive pair `(result(i), result(i+1))` with `i in 0 until 9`, `result(i) >= result(i+1)`
