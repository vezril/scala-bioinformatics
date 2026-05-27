## Purpose
Define the partial-permutations counting capability: the `PartialPermutationProblem` validated parameter bundle (n + k with `k ≤ n` cross-constraint), its error ADT, and the `PartialPermutations.count(problem): Int` algorithm computing `P(n, k) = n × (n-1) × ... × (n-k+1) mod 1,000,000` via an incremental product with per-step modulo. Serves the Rosalind "Partial Permutations" problem and extends the spec-15-era `combinatorics` subdomain with its second algorithm.

## Requirements

### Requirement: PartialPermutationProblemError is a sealed ADT of PartialPermutationProblem construction failures
The system SHALL provide a `sealed trait PartialPermutationProblemError` with cases `final case class NonPositiveN(value: Int)`, `final case class NExceedsMaximum(value: Int, max: Int)`, `final case class NonPositiveK(value: Int)`, `final case class KExceedsMaximum(value: Int, max: Int)`, and `final case class KExceedsN(k: Int, n: Int)`. The type SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: NonPositiveN carries the offending value
- **WHEN** `PartialPermutationProblemError.NonPositiveN(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NExceedsMaximum carries the offending value and the maximum
- **WHEN** `PartialPermutationProblemError.NExceedsMaximum(101, 100)` is constructed
- **THEN** the value's `value` field equals `101` and `max` equals `100`

#### Scenario: NonPositiveK carries the offending value
- **WHEN** `PartialPermutationProblemError.NonPositiveK(-1)` is constructed
- **THEN** the value's `value` field equals `-1`

#### Scenario: KExceedsMaximum carries the offending value and the maximum
- **WHEN** `PartialPermutationProblemError.KExceedsMaximum(11, 10)` is constructed
- **THEN** the value's `value` field equals `11` and `max` equals `10`

#### Scenario: KExceedsN carries both offending inputs
- **WHEN** `PartialPermutationProblemError.KExceedsN(5, 3)` is constructed
- **THEN** the value's `k` field equals `5` and `n` equals `3`

### Requirement: PartialPermutationProblem is a validated parameter bundle for the partial-permutations algorithm
The system SHALL provide a `sealed abstract case class PartialPermutationProblem(n: Int, k: Int)`. Construction SHALL be possible only through `PartialPermutationProblem.from(n: Int, k: Int): Either[PartialPermutationProblemError, PartialPermutationProblem]` enforcing `1 <= n <= 100`, `1 <= k <= 10`, and `k <= n`. Validation SHALL apply in the order: `n` lower bound, `n` upper bound, `k` lower bound, `k` upper bound, `k <= n` cross-constraint. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `PartialPermutationProblem(21, 7)` MUST be a compile error. The type SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: Rosalind sample parameters are accepted
- **WHEN** `PartialPermutationProblem.from(21, 7)` is called
- **THEN** the result is `Right(<PartialPermutationProblem with n=21, k=7>)`

#### Scenario: Lower-bound parameters (1, 1) are accepted
- **WHEN** `PartialPermutationProblem.from(1, 1)` is called
- **THEN** the result is `Right(<PartialPermutationProblem with n=1, k=1>)`

#### Scenario: Upper-bound parameters (100, 10) are accepted
- **WHEN** `PartialPermutationProblem.from(100, 10)` is called
- **THEN** the result is `Right(<PartialPermutationProblem with n=100, k=10>)`

#### Scenario: Equal n and k (boundary k = n) is accepted
- **WHEN** `PartialPermutationProblem.from(5, 5)` is called
- **THEN** the result is `Right(<PartialPermutationProblem with n=5, k=5>)`

#### Scenario: Zero n is rejected
- **WHEN** `PartialPermutationProblem.from(0, 1)` is called
- **THEN** the result is `Left(PartialPermutationProblemError.NonPositiveN(0))`

#### Scenario: n exceeding 100 is rejected
- **WHEN** `PartialPermutationProblem.from(101, 1)` is called
- **THEN** the result is `Left(PartialPermutationProblemError.NExceedsMaximum(101, 100))`

#### Scenario: Zero k is rejected
- **WHEN** `PartialPermutationProblem.from(10, 0)` is called
- **THEN** the result is `Left(PartialPermutationProblemError.NonPositiveK(0))`

#### Scenario: k exceeding 10 is rejected
- **WHEN** `PartialPermutationProblem.from(10, 11)` is called
- **THEN** the result is `Left(PartialPermutationProblemError.KExceedsMaximum(11, 10))`

#### Scenario: k exceeding n is rejected
- **WHEN** `PartialPermutationProblem.from(3, 5)` is called
- **THEN** the result is `Left(PartialPermutationProblemError.KExceedsN(5, 3))`

#### Scenario: Validation order — n lower bound first
- **WHEN** `PartialPermutationProblem.from(0, 0)` is called (both invalid)
- **THEN** the result is `Left(PartialPermutationProblemError.NonPositiveN(0))`

#### Scenario: Validation order — n upper bound before k checks
- **WHEN** `PartialPermutationProblem.from(101, 0)` is called (n over, k under)
- **THEN** the result is `Left(PartialPermutationProblemError.NExceedsMaximum(101, 100))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.combinatorics.PartialPermutationProblem(21, 7)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: PartialPermutations.count computes P(n, k) modulo 1,000,000
The system SHALL provide `PartialPermutations.count(problem: PartialPermutationProblem): Int` returning `P(n, k) mod 1_000_000` where `P(n, k) = n × (n-1) × ... × (n-k+1)`. The algorithm SHALL reside in the `bio.algorithms.combinatorics` package. The function SHALL be total — every valid `PartialPermutationProblem` produces a defined `Int` in `[0, 999_999]`.

#### Scenario: Rosalind sample (n=21, k=7) produces 51200
- **WHEN** `PartialPermutations.count(problem)` is called with `problem = PartialPermutationProblem.from(21, 7).toOption.get`
- **THEN** the result is `51200`

#### Scenario: P(n, 1) = n for any n
- **WHEN** `PartialPermutations.count(problem)` is called with `problem = PartialPermutationProblem.from(5, 1).toOption.get`
- **THEN** the result is `5`

#### Scenario: P(1, 1) = 1
- **WHEN** `PartialPermutations.count(problem)` is called with `problem = PartialPermutationProblem.from(1, 1).toOption.get`
- **THEN** the result is `1`

#### Scenario: P(n, n) = n!  (no modulo wrap for small n)
- **WHEN** `PartialPermutations.count(problem)` is called with `problem = PartialPermutationProblem.from(5, 5).toOption.get`
- **THEN** the result is `120` (= 5!)

#### Scenario: P(7, 7) = 5040 = 7!
- **WHEN** `PartialPermutations.count(problem)` is called with `problem = PartialPermutationProblem.from(7, 7).toOption.get`
- **THEN** the result is `5040`

#### Scenario: P(10, 3) = 720
- **WHEN** `PartialPermutations.count(problem)` is called with `problem = PartialPermutationProblem.from(10, 3).toOption.get`
- **THEN** the result is `720` (= 10 × 9 × 8)

#### Scenario: Modulo wrap for the upper-bound inputs (n=100, k=10) produces 472000
- **WHEN** `PartialPermutations.count(problem)` is called with `problem = PartialPermutationProblem.from(100, 10).toOption.get`
- **THEN** the result is `472000` (= P(100, 10) mod 1,000,000, where P(100, 10) = 62,815,650,955,529,472,000)
