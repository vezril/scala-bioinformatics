## ADDED Requirements

### Requirement: Interwoven Motif Problem domain type

The system SHALL provide a validated `InterwovenMotifProblem` domain type in `bio.domain.analysis` wrapping a text `DnaString` and a `Vector[DnaString]` of patterns. It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(text: DnaString, patterns: Vector[DnaString]): Either[InterwovenMotifProblemError, InterwovenMotifProblem]`. The constructor SHALL enforce, with first-failure-wins ordering: at most 10 patterns, text length at most 10000, and each pattern length at most 10. Character validity (`A`, `C`, `G`, `T`) is enforced upstream by `DnaString`.

#### Scenario: Accepts a valid text and pattern set
- **WHEN** `InterwovenMotifProblem.from` is called with a text of length ≤ 10000 and ≤ 10 patterns each of length ≤ 10
- **THEN** it returns a `Right` holding an `InterwovenMotifProblem` wrapping that text and pattern vector

#### Scenario: Accepts an empty pattern list
- **WHEN** `InterwovenMotifProblem.from` is called with an empty pattern vector
- **THEN** it returns a `Right` holding an `InterwovenMotifProblem`

#### Scenario: Rejects more than ten patterns
- **WHEN** `InterwovenMotifProblem.from` is called with 11 patterns
- **THEN** it returns a `Left` holding `InterwovenMotifProblemError.TooManyPatterns(11, 10)`

#### Scenario: Rejects a text longer than the bound
- **WHEN** `InterwovenMotifProblem.from` is called with a text of length 10001
- **THEN** it returns a `Left` holding `InterwovenMotifProblemError.TextTooLong(10001, 10000)`

#### Scenario: Rejects a pattern longer than the bound
- **WHEN** `InterwovenMotifProblem.from` is called with a valid text and a pattern of length 11
- **THEN** it returns a `Left` holding `InterwovenMotifProblemError.PatternTooLong(11, 10)`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.analysis.InterwovenMotifProblem(text, patterns)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `InterwovenMotifProblem`
- **THEN** the code does not compile

### Requirement: Interwoven Motif Problem error ADT

The system SHALL provide an `InterwovenMotifProblemError` sealed ADT in `bio.domain.analysis` enumerating the validation failures for `InterwovenMotifProblem`: `TooManyPatterns(count: Int, max: Int)`, `TextTooLong(length: Int, max: Int)`, and `PatternTooLong(length: Int, max: Int)`, each carrying the offending value and the relevant bound.

#### Scenario: Reports the offending pattern count and the maximum
- **WHEN** 12 patterns are rejected for exceeding the count bound
- **THEN** the error is `InterwovenMotifProblemError.TooManyPatterns(12, 10)`

### Requirement: Interwoven Motif Matrix result type

The system SHALL provide an `InterwovenMotifMatrix` result type in `bio.domain.analysis` holding the `n × n` 0/1 matrix as a `Vector[Vector[Int]]`, and exposing a `format: String` that renders each row's entries joined by a single space, with rows joined by newlines.

#### Scenario: Formats a matrix as space-separated rows
- **WHEN** an `InterwovenMotifMatrix` holding `Vector(Vector(0,0,1), Vector(0,1,0), Vector(1,0,0))` is formatted
- **THEN** `format` returns `"0 0 1\n0 1 0\n1 0 0"`

#### Scenario: Formats an empty matrix as the empty string
- **WHEN** an `InterwovenMotifMatrix` holding an empty row vector is formatted
- **THEN** `format` returns `""`

### Requirement: Interwoven motif matrix algorithm

The system SHALL provide an `InterwovenMotifs` algorithm in `bio.algorithms.analysis` with a pure, total method `compute(problem: InterwovenMotifProblem): InterwovenMotifMatrix`. For every pattern pair `(j, k)` it SHALL set `M[j][k] = 1` iff some contiguous substring of the text is an interleaving of patterns `j` and `k` (the two patterns appearing as disjoint subsequences that together cover the substring exactly), and `0` otherwise. The decision SHALL use an interleaving-string dynamic program evaluated over every start window of the text; the relation is symmetric so `M[j][k] = M[k][j]`.

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `compute` is run on text `GACCACGGTT` with patterns `ACAG`, `GT`, `CCG`
- **THEN** the result matrix is `Vector(Vector(0,0,1), Vector(0,1,0), Vector(1,0,0))`

#### Scenario: A pattern can be interwoven with itself
- **WHEN** `compute` is run on text `GACCACGGTT` with the single pattern `GT`
- **THEN** `M[0][0]` is `1` (e.g. the substring `GGTT` is an interleaving of `GT` and `GT`)

#### Scenario: Disjoint coverage forbids reuse of overlapping characters
- **WHEN** the patterns `ACAG` and `CCG` are tested for interweaving into `GACCACAAAAGGTT`
- **THEN** the pair is not interweavable (the result entry is `0`)

#### Scenario: An interleaving must cover a contiguous window exactly
- **WHEN** the patterns `ACAG` and `CCG` are tested for interweaving into `ACACG`
- **THEN** the pair is not interweavable (the result entry is `0`)
