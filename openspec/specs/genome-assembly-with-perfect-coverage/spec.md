## Purpose

Reconstructs a cyclic chromosome from a perfect-coverage collection of equal-length
DNA reads per the Rosalind "Genome Assembly with Perfect Coverage" (PCOV) problem.
Provides the validated `PerfectCoverageProblem` input bundle with its
`PerfectCoverageProblemError` ADT, and reconstructs the chromosome by walking the
single de Bruijn cycle to produce a `CyclicSuperstring`.

## Requirements

### Requirement: Perfect-coverage input errors are represented as a dedicated ADT

The system SHALL represent the ways a perfect-coverage k-mer collection can be
invalid as a sealed `PerfectCoverageProblemError` ADT with the cases
`EmptyKmerCollection`, `KmerTooShort(index, length, min)`, `KmerTooLong(index,
length, max)`, and `InconsistentLength(index, length, expected)`, where `index`
identifies the first offending k-mer, `length` is its length, `min`/`max` are the
relevant Rosalind caps, and `expected` is the length shared by the preceding
k-mers. DNA-character validity is handled upstream by `DnaString` and is not
re-encoded by this ADT.

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `PerfectCoverageProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `EmptyKmerCollection`,
  `KmerTooShort` carrying the offending index, length, and minimum, `KmerTooLong`
  carrying the offending index, length, and maximum, and `InconsistentLength`
  carrying the offending index, its length, and the expected length

#### Scenario: Too-short error carries the index, length, and minimum

- **WHEN** a `KmerTooShort` is produced for a length-1 k-mer at index 2
- **THEN** it carries index 2, length 1, and the minimum 2

### Requirement: Perfect-coverage problem is a validated, invariant-bearing bundle

The system SHALL provide a `PerfectCoverageProblem` wrapping the collection of
read `DnaString`s, constructed only through a smart constructor `from(kmers)` that
returns `Either[PerfectCoverageProblemError, PerfectCoverageProblem]`. The
constructor SHALL reject an empty collection, a k-mer shorter than 2 symbols, a
k-mer longer than 50 symbols, and k-mers of unequal length; it SHALL report the
first failure encountered in the order empty → too-short → too-long →
inconsistent-length (per-k-mer checks scanning in index order). The type SHALL NOT
expose a public `apply` or `copy` that bypasses validation.

#### Scenario: A valid, equal-length collection yields a problem

- **WHEN** `from` is given the canonical sample of seven length-5 k-mers
- **THEN** it returns a `Right` containing the `PerfectCoverageProblem` carrying
  those k-mers in order

#### Scenario: An empty collection is rejected

- **WHEN** `from` is given an empty collection
- **THEN** it returns `Left(EmptyKmerCollection)`

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
  (empty → too-short → too-long → inconsistent-length)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on
  `PerfectCoverageProblem`
- **THEN** the code does not compile

### Requirement: The cyclic chromosome is reconstructed by walking the single de Bruijn cycle

The system SHALL compute, from a `PerfectCoverageProblem`, a `CyclicSuperstring`
that is a minimal-length cyclic superstring containing every read. For reads of
length `L`, the system SHALL build the de Bruijn graph whose nodes are the
distinct length-`(L-1)` k-mers and whose edges map each read's length-`(L-1)`
prefix to its length-`(L-1)` suffix; duplicate reads SHALL collapse to a single
edge. Because the graph is a single simple cycle, the system SHALL walk that cycle
beginning from the lexicographically smallest node, emitting the first symbol of
each visited node, to produce a cyclic superstring whose length equals the number
of distinct nodes. The chosen starting node makes the output a deterministic
rotation of the circular chromosome; Rosalind accepts any rotation as correct.
`CyclicSuperstring` SHALL render the chromosome as the bare symbol sequence via
`format`.

#### Scenario: Canonical sample chromosome is reconstructed

- **WHEN** reconstructing the chromosome for the canonical sample reads `ATTAC,
  TACAG, GATTA, ACAGA, CAGAT, TTACA, AGATT`
- **THEN** the result is `ACAGATT`, a length-7 rotation of the Rosalind sample
  output `GATTACA` (the same circular chromosome)

#### Scenario: A single self-looping read yields a length-1 chromosome

- **WHEN** reconstructing the chromosome for the single read `AA` (whose prefix
  and suffix are both `A`)
- **THEN** the result is `A`

#### Scenario: A two-read cycle yields a length-2 chromosome

- **WHEN** reconstructing the chromosome for the reads `AT, TA`
- **THEN** the result is `AT`

#### Scenario: Duplicate reads do not change the chromosome

- **WHEN** reconstructing the chromosome for the canonical sample reads with one
  read repeated
- **THEN** the result is the same `ACAGATT` as without the duplicate

#### Scenario: Rendering is the bare symbol sequence

- **WHEN** a `CyclicSuperstring` is rendered via `format`
- **THEN** it is the chromosome's symbol sequence with no surrounding punctuation
