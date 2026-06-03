## ADDED Requirements

### Requirement: Distance Matrix Problem domain type

The system SHALL provide a validated `DistanceMatrixProblem` domain type in `bio.domain.analysis` wrapping a `Vector[DnaString]`. It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(strings: Vector[DnaString]): Either[DistanceMatrixProblemError, DistanceMatrixProblem]`. The constructor SHALL enforce, with first-failure-wins ordering: at most 10 strings, each string length at most 1000, and all strings of equal length. Character validity (`A`, `C`, `G`, `T`) is enforced upstream by `DnaString`.

#### Scenario: Accepts equal-length strings within the bounds
- **WHEN** `DistanceMatrixProblem.from` is called with 4 DNA strings each of length 10
- **THEN** it returns a `Right` holding a `DistanceMatrixProblem` wrapping them

#### Scenario: Accepts an empty string list
- **WHEN** `DistanceMatrixProblem.from` is called with an empty vector
- **THEN** it returns a `Right` holding a `DistanceMatrixProblem`

#### Scenario: Rejects more than ten strings
- **WHEN** `DistanceMatrixProblem.from` is called with 11 strings
- **THEN** it returns a `Left` holding `DistanceMatrixProblemError.TooManyStrings(11, 10)`

#### Scenario: Rejects a string longer than the bound
- **WHEN** `DistanceMatrixProblem.from` is called with a string of length 1001
- **THEN** it returns a `Left` holding `DistanceMatrixProblemError.StringTooLong(1001, 1000)`

#### Scenario: Rejects strings of unequal length
- **WHEN** `DistanceMatrixProblem.from` is called with strings of lengths 4 and 5
- **THEN** it returns a `Left` holding `DistanceMatrixProblemError.UnequalLengths(Vector(4, 5))`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.analysis.DistanceMatrixProblem(strings)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `DistanceMatrixProblem`
- **THEN** the code does not compile

### Requirement: Distance Matrix Problem error ADT

The system SHALL provide a `DistanceMatrixProblemError` sealed ADT in `bio.domain.analysis` enumerating the validation failures for `DistanceMatrixProblem`: `TooManyStrings(count: Int, max: Int)`, `StringTooLong(length: Int, max: Int)`, and `UnequalLengths(lengths: Vector[Int])`, each carrying the relevant diagnostic values.

#### Scenario: Reports all the differing lengths
- **WHEN** strings of lengths 4, 4, and 6 are rejected for unequal length
- **THEN** the error is `DistanceMatrixProblemError.UnequalLengths(Vector(4, 4, 6))`

### Requirement: Distance Matrix result type

The system SHALL provide a `DistanceMatrix` result type in `bio.domain.analysis` holding the `n × n` matrix as a `Vector[Vector[Double]]`, and exposing a `format: String` that renders each value to 5 decimal places, entries space-separated, with rows joined by newlines.

#### Scenario: Formats the matrix to five decimal places
- **WHEN** a `DistanceMatrix` holding `Vector(Vector(0.0, 0.4), Vector(0.4, 0.0))` is formatted
- **THEN** `format` returns `"0.00000 0.40000\n0.40000 0.00000"`

#### Scenario: Formats an empty matrix as the empty string
- **WHEN** a `DistanceMatrix` holding an empty row vector is formatted
- **THEN** `format` returns `""`

### Requirement: P-distance matrix algorithm

The system SHALL provide a `PDistanceMatrix` algorithm in `bio.algorithms.analysis` with a pure, total method `compute(problem: DistanceMatrixProblem): DistanceMatrix`. For each pair `(i, j)` it SHALL set `D[i][j]` to the p-distance between strings `i` and `j` — the number of positions at which they differ divided by their common length — with `D[i][i] = 0` and `D[i][j] = 0` when the common length is 0.

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `compute` is run on the strings `TTTCCATTTA`, `GATTCATTTC`, `TTTCCATTTT`, `GTTCCATTTA`
- **THEN** the matrix equals (within 0.001) `Vector(Vector(0.0,0.4,0.1,0.1), Vector(0.4,0.0,0.4,0.3), Vector(0.1,0.4,0.0,0.2), Vector(0.1,0.3,0.2,0.0))`

#### Scenario: A single string yields a 1×1 zero matrix
- **WHEN** `compute` is run on a single DNA string
- **THEN** the matrix is `Vector(Vector(0.0))`

#### Scenario: The diagonal is always zero
- **WHEN** `compute` is run on any valid problem
- **THEN** every diagonal entry `D[i][i]` is `0.0`

#### Scenario: All-empty strings yield a zero matrix
- **WHEN** `compute` is run on two empty DNA strings
- **THEN** the matrix is `Vector(Vector(0.0, 0.0), Vector(0.0, 0.0))`
