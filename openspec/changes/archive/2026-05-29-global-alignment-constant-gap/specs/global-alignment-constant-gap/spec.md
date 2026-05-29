## ADDED Requirements

### Requirement: Validated ConstantGapAlignmentScoreProblem input bundle

The system SHALL provide a validated domain type
`bio.domain.protein.ConstantGapAlignmentScoreProblem` constructed only
through a smart constructor
`ConstantGapAlignmentScoreProblem.from(left: ProteinString, right: ProteinString): Either[ConstantGapAlignmentScoreProblemError, ConstantGapAlignmentScoreProblem]`.
The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, 1000)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, 1000)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value
MUST expose `left: ProteinString` and `right: ProteinString`. The case
class MUST be `sealed abstract` so the synthesised `apply` and `copy`
cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind GCON sample
- **WHEN** `ConstantGapAlignmentScoreProblem.from` is called with `PLEASANTLY` and `MEANLY`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `ConstantGapAlignmentScoreProblem.from` is called with two empty `ProteinString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `ConstantGapAlignmentScoreProblem.from` is called with empty left and `MEANLY` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `ConstantGapAlignmentScoreProblem.from` is called with `PLEASANTLY` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-aa upper bound
- **WHEN** `ConstantGapAlignmentScoreProblem.from` is called with a 1000-aa left and a 1000-aa right
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-aa left as LeftTooLong(1001, 1000)
- **WHEN** `ConstantGapAlignmentScoreProblem.from` is called with a 1001-aa left and a short right
- **THEN** it returns `Left(ConstantGapAlignmentScoreProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-aa right as RightTooLong(1001, 1000)
- **WHEN** `ConstantGapAlignmentScoreProblem.from` is called with a short left and a 1001-aa right
- **THEN** it returns `Left(ConstantGapAlignmentScoreProblemError.RightTooLong(1001, 1000))`

#### Scenario: Reports LeftTooLong first when both sides exceed the cap
- **WHEN** `ConstantGapAlignmentScoreProblem.from` is called with a 1001-aa left and a 1001-aa right
- **THEN** it returns `Left(ConstantGapAlignmentScoreProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `ConstantGapAlignmentScoreProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: ConstantGapAlignmentScore.compute returns the maximum constant-gap global alignment score

The system SHALL provide an algorithm object
`bio.algorithms.protein.ConstantGapAlignmentScore` with a method
`compute(problem: ConstantGapAlignmentScoreProblem): Int` that returns the
maximum global alignment score of `problem.left` and `problem.right` under
the BLOSUM62 substitution matrix and a **constant** gap penalty of `5` —
every gap (a maximal run of contiguous insertions or contiguous deletions)
is charged `5` exactly once, regardless of its length.

The implementation MUST use a three-state `O(m · n)` dynamic program with
tables `M` (ending in a match/substitution), `X` (ending in a gap in
`right`), and `Y` (ending in a gap in `left`), where opening a gap or
switching gap direction costs `-5` and extending an existing gap costs `0`:

- `M(i)(j) = Blosum62.score(s_i, t_j) + max(M(i-1)(j-1), X(i-1)(j-1), Y(i-1)(j-1))`;
- `X(i)(j) = max(M(i-1)(j) - 5, X(i-1)(j), Y(i-1)(j) - 5)`;
- `Y(i)(j) = max(M(i)(j-1) - 5, Y(i)(j-1), X(i)(j-1) - 5)`;
- boundaries `M(0)(0) = 0`, `X(i)(0) = -5` for `i >= 1`, `Y(0)(j) = -5`
  for `j >= 1`, all other boundary states `-∞`;
- the answer is `max(M(m)(n), X(m)(n), Y(m)(n))`.

#### Scenario: Canonical Rosalind GCON sample
- **WHEN** `ConstantGapAlignmentScore.compute` is called with `PLEASANTLY` and `MEANLY`
- **THEN** the result is `13` — the exact Rosalind published output

#### Scenario: Identical strings score the sum of their self-substitution values
- **WHEN** `ConstantGapAlignmentScore.compute` is called with `MEANLY` and `MEANLY`
- **THEN** the result is `31` (M/M 5 + E/E 5 + A/A 4 + N/N 6 + L/L 4 + Y/Y 7)

#### Scenario: An empty left scores a single constant gap regardless of the right length
- **WHEN** `ConstantGapAlignmentScore.compute` is called with an empty left and `MEANLY` right
- **THEN** the result is `-5`

#### Scenario: An empty right scores a single constant gap regardless of the left length
- **WHEN** `ConstantGapAlignmentScore.compute` is called with `PLEASANTLY` left and an empty right
- **THEN** the result is `-5`

#### Scenario: Two empty strings score zero
- **WHEN** `ConstantGapAlignmentScore.compute` is called with two empty strings
- **THEN** the result is `0`

#### Scenario: A single matched pair scores its BLOSUM62 diagonal value
- **WHEN** `ConstantGapAlignmentScore.compute` is called with `W` and `W`
- **THEN** the result is `11`

#### Scenario: A single mismatched pair scores its BLOSUM62 off-diagonal value
- **WHEN** `ConstantGapAlignmentScore.compute` is called with `A` and `R`
- **THEN** the result is `-1`

#### Scenario: The gap penalty is independent of gap length
- **WHEN** `ConstantGapAlignmentScore.compute` is called with `A` and `AA`, and with `A` and a right of ten `A`s
- **THEN** both calls return `-1` (one matched `A` scoring 4 minus a single constant gap of 5), demonstrating length-independence

#### Scenario: The score is symmetric in its arguments
- **WHEN** `ConstantGapAlignmentScore.compute` is called with `(PLEASANTLY, MEANLY)` and with `(MEANLY, PLEASANTLY)`
- **THEN** both calls return the same score
