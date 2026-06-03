## ADDED Requirements

### Requirement: Read Correction Problem domain type

The system SHALL provide a validated `ReadCorrectionProblem` domain type in `bio.domain.analysis` wrapping a `Vector[DnaString]` of reads. It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(reads: Vector[DnaString]): Either[ReadCorrectionProblemError, ReadCorrectionProblem]`. The constructor SHALL enforce, with first-failure-wins ordering: at most 1000 reads, each read length at most 50, and all reads of equal length. Character validity (`A`, `C`, `G`, `T`) is enforced upstream by `DnaString`.

#### Scenario: Accepts equal-length reads within the bounds
- **WHEN** `ReadCorrectionProblem.from` is called with several DNA reads all of length 5
- **THEN** it returns a `Right` holding a `ReadCorrectionProblem` wrapping them

#### Scenario: Accepts an empty read list
- **WHEN** `ReadCorrectionProblem.from` is called with an empty vector
- **THEN** it returns a `Right` holding a `ReadCorrectionProblem`

#### Scenario: Rejects more than 1000 reads
- **WHEN** `ReadCorrectionProblem.from` is called with 1001 reads
- **THEN** it returns a `Left` holding `ReadCorrectionProblemError.TooManyReads(1001, 1000)`

#### Scenario: Rejects a read longer than the bound
- **WHEN** `ReadCorrectionProblem.from` is called with a read of length 51
- **THEN** it returns a `Left` holding `ReadCorrectionProblemError.ReadTooLong(51, 50)`

#### Scenario: Rejects reads of unequal length
- **WHEN** `ReadCorrectionProblem.from` is called with reads of lengths 5 and 6
- **THEN** it returns a `Left` holding `ReadCorrectionProblemError.UnequalLengths(Vector(5, 6))`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.analysis.ReadCorrectionProblem(reads)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `ReadCorrectionProblem`
- **THEN** the code does not compile

### Requirement: Read Correction Problem error ADT

The system SHALL provide a `ReadCorrectionProblemError` sealed ADT in `bio.domain.analysis` enumerating the validation failures for `ReadCorrectionProblem`: `TooManyReads(count: Int, max: Int)`, `ReadTooLong(length: Int, max: Int)`, and `UnequalLengths(lengths: Vector[Int])`, each carrying the relevant diagnostic values.

#### Scenario: Reports the offending read count and the maximum
- **WHEN** 1500 reads are rejected for exceeding the count bound
- **THEN** the error is `ReadCorrectionProblemError.TooManyReads(1500, 1000)`

### Requirement: Correction and Read Corrections result types

The system SHALL provide a `Correction` type in `bio.domain.analysis` holding an `oldRead` and a `newRead` string with `format = "oldRead->newRead"`, and a `ReadCorrections` result type holding an ordered `Vector[Correction]` with a `format` that renders each correction on its own line.

#### Scenario: Formats a single correction
- **WHEN** a `Correction("TTCAT", "TTGAT")` is formatted
- **THEN** `format` returns `"TTCAT->TTGAT"`

#### Scenario: Formats a collection of corrections on separate lines
- **WHEN** a `ReadCorrections` holding `Correction("TTCAT","TTGAT")` and `Correction("GAGGA","GATGA")` is formatted
- **THEN** `format` returns `"TTCAT->TTGAT\nGAGGA->GATGA"`

### Requirement: Read error-correction algorithm

The system SHALL provide a `ReadErrorCorrection` algorithm in `bio.algorithms.analysis` with a pure, total method `correct(problem: ReadCorrectionProblem): ReadCorrections`. A read SHALL be considered *correct* if it or its reverse complement occurs in the dataset at least twice, and *incorrect* if it occurs exactly once (counting reverse complements). For each incorrect read it SHALL emit `Correction(read, form)` where `form` is a correct read or the reverse complement of a correct read at Hamming distance exactly 1 from the incorrect read.

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `correct` is run on the reads `TCATC`, `TTCAT`, `TCATC`, `TGAAA`, `GAGGA`, `TTTCA`, `ATCAA`, `TTGAT`, `TTTCC`
- **THEN** the corrections are exactly `{TTCAT->TTGAT, GAGGA->GATGA, TTTCC->TTTCA}`

#### Scenario: A read duplicated in the dataset needs no correction
- **WHEN** `correct` is run on two identical reads `ACGTA` and `ACGTA`
- **THEN** no corrections are returned

#### Scenario: A read whose reverse complement is present is correct
- **WHEN** `correct` is run on `AAA` and `TTT` (reverse complements of each other)
- **THEN** no corrections are returned

#### Scenario: An empty dataset yields no corrections
- **WHEN** `correct` is run on an empty read list
- **THEN** no corrections are returned
