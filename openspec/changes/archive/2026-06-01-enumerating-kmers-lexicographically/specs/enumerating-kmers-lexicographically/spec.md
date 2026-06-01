## ADDED Requirements

### Requirement: K-mer-enumeration input errors are represented as a dedicated ADT

The system SHALL represent the ways a k-mer-enumeration input can be invalid as a
sealed `KmerEnumerationProblemError` ADT with the cases `EmptyAlphabet`,
`TooManySymbols(count, max)`, `DuplicateSymbol(symbol)`,
`NonPositiveLength(length)`, and `LengthExceedsMaximum(length, max)`, where `count`
is the number of supplied symbols, `symbol` is the repeated character, `length` is
the requested word length, and `max` is the relevant Rosalind cap.

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `KmerEnumerationProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `EmptyAlphabet`, `TooManySymbols`
  carrying the symbol count and the maximum, `DuplicateSymbol` carrying the repeated
  character, `NonPositiveLength` carrying the requested length, and
  `LengthExceedsMaximum` carrying the requested length and the maximum

#### Scenario: Too-many-symbols error carries the count and maximum

- **WHEN** a `TooManySymbols` is produced for an 11-symbol alphabet
- **THEN** it carries count 11 and the maximum 10

### Requirement: K-mer-enumeration problem is a validated, invariant-bearing bundle

The system SHALL provide a `KmerEnumerationProblem` pairing an ordered alphabet
(`Vector[Char]`, retained in the given order) with a word length, constructed only
through a smart constructor `from(alphabet, length)` that returns
`Either[KmerEnumerationProblemError, KmerEnumerationProblem]`. The constructor SHALL
reject an empty alphabet, an alphabet of more than 10 symbols, an alphabet
containing a duplicate symbol, a non-positive length, and a length greater than 10;
it SHALL report the first failure encountered in the order empty-alphabet →
too-many-symbols → duplicate-symbol → non-positive-length → length-exceeds-maximum.
The type SHALL NOT expose a public `apply` or `copy` that bypasses validation.

#### Scenario: Valid alphabet and length yield a problem

- **WHEN** `from` is given the alphabet `[A, C, G, T]` and length 2
- **THEN** it returns a `Right` containing the `KmerEnumerationProblem` whose
  alphabet preserves the given order

#### Scenario: An empty alphabet is rejected

- **WHEN** `from` is given an empty alphabet
- **THEN** it returns `Left(EmptyAlphabet)`

#### Scenario: Too many symbols are rejected

- **WHEN** `from` is given an alphabet of 11 symbols
- **THEN** it returns `Left(TooManySymbols(11, 10))`

#### Scenario: A duplicate symbol is rejected

- **WHEN** `from` is given an alphabet containing the same symbol twice
- **THEN** it returns `Left(DuplicateSymbol(symbol))` for the repeated symbol

#### Scenario: A non-positive length is rejected

- **WHEN** `from` is given a length of 0
- **THEN** it returns `Left(NonPositiveLength(0))`

#### Scenario: A length over the maximum is rejected

- **WHEN** `from` is given a length of 11
- **THEN** it returns `Left(LengthExceedsMaximum(11, 10))`

#### Scenario: First failure wins

- **WHEN** an input is invalid in more than one way
- **THEN** the error reflects the earliest failure in the validation order
  (empty-alphabet → too-many-symbols → duplicate-symbol → non-positive-length →
  length-exceeds-maximum)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on
  `KmerEnumerationProblem`
- **THEN** the code does not compile

### Requirement: All length-n strings are enumerated in lexicographic order

The system SHALL compute, from a `KmerEnumerationProblem`, a `KmerEnumeration`
holding every string of the requested length formed from the alphabet, ordered
lexicographically under the given alphabet order (first position most significant).
The result SHALL contain exactly `alphabetSize ^ length` strings, each of the
requested length. `KmerEnumeration` SHALL render its strings one per line via
`format`.

#### Scenario: Canonical sample is enumerated

- **WHEN** enumerating the alphabet `[A, C, G, T]` with length 2
- **THEN** the result is the 16 strings `AA, AC, AG, AT, CA, CC, CG, CT, GA, GC,
  GG, GT, TA, TC, TG, TT` in that order

#### Scenario: Enumeration count and width are exact

- **WHEN** enumerating an alphabet of `k` symbols with length `n`
- **THEN** the result contains exactly `k ^ n` strings, each of length `n`

#### Scenario: Length one returns the alphabet itself

- **WHEN** enumerating an alphabet with length 1
- **THEN** the result is the alphabet's symbols as single-character strings in the
  given order

#### Scenario: A single-symbol alphabet returns one repeated string

- **WHEN** enumerating a one-symbol alphabet `[A]` with length 3
- **THEN** the result is the single string `AAA`

#### Scenario: Rendering is one k-mer per line

- **WHEN** a `KmerEnumeration` is rendered via `format`
- **THEN** its strings appear in order, each on its own line
