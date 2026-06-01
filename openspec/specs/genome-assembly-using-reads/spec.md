## Purpose

Validates an equal-length DNA read collection and assembles the circular chromosome by
finding the de Bruijn graph over the reads plus their reverse complements that forms
exactly two reverse-complement cycles, returning a cyclic superstring that contains every
read or its reverse complement.

## Requirements

### Requirement: Validated genome-assembly read collection

The system SHALL provide a `GenomeAssemblyReadsProblem` domain type that wraps a
non-empty `Vector` of equal-length DNA reads and is constructible only through a smart
constructor returning `Either[GenomeAssemblyReadsProblemError, GenomeAssemblyReadsProblem]`.
Validation SHALL be first-failure-wins in this order: empty collection, then for each
read in order its length below the minimum (2), then its length above the maximum (50),
then its length differing from the first read's length. The type SHALL NOT expose a
public `apply` or `copy` that bypasses validation. There SHALL be no cap on the number
of reads.

#### Scenario: Accepts the canonical sample reads

- **WHEN** `GenomeAssemblyReadsProblem.from` is called with reads `AATCT`, `TGTAA`, `GATTA`, `ACAGA`
- **THEN** it returns a `Right` whose `reads` preserve the four input strings in order

#### Scenario: Rejects an empty collection

- **WHEN** `GenomeAssemblyReadsProblem.from` is called with an empty vector
- **THEN** it returns `Left(EmptyReadCollection)`

#### Scenario: Rejects a read shorter than the minimum length

- **WHEN** `GenomeAssemblyReadsProblem.from` is called with reads where the read at index 1 has length 1
- **THEN** it returns `Left(ReadTooShort(1, 1, 2))`

#### Scenario: Rejects a read longer than the maximum length

- **WHEN** `GenomeAssemblyReadsProblem.from` is called with a read of length 51
- **THEN** it returns `Left(ReadTooLong(index, 51, 50))` for that read's index

#### Scenario: Rejects reads of inconsistent length

- **WHEN** `GenomeAssemblyReadsProblem.from` is called with reads whose first has length 5 and a later read at index 2 has length 4
- **THEN** it returns `Left(InconsistentLength(2, 4, 5))`

#### Scenario: Reports the first failure when multiple reads are invalid

- **WHEN** `GenomeAssemblyReadsProblem.from` is called with reads where an earlier read is too short and a later read is inconsistent
- **THEN** it returns the error for the earliest offending read

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `GenomeAssemblyReadsProblem(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Assemble the circular chromosome from reads and reverse complements

The system SHALL provide a pure `GenomeAssemblyReads.assemble` function that takes a
`GenomeAssemblyReadsProblem` and returns a `CyclicSuperstring`. The algorithm SHALL
augment the reads with their reverse complements, search descending k-mer sizes for the
de Bruijn graph (nodes are the distinct (size-1)-mers, edges connect each size-mer's
prefix to its suffix) that decomposes into exactly two reverse-complement directed
cycles, and emit a cyclic superstring that contains every read or its reverse
complement. The output SHALL be deterministic: the lexicographically smallest minimal
rotation across the two cycles. The function SHALL be total, performing no I/O.

#### Scenario: Reconstructs the canonical sample chromosome

- **WHEN** `assemble` is called with reads `AATCT`, `TGTAA`, `GATTA`, `ACAGA`
- **THEN** it returns a `CyclicSuperstring` whose value is `AATCTGT`

#### Scenario: Output is a rotation of a strand of the Rosalind sample

- **WHEN** `assemble` is called with reads `AATCT`, `TGTAA`, `GATTA`, `ACAGA`
- **THEN** the result is a rotation of either `GATTACA` or its reverse complement `AATCTGT`

#### Scenario: Is unaffected by duplicate reads

- **WHEN** `assemble` is called with the canonical sample reads plus a duplicate of `GATTA`
- **THEN** it returns the same `CyclicSuperstring` value `AATCTGT`

#### Scenario: Is unaffected by reads already supplied on both strands

- **WHEN** `assemble` is called with the canonical reads together with the reverse complement of one of them
- **THEN** it returns the same `CyclicSuperstring` value `AATCTGT`

### Requirement: Cyclic superstring result formatting

The system SHALL render the assembled chromosome through the `CyclicSuperstring.format`
method as the bare chromosome string with no surrounding text.

#### Scenario: Formats the assembled chromosome as the raw string

- **WHEN** `format` is called on a `CyclicSuperstring` holding `AATCTGT`
- **THEN** it returns exactly `AATCTGT`

### Requirement: Read and solve the GASM dataset

The system SHALL provide a `GASMProb` runner that reads newline-separated DNA reads
from `gasm_data.txt`, validates them into a `GenomeAssemblyReadsProblem`, assembles the
chromosome, and prints the formatted cyclic superstring through the `IO` monad. Invalid
input SHALL produce a printed error message rather than an exception.

#### Scenario: Prints the assembled chromosome for the dataset

- **WHEN** `GASMProb.solve()` runs against a dataset of equal-length reads forming a valid two-cycle de Bruijn graph
- **THEN** it prints the formatted cyclic superstring

#### Scenario: Prints an error for invalid input

- **WHEN** `GASMProb.solve()` runs against a dataset containing an invalid read
- **THEN** it prints a descriptive error message and does not throw
