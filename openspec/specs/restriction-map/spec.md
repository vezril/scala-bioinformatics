# Creating a Restriction Map

## Purpose

This capability reconstructs a set `X` of `n` nonnegative integer positions whose difference multiset `ΔX` equals a given multiset `L` of `C(n,2)` pairwise distances. It solves the Rosalind PDPL problem — the Turnpike (Partial Digest) problem — via backtracking, returning the positions in ascending order including `0`, or no solution when the multiset is unrealisable.

## Requirements

### Requirement: Restriction map problem validation

The system SHALL provide a validated `RestrictionMapProblem` domain type wrapping the distance multiset `L` as a `Vector[Int]` of positive integers whose size is a triangular number `C(n,2) = n(n-1)/2` for some positive integer `n`. It MUST be constructed only through a smart constructor `from(distances)` returning `Either[RestrictionMapProblemError, RestrictionMapProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a valid distance multiset

- **WHEN** `RestrictionMapProblem.from` is called with `[2, 2, 3, 3, 4, 5, 6, 7, 8, 10]` (size 10 = C(5,2))
- **THEN** it returns a `Right` whose `distances` match the input

#### Scenario: Accepts an empty multiset

- **WHEN** `RestrictionMapProblem.from` is called with an empty multiset (size 0 = C(1,2))
- **THEN** it returns a `Right`

#### Scenario: Accepts a single-distance multiset

- **WHEN** `RestrictionMapProblem.from` is called with `[5]` (size 1 = C(2,2))
- **THEN** it returns a `Right`

#### Scenario: Rejects a multiset whose size is not triangular

- **WHEN** `RestrictionMapProblem.from` is called with `[2, 3]` (size 2, not of the form n(n-1)/2)
- **THEN** it returns `Left(InvalidSize(2))`

#### Scenario: Rejects a non-positive distance

- **WHEN** `RestrictionMapProblem.from` is called with `[2, -1, 3]`
- **THEN** it returns `Left(NonPositiveDistance(1, -1))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `RestrictionMapProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Restriction map result rendering

The system SHALL provide a `RestrictionMap` result type holding the reconstructed positions as an ascending `Vector[Int]` and exposing a `format: String` that renders the positions separated by single spaces.

#### Scenario: Exposes the positions

- **WHEN** a `RestrictionMap` result is constructed from `[0, 2, 5]`
- **THEN** its `points` field returns `[0, 2, 5]`

#### Scenario: Formats positions space-separated

- **WHEN** `format` is called on a result holding `[0, 2, 4, 7, 10]`
- **THEN** it returns `"0 2 4 7 10"`

#### Scenario: Formats a single-position map

- **WHEN** `format` is called on a result holding `[0]`
- **THEN** it returns `"0"`

### Requirement: Restriction map reconstruction

The system SHALL provide an algorithm that, given a `RestrictionMapProblem`, returns a set `X` of nonnegative integers whose difference multiset `ΔX` (all positive pairwise differences) equals the input multiset `L`, or `None` if no such set exists. When a solution exists the positions MUST be returned in ascending order and MUST include `0`.

#### Scenario: Reconstructs the canonical Rosalind PDPL sample

- **WHEN** the algorithm is run on `[2, 2, 3, 3, 4, 5, 6, 7, 8, 10]`
- **THEN** it returns a `Some` whose positions, when expanded into their pairwise differences, equal the input multiset, and whose smallest position is `0` with 5 positions in total

#### Scenario: Reconstructs a single-distance multiset

- **WHEN** the algorithm is run on `[5]`
- **THEN** it returns `Some` with positions `[0, 5]`

#### Scenario: Reconstructs the trivial empty multiset

- **WHEN** the algorithm is run on an empty multiset
- **THEN** it returns `Some` with positions `[0]`

#### Scenario: Returns no solution for an unrealisable multiset

- **WHEN** the algorithm is run on `[1, 1, 1]` (size 3, but no three points are mutually unit-distant)
- **THEN** it returns `None`
