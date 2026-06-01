## Purpose
Define the quartets capability: inferring all quartets (partial splits `{a,b} | {c,d}`) from a partial character table per the Rosalind "Quartets" (QRT) problem. Provides the `QuartetsProblem` validated partial-character-table input type with its `QuartetsProblemError` ADT, the canonicalised `Quartet` ADT for splits, and the `Quartets.compute` algorithm that derives every inferable quartet.

## Requirements

### Requirement: QuartetsProblemError is a sealed ADT of QuartetsProblem construction failures

The system SHALL provide a `sealed trait QuartetsProblemError` in the
`bio.domain.graph` package with cases:
- `EmptyTaxa` — no taxon names were supplied;
- `DuplicateTaxon(name: String)` — a taxon name occurred more than once;
- `EmptyTable` — no character rows were supplied;
- `InconsistentWidth(rowIndex: Int, expected: Int, actual: Int)` — a row's length
  differed from the taxon count;
- `InvalidSymbol(rowIndex: Int, colIndex: Int, symbol: Char)` — a row contained a
  symbol other than `0`, `1`, or `x`.

#### Scenario: DuplicateTaxon carries the offending name

- **WHEN** `QuartetsProblemError.DuplicateTaxon("dog")` is constructed
- **THEN** its `name` field equals `"dog"`

#### Scenario: InconsistentWidth carries the row index and the lengths

- **WHEN** `QuartetsProblemError.InconsistentWidth(2, 7, 6)` is constructed
- **THEN** its `rowIndex` equals `2`, `expected` equals `7`, and `actual` equals `6`

#### Scenario: InvalidSymbol carries the position and the bad symbol

- **WHEN** `QuartetsProblemError.InvalidSymbol(1, 3, '2')` is constructed
- **THEN** its `rowIndex` equals `1`, `colIndex` equals `3`, and `symbol` equals `'2'`

### Requirement: QuartetsProblem is a validated partial character table

The system SHALL provide a `sealed abstract case class QuartetsProblem(taxa: Vector[String], characters: Vector[String])`
in the `bio.domain.graph` package, constructable only through
`QuartetsProblem.from(taxa: Vector[String], characters: Vector[String]): Either[QuartetsProblemError, QuartetsProblem]`.
Validation SHALL apply first-failure-wins in the order: non-empty taxa, distinct
taxa, non-empty characters, each row width equal to the taxon count, each row
symbol in `{0, 1, x}`. The synthesized `apply` and `copy` SHALL NOT be public —
direct construction MUST be a compile error.

#### Scenario: Accepts the canonical Rosalind sample table

- **WHEN** `QuartetsProblem.from` is called with taxa `[cat, dog, elephant, ostrich, mouse, rabbit, robot]` and characters `[01xxx00, x11xx00, 111x00x]`
- **THEN** the result is `Right` of a problem carrying those taxa and characters

#### Scenario: Rejects an empty taxa vector

- **WHEN** `QuartetsProblem.from` is called with empty taxa and a non-empty character list
- **THEN** the result is `Left(QuartetsProblemError.EmptyTaxa)`

#### Scenario: Rejects duplicate taxon names

- **WHEN** `QuartetsProblem.from` is called with taxa `[cat, dog, cat]` and any valid-width characters
- **THEN** the result is `Left(QuartetsProblemError.DuplicateTaxon("cat"))`

#### Scenario: Rejects an empty character table

- **WHEN** `QuartetsProblem.from` is called with non-empty taxa and an empty character list
- **THEN** the result is `Left(QuartetsProblemError.EmptyTable)`

#### Scenario: Rejects a row whose width differs from the taxon count

- **WHEN** `QuartetsProblem.from` is called with 7 taxa and a character row of length 6 at index 1
- **THEN** the result is `Left(QuartetsProblemError.InconsistentWidth(1, 7, 6))`

#### Scenario: Rejects a row containing a symbol other than 0, 1, or x

- **WHEN** `QuartetsProblem.from` is called with a row containing `'2'` at column 3 of row 0
- **THEN** the result is `Left(QuartetsProblemError.InvalidSymbol(0, 3, '2'))`

#### Scenario: Direct apply does not compile

- **WHEN** source code `bio.domain.graph.QuartetsProblem(Vector("a"), Vector("0"))` is compiled
- **THEN** the compiler rejects the expression

### Requirement: Quartet is a canonicalised partial split of two pairs

The system SHALL provide a `Quartet` type in the `bio.domain.graph` package
representing a partial split `{a,b} | {c,d}`, constructed via a total smart
constructor `Quartet.of(w: String, x: String, y: String, z: String): Quartet`
that canonicalises the value so that two quartets describing the same split
compare equal regardless of the order of the two taxa within a side or the order
of the two sides. The synthesized `apply` and `copy` SHALL NOT be public. The
type SHALL expose a `render: String` producing `"{a, b} {c, d}"`.

#### Scenario: Within-side taxon order does not affect equality

- **WHEN** `Quartet.of("dog", "elephant", "rabbit", "robot")` and `Quartet.of("elephant", "dog", "robot", "rabbit")` are constructed
- **THEN** the two quartets are equal

#### Scenario: Side order does not affect equality

- **WHEN** `Quartet.of("dog", "elephant", "rabbit", "robot")` and `Quartet.of("rabbit", "robot", "dog", "elephant")` are constructed
- **THEN** the two quartets are equal

#### Scenario: render produces the two-brace-pair format

- **WHEN** `Quartet.of("dog", "elephant", "rabbit", "robot").render` is evaluated
- **THEN** the result is `"{dog, elephant} {rabbit, robot}"`

### Requirement: Quartets.compute infers all quartets from the partial splits

The system SHALL provide `Quartets.compute(problem: QuartetsProblem): Vector[Quartet]`
in the `bio.algorithms.graph` package returning every quartet inferable from the
table: for each character row, every `{a,b} | {c,d}` with `{a,b}` two taxa marked
`1` and `{c,d}` two taxa marked `0` (taxa marked `x` excluded). The result SHALL
be deduplicated (a quartet inferable from several characters appears once) and
returned in a deterministic order.

#### Scenario: Reproduces the canonical sample's four quartets

- **WHEN** `Quartets.compute` is called on the sample table (taxa `[cat, dog, elephant, ostrich, mouse, rabbit, robot]`, characters `[01xxx00, x11xx00, 111x00x]`)
- **THEN** the rendered results are exactly the set `{ "{dog, elephant} {rabbit, robot}", "{cat, dog} {mouse, rabbit}", "{cat, elephant} {mouse, rabbit}", "{dog, elephant} {mouse, rabbit}" }`

#### Scenario: A character with fewer than two taxa on a side contributes no quartet

- **WHEN** `Quartets.compute` is called on taxa `[cat, dog, elephant, ostrich, mouse, rabbit, robot]` with the single character `01xxx00` (1-side `{dog}` has one taxon)
- **THEN** the result is empty

#### Scenario: An all-x character contributes no quartet

- **WHEN** `Quartets.compute` is called on taxa `[a, b, c, d]` with the single character `xxxx`
- **THEN** the result is empty

#### Scenario: A quartet inferable from two characters appears once

- **WHEN** `Quartets.compute` is called on taxa `[a, b, c, d]` with characters `[1100, 1100]`
- **THEN** the result has size `1` and its rendered value is `"{a, b} {c, d}"`

#### Scenario: A four-taxon even split yields a single quartet

- **WHEN** `Quartets.compute` is called on taxa `[a, b, c, d]` with the single character `1100`
- **THEN** the result has size `1` and its rendered value is `"{a, b} {c, d}"`
