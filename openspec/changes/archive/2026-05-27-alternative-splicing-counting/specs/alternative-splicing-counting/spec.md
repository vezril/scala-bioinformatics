## ADDED Requirements

### Requirement: CombinationSumProblemError is a sealed ADT of CombinationSumProblem construction failures
The system SHALL provide a `sealed trait CombinationSumProblemError` with cases `final case class NegativeN(value: Int)`, `final case class NExceedsMaximum(value: Int, max: Int)`, `final case class NegativeM(value: Int)`, and `final case class MExceedsN(m: Int, n: Int)`. The type SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: NegativeN carries the offending value
- **WHEN** `CombinationSumProblemError.NegativeN(-1)` is constructed
- **THEN** the value's `value` field equals `-1`

#### Scenario: NExceedsMaximum carries the offending value and the maximum
- **WHEN** `CombinationSumProblemError.NExceedsMaximum(2001, 2000)` is constructed
- **THEN** the value's `value` field equals `2001` and `max` equals `2000`

#### Scenario: NegativeM carries the offending value
- **WHEN** `CombinationSumProblemError.NegativeM(-3)` is constructed
- **THEN** the value's `value` field equals `-3`

#### Scenario: MExceedsN carries both offending inputs
- **WHEN** `CombinationSumProblemError.MExceedsN(5, 3)` is constructed
- **THEN** the value's `m` field equals `5` and `n` equals `3`

### Requirement: CombinationSumProblem is a validated parameter bundle enforcing 0 <= m <= n <= 2000
The system SHALL provide a `sealed abstract case class CombinationSumProblem(n: Int, m: Int)`. Construction SHALL be possible only through `CombinationSumProblem.from(n: Int, m: Int): Either[CombinationSumProblemError, CombinationSumProblem]` enforcing `n >= 0`, `n <= 2000`, `m >= 0`, and `m <= n`. Validation SHALL apply in the order: `n` lower bound, `n` upper bound, `m` lower bound, then `m <= n` cross-constraint. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `CombinationSumProblem(6, 3)` MUST be a compile error. The type SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: Rosalind sample parameters (n=6, m=3) are accepted
- **WHEN** `CombinationSumProblem.from(6, 3)` is called
- **THEN** the result is `Right(<CombinationSumProblem with n=6, m=3>)`

#### Scenario: Minimum-bound parameters (n=0, m=0) are accepted
- **WHEN** `CombinationSumProblem.from(0, 0)` is called
- **THEN** the result is `Right(<CombinationSumProblem with n=0, m=0>)`

#### Scenario: Upper-bound parameters (n=2000, m=2000) are accepted
- **WHEN** `CombinationSumProblem.from(2000, 2000)` is called
- **THEN** the result is `Right(<CombinationSumProblem with n=2000, m=2000>)`

#### Scenario: Equal m and n (m = n) at a mid-range value is accepted
- **WHEN** `CombinationSumProblem.from(5, 5)` is called
- **THEN** the result is `Right(<CombinationSumProblem with n=5, m=5>)`

#### Scenario: Negative n is rejected
- **WHEN** `CombinationSumProblem.from(-1, 0)` is called
- **THEN** the result is `Left(CombinationSumProblemError.NegativeN(-1))`

#### Scenario: n exceeding 2000 is rejected
- **WHEN** `CombinationSumProblem.from(2001, 0)` is called
- **THEN** the result is `Left(CombinationSumProblemError.NExceedsMaximum(2001, 2000))`

#### Scenario: Negative m is rejected
- **WHEN** `CombinationSumProblem.from(10, -1)` is called
- **THEN** the result is `Left(CombinationSumProblemError.NegativeM(-1))`

#### Scenario: m exceeding n is rejected
- **WHEN** `CombinationSumProblem.from(3, 5)` is called
- **THEN** the result is `Left(CombinationSumProblemError.MExceedsN(5, 3))`

#### Scenario: Validation order — n lower bound first
- **WHEN** `CombinationSumProblem.from(-1, -1)` is called (both n and m negative)
- **THEN** the result is `Left(CombinationSumProblemError.NegativeN(-1))`

#### Scenario: Validation order — n upper bound before m checks
- **WHEN** `CombinationSumProblem.from(2001, -1)` is called (n over, m negative)
- **THEN** the result is `Left(CombinationSumProblemError.NExceedsMaximum(2001, 2000))`

#### Scenario: Validation order — m lower bound before m <= n cross-constraint
- **WHEN** `CombinationSumProblem.from(3, -5)` is called (m negative; also would violate m<=n trivially if not for that check, but -5 <= 3 is true so the cross-constraint alone wouldn't trigger)
- **THEN** the result is `Left(CombinationSumProblemError.NegativeM(-5))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.combinatorics.CombinationSumProblem(6, 3)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: Combinations.sumFrom computes the modular tail-sum of binomial coefficients
The system SHALL provide `Combinations.sumFrom(problem: CombinationSumProblem): Int` returning `(Σ_{k=problem.m}^{problem.n} C(problem.n, k)) mod 1,000,000`. The algorithm SHALL reside in the `bio.algorithms.combinatorics` package. The function SHALL be total — every valid `CombinationSumProblem` produces a defined `Int` in `[0, 999_999]`.

#### Scenario: Rosalind sample (n=6, m=3) produces 42
- **WHEN** `Combinations.sumFrom(problem)` is called with `problem = CombinationSumProblem.from(6, 3).toOption.get`
- **THEN** the result is `42` (= C(6,3) + C(6,4) + C(6,5) + C(6,6) = 20 + 15 + 6 + 1)

#### Scenario: n = m yields C(n, n) = 1
- **WHEN** `Combinations.sumFrom(problem)` is called with `problem = CombinationSumProblem.from(6, 6).toOption.get`
- **THEN** the result is `1`

#### Scenario: m = 0 yields 2^n (full row sum)
- **WHEN** `Combinations.sumFrom(problem)` is called with `problem = CombinationSumProblem.from(6, 0).toOption.get`
- **THEN** the result is `64` (= 2^6)

#### Scenario: n = 0, m = 0 yields 1
- **WHEN** `Combinations.sumFrom(problem)` is called with `problem = CombinationSumProblem.from(0, 0).toOption.get`
- **THEN** the result is `1` (= C(0, 0))

#### Scenario: A mid-range example (n=10, m=5) produces 638
- **WHEN** `Combinations.sumFrom(problem)` is called with `problem = CombinationSumProblem.from(10, 5).toOption.get`
- **THEN** the result is `638` (= C(10,5)+C(10,6)+C(10,7)+C(10,8)+C(10,9)+C(10,10) = 252+210+120+45+10+1)

#### Scenario: Upper bound n=2000, m=0 produces 29376 (= 2^2000 mod 1,000,000)
- **WHEN** `Combinations.sumFrom(problem)` is called with `problem = CombinationSumProblem.from(2000, 0).toOption.get`
- **THEN** the result is `29376`

#### Scenario: Upper bound n=2000, m=2000 produces 1
- **WHEN** `Combinations.sumFrom(problem)` is called with `problem = CombinationSumProblem.from(2000, 2000).toOption.get`
- **THEN** the result is `1` (= C(2000, 2000))
