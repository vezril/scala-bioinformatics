## ADDED Requirements

### Requirement: Set-operations input errors are represented as a dedicated ADT

The system SHALL represent the ways a set-operations input can be invalid as a sealed
`SetOperationsProblemError` ADT with the cases `NonPositiveUniverse(value)`,
`ExceedsMaximum(value, max)`, and `ElementOutOfRange(setLabel, value, universe)`, where
`setLabel` identifies the offending subset (`"A"` or `"B"`), `value` is the offending
element or universe size, and `universe` is the declared `n`.

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `SetOperationsProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `NonPositiveUniverse` carrying the
  offending universe size, `ExceedsMaximum` carrying the offending size and the maximum,
  and `ElementOutOfRange` carrying the offending subset label, element value, and universe

#### Scenario: Out-of-range error identifies the offending subset

- **WHEN** an `ElementOutOfRange` is produced for subset `B`
- **THEN** its `setLabel` is `"B"` and it carries the offending element value and the
  declared universe size

### Requirement: Set-operations problem is a validated, invariant-bearing bundle

The system SHALL provide a `SetOperationsProblem` bundling a universe size `n` and two
subsets `A` and `B` of `{1, …, n}`, constructed only through a smart constructor
`from(n, a, b)` that returns `Either[SetOperationsProblemError, SetOperationsProblem]`.
The constructor SHALL reject a universe size below 1, a universe size above 20000, and
any subset element outside `{1, …, n}`, reporting the first failure encountered. The
type SHALL NOT expose a public `apply` or `copy` that bypasses validation.

#### Scenario: Valid input yields a problem

- **WHEN** `from` is given `n = 10`, `A = {1,2,3,4,5}`, and `B = {2,8,5,10}`
- **THEN** it returns a `Right` containing the `SetOperationsProblem`

#### Scenario: Empty subsets are valid

- **WHEN** `from` is given a valid `n` with `A` and/or `B` empty
- **THEN** it returns a `Right` (the empty set is a subset of every universe)

#### Scenario: A non-positive universe is rejected

- **WHEN** `from` is given `n = 0`
- **THEN** it returns `Left(NonPositiveUniverse(0))`

#### Scenario: A universe above the maximum is rejected

- **WHEN** `from` is given `n = 20001`
- **THEN** it returns `Left(ExceedsMaximum(20001, 20000))`

#### Scenario: An element outside the universe is rejected

- **WHEN** `from` is given `n = 5` and `A` contains `6`
- **THEN** it returns `Left(ElementOutOfRange("A", 6, 5))`

#### Scenario: First failure wins

- **WHEN** an input is invalid in more than one way (e.g. `n = 0` and an out-of-range
  element)
- **THEN** the error reflects the earliest failure in the validation order
  (non-positive → exceeds-maximum → subset `A` range → subset `B` range)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on `SetOperationsProblem`
- **THEN** the code does not compile

### Requirement: Set operations compute the six derived sets

The system SHALL compute, from a `SetOperationsProblem`, a `SetOperationsResult` holding
the union `A∪B`, the intersection `A∩B`, the differences `A−B` and `B−A`, and the
complements `Aᶜ = {1,…,n} − A` and `Bᶜ = {1,…,n} − B`. The result SHALL render each set
on its own line as `{e1, e2, …}` with elements in ascending order (an empty set as `{}`).

#### Scenario: Canonical sample

- **WHEN** computing the result for `n = 10`, `A = {1,2,3,4,5}`, `B = {2,8,5,10}`
- **THEN** the union is `{1,2,3,4,5,8,10}`, the intersection is `{2,5}`, `A−B` is
  `{1,3,4}`, `B−A` is `{8,10}`, `Aᶜ` is `{6,7,8,9,10}`, and `Bᶜ` is `{1,3,4,6,7,9}`

#### Scenario: Differences are not symmetric

- **WHEN** `A` and `B` differ
- **THEN** `A−B` and `B−A` may differ, while `A∪B` equals `B∪A` and `A∩B` equals `B∩A`

#### Scenario: Disjoint subsets

- **WHEN** `A` and `B` share no elements
- **THEN** the intersection is empty (`{}`), the union is `A∪B`, `A−B` equals `A`, and
  `B−A` equals `B`

#### Scenario: Rendering is ascending and brace-delimited

- **WHEN** a result set is rendered
- **THEN** its elements appear in ascending numeric order inside `{` and `}`, separated
  by `, `, and an empty set renders as `{}`
