## ADDED Requirements

### Requirement: Inconsistent-character-set input errors are represented as a dedicated ADT

The system SHALL represent the ways a character-table input can be structurally
invalid as a sealed `InconsistentCharacterSetProblemError` ADT with the cases
`EmptyTable`, `RaggedTable(rowIndex, expected, actual)`,
`ExceedsMaximumTaxa(count, max)`, and `InvalidCharacter(rowIndex, ch)`, where
`rowIndex` is a zero-based row position, `expected`/`actual` are column counts,
and `count` is the number of taxa (columns).

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `InconsistentCharacterSetProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `EmptyTable`, `RaggedTable`
  carrying the offending row index with the expected and actual widths,
  `ExceedsMaximumTaxa` carrying the taxa count and the maximum, and
  `InvalidCharacter` carrying the offending row index and character

#### Scenario: Ragged error identifies the offending row

- **WHEN** a `RaggedTable` is produced for a row of width 5 in a 6-column table
- **THEN** it carries that row's zero-based index, the expected width 6, and the
  actual width 5

### Requirement: Inconsistent-character-set problem is a validated, invariant-bearing bundle

The system SHALL provide an `InconsistentCharacterSetProblem` bundling the `0/1`
character rows of a table (columns are taxa), constructed only through a smart
constructor `from(rows)` that returns
`Either[InconsistentCharacterSetProblemError, InconsistentCharacterSetProblem]`.
The constructor SHALL reject an empty table, a row whose width differs from the
first row's width, a table with more than 100 taxa (columns), and any row
containing a symbol other than `0` or `1`; it SHALL report the first failure
encountered. The type SHALL NOT expose a public `apply` or `copy` that bypasses
validation.

#### Scenario: Valid table yields a problem

- **WHEN** `from` is given rows `[100001, 000110, 111000, 100111]`
- **THEN** it returns a `Right` containing the `InconsistentCharacterSetProblem`

#### Scenario: An empty table is rejected

- **WHEN** `from` is given no rows
- **THEN** it returns `Left(EmptyTable)`

#### Scenario: A ragged table is rejected

- **WHEN** `from` is given rows where a later row's width differs from the first
- **THEN** it returns `Left(RaggedTable(rowIndex, expected, actual))` for that row

#### Scenario: Too many taxa are rejected

- **WHEN** `from` is given rows of width 101
- **THEN** it returns `Left(ExceedsMaximumTaxa(101, 100))`

#### Scenario: A non-binary character symbol is rejected

- **WHEN** `from` is given a row containing a character other than `0` or `1`
- **THEN** it returns `Left(InvalidCharacter(rowIndex, ch))` for that row and
  character

#### Scenario: First failure wins

- **WHEN** an input is invalid in more than one way
- **THEN** the error reflects the earliest failure in the validation order
  (empty → ragged → exceeds-maximum → invalid character)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on
  `InconsistentCharacterSetProblem`
- **THEN** the code does not compile

### Requirement: A single conflicting character is deleted to restore consistency

The system SHALL compute, from an `InconsistentCharacterSetProblem`, an
`Option[ConsistentCharacterTable]` holding the character rows that remain after
deleting exactly one row so that no two remaining characters' splits conflict
(all four cross-intersections non-empty). When a single deletion cannot make the
table consistent, the result SHALL be `None`. The retained rows SHALL keep their
original input order, and `ConsistentCharacterTable` SHALL render its rows one
per line via `format`.

#### Scenario: Canonical sample is repaired

- **WHEN** repairing the table `[100001, 000110, 111000, 100111]`
- **THEN** the result is a `Some` whose rows are exactly the input rows with one
  row removed, and no two of the remaining rows conflict

#### Scenario: The result is a single-row submatrix on the same taxa

- **WHEN** a table of `m` rows of width `n` is repaired to `Some(table)`
- **THEN** `table` has `m − 1` rows, each of width `n`, and every retained row is
  one of the input rows

#### Scenario: Conflicts sharing one character are fixable

- **WHEN** exactly one character conflicts with two others (all conflicts share
  that single row)
- **THEN** deleting that shared row yields a `Some` consistent table

#### Scenario: Independent conflicts cannot be fixed by one deletion

- **WHEN** the table has two disjoint conflicting pairs (no single row covers
  both conflicts)
- **THEN** the result is `None`

#### Scenario: Rendering is one row per line

- **WHEN** a `ConsistentCharacterTable` is rendered via `format`
- **THEN** its rows appear in order, each on its own line
