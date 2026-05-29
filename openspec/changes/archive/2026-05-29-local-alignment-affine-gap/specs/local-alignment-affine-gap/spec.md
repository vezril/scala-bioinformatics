## ADDED Requirements

### Requirement: Validated local-affine-alignment input bundle

The system SHALL provide a `LocalAffineAlignmentProblem` value bundling two
`ProteinString`s (`left` and `right`), each at most 10,000 amino acids, with a
smart constructor `from` returning `Either[LocalAffineAlignmentProblemError, LocalAffineAlignmentProblem]`.
The type SHALL be a `sealed abstract case class` so neither a synthesized
`apply` nor `copy` can bypass validation. Validation SHALL be first-failure-wins:
an over-length `left` SHALL be reported before an over-length `right`.

#### Scenario: Accepts the canonical sample inputs

- **WHEN** `from` is called with `left = "PLEASANTLY"` and `right = "MEANLY"`
- **THEN** it returns `Right` of a problem whose `left` and `right` match the inputs

#### Scenario: Accepts empty protein strings

- **WHEN** `from` is called with an empty `left` and/or empty `right`
- **THEN** it returns `Right` of a problem carrying those empty strings

#### Scenario: Accepts strings at the 10,000 amino-acid cap

- **WHEN** `from` is called with a `left` and `right` each exactly 10,000 amino acids long
- **THEN** it returns `Right` of a problem carrying those strings

#### Scenario: Rejects an over-length left string

- **WHEN** `from` is called with a `left` of 10,001 amino acids
- **THEN** it returns `Left(LeftTooLong(10001, 10000))`

#### Scenario: Rejects an over-length right string

- **WHEN** `from` is called with a valid `left` and a `right` of 10,001 amino acids
- **THEN** it returns `Left(RightTooLong(10001, 10000))`

#### Scenario: Reports the left failure first when both are over-length

- **WHEN** `from` is called with both `left` and `right` at 10,001 amino acids
- **THEN** it returns `Left(LeftTooLong(10001, 10000))`

#### Scenario: Construction cannot bypass validation

- **WHEN** code attempts to call `LocalAffineAlignmentProblem.apply` or `.copy` directly
- **THEN** the code SHALL fail to compile

### Requirement: Local alignment score and substrings under BLOSUM62 with an affine gap penalty

The system SHALL compute, via `LocalAffineAlignment.compute(problem)`, the
maximum *local* alignment score of the two protein strings under the BLOSUM62
substitution matrix and an affine gap penalty with gap-opening cost 11 and
gap-extension cost 1, together with one optimal pair of aligned substrings `r`
(of `left`) and `u` (of `right`). The result SHALL be a
`LocalAffineAlignment(score, leftSubstring, rightSubstring)` whose substrings are
contiguous regions of the respective inputs (containing no gap symbols). When
either input is empty, the result SHALL be `LocalAffineAlignment(0, "", "")`.

#### Scenario: Reproduces the canonical Rosalind LAFF sample

- **WHEN** `compute` is called with `left = "PLEASANTLY"` and `right = "MEANLY"`
- **THEN** the score is `12`, `leftSubstring` is `"LEAS"`, and `rightSubstring` is `"MEAN"`

#### Scenario: Returned substrings are contiguous infixes of the inputs

- **WHEN** `compute` is called with `left = "PLEASANTLY"` and `right = "MEANLY"`
- **THEN** `leftSubstring` is a contiguous substring of `left` and `rightSubstring` is a contiguous substring of `right`

#### Scenario: Identical strings align to themselves with no gaps

- **WHEN** `compute` is called with `left = "MEANLY"` and `right = "MEANLY"`
- **THEN** the score is `31` with `leftSubstring` and `rightSubstring` both `"MEANLY"`

#### Scenario: A single positively-scoring matched pair

- **WHEN** `compute` is called with `left = "W"` and `right = "W"`
- **THEN** the score is `11` with both substrings `"W"`

#### Scenario: A single negatively-scoring pair floors at zero

- **WHEN** `compute` is called with `left = "A"` and `right = "R"`
- **THEN** the score is `0` with both substrings `""`

#### Scenario: Local alignment avoids an unnecessary gap

- **WHEN** `compute` is called with `left = "A"` and `right = "AA"`
- **THEN** the score is `4` with `leftSubstring` `"A"` and `rightSubstring` `"A"`

#### Scenario: An empty left input yields a zero-score empty result

- **WHEN** `compute` is called with an empty `left` and `right = "MEANLY"`
- **THEN** the result is `LocalAffineAlignment(0, "", "")`

#### Scenario: An empty right input yields a zero-score empty result

- **WHEN** `compute` is called with `left = "PLEASANTLY"` and an empty `right`
- **THEN** the result is `LocalAffineAlignment(0, "", "")`

#### Scenario: Score is symmetric under argument swap

- **WHEN** `compute` is called with `(left, right)` and again with `(right, left)`
- **THEN** the two scores are equal
