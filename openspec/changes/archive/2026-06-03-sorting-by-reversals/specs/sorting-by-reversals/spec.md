## ADDED Requirements

### Requirement: Reversal domain type

The system SHALL provide a `Reversal` domain type in `bio.domain.combinatorics` encoding a single reversal by its 1-based interval endpoints `from` and `to` (with `from < to`), and exposing a `format: String` that renders the endpoints space-separated.

#### Scenario: Formats the endpoints space-separated
- **WHEN** a `Reversal(4, 9)` is formatted
- **THEN** `format` returns `"4 9"`

### Requirement: Reversal Sorting result type

The system SHALL provide a `ReversalSorting` result type in `bio.domain.combinatorics` holding the reversal `distance` (an `Int`) and the ordered collection of `reversals` (`Vector[Reversal]`), and exposing a `format: String` that renders the distance on the first line followed by each reversal on its own line.

#### Scenario: Formats the distance and the reversals on separate lines
- **WHEN** a `ReversalSorting` with distance `2` and reversals `Vector(Reversal(4, 9), Reversal(2, 5))` is formatted
- **THEN** `format` returns `"2\n4 9\n2 5"`

#### Scenario: Formats a zero-distance sorting as just the count
- **WHEN** a `ReversalSorting` with distance `0` and no reversals is formatted
- **THEN** `format` returns `"0"`

### Requirement: Reversal sorting algorithm

The system SHALL provide a `ReversalSortingSearch` algorithm in `bio.algorithms.combinatorics` with a pure, total method `sort(problem: ReversalDistanceProblem): ReversalSorting`. The returned `distance` SHALL equal the reversal distance between the source and target permutations, and the returned `reversals` SHALL be an ordered collection of exactly `distance` reversals which, applied successively to the source permutation, yield the target. Each reversal's endpoints SHALL satisfy `1 ≤ from < to ≤ n`. When multiple sorting collections exist, any one is acceptable.

#### Scenario: Matches the canonical Rosalind sample distance and sorts the permutation
- **WHEN** `sort` is run on source `1 2 3 4 5 6 7 8 9 10` and target `1 8 9 3 2 7 6 5 4 10`
- **THEN** the distance is `2`
- **AND** the collection contains exactly 2 reversals which, applied in order to the source, yield the target

#### Scenario: Identical permutations need no reversals
- **WHEN** `sort` is run on two identical length-10 permutations
- **THEN** the distance is `0` and the reversal collection is empty

#### Scenario: A single reversal apart yields one sorting reversal
- **WHEN** `sort` is run on source `1 2 3 4 5` and target `1 4 3 2 5`
- **THEN** the distance is `1`
- **AND** the single returned reversal, applied to the source, yields the target

#### Scenario: Every returned reversal is a valid interval
- **WHEN** `sort` is run on any valid problem of length `n`
- **THEN** each returned reversal satisfies `1 ≤ from < to ≤ n`
