## ADDED Requirements

### Requirement: Transition/Transversion Problem domain type

The system SHALL provide a validated `TransitionTransversionProblem` domain type in `bio.domain.nucleic` wrapping a `first` and a `second` `DnaString`. It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(first: DnaString, second: DnaString): Either[TransitionTransversionProblemError, TransitionTransversionProblem]`. The constructor SHALL enforce, with first-failure-wins ordering: each sequence length at most 1000, then equal length. Character validity (`A`, `C`, `G`, `T`) is enforced upstream by `DnaString`.

#### Scenario: Accepts two equal-length sequences within the bound
- **WHEN** `TransitionTransversionProblem.from` is called with two DNA strings of length 10
- **THEN** it returns a `Right` holding a `TransitionTransversionProblem`

#### Scenario: Accepts two equal empty sequences
- **WHEN** `TransitionTransversionProblem.from` is called with two empty DNA strings
- **THEN** it returns a `Right` holding a `TransitionTransversionProblem`

#### Scenario: Rejects a sequence longer than the bound
- **WHEN** `TransitionTransversionProblem.from` is called with a sequence of length 1001
- **THEN** it returns a `Left` holding `TransitionTransversionProblemError.SequenceTooLong(1001, 1000)`

#### Scenario: Rejects sequences of unequal length
- **WHEN** `TransitionTransversionProblem.from` is called with sequences of lengths 4 and 5
- **THEN** it returns a `Left` holding `TransitionTransversionProblemError.LengthMismatch(4, 5)`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.nucleic.TransitionTransversionProblem(first, second)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `TransitionTransversionProblem`
- **THEN** the code does not compile

### Requirement: Transition/Transversion Problem error ADT

The system SHALL provide a `TransitionTransversionProblemError` sealed ADT in `bio.domain.nucleic` enumerating the validation failures for `TransitionTransversionProblem`: `SequenceTooLong(length: Int, max: Int)` and `LengthMismatch(firstLength: Int, secondLength: Int)`, each carrying the relevant diagnostic values.

#### Scenario: Reports the two differing lengths
- **WHEN** sequences of lengths 7 and 9 are rejected for unequal length
- **THEN** the error is `TransitionTransversionProblemError.LengthMismatch(7, 9)`

### Requirement: Transition/Transversion ratio result type

The system SHALL provide a `TransitionTransversionRatio` result type in `bio.domain.nucleic` holding the `transitions` and `transversions` counts, exposing `ratio: Double` (the transitions-to-transversions ratio, defined as `0.0` when there are no transversions) and a `format: String` rendering the ratio to 11 decimal places.

#### Scenario: Computes the ratio from the counts
- **WHEN** a `TransitionTransversionRatio` with `transitions = 17` and `transversions = 14` is queried
- **THEN** `ratio` is approximately `1.2142857` (within 0.0001) and `format` returns `"1.21428571429"`

#### Scenario: Defines the ratio as zero when there are no transversions
- **WHEN** a `TransitionTransversionRatio` with `transversions = 0` is queried
- **THEN** `ratio` is `0.0`

### Requirement: Transition/Transversion analysis algorithm

The system SHALL provide a `TransitionTransversionAnalysis` algorithm in `bio.algorithms.nucleic` with a pure, total method `analyze(problem: TransitionTransversionProblem): TransitionTransversionRatio`. It SHALL count, over the mismatched positions, the transitions (both bases purines `A`/`G`, or both pyrimidines `C`/`T`) and the transversions (all other mismatches).

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `analyze` is run on `GCAACGCACAACGAAAACCCTTAGGGACTGGATTATTTCGTGATCGTTGTAGTTATTGGAAGTACGGGCATCAACCCAGTT` and `TTATCTGACAAAGAAAGCCGTCAACGGCTGGATAATTTCGCGATCGTGCTGGTTACTGGCGGTACGAGTGTTCCTTTGGGT`
- **THEN** the result's `format` returns `"1.21428571429"`

#### Scenario: Identical sequences have no substitutions
- **WHEN** `analyze` is run on two identical DNA strings
- **THEN** `transitions` and `transversions` are both `0` and `ratio` is `0.0`

#### Scenario: Counts a pure-transition pairing
- **WHEN** `analyze` is run on `AC` and `GT` (`A→G` and `C→T`, both transitions)
- **THEN** `transitions` is `2`, `transversions` is `0`

#### Scenario: Counts a pure-transversion pairing
- **WHEN** `analyze` is run on `A` and `C` (a purine-to-pyrimidine substitution)
- **THEN** `transitions` is `0`, `transversions` is `1`
