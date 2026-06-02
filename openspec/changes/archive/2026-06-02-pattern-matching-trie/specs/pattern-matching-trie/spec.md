## ADDED Requirements

### Requirement: Trie edge representation

The system SHALL provide a `TrieEdge` type representing one directed, symbol-labelled edge of a trie: a parent node integer, a child node integer, and a `DnaNucleotide` symbol.

#### Scenario: Exposes parent, child, and symbol

- **WHEN** a `TrieEdge` is constructed with parent `1`, child `2`, and symbol `A`
- **THEN** its `parent` is `1`, its `child` is `2`, and its `symbol` is the `A` nucleotide

### Requirement: Pattern trie problem validation

The system SHALL provide a validated `PatternTrieProblem` domain type wrapping the collection of pattern `DnaString`s, with at most 100 patterns, each of length at most 100 bp, and none a prefix of another. It MUST be constructed only through a smart constructor `from(patterns)` returning `Either[PatternTrieProblemError, PatternTrieProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a valid collection of patterns

- **WHEN** `PatternTrieProblem.from` is called with patterns `ATAGA`, `ATC`, `GAT`
- **THEN** it returns a `Right` whose `patterns` match the inputs

#### Scenario: Accepts an empty collection

- **WHEN** `PatternTrieProblem.from` is called with no patterns
- **THEN** it returns a `Right`

#### Scenario: Rejects more than 100 patterns

- **WHEN** `PatternTrieProblem.from` is called with 101 patterns
- **THEN** it returns `Left(TooManyPatterns(101, 100))`

#### Scenario: Rejects a pattern longer than 100 bp

- **WHEN** `PatternTrieProblem.from` is called with a collection whose pattern at index 0 has length 101
- **THEN** it returns `Left(PatternTooLong(0, 101, 100))`

#### Scenario: Rejects a pattern that is a proper prefix of another

- **WHEN** `PatternTrieProblem.from` is called with patterns `AT`, `ATC`
- **THEN** it returns `Left(PrefixConflict(0, 1))`

#### Scenario: Rejects duplicate patterns

- **WHEN** `PatternTrieProblem.from` is called with patterns `AT`, `AT`
- **THEN** it returns `Left(PrefixConflict(0, 1))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `PatternTrieProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Pattern trie result rendering

The system SHALL provide a `PatternTrie` result type holding the trie edges as a `Vector[TrieEdge]` in creation order and exposing a `format: String` rendering each edge as `parent child symbol`, one per line. The empty trie MUST render as the empty string.

#### Scenario: Exposes the edges

- **WHEN** a `PatternTrie` result is constructed from a vector of edges
- **THEN** its `edges` field returns exactly that vector

#### Scenario: Formats edges as parent/child/symbol triples

- **WHEN** `format` is called on the trie for patterns `ATAGA`, `ATC`, `GAT`
- **THEN** it returns `"1 2 A\n2 3 T\n3 4 A\n4 5 G\n5 6 A\n3 7 C\n1 8 G\n8 9 A\n9 10 T"`

#### Scenario: Empty trie renders as the empty string

- **WHEN** `format` is called on a trie with no edges
- **THEN** it returns `""`

### Requirement: Trie construction

The system SHALL provide an algorithm that, given a `PatternTrieProblem`, constructs the trie encoding its patterns. The root MUST be labelled `1`; new nodes MUST receive successive integers (`2, 3, …`) in the order their edges are created as patterns are inserted in input order. Inserting a pattern walks from the root symbol-by-symbol, reusing an existing edge when the current node already has one for that symbol and creating a new node and edge otherwise.

#### Scenario: Builds the canonical Rosalind TRIE sample

- **WHEN** the algorithm is run on patterns `ATAGA`, `ATC`, `GAT`
- **THEN** the edges are exactly, in order: `(1,2,A)`, `(2,3,T)`, `(3,4,A)`, `(4,5,G)`, `(5,6,A)`, `(3,7,C)`, `(1,8,G)`, `(8,9,A)`, `(9,10,T)`

#### Scenario: Builds a single linear path for one pattern

- **WHEN** the algorithm is run on the single pattern `AT`
- **THEN** the edges are exactly, in order: `(1,2,A)`, `(2,3,T)`

#### Scenario: Reuses a shared-prefix node and branches

- **WHEN** the algorithm is run on patterns `AT`, `AG`
- **THEN** the edges are exactly, in order: `(1,2,A)`, `(2,3,T)`, `(2,4,G)`

#### Scenario: Produces no edges for an empty pattern collection

- **WHEN** the algorithm is run on no patterns
- **THEN** the result contains no edges
