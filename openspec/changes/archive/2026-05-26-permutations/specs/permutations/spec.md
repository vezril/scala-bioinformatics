## ADDED Requirements

### Requirement: PermutationLengthError is a sealed ADT of PermutationLength construction failures
The system SHALL provide a `sealed trait PermutationLengthError` with cases `final case class NonPositive(value: Int)` and `final case class ExceedsMaximum(value: Int, max: Int)`. The type SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: NonPositive carries the offending value
- **WHEN** `PermutationLengthError.NonPositive(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: ExceedsMaximum carries the offending value and the maximum
- **WHEN** `PermutationLengthError.ExceedsMaximum(8, 7)` is constructed
- **THEN** the value's `value` field equals `8` and `max` equals `7`

### Requirement: PermutationLength is a validated value in [1, 7]
The system SHALL provide a `sealed abstract case class PermutationLength(value: Int)`. Construction SHALL be possible only through `PermutationLength.from(value: Int): Either[PermutationLengthError, PermutationLength]` enforcing `1 <= value <= 7`. The synthesized `apply` and `copy` SHALL NOT be public — direct construction `PermutationLength(3)` MUST be a compile error. The type SHALL reside in the `bio.domain.combinatorics` package.

#### Scenario: Lower-bound value 1 is accepted
- **WHEN** `PermutationLength.from(1)` is called
- **THEN** the result is `Right(<PermutationLength with value=1>)`

#### Scenario: Rosalind sample value 3 is accepted
- **WHEN** `PermutationLength.from(3)` is called
- **THEN** the result is `Right(<PermutationLength with value=3>)`

#### Scenario: Upper-bound value 7 is accepted
- **WHEN** `PermutationLength.from(7)` is called
- **THEN** the result is `Right(<PermutationLength with value=7>)`

#### Scenario: Zero is rejected as NonPositive
- **WHEN** `PermutationLength.from(0)` is called
- **THEN** the result is `Left(PermutationLengthError.NonPositive(0))`

#### Scenario: Negative value is rejected as NonPositive
- **WHEN** `PermutationLength.from(-1)` is called
- **THEN** the result is `Left(PermutationLengthError.NonPositive(-1))`

#### Scenario: Value 8 is rejected as ExceedsMaximum
- **WHEN** `PermutationLength.from(8)` is called
- **THEN** the result is `Left(PermutationLengthError.ExceedsMaximum(8, 7))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.combinatorics.PermutationLength(3)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: Permutations.enumerate produces every permutation of {1, ..., length}
The system SHALL provide `Permutations.enumerate(length: PermutationLength): Vector[Vector[Int]]` returning every permutation of `{1, 2, ..., length.value}`. The result SHALL contain exactly `length.value!` permutations. Each permutation SHALL be a `Vector[Int]` of size `length.value` containing every value in `1..length.value` exactly once. The result SHALL contain no duplicate permutations. The algorithm SHALL reside in the `bio.algorithms.combinatorics` package. The function SHALL be total — every valid `PermutationLength` produces a defined non-empty result.

#### Scenario: Length 1 produces the singleton [[1]]
- **WHEN** `Permutations.enumerate(PermutationLength.from(1).toOption.get)` is called
- **THEN** the result equals `Vector(Vector(1))`

#### Scenario: Length 2 produces both orderings
- **WHEN** `Permutations.enumerate(PermutationLength.from(2).toOption.get)` is called
- **THEN** the result has size `2` and contains both `Vector(1, 2)` and `Vector(2, 1)`

#### Scenario: Rosalind sample length 3 produces 6 permutations
- **WHEN** `Permutations.enumerate(PermutationLength.from(3).toOption.get)` is called
- **THEN** the result has size `6` (= 3!) and contains all six permutations: `Vector(1, 2, 3)`, `Vector(1, 3, 2)`, `Vector(2, 1, 3)`, `Vector(2, 3, 1)`, `Vector(3, 1, 2)`, `Vector(3, 2, 1)`

#### Scenario: Length 7 produces 5040 permutations
- **WHEN** `Permutations.enumerate(PermutationLength.from(7).toOption.get)` is called
- **THEN** the result has size `5040` (= 7!)

#### Scenario: Every permutation has length equal to the input
- **WHEN** `Permutations.enumerate(PermutationLength.from(5).toOption.get)` is called
- **THEN** every element of the result is a `Vector[Int]` of size `5`

#### Scenario: Every permutation contains each value 1..n exactly once
- **WHEN** `Permutations.enumerate(PermutationLength.from(4).toOption.get)` is called
- **THEN** every permutation, when converted to a `Set`, equals `Set(1, 2, 3, 4)` (no duplicates, no missing values)

#### Scenario: The result contains no duplicate permutations
- **WHEN** `Permutations.enumerate(PermutationLength.from(5).toOption.get)` is called
- **THEN** the result's `.distinct.size` equals its `.size` (every permutation is unique)
