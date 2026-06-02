## ADDED Requirements

### Requirement: Rooted tree leaf count validation

The system SHALL provide a validated `RootedTreeLeafCount` domain type wrapping the number of taxa `n` as a positive integer at most 1000. It MUST be constructed only through a smart constructor `from(n)` returning `Either[RootedTreeLeafCountError, RootedTreeLeafCount]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a value within bounds

- **WHEN** `RootedTreeLeafCount.from` is called with `4`
- **THEN** it returns a `Right` whose `value` is `4`

#### Scenario: Accepts the lower bound of one

- **WHEN** `RootedTreeLeafCount.from` is called with `1`
- **THEN** it returns a `Right`

#### Scenario: Accepts the upper bound of 1000

- **WHEN** `RootedTreeLeafCount.from` is called with `1000`
- **THEN** it returns a `Right`

#### Scenario: Rejects a non-positive value

- **WHEN** `RootedTreeLeafCount.from` is called with `0`
- **THEN** it returns `Left(NonPositive(0))`

#### Scenario: Rejects a value above the maximum

- **WHEN** `RootedTreeLeafCount.from` is called with `1001`
- **THEN** it returns `Left(ExceedsMaximum(1001, 1000))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `RootedTreeLeafCount(4)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Rooted binary tree counting

The system SHALL provide an algorithm that, given a `RootedTreeLeafCount` of `n` taxa, returns `B(n) = (2n−3)!! mod 1,000,000`, the number of distinct rooted binary trees on `n` labeled taxa, reduced modulo 1,000,000.

#### Scenario: Computes the canonical Rosalind ROOT sample

- **WHEN** the algorithm is run with `n = 4`
- **THEN** it returns `15`

#### Scenario: Counts one tree for a single taxon

- **WHEN** the algorithm is run with `n = 1`
- **THEN** it returns `1`

#### Scenario: Counts one tree for two taxa

- **WHEN** the algorithm is run with `n = 2`
- **THEN** it returns `1`

#### Scenario: Counts three trees for three taxa

- **WHEN** the algorithm is run with `n = 3`
- **THEN** it returns `3`

#### Scenario: Reduces large counts modulo 1,000,000

- **WHEN** the algorithm is run with `n = 10` (where `(2·10−3)!! = 34,459,425`)
- **THEN** it returns `459425`
