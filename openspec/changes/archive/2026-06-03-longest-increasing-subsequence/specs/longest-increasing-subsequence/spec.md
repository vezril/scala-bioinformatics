## ADDED Requirements

### Requirement: Permutation domain type

The system SHALL provide a validated `Permutation` domain type in `bio.domain.combinatorics` wrapping a `Vector[Int]` that is a permutation of `{1, …, n}` where `n` is its length. It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(values: Vector[Int]): Either[PermutationError, Permutation]`. The constructor SHALL enforce, with first-failure-wins ordering: length at most 10000, then that the values are exactly a permutation of `{1, …, length}`.

#### Scenario: Accepts a valid permutation
- **WHEN** `Permutation.from` is called with `Vector(5, 1, 4, 2, 3)`
- **THEN** it returns a `Right` holding a `Permutation` wrapping those values

#### Scenario: Accepts the empty permutation
- **WHEN** `Permutation.from` is called with an empty vector
- **THEN** it returns a `Right` holding a `Permutation`

#### Scenario: Rejects a sequence longer than the bound
- **WHEN** `Permutation.from` is called with a vector of length 10001
- **THEN** it returns a `Left` holding `PermutationError.TooLong(10001, 10000)`

#### Scenario: Rejects a sequence that is not a permutation of 1..n
- **WHEN** `Permutation.from` is called with `Vector(1, 2, 2)` (3 is missing, 2 repeats)
- **THEN** it returns a `Left` holding `PermutationError.NotAPermutation(Vector(1, 2, 2))`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.combinatorics.Permutation(Vector(1, 2, 3))` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `Permutation`
- **THEN** the code does not compile

### Requirement: Permutation error ADT

The system SHALL provide a `PermutationError` sealed ADT in `bio.domain.combinatorics` enumerating the validation failures for `Permutation`: `TooLong(length: Int, max: Int)` carrying the offending length and the bound, and `NotAPermutation(values: Vector[Int])` carrying the rejected sequence.

#### Scenario: Reports the offending length and the maximum
- **WHEN** a vector of length 12000 is rejected for exceeding the bound
- **THEN** the error is `PermutationError.TooLong(12000, 10000)`

### Requirement: Monotonic subsequences result type

The system SHALL provide a `MonotonicSubsequences` result type in `bio.domain.combinatorics` holding the `increasing` and `decreasing` subsequences as `Vector[Int]`, and exposing a `format: String` that renders the increasing values space-separated on the first line and the decreasing values space-separated on the second line.

#### Scenario: Formats the two subsequences on separate lines
- **WHEN** a `MonotonicSubsequences` with increasing `Vector(1, 2, 3)` and decreasing `Vector(5, 4, 2)` is formatted
- **THEN** `format` returns `"1 2 3\n5 4 2"`

### Requirement: Longest subsequences algorithm

The system SHALL provide a `LongestSubsequences` algorithm in `bio.algorithms.combinatorics` with a pure, total method `find(permutation: Permutation): MonotonicSubsequences`. The `increasing` field SHALL be a longest strictly-increasing subsequence of the permutation and the `decreasing` field SHALL be a longest strictly-decreasing subsequence, each computed in O(n log n) time. When more than one longest subsequence exists, any one of maximal length is acceptable.

#### Scenario: Produces longest subsequences for the canonical sample
- **WHEN** `find` is run on the permutation `5 1 4 2 3`
- **THEN** the increasing subsequence has length 3, is strictly increasing, and is a subsequence of the input
- **AND** the decreasing subsequence has length 3, is strictly decreasing, and is a subsequence of the input

#### Scenario: A sorted-ascending permutation has a full-length increasing run
- **WHEN** `find` is run on the permutation `1 2 3 4`
- **THEN** the increasing subsequence is `Vector(1, 2, 3, 4)`
- **AND** the decreasing subsequence has length 1

#### Scenario: A single-element permutation yields that element for both
- **WHEN** `find` is run on the permutation `1`
- **THEN** both the increasing and decreasing subsequences are `Vector(1)`

#### Scenario: The empty permutation yields two empty subsequences
- **WHEN** `find` is run on the empty permutation
- **THEN** both the increasing and decreasing subsequences are empty
