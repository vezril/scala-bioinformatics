## ADDED Requirements

### Requirement: Linguistic complexity problem representation

The system SHALL provide a `LinguisticComplexityProblem` domain type wrapping the DNA string `s` as a `DnaString`. The 100 kbp Rosalind bound is already guaranteed by `DnaString` (whose own maximum length is 100000 bp), so the problem type carries no additional invariant and exposes the wrapped DNA.

#### Scenario: Wraps and exposes the DNA string

- **WHEN** a `LinguisticComplexityProblem` is constructed from the `DnaString` `ATTTGGATT`
- **THEN** its `dna` field returns that `DnaString`

#### Scenario: Accepts an empty DNA string

- **WHEN** a `LinguisticComplexityProblem` is constructed from an empty `DnaString`
- **THEN** its `dna` field returns the empty `DnaString`

### Requirement: Linguistic complexity result rendering

The system SHALL provide a `LinguisticComplexity` result type holding the complexity ratio as a `Double` and exposing a `format: String` that renders it rounded to three decimal places.

#### Scenario: Exposes the value

- **WHEN** a `LinguisticComplexity` result is constructed with value `0.875`
- **THEN** its `value` field returns `0.875`

#### Scenario: Formats to three decimal places

- **WHEN** `format` is called on a result with value `0.875`
- **THEN** it returns `"0.875"`

#### Scenario: Rounds to three decimal places

- **WHEN** `format` is called on a result with value `0.4`
- **THEN** it returns `"0.400"`

### Requirement: Linguistic complexity computation

The system SHALL provide an algorithm that, given a `LinguisticComplexityProblem`, returns `lc(s) = sub(s) / m(4,n)`, where `sub(s)` is the number of distinct substrings of `s`, `n` is the length of `s`, and `m(4,n) = Σ_{k=1}^{n} min(4^k, n−k+1)` is the maximum possible number of distinct substrings.

#### Scenario: Computes the canonical Rosalind LING sample

- **WHEN** the algorithm is run on the DNA string `ATTTGGATT`
- **THEN** the result value is within `0.001` of `0.875`

#### Scenario: A non-repetitive single character has maximal complexity

- **WHEN** the algorithm is run on the DNA string `A`
- **THEN** the result value is `1.0` (sub = 1, m(4,1) = 1)

#### Scenario: A highly repetitive string has low complexity

- **WHEN** the algorithm is run on the DNA string `AAAA`
- **THEN** the result value is within `0.001` of `0.4` (sub = 4, m(4,4) = 10)

#### Scenario: Agrees with the direct distinct-substring count

- **WHEN** the algorithm is run on any DNA string `s`
- **THEN** the numerator `sub(s)` equals the number of distinct non-empty substrings of `s`, and the result equals `sub(s) / m(4,n)`
