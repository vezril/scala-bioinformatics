# reversal-distance Specification

## Purpose

Computes the reversal distance between two permutations — the minimum number
of contiguous-interval reversals required to transform one into the other
(Rosalind spec — REAR, "Reversal Distance"). Provides a validated
`ReversalDistanceProblem` input bundle (equal length, length cap of 10), a
`ReversalDistance` result type, and the `ReversalDistanceSearch.distance`
bidirectional breadth-first-search algorithm over the reversal graph.

## Requirements

### Requirement: Reversal Distance Problem domain type

The system SHALL provide a validated `ReversalDistanceProblem` domain type in `bio.domain.combinatorics` wrapping a `source` and a `target` `Permutation`. It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(source: Permutation, target: Permutation): Either[ReversalDistanceProblemError, ReversalDistanceProblem]`. The constructor SHALL enforce, with first-failure-wins ordering: the two permutations have equal length, then that length is at most 10.

#### Scenario: Accepts two equal-length permutations within the bound
- **WHEN** `ReversalDistanceProblem.from` is called with two length-10 permutations
- **THEN** it returns a `Right` holding a `ReversalDistanceProblem` wrapping them

#### Scenario: Accepts two equal empty permutations
- **WHEN** `ReversalDistanceProblem.from` is called with two empty permutations
- **THEN** it returns a `Right` holding a `ReversalDistanceProblem`

#### Scenario: Rejects permutations of differing length
- **WHEN** `ReversalDistanceProblem.from` is called with a length-3 and a length-4 permutation
- **THEN** it returns a `Left` holding `ReversalDistanceProblemError.LengthMismatch(3, 4)`

#### Scenario: Rejects permutations longer than the BFS-tractable bound
- **WHEN** `ReversalDistanceProblem.from` is called with two length-11 permutations
- **THEN** it returns a `Left` holding `ReversalDistanceProblemError.LengthExceedsMax(11, 10)`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.combinatorics.ReversalDistanceProblem(source, target)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `ReversalDistanceProblem`
- **THEN** the code does not compile

### Requirement: Reversal Distance Problem error ADT

The system SHALL provide a `ReversalDistanceProblemError` sealed ADT in `bio.domain.combinatorics` enumerating the validation failures for `ReversalDistanceProblem`: `LengthMismatch(sourceLength: Int, targetLength: Int)` carrying the two differing lengths, and `LengthExceedsMax(length: Int, max: Int)` carrying the offending length and the bound.

#### Scenario: Reports the two differing lengths
- **WHEN** a length-5 and a length-8 permutation are rejected for unequal length
- **THEN** the error is `ReversalDistanceProblemError.LengthMismatch(5, 8)`

### Requirement: Reversal Distance result type

The system SHALL provide a `ReversalDistance` result type in `bio.domain.combinatorics` holding the integer reversal distance, and exposing a `format: String` that renders the distance as its decimal string.

#### Scenario: Formats the distance as a decimal string
- **WHEN** a `ReversalDistance` holding `9` is formatted
- **THEN** `format` returns `"9"`

### Requirement: Reversal distance algorithm

The system SHALL provide a `ReversalDistanceSearch` algorithm in `bio.algorithms.combinatorics` with a pure, total method `distance(problem: ReversalDistanceProblem): ReversalDistance`. It SHALL return the minimum number of interval reversals required to transform the source permutation into the target, computed by bidirectional breadth-first search over the reversal graph (each edge a single contiguous-interval reversal).

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `distance` is computed for each of the five sample pairs in order
- **THEN** the distances are `9`, `4`, `5`, `7`, and `0`

#### Scenario: Identical permutations have distance zero
- **WHEN** `distance` is computed for two identical length-10 permutations
- **THEN** the result is `0`

#### Scenario: A single reversal apart has distance one
- **WHEN** `distance` is computed for `1 2 3 4 5` and `1 4 3 2 5` (the interval `2 3 4` reversed)
- **THEN** the result is `1`

#### Scenario: Equal empty permutations have distance zero
- **WHEN** `distance` is computed for two empty permutations
- **THEN** the result is `0`
