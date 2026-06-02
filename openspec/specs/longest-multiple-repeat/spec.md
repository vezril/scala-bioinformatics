# Finding the Longest Multiple Repeat

## Purpose

Given the suffix tree of `s$` and an integer `k`, return the longest substring of `s` occurring at least `k` times (Rosalind LREP) by finding the string-deepest internal node whose subtree has at least `k` leaves.

## Requirements

### Requirement: Suffix tree edge representation

The system SHALL provide a `SuffixTreeEdge` type representing one edge of a suffix tree: a parent node label, a child node label, the 1-based start position in the text `s$` of the edge's substring label, and the length of that substring.

#### Scenario: Exposes parent, child, start, and length

- **WHEN** a `SuffixTreeEdge` is constructed with parent `node1`, child `node2`, start `1`, and length `1`
- **THEN** its `parent` is `node1`, its `child` is `node2`, its `start` is `1`, and its `length` is `1`

### Requirement: Longest repeat problem validation

The system SHALL provide a validated `LongestRepeatProblem` domain type wrapping the text `s$`, a positive repeat threshold `k`, and the list of `SuffixTreeEdge`s. The text length MUST be at most 20001 (20 kbp plus the terminator) and every edge's substring label MUST lie within the text. It MUST be constructed only through a smart constructor `from(text, k, edges)` returning `Either[LongestRepeatProblemError, LongestRepeatProblem]`, applying first-failure-wins validation, and MUST NOT expose a public `apply` or `copy` that could bypass validation.

#### Scenario: Accepts a valid suffix tree problem

- **WHEN** `LongestRepeatProblem.from` is called with text `CATACATAC$`, `k` = 2, and a within-bounds edge list
- **THEN** it returns a `Right` whose `text`, `k`, and `edges` match the inputs

#### Scenario: Accepts an empty edge list

- **WHEN** `LongestRepeatProblem.from` is called with a valid text, `k` = 2, and no edges
- **THEN** it returns a `Right`

#### Scenario: Rejects a non-positive k

- **WHEN** `LongestRepeatProblem.from` is called with `k` = 0
- **THEN** it returns `Left(NonPositiveK(0))`

#### Scenario: Rejects a text longer than the maximum

- **WHEN** `LongestRepeatProblem.from` is called with a text of length 20002
- **THEN** it returns `Left(TextTooLong(20002, 20001))`

#### Scenario: Rejects an edge whose substring is out of bounds

- **WHEN** `LongestRepeatProblem.from` is called with text `AC$` (length 3) and an edge with start `5` and length `1`
- **THEN** it returns `Left(EdgeOutOfBounds(0, 5, 1, 3))`

#### Scenario: Cannot be constructed via a public apply or copy

- **WHEN** code attempts `LongestRepeatProblem(...)` or `.copy(...)` on a constructed instance
- **THEN** the code fails to compile

### Requirement: Longest repeat result rendering

The system SHALL provide a `LongestRepeat` result type holding the answer substring and exposing a `format: String` that returns the substring verbatim.

#### Scenario: Exposes the substring

- **WHEN** a `LongestRepeat` result is constructed with substring `CATAC`
- **THEN** its `substring` field returns `CATAC`

#### Scenario: Formats the substring verbatim

- **WHEN** `format` is called on a result holding `CATAC`
- **THEN** it returns `"CATAC"`

#### Scenario: Empty result renders as the empty string

- **WHEN** `format` is called on a result holding the empty substring
- **THEN** it returns `""`

### Requirement: Longest multiple repeat computation

The system SHALL provide an algorithm that, given a `LongestRepeatProblem`, returns the longest substring of `s` that occurs at least `k` times. Occurrence count is the number of leaves beneath the corresponding suffix-tree node, and the answer is the path-string (concatenated edge labels from the root) of the string-deepest internal node whose subtree has at least `k` leaves. When no internal node qualifies, the algorithm MUST return the empty substring.

#### Scenario: Finds the canonical Rosalind LREP sample

- **WHEN** the algorithm is run on the suffix tree of `CATACATAC$` with `k` = 2
- **THEN** it returns the substring `CATAC`

#### Scenario: Returns the deepest substring meeting a higher threshold

- **WHEN** the algorithm is run on the suffix tree of `CATACATAC$` with `k` = 4
- **THEN** it returns the substring `A` (the only nonempty substring occurring at least 4 times)

#### Scenario: Returns the empty substring when no repeat meets the threshold

- **WHEN** the algorithm is run on the suffix tree of `CATACATAC$` with `k` = 5
- **THEN** it returns the empty substring

#### Scenario: Prefers the longest qualifying substring

- **WHEN** multiple substrings occur at least `k` times (e.g. both `ATAC` and `CATAC` occur twice for `k` = 2)
- **THEN** the algorithm returns the longest one (`CATAC`)
