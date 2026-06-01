## Purpose
Define the counting-unrooted-binary-trees capability: the `LeafCount` validated value-type wrapper enforcing `1 ≤ n ≤ 1000`, its `LeafCountError` ADT, and the `UnrootedBinaryTrees.count(leaves): Int` algorithm computing the odd double factorial `(2n − 5)!! mod 1,000,000`. Serves the Rosalind "Counting Unrooted Binary Trees" (CUNR) problem, sharing the single-value-type input pattern and per-step-modulo computation pattern with the other `combinatorics` subdomain algorithms.

## Requirements

### Requirement: LeafCountError is a sealed ADT of LeafCount construction failures

The system SHALL provide a `sealed trait LeafCountError` with cases
`final case class NonPositive(value: Int)` (the supplied value was less than 1)
and `final case class ExceedsMaximum(value: Int, max: Int)` (the supplied value
exceeded the per-problem maximum). The type SHALL reside in the
`bio.domain.combinatorics` package.

#### Scenario: NonPositive carries the offending value

- **WHEN** `LeafCountError.NonPositive(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NonPositive accepts a negative value

- **WHEN** `LeafCountError.NonPositive(-5)` is constructed
- **THEN** the value's `value` field equals `-5`

#### Scenario: ExceedsMaximum carries the offending value and the maximum

- **WHEN** `LeafCountError.ExceedsMaximum(1001, 1000)` is constructed
- **THEN** the value's `value` field equals `1001` and `max` equals `1000`

### Requirement: LeafCount is a validated positive integer wrapper enforcing 1 <= n <= 1000

The system SHALL provide a `sealed abstract case class LeafCount(value: Int)`.
Construction SHALL be possible only through
`LeafCount.from(value: Int): Either[LeafCountError, LeafCount]` enforcing
`1 <= value <= 1000`. Validation SHALL apply in the order: lower bound, then
upper bound (first failure wins). The synthesized `apply` and `copy` SHALL NOT
be public — direct construction `LeafCount(5)` MUST be a compile error. The type
SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: n = 1 (minimum) is accepted

- **WHEN** `LeafCount.from(1)` is called
- **THEN** the result is `Right(<LeafCount with value=1>)`

#### Scenario: n = 5 (Rosalind sample) is accepted

- **WHEN** `LeafCount.from(5)` is called
- **THEN** the result is `Right(<LeafCount with value=5>)`

#### Scenario: n = 1000 (upper bound) is accepted

- **WHEN** `LeafCount.from(1000)` is called
- **THEN** the result is `Right(<LeafCount with value=1000>)`

#### Scenario: n = 0 is rejected as NonPositive

- **WHEN** `LeafCount.from(0)` is called
- **THEN** the result is `Left(LeafCountError.NonPositive(0))`

#### Scenario: A negative n is rejected as NonPositive

- **WHEN** `LeafCount.from(-5)` is called
- **THEN** the result is `Left(LeafCountError.NonPositive(-5))`

#### Scenario: n exceeding 1000 is rejected as ExceedsMaximum

- **WHEN** `LeafCount.from(1001)` is called
- **THEN** the result is `Left(LeafCountError.ExceedsMaximum(1001, 1000))`

#### Scenario: Validation order — lower bound is checked before upper bound

- **WHEN** `LeafCount.from(0)` is called
- **THEN** the result is `Left(LeafCountError.NonPositive(0))` (the lower-bound failure wins)

#### Scenario: Direct apply does not compile

- **WHEN** source code `bio.domain.combinatorics.LeafCount(5)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: UnrootedBinaryTrees.count computes (2n - 5)!! modulo 1,000,000

The system SHALL provide
`UnrootedBinaryTrees.count(leaves: LeafCount): Int` returning the number of
distinct unrooted binary trees on `n = leaves.value` labeled leaves modulo
1,000,000 — the odd double factorial `(2n − 5)!! = 1·3·5···(2n − 5)` for
`n ≥ 3`, and `1` for `n ≤ 2`. The algorithm SHALL reside in the
`bio.algorithms.combinatorics` package and SHALL be total — every valid
`LeafCount` produces a defined `Int` in `[0, 999_999]`.

#### Scenario: Rosalind sample (n = 5) produces 15

- **WHEN** `UnrootedBinaryTrees.count(leaves)` is called with `leaves = LeafCount.from(5).toOption.get`
- **THEN** the result is `15`

#### Scenario: n = 4 produces 3

- **WHEN** `UnrootedBinaryTrees.count(leaves)` is called with `leaves = LeafCount.from(4).toOption.get`
- **THEN** the result is `3`

#### Scenario: n = 3 produces 1 (single tree)

- **WHEN** `UnrootedBinaryTrees.count(leaves)` is called with `leaves = LeafCount.from(3).toOption.get`
- **THEN** the result is `1`

#### Scenario: n = 2 produces 1 (empty product edge case)

- **WHEN** `UnrootedBinaryTrees.count(leaves)` is called with `leaves = LeafCount.from(2).toOption.get`
- **THEN** the result is `1`

#### Scenario: n = 1 produces 1 (empty product edge case)

- **WHEN** `UnrootedBinaryTrees.count(leaves)` is called with `leaves = LeafCount.from(1).toOption.get`
- **THEN** the result is `1`

#### Scenario: n = 6 produces 105

- **WHEN** `UnrootedBinaryTrees.count(leaves)` is called with `leaves = LeafCount.from(6).toOption.get`
- **THEN** the result is `105`

#### Scenario: n = 1000 (upper bound) stays within Int range and modulus

- **WHEN** `UnrootedBinaryTrees.count(leaves)` is called with `leaves = LeafCount.from(1000).toOption.get`
- **THEN** the result is an `Int` in `[0, 999_999]`
