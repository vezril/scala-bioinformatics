## Purpose
Define the counting-quartets capability: computing `q(T)`, the number of quartets consistent with a fully resolved unrooted binary tree on `n` taxa, per the Rosalind "Counting Quartets" (CNTQ) problem. Because every fully resolved unrooted binary tree induces exactly one quartet per 4-element taxon subset, this count is topology-independent and equals `C(n, 4) = n(n − 1)(n − 2)(n − 3) / 24 mod 1,000,000`. Provides the validated `CountingQuartetsProblem(n, tree)` input bundle with its `CountingQuartetsProblemError` ADT and the `CountingQuartets.count` algorithm.

## Requirements

### Requirement: CountingQuartetsProblemError is a sealed ADT of CountingQuartetsProblem construction failures

The system SHALL provide a `sealed trait CountingQuartetsProblemError` in the
`bio.domain.graph` package with cases:
- `BelowMinimum(value: Int, min: Int)` — the leaf count was below the minimum `4`;
- `ExceedsMaximum(value: Int, max: Int)` — the leaf count exceeded the maximum `5000`;
- `LeafCountMismatch(declared: Int, actual: Int)` — the parsed tree's leaf count
  differed from the declared `n`.

#### Scenario: BelowMinimum carries the value and the bound

- **WHEN** `CountingQuartetsProblemError.BelowMinimum(3, 4)` is constructed
- **THEN** its `value` equals `3` and `min` equals `4`

#### Scenario: ExceedsMaximum carries the value and the bound

- **WHEN** `CountingQuartetsProblemError.ExceedsMaximum(5001, 5000)` is constructed
- **THEN** its `value` equals `5001` and `max` equals `5000`

#### Scenario: LeafCountMismatch carries the declared and actual counts

- **WHEN** `CountingQuartetsProblemError.LeafCountMismatch(5, 6)` is constructed
- **THEN** its `declared` equals `5` and `actual` equals `6`

### Requirement: CountingQuartetsProblem is a validated leaf-count-and-tree bundle

The system SHALL provide a
`sealed abstract case class CountingQuartetsProblem(n: Int, tree: NewickTree)`
in the `bio.domain.graph` package, constructable only through
`CountingQuartetsProblem.from(n: Int, tree: NewickTree): Either[CountingQuartetsProblemError, CountingQuartetsProblem]`.
Validation SHALL apply first-failure-wins in the order: `n` at least `4`, `n` at
most `5000`, the tree's leaf count equal to `n`. The synthesized `apply` and
`copy` SHALL NOT be public — direct construction MUST be a compile error.

#### Scenario: Accepts the canonical Rosalind sample input

- **WHEN** `CountingQuartetsProblem.from` is called with `n = 6` and the parsed sample tree `(lobster,(cat,dog),(caterpillar,(elephant,mouse)));`
- **THEN** the result is `Right` of a problem carrying `n = 6` and that tree

#### Scenario: Rejects a leaf count below the minimum

- **WHEN** `CountingQuartetsProblem.from` is called with `n = 3` and any tree with three leaves
- **THEN** the result is `Left(CountingQuartetsProblemError.BelowMinimum(3, 4))`

#### Scenario: Rejects a leaf count above the maximum

- **WHEN** `CountingQuartetsProblem.from` is called with `n = 5001` and a tree
- **THEN** the result is `Left(CountingQuartetsProblemError.ExceedsMaximum(5001, 5000))`

#### Scenario: Rejects a tree whose leaf count differs from the declared n

- **WHEN** `CountingQuartetsProblem.from` is called with `n = 5` and the 6-leaf sample tree
- **THEN** the result is `Left(CountingQuartetsProblemError.LeafCountMismatch(5, 6))`

#### Scenario: Checks the bounds before the tree leaf count (first-failure-wins)

- **WHEN** `CountingQuartetsProblem.from` is called with `n = 3` and the 6-leaf sample tree (both the bound and the leaf count would fail)
- **THEN** the result is `Left(CountingQuartetsProblemError.BelowMinimum(3, 4))`

#### Scenario: Direct apply does not compile

- **WHEN** source code `bio.domain.graph.CountingQuartetsProblem(6, t)` is compiled
- **THEN** the compiler rejects the expression

### Requirement: CountingQuartets.count returns the number of consistent quartets modulo 1,000,000

The system SHALL provide
`CountingQuartets.count(problem: CountingQuartetsProblem): Int` in the
`bio.algorithms.graph` package returning `C(n, 4) mod 1,000,000`, where `n` is the
problem's leaf count and `C(n, 4) = n(n − 1)(n − 2)(n − 3) / 24` is the number of
quartets consistent with a fully resolved unrooted binary tree on `n` taxa. The
result SHALL always lie in `[0, 999999]`.

#### Scenario: Reproduces the canonical sample count

- **WHEN** `CountingQuartets.count` is called on the sample problem (`n = 6`)
- **THEN** the result is `15`

#### Scenario: Yields one quartet at the minimum leaf count

- **WHEN** `CountingQuartets.count` is called on a problem with `n = 4`
- **THEN** the result is `1`

#### Scenario: Applies the modulus when the count exceeds 1,000,000

- **WHEN** `CountingQuartets.count` is called on a problem with `n = 100` (`C(100, 4) = 3,921,225`)
- **THEN** the result is `921225`

#### Scenario: Stays within the residue range at the maximum leaf count

- **WHEN** `CountingQuartets.count` is called on a problem with `n = 5000`
- **THEN** the result is a value in `[0, 999999]`
