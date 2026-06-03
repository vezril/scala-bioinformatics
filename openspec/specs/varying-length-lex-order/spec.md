# varying-length-lex-order Specification

## Purpose

Enumerates every string of length 1 to a maximum length formed from an ordered
alphabet, listed in varying-length lexicographic order (Rosalind spec — LEXV,
"Ordering Strings of Varying Length Lexicographically"). The given alphabet order
defines the lexicographic order, and a string precedes any longer string that
extends it (a pre-order traversal of the alphabet tree). Provides a validated
`LexOrderProblem` input bundle (non-empty alphabet of at most 12 distinct symbols,
length 1 to 4), a `LexOrdering` result type, and the pure
`VaryingLengthLexOrder.enumerate` algorithm.

## Requirements

### Requirement: Lex Order Problem domain type

The system SHALL provide a validated `LexOrderProblem` domain type in `bio.domain.combinatorics` pairing an ordered `alphabet` (`Vector[Char]`) with a maximum length `maxLength` (`Int`). It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(alphabet: Vector[Char], maxLength: Int): Either[LexOrderProblemError, LexOrderProblem]`. The constructor SHALL validate, with first-failure-wins ordering: the alphabet is non-empty, has at most 12 symbols, has no duplicate symbol, and the length is between 1 and 4 inclusive. The given alphabet order defines the lexicographic order.

#### Scenario: Accepts a valid alphabet and length
- **WHEN** `LexOrderProblem.from` is called with alphabet `Vector('D','N','A')` and length 3
- **THEN** it returns a `Right` holding a `LexOrderProblem`

#### Scenario: Rejects an empty alphabet
- **WHEN** `LexOrderProblem.from` is called with an empty alphabet
- **THEN** it returns a `Left` holding `LexOrderProblemError.EmptyAlphabet`

#### Scenario: Rejects more than twelve symbols
- **WHEN** `LexOrderProblem.from` is called with a 13-symbol alphabet
- **THEN** it returns a `Left` holding `LexOrderProblemError.TooManySymbols(13, 12)`

#### Scenario: Rejects a duplicate symbol
- **WHEN** `LexOrderProblem.from` is called with alphabet `Vector('A','B','A')`
- **THEN** it returns a `Left` holding `LexOrderProblemError.DuplicateSymbol('A')`

#### Scenario: Rejects a non-positive length
- **WHEN** `LexOrderProblem.from` is called with length 0
- **THEN** it returns a `Left` holding `LexOrderProblemError.NonPositiveLength(0)`

#### Scenario: Rejects a length over the cap
- **WHEN** `LexOrderProblem.from` is called with length 5
- **THEN** it returns a `Left` holding `LexOrderProblemError.LengthExceedsMaximum(5, 4)`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.combinatorics.LexOrderProblem(Vector('A'), 1)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `LexOrderProblem`
- **THEN** the code does not compile

### Requirement: Lex Order Problem error ADT

The system SHALL provide a `LexOrderProblemError` sealed ADT in `bio.domain.combinatorics` enumerating the validation failures for `LexOrderProblem`: `EmptyAlphabet`, `TooManySymbols(count: Int, max: Int)`, `DuplicateSymbol(symbol: Char)`, `NonPositiveLength(length: Int)`, and `LengthExceedsMaximum(length: Int, max: Int)`.

#### Scenario: Reports the offending symbol count and the maximum
- **WHEN** a 15-symbol alphabet is rejected for exceeding the symbol cap
- **THEN** the error is `LexOrderProblemError.TooManySymbols(15, 12)`

### Requirement: Lex Ordering result type

The system SHALL provide a `LexOrdering` result type in `bio.domain.combinatorics` holding the ordered `strings` (`Vector[String]`), and exposing a `format: String` that renders the strings one per line.

#### Scenario: Formats the strings one per line
- **WHEN** a `LexOrdering` holding `Vector("D", "DD", "DN")` is formatted
- **THEN** `format` returns `"D\nDD\nDN"`

### Requirement: Varying-length lexicographic enumeration algorithm

The system SHALL provide a `VaryingLengthLexOrder` algorithm in `bio.algorithms.combinatorics` with a pure, total method `enumerate(problem: LexOrderProblem): LexOrdering`. It SHALL produce every string of length 1 to `maxLength` formed from the alphabet, in varying-length lexicographic order (a pre-order traversal of the alphabet tree, where a string precedes any longer string that extends it), using the given alphabet order as the symbol order.

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `enumerate` is run on alphabet `Vector('D','N','A')` with maxLength 3
- **THEN** the strings are, in order, `D, DD, DDD, DDN, DDA, DN, DND, DNN, DNA, DA, DAD, DAN, DAA, N, ND, NDD, NDN, NDA, NN, NND, NNN, NNA, NA, NAD, NAN, NAA, A, AD, ADD, ADN, ADA, AN, AND, ANN, ANA, AA, AAD, AAN, AAA`

#### Scenario: With maxLength 1 yields exactly the alphabet in order
- **WHEN** `enumerate` is run on alphabet `Vector('D','N','A')` with maxLength 1
- **THEN** the strings are `D, N, A`

#### Scenario: A single-symbol alphabet yields a chain
- **WHEN** `enumerate` is run on alphabet `Vector('X')` with maxLength 3
- **THEN** the strings are `X, XX, XXX`
