## ADDED Requirements

### Requirement: Validated restriction-site problem

The system SHALL provide a `RestrictionSiteProblem` domain type that wraps a
`DnaString` and is constructible only through a smart constructor returning
`Either[RestrictionSiteProblemError, RestrictionSiteProblem]`. Validation SHALL
reject a sequence longer than 1000 bp. The empty sequence SHALL be accepted. The
type SHALL NOT expose a public `apply` or `copy` that bypasses validation.

#### Scenario: Accepts the canonical sample sequence

- **WHEN** `RestrictionSiteProblem.from` is called with the DNA string `TCAATGCATGCGGGTCTATATGCAT`
- **THEN** it returns a `Right` whose `dna` preserves the input

#### Scenario: Accepts an empty sequence

- **WHEN** `RestrictionSiteProblem.from` is called with an empty `DnaString`
- **THEN** it returns a `Right`

#### Scenario: Accepts a sequence at the 1000 bp upper bound

- **WHEN** `RestrictionSiteProblem.from` is called with a 1000 bp sequence
- **THEN** it returns a `Right`

#### Scenario: Rejects a sequence longer than 1000 bp

- **WHEN** `RestrictionSiteProblem.from` is called with a 1001 bp sequence
- **THEN** it returns `Left(SequenceTooLong(1001, 1000))`

#### Scenario: Does not expose a public apply or copy

- **WHEN** code attempts to call `RestrictionSiteProblem(...)` or `.copy(...)` directly
- **THEN** the code fails to compile

### Requirement: Locate reverse palindromes

The system SHALL provide a pure, total `RestrictionSites.locate` function that takes
a `RestrictionSiteProblem` and returns a `RestrictionSites` result listing every
reverse palindrome — a substring equal to its reverse complement — of length between
4 and 12 inclusive. Each located site SHALL carry its 1-based start position and its
length. The function SHALL perform no I/O.

#### Scenario: Finds all sites in the canonical sample

- **WHEN** `locate` is called with the sample `TCAATGCATGCGGGTCTATATGCAT`
- **THEN** the returned sites are exactly `(4,6) (5,4) (6,6) (7,4) (17,4) (18,4) (20,6) (21,4)` in that order

#### Scenario: Returns no sites when none exist

- **WHEN** `locate` is called with `AAAAAAAA`
- **THEN** the returned sites are empty

#### Scenario: Considers only even lengths from 4 to 12

- **WHEN** `locate` is called with any sequence
- **THEN** every returned site has a length in `{4, 6, 8, 10, 12}`

#### Scenario: Ignores reverse palindromes shorter than 4

- **WHEN** `locate` is called with `GC` (a length-2 reverse palindrome)
- **THEN** the returned sites are empty

#### Scenario: Finds a single minimal site

- **WHEN** `locate` is called with `GTAC`
- **THEN** the returned sites are exactly `(1,4)` for the `GTAC` palindrome

### Requirement: Render the restriction sites

The system SHALL provide a `RestrictionSites.format` method that renders each site
as its position and length separated by a space, one site per line. The empty result
SHALL render as the empty string.

#### Scenario: Formats the canonical sample result

- **WHEN** `format` is called on the sample result
- **THEN** it returns the eight `position length` lines joined by newlines, starting `4 6` and ending `21 4`

#### Scenario: Formats an empty result as the empty string

- **WHEN** `format` is called on a result with no sites
- **THEN** it returns `""`

### Requirement: Read and solve the REVP dataset

The system SHALL provide a `REVPProb` runner that reads a single FASTA DNA string
from `revp_data.txt`, validates it into a `RestrictionSiteProblem`, locates the
reverse palindromes, and prints the formatted position/length pairs through the `IO`
monad. Missing or invalid input SHALL produce a printed error message rather than an
exception.

#### Scenario: Prints the sites for the dataset

- **WHEN** `REVPProb.solve()` runs against the canonical dataset
- **THEN** it prints the eight position/length pairs, the first line `4 6`

#### Scenario: Prints an error for missing input

- **WHEN** `REVPProb.solve()` runs against a file with no FASTA record
- **THEN** it prints a descriptive error message and does not throw
