## Purpose

Validates a collection of DNA reads and assembles them into the shortest superstring by
gluing reads that overlap by more than half their length, per the Rosalind "Genome
Assembly as Shortest Superstring" (LONG) problem. Provides the validated
`GenomeAssemblyProblem` input bundle with its `GenomeAssemblyProblemError` ADT and the
`ShortestSuperstring` result that renders the assembled sequence.

## Requirements

### Requirement: Genome-assembly input errors are represented as a dedicated ADT

The system SHALL represent the ways a collection of DNA reads can be structurally
invalid as a sealed `GenomeAssemblyProblemError` ADT with the cases
`EmptyReadCollection`, `TooManyReads(count, max)`, and
`ReadTooLong(index, length, max)`, where `count` is the number of reads supplied,
`index` is the zero-based position of the offending read, `length` is its length,
and `max` is the relevant Rosalind cap.

#### Scenario: Error ADT enumerates the invalid-input cases

- **WHEN** the `GenomeAssemblyProblemError` ADT is inspected
- **THEN** it is a sealed type whose cases are `EmptyReadCollection`,
  `TooManyReads` carrying the read count and the maximum, and `ReadTooLong`
  carrying the offending read's zero-based index, its length, and the maximum

#### Scenario: Read-too-long error identifies the offending read

- **WHEN** a `ReadTooLong` is produced for a 1001 bp read at index 3
- **THEN** it carries index 3, length 1001, and the maximum 1000

### Requirement: Genome-assembly problem is a validated, invariant-bearing bundle

The system SHALL provide a `GenomeAssemblyProblem` bundling the DNA reads to be
assembled, constructed only through a smart constructor `from(reads)` that returns
`Either[GenomeAssemblyProblemError, GenomeAssemblyProblem]`. The constructor SHALL
reject an empty collection, a collection of more than 50 reads, and any read longer
than 1000 bp; it SHALL report the first failure encountered in the order
empty → too-many → read-too-long. The type SHALL NOT expose a public `apply` or
`copy` that bypasses validation.

#### Scenario: Valid read collection yields a problem

- **WHEN** `from` is given the sample reads `[ATTAGACCTG, CCTGCCGGAA, AGACCTGCCG,
  GCCGGAATAC]`
- **THEN** it returns a `Right` containing the `GenomeAssemblyProblem`

#### Scenario: An empty collection is rejected

- **WHEN** `from` is given no reads
- **THEN** it returns `Left(EmptyReadCollection)`

#### Scenario: Too many reads are rejected

- **WHEN** `from` is given 51 reads
- **THEN** it returns `Left(TooManyReads(51, 50))`

#### Scenario: An over-long read is rejected

- **WHEN** `from` is given a collection containing a read longer than 1000 bp
- **THEN** it returns `Left(ReadTooLong(index, length, 1000))` for that read

#### Scenario: First failure wins

- **WHEN** an input is invalid in more than one way
- **THEN** the error reflects the earliest failure in the validation order
  (empty → too-many → read-too-long)

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call a public `apply` or `copy` on
  `GenomeAssemblyProblem`
- **THEN** the code does not compile

### Requirement: Reads are assembled into the shortest superstring via more-than-half-length overlaps

The system SHALL compute, from a `GenomeAssemblyProblem`, an
`Option[ShortestSuperstring]` holding a shortest superstring that contains every
read as a substring, formed by gluing reads that overlap by more than half their
length. Each read SHALL appear as a substring of the result, and the result SHALL
be no longer than the concatenation of all reads. When the reads do not form a
unique assembly (no single chain covers every read), the result SHALL be `None`.
`ShortestSuperstring` SHALL render its assembled sequence via `format`.

#### Scenario: Canonical sample is assembled

- **WHEN** assembling the reads `[ATTAGACCTG, CCTGCCGGAA, AGACCTGCCG, GCCGGAATAC]`
- **THEN** the result is `Some` whose superstring is `ATTAGACCTGCCGGAATAC`

#### Scenario: Every read is a substring of the superstring

- **WHEN** a collection of reads is assembled to `Some(result)`
- **THEN** every input read appears as a substring of `result`'s sequence, and the
  sequence is no longer than the reads concatenated together

#### Scenario: A single read assembles to itself

- **WHEN** assembling a collection containing exactly one read
- **THEN** the result is `Some` whose superstring equals that read

#### Scenario: Reads that share no qualifying overlap cannot be assembled

- **WHEN** the reads cannot be linked into a single chain by more-than-half-length
  overlaps
- **THEN** the result is `None`

#### Scenario: Rendering returns the assembled sequence

- **WHEN** a `ShortestSuperstring` is rendered via `format`
- **THEN** it returns the assembled superstring sequence
