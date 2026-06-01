## ADDED Requirements

### Requirement: De-Bruijn-graph input errors are represented as a dedicated ADT

The system SHALL represent the ways a de-Bruijn-graph (k+1)-mer collection can be
invalid as a sealed `DeBruijnGraphProblemError` ADT with the cases
`EmptyKmerCollection`, `TooManyKmers(count, max)`, `KmerTooShort(index, length,
min)`, `KmerTooLong(index, length, max)`, and `InconsistentLength(index, length,
expected)`, where `count` is the number of supplied k-mers, `index` identifies the
first offending k-mer, `length` is its length, `min`/`max` are the relevant Rosalind
caps, and `expected` is the length shared by the preceding k-mers. DNA-character
validity is handled upstream by `DnaString` and is not re-encoded by this ADT.

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `DeBruijnGraphProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `EmptyKmerCollection`,
  `TooManyKmers` carrying the count and the maximum, `KmerTooShort` carrying the
  offending index, length, and minimum, `KmerTooLong` carrying the offending index,
  length, and maximum, and `InconsistentLength` carrying the offending index, its
  length, and the expected length

#### Scenario: Too-many-k-mers error carries the count and maximum

- **WHEN** a `TooManyKmers` is produced for a collection of 1001 k-mers
- **THEN** it carries count 1001 and the maximum 1000

### Requirement: De-Bruijn-graph problem is a validated, invariant-bearing bundle

The system SHALL provide a `DeBruijnGraphProblem` wrapping the collection of
(k+1)-mer `DnaString`s, constructed only through a smart constructor `from(kmers)`
that returns `Either[DeBruijnGraphProblemError, DeBruijnGraphProblem]`. The
constructor SHALL reject an empty collection, a collection of more than 1000 k-mers,
a k-mer shorter than 2 symbols, a k-mer longer than 50 symbols, and k-mers of
unequal length; it SHALL report the first failure encountered in the order
empty → too-many → too-short → too-long → inconsistent-length (per-k-mer checks
scanning in index order). The type SHALL NOT expose a public `apply` or `copy` that
bypasses validation.

#### Scenario: A valid, equal-length collection yields a problem

- **WHEN** `from` is given the canonical sample of six length-4 k-mers
- **THEN** it returns a `Right` containing the `DeBruijnGraphProblem` carrying those
  k-mers

#### Scenario: An empty collection is rejected

- **WHEN** `from` is given an empty collection
- **THEN** it returns `Left(EmptyKmerCollection)`

#### Scenario: Too many k-mers are rejected

- **WHEN** `from` is given a collection of 1001 k-mers
- **THEN** it returns `Left(TooManyKmers(1001, 1000))`

#### Scenario: A k-mer too short to split is rejected

- **WHEN** `from` is given a collection containing a length-1 k-mer
- **THEN** it returns `Left(KmerTooShort(index, 1, 2))` for the first such k-mer

#### Scenario: A k-mer longer than the maximum is rejected

- **WHEN** `from` is given a collection containing a length-51 k-mer
- **THEN** it returns `Left(KmerTooLong(index, 51, 50))` for the first such k-mer

#### Scenario: K-mers of unequal length are rejected

- **WHEN** `from` is given a collection whose k-mers are not all the same length
- **THEN** it returns `Left(InconsistentLength(index, length, expected))` for the
  first k-mer whose length differs from the first k-mer's length

#### Scenario: First failure wins

- **WHEN** an input is invalid in more than one way
- **THEN** the error reflects the earliest failure in the validation order
  (empty → too-many → too-short → too-long → inconsistent-length)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on
  `DeBruijnGraphProblem`
- **THEN** the code does not compile

### Requirement: The de Bruijn graph is constructed over the k-mers and their reverse complements

The system SHALL compute, from a `DeBruijnGraphProblem`, a `DeBruijnGraph` whose
edges are the directed edges of the de Bruijn graph `B_k` over `S ∪ S^rc`, where `S`
is the de-duplicated set of input (k+1)-mers and `S^rc` is the set of their reverse
complements. For each distinct (k+1)-mer `r` in `S ∪ S^rc` the graph SHALL contain
exactly one edge from the length-`k` prefix of `r` to the length-`k` suffix of `r`.
Duplicate inputs and reverse-complement palindromes SHALL NOT produce duplicate
edges. The edges SHALL be ordered lexicographically by `(from, to)`. `DeBruijnGraph`
SHALL render each edge as `(from, to)` on its own line via `format`.

#### Scenario: Canonical sample graph is constructed

- **WHEN** constructing the de Bruijn graph for the canonical sample k-mers
  `TGAT, CATG, TCAT, ATGC, CATC, CATC`
- **THEN** the edges, in lexicographic order, are
  `(ATC, TCA), (ATG, TGA), (ATG, TGC), (CAT, ATC), (CAT, ATG), (GAT, ATG),
  (GCA, CAT), (TCA, CAT), (TGA, GAT)`

#### Scenario: Reverse complements contribute edges

- **WHEN** constructing the graph for the single k-mer `AAAA` (whose reverse
  complement is `TTTT`)
- **THEN** the edges, in lexicographic order, are `(AAA, AAA), (TTT, TTT)`

#### Scenario: A reverse-complement palindrome is counted once

- **WHEN** constructing the graph for the single k-mer `ATAT` (which equals its own
  reverse complement)
- **THEN** the only edge is `(ATA, TAT)`

#### Scenario: Duplicate inputs do not produce duplicate edges

- **WHEN** constructing the graph for the repeated input `GGGG, GGGG` (reverse
  complement `CCCC`)
- **THEN** the edges, in lexicographic order, are `(CCC, CCC), (GGG, GGG)`

#### Scenario: Rendering is one parenthesized edge per line

- **WHEN** a `DeBruijnGraph` is rendered via `format`
- **THEN** each edge appears as `(from, to)` on its own line, in order
