## ADDED Requirements

### Requirement: SubsetUniverseSizeError is a sealed ADT of SubsetUniverseSize construction failures
The system SHALL provide a `sealed trait SubsetUniverseSizeError` with cases `final case class NonPositive(value: Int)` (the supplied value was less than 1) and `final case class ExceedsMaximum(value: Int, max: Int)` (the supplied value exceeded the per-problem maximum). The type SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: NonPositive carries the offending value
- **WHEN** `SubsetUniverseSizeError.NonPositive(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NonPositive accepts a negative value
- **WHEN** `SubsetUniverseSizeError.NonPositive(-5)` is constructed
- **THEN** the value's `value` field equals `-5`

#### Scenario: ExceedsMaximum carries the offending value and the maximum
- **WHEN** `SubsetUniverseSizeError.ExceedsMaximum(1001, 1000)` is constructed
- **THEN** the value's `value` field equals `1001` and `max` equals `1000`

### Requirement: SubsetUniverseSize is a validated positive integer wrapper enforcing 1 <= n <= 1000
The system SHALL provide a `sealed abstract case class SubsetUniverseSize(value: Int)`. Construction SHALL be possible only through `SubsetUniverseSize.from(value: Int): Either[SubsetUniverseSizeError, SubsetUniverseSize]` enforcing `1 <= value <= 1000`. Validation SHALL apply in the order: lower bound, then upper bound (first failure wins). The synthesized `apply` and `copy` SHALL NOT be public — direct construction `SubsetUniverseSize(3)` MUST be a compile error. The type SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: n = 1 (minimum) is accepted
- **WHEN** `SubsetUniverseSize.from(1)` is called
- **THEN** the result is `Right(<SubsetUniverseSize with value=1>)`

#### Scenario: n = 3 (Rosalind sample) is accepted
- **WHEN** `SubsetUniverseSize.from(3)` is called
- **THEN** the result is `Right(<SubsetUniverseSize with value=3>)`

#### Scenario: n = 1000 (upper bound) is accepted
- **WHEN** `SubsetUniverseSize.from(1000)` is called
- **THEN** the result is `Right(<SubsetUniverseSize with value=1000>)`

#### Scenario: n = 0 is rejected as NonPositive
- **WHEN** `SubsetUniverseSize.from(0)` is called
- **THEN** the result is `Left(SubsetUniverseSizeError.NonPositive(0))`

#### Scenario: A negative n is rejected as NonPositive
- **WHEN** `SubsetUniverseSize.from(-5)` is called
- **THEN** the result is `Left(SubsetUniverseSizeError.NonPositive(-5))`

#### Scenario: n exceeding 1000 is rejected as ExceedsMaximum
- **WHEN** `SubsetUniverseSize.from(1001)` is called
- **THEN** the result is `Left(SubsetUniverseSizeError.ExceedsMaximum(1001, 1000))`

#### Scenario: Validation order — lower bound is checked before upper bound
- **WHEN** `SubsetUniverseSize.from(0)` is called (which is both `<1` and `<1000`)
- **THEN** the result is `Left(SubsetUniverseSizeError.NonPositive(0))` (the lower-bound failure wins)

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.combinatorics.SubsetUniverseSize(3)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: Subsets.count computes 2^n modulo 1,000,000
The system SHALL provide `Subsets.count(size: SubsetUniverseSize): Int` returning `2^size.value mod 1,000,000`. The algorithm SHALL reside in the `bio.algorithms.combinatorics` package. The function SHALL be total — every valid `SubsetUniverseSize` produces a defined `Int` in `[0, 999_999]`.

#### Scenario: Rosalind sample (n=3) produces 8
- **WHEN** `Subsets.count(size)` is called with `size = SubsetUniverseSize.from(3).toOption.get`
- **THEN** the result is `8` (= 2^3)

#### Scenario: n = 1 produces 2
- **WHEN** `Subsets.count(size)` is called with `size = SubsetUniverseSize.from(1).toOption.get`
- **THEN** the result is `2` (= 2^1, i.e. the empty set and {1})

#### Scenario: n = 10 produces 1024
- **WHEN** `Subsets.count(size)` is called with `size = SubsetUniverseSize.from(10).toOption.get`
- **THEN** the result is `1024` (= 2^10, no modulo wrap)

#### Scenario: n = 19 produces 524288 (last value before modulo first wraps)
- **WHEN** `Subsets.count(size)` is called with `size = SubsetUniverseSize.from(19).toOption.get`
- **THEN** the result is `524288` (= 2^19)

#### Scenario: n = 20 produces 48576 (first value where modulo wraps — 2^20 = 1048576 mod 1000000)
- **WHEN** `Subsets.count(size)` is called with `size = SubsetUniverseSize.from(20).toOption.get`
- **THEN** the result is `48576` (= 1048576 mod 1000000)

#### Scenario: Upper bound n = 1000 produces 69376 (= 2^1000 mod 1,000,000)
- **WHEN** `Subsets.count(size)` is called with `size = SubsetUniverseSize.from(1000).toOption.get`
- **THEN** the result is `69376` (and satisfies `0 <= result <= 999_999`)
