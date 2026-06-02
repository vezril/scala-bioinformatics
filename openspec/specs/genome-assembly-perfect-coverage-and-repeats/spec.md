## Purpose

Implements the Rosalind "Genome Assembly with Perfect Coverage and Repeats"
(GREP) problem: it validates a collection of equal-length (k+1)-mer DNA reads
(k ≤ 5) drawn from a circular chromosome and enumerates every circular string
assembled by a complete cycle — an Eulerian circuit that traverses each
(k+1)-mer edge exactly as many times as its read occurs — in the de Bruijn
graph B_k of the reads. Each assembled circular string begins with the first
input read, and read multiplicity is preserved so that repeats yield multiple
distinct assemblies.

## Requirements

### Requirement: Validated complete-cycle read collection

The system SHALL provide a `CompleteCycleProblem` domain type that wraps a non-empty
`Vector` of equal-length DNA reads (the (k+1)-mers, k ≤ 5) and is constructible only
through a smart constructor returning
`Either[CompleteCycleProblemError, CompleteCycleProblem]`. Validation SHALL be
first-failure-wins in this order: empty collection, then more than 50 reads, then the
first read shorter than 2, then the first read longer than 6, then the first read
whose length differs from the first read's length. The type SHALL NOT expose a public
`apply` or `copy` that bypasses validation.

#### Scenario: Accepts the canonical sample reads

- **WHEN** `CompleteCycleProblem.from` is called with the 17 canonical sample 3-mers
- **THEN** it returns a `Right` whose `kmers` preserve the input reads in order, including repeated reads

#### Scenario: Rejects an empty collection

- **WHEN** `CompleteCycleProblem.from` is called with an empty vector
- **THEN** it returns `Left(EmptyKmerCollection)`

#### Scenario: Rejects more than the maximum number of reads

- **WHEN** `CompleteCycleProblem.from` is called with 51 reads
- **THEN** it returns `Left(TooManyReads(51, 50))`

#### Scenario: Rejects a read shorter than the minimum length

- **WHEN** `CompleteCycleProblem.from` is called with reads where the read at index 1 has length 1
- **THEN** it returns `Left(KmerTooShort(1, 1, 2))`

#### Scenario: Rejects a read longer than the maximum length

- **WHEN** `CompleteCycleProblem.from` is called with reads where the read at index 1 has length 7
- **THEN** it returns `Left(KmerTooLong(1, 7, 6))`

#### Scenario: Rejects a read of inconsistent length

- **WHEN** `CompleteCycleProblem.from` is called with reads where the read at index 1 has a different length from the first read
- **THEN** it returns `Left(InconsistentLength(1, <its length>, <first read length>))`

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `CompleteCycleProblem(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Enumerate all complete cycles

The system SHALL provide a pure `CompleteCycleAssembly.assemble` function that takes a
`CompleteCycleProblem` and returns a `CompleteCycleAssemblies` value containing every
circular string assembled by a complete cycle in the de Bruijn graph B_k of the reads.
A complete cycle is an Eulerian circuit that traverses each (k+1)-mer edge exactly as
many times as its read occurs in the input. Each returned circular string SHALL begin
with the first input read, SHALL have length equal to the number of reads, and the
returned strings SHALL be distinct. The function SHALL be total, performing no I/O.

#### Scenario: Enumerates the six circular strings of the canonical sample

- **WHEN** `assemble` is called with the 17 canonical sample 3-mers
- **THEN** it returns exactly the six strings `CAGTTCAATTTGGCGTT`, `CAGTTCAATTGGCGTTT`, `CAGTTTCAATTGGCGTT`, `CAGTTTGGCGTTCAATT`, `CAGTTGGCGTTCAATTT`, `CAGTTGGCGTTTCAATT`, in any order

#### Scenario: Every assembled string begins with the first input read

- **WHEN** `assemble` is called with the canonical sample (first read `CAG`)
- **THEN** every returned string starts with `CAG`

#### Scenario: Returns a single string for a simple cycle with no repeats

- **WHEN** `assemble` is called with the reads `AT`, `TG`, `GA`
- **THEN** it returns exactly the single string `ATG`

#### Scenario: Returns multiple strings when a branch admits more than one cycle

- **WHEN** `assemble` is called with the reads `CA`, `AT`, `TA`, `AG`, `GA`, `AC`
- **THEN** it returns exactly the two strings `CATAGA` and `CAGATA`, in any order

### Requirement: Render the assembled circular strings

The system SHALL provide a `CompleteCycleAssemblies` type whose `format` method
renders the assembled circular strings as one string per line, in the order held by
the value.

#### Scenario: Formats the strings one per line

- **WHEN** `format` is called on a `CompleteCycleAssemblies` holding `ATG` and `AGT`
- **THEN** it returns exactly `ATG\nAGT`

### Requirement: Read and solve the GREP dataset

The system SHALL provide a `GREPProb` runner that reads newline-separated DNA reads
from `grep_data.txt`, validates them into a `CompleteCycleProblem`, enumerates the
complete cycles, and prints the assembled circular strings through the `IO` monad.
Invalid input SHALL produce a printed error message rather than an exception.

#### Scenario: Prints the circular strings for the dataset

- **WHEN** `GREPProb.solve()` runs against the canonical dataset
- **THEN** it prints the six assembled circular strings, one per line, each beginning with `CAG`

#### Scenario: Prints an error for invalid input

- **WHEN** `GREPProb.solve()` runs against a dataset containing an invalid read
- **THEN** it prints a descriptive error message and does not throw
