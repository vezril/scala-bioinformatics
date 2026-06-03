# wobble-rna-matchings Specification

## Purpose

Counts the exact number of valid noncrossing matchings in the bonding graph of an
RNA string under wobble base-pairing rules, serving the Rosalind "Wobble Bonding and
RNA Secondary Structures" (RNAS) problem. Provides the validated
`WobbleMatchingProblem` input type (an `RnaString` of length at most 200) with its
`WobbleMatchingProblemError` ADT, the `WobbleMatchings` result type carrying an exact
`BigInt` count, and the `WobbleMatching` algorithm whose pure, total `count` method
admits Watson–Crick pairs (`A`–`U`, `C`–`G`) and the wobble pairs (`U`–`G`), requiring
a minimum separation of four positions between paired bases, computed via an O(n³)
interval dynamic program in exact `BigInt` arithmetic.

## Requirements

### Requirement: Wobble Matching Problem domain type

The system SHALL provide a validated `WobbleMatchingProblem` domain type in `bio.domain.nucleic` wrapping an `RnaString` whose length is at most 200 bases. It SHALL be a `sealed abstract case class` with a private construction path, built only through a smart constructor `from(rna: RnaString): Either[WobbleMatchingProblemError, WobbleMatchingProblem]`. Character validity (`A`, `C`, `G`, `U`) is enforced upstream by `RnaString`.

#### Scenario: Accepts an RNA string within the length bound
- **WHEN** `WobbleMatchingProblem.from` is called with an `RnaString` of length ≤ 200
- **THEN** it returns a `Right` holding a `WobbleMatchingProblem` wrapping that RNA string

#### Scenario: Accepts the empty RNA string
- **WHEN** `WobbleMatchingProblem.from` is called with an empty `RnaString`
- **THEN** it returns a `Right` holding a `WobbleMatchingProblem`

#### Scenario: Rejects an RNA string longer than the bound
- **WHEN** `WobbleMatchingProblem.from` is called with an `RnaString` of length 201
- **THEN** it returns a `Left` holding `WobbleMatchingProblemError.SequenceTooLong(201, 200)`

#### Scenario: Cannot be constructed via a public companion apply
- **WHEN** `bio.domain.nucleic.WobbleMatchingProblem(rna)` is referenced in source
- **THEN** the code does not compile

#### Scenario: Does not expose a public copy method
- **WHEN** `copy` is invoked on a constructed `WobbleMatchingProblem`
- **THEN** the code does not compile

### Requirement: Wobble Matching Problem error ADT

The system SHALL provide a `WobbleMatchingProblemError` sealed ADT in `bio.domain.nucleic` enumerating the validation failures for `WobbleMatchingProblem`. It SHALL include a `SequenceTooLong(length: Int, max: Int)` case carrying the offending length and the maximum allowed length.

#### Scenario: Reports the offending and maximum lengths
- **WHEN** an RNA string of length 250 is rejected for exceeding the bound
- **THEN** the error is `WobbleMatchingProblemError.SequenceTooLong(250, 200)`

### Requirement: Wobble Matchings result type

The system SHALL provide a `WobbleMatchings` result type in `bio.domain.nucleic` holding the exact total count of valid noncrossing matchings as a `BigInt`, and exposing a `format: String` that renders the count as its decimal string.

#### Scenario: Formats the count as a decimal string
- **WHEN** a `WobbleMatchings` holding the count `284850219977421` is formatted
- **THEN** `format` returns `"284850219977421"`

### Requirement: Wobble matching count algorithm

The system SHALL provide a `WobbleMatching` algorithm in `bio.algorithms.nucleic` with a pure, total method `count(problem: WobbleMatchingProblem): WobbleMatchings`. It SHALL count every valid noncrossing perfect-or-partial matching in the bonding graph of the RNA string, where an edge may connect positions `i < k` only when `pair(s_i, s_k)` holds and `k ≥ i + 4`. The pairing relation `pair` SHALL admit Watson–Crick pairs `A`–`U` / `U`–`A`, `C`–`G` / `G`–`C`, and the wobble pairs `U`–`G` / `G`–`U`. The empty matching always counts. The count SHALL be computed exactly in `BigInt` arithmetic with no modular reduction, via an O(n³) interval dynamic program.

#### Scenario: Matches the canonical Rosalind sample
- **WHEN** `count` is run on the RNA string `AUGCUAGUACGGAGCGAGUCUAGCGAGCGAUGUCGUGAGUACUAUAUAUGCGCAUAAGCCACGU`
- **THEN** the result's count is `284850219977421`

#### Scenario: Empty string has exactly one (empty) matching
- **WHEN** `count` is run on the empty RNA string
- **THEN** the result's count is `1`

#### Scenario: A string with no admissible pairs has exactly one matching
- **WHEN** `count` is run on `AAAA` (no base can pair with another)
- **THEN** the result's count is `1`

#### Scenario: A wobble pair closer than the minimum separation does not count
- **WHEN** `count` is run on `GU` (a wobble pair, but the two positions are only one apart)
- **THEN** the result's count is `1`

#### Scenario: A wobble pair at the minimum separation is counted
- **WHEN** `count` is run on `GAAAU` (positions 0 and 4 form a `G`–`U` wobble pair at distance 4)
- **THEN** the result's count is `2`
