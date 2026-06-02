## ADDED Requirements

### Requirement: Validated contig collection

The system SHALL provide a `ContigCollection` domain type that wraps a non-empty
`Vector` of DNA contigs and is constructible only through a smart constructor returning
`Either[ContigCollectionError, ContigCollection]`. Validation SHALL be first-failure-wins
in this order: empty collection, then more than 1000 contigs, then the first contig of
length 0, then a combined contig length exceeding 50000. The type SHALL NOT expose a
public `apply` or `copy` that bypasses validation.

#### Scenario: Accepts the canonical sample contigs

- **WHEN** `ContigCollection.from` is called with contigs `GATTACA`, `TACTACTAC`, `ATTGAT`, `GAAGA`
- **THEN** it returns a `Right` whose `contigs` preserve the four input strings in order

#### Scenario: Rejects an empty collection

- **WHEN** `ContigCollection.from` is called with an empty vector
- **THEN** it returns `Left(EmptyContigCollection)`

#### Scenario: Rejects more than the maximum number of contigs

- **WHEN** `ContigCollection.from` is called with 1001 contigs
- **THEN** it returns `Left(TooManyContigs(1001, 1000))`

#### Scenario: Rejects a zero-length contig

- **WHEN** `ContigCollection.from` is called with contigs where the contig at index 1 is the empty string
- **THEN** it returns `Left(EmptyContig(1))`

#### Scenario: Rejects a collection whose combined length exceeds the maximum

- **WHEN** `ContigCollection.from` is called with contigs whose combined length is 50001
- **THEN** it returns `Left(ExceedsTotalLength(50001, 50000))`

#### Scenario: Reports the first failure when multiple checks fail

- **WHEN** `ContigCollection.from` is called with an empty vector
- **THEN** it returns `Left(EmptyContigCollection)` ahead of any other check

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `ContigCollection(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Compute the N-statistic for a percentile

The system SHALL provide a pure `AssemblyStatistics.nStatistic` function that takes a
`ContigCollection` and an integer percentile (1..99) and returns the maximum positive
integer `L` such that the total length of contigs of length at least `L` is at least the
given percentage of the combined contig length. The threshold comparison SHALL be exact
(integer arithmetic, no floating-point rounding). The function SHALL be total, performing
no I/O.

#### Scenario: Computes N50 for the canonical sample

- **WHEN** `nStatistic` is called with the sample contigs and percentile 50
- **THEN** it returns 7

#### Scenario: Computes N75 for the canonical sample

- **WHEN** `nStatistic` is called with the sample contigs and percentile 75
- **THEN** it returns 6

#### Scenario: Returns the single contig length for a one-contig collection

- **WHEN** `nStatistic` is called with a collection containing only `GATTACA` and any percentile
- **THEN** it returns 7

#### Scenario: Returns the common length when all contigs are equal length

- **WHEN** `nStatistic` is called with contigs `ACGT`, `TGCA`, `GGGG` and percentile 50
- **THEN** it returns 4

### Requirement: Assess assembly quality as N50 and N75

The system SHALL provide an `AssemblyStatistics.assess` function returning an
`AssemblyQuality` value carrying the collection's N50 and N75, and `AssemblyQuality`
SHALL render through a `format` method as the two values separated by a single space.

#### Scenario: Assesses the canonical sample as N50 7 and N75 6

- **WHEN** `assess` is called with the sample contigs
- **THEN** it returns an `AssemblyQuality` with `n50 = 7` and `n75 = 6`

#### Scenario: Formats the result as space-separated values

- **WHEN** `format` is called on an `AssemblyQuality` with `n50 = 7` and `n75 = 6`
- **THEN** it returns exactly `7 6`

### Requirement: Read and solve the ASMQ dataset

The system SHALL provide an `ASMQProb` runner that reads newline-separated DNA contigs
from `asmq_data.txt`, validates them into a `ContigCollection`, computes the assembly
quality, and prints the formatted N50/N75 through the `IO` monad. Invalid input SHALL
produce a printed error message rather than an exception.

#### Scenario: Prints N50 and N75 for the dataset

- **WHEN** `ASMQProb.solve()` runs against the canonical dataset
- **THEN** it prints `7 6`

#### Scenario: Prints an error for invalid input

- **WHEN** `ASMQProb.solve()` runs against a dataset containing an invalid contig
- **THEN** it prints a descriptive error message and does not throw
