## ADDED Requirements

### Requirement: Validated AffineGapAlignmentProblem input bundle

The system SHALL provide a validated domain type
`bio.domain.protein.AffineGapAlignmentProblem` constructed only through a
smart constructor
`AffineGapAlignmentProblem.from(left: ProteinString, right: ProteinString): Either[AffineGapAlignmentProblemError, AffineGapAlignmentProblem]`.
The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 100`, else `LeftTooLong(length, 100)`.
2. `right.value.length <= 100`, else `RightTooLong(length, 100)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value
MUST expose `left: ProteinString` and `right: ProteinString`. The case class
MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak
around the smart constructor.

#### Scenario: Accepts the canonical Rosalind GAFF sample
- **WHEN** `AffineGapAlignmentProblem.from` is called with `PRTEINS` and `PRTWPSEIN`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `AffineGapAlignmentProblem.from` is called with two empty `ProteinString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `AffineGapAlignmentProblem.from` is called with empty left and `MEANLY` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `AffineGapAlignmentProblem.from` is called with `PRTEINS` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 100-aa upper bound
- **WHEN** `AffineGapAlignmentProblem.from` is called with a 100-aa left and a 100-aa right
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 101-aa left as LeftTooLong(101, 100)
- **WHEN** `AffineGapAlignmentProblem.from` is called with a 101-aa left and a short right
- **THEN** it returns `Left(AffineGapAlignmentProblemError.LeftTooLong(101, 100))`

#### Scenario: Rejects a 101-aa right as RightTooLong(101, 100)
- **WHEN** `AffineGapAlignmentProblem.from` is called with a short left and a 101-aa right
- **THEN** it returns `Left(AffineGapAlignmentProblemError.RightTooLong(101, 100))`

#### Scenario: Reports LeftTooLong first when both sides exceed the cap
- **WHEN** `AffineGapAlignmentProblem.from` is called with a 101-aa left and a 101-aa right
- **THEN** it returns `Left(AffineGapAlignmentProblemError.LeftTooLong(101, 100))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `AffineGapAlignmentProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: AffineGapAlignment.compute returns the maximum affine-gap global alignment score and one optimal alignment

The system SHALL provide an algorithm object
`bio.algorithms.protein.AffineGapAlignment` with a method
`compute(problem: AffineGapAlignmentProblem): bio.domain.protein.AffineGapAlignment`
that returns the maximum global alignment score of `problem.left` and
`problem.right` under the BLOSUM62 substitution matrix and an **affine** gap
penalty `a + b·(L − 1)` with gap-opening penalty `a = 11` and gap-extension
penalty `b = 1` (a gap of length `L` costs `11 + (L − 1)`), together with two
augmented strings realising one optimal alignment.

The result ADT `bio.domain.protein.AffineGapAlignment` MUST be a plain
`final case class` exposing `score: Int`, `augmentedLeft: String`, and
`augmentedRight: String`. The two augmented strings are plain `String`
(they contain `-` gap symbols, which are not valid amino-acid codes) and MUST
satisfy: equal length; no column with `-` in both rows; stripping `-` from
`augmentedLeft` recovers `problem.left.value` and from `augmentedRight`
recovers `problem.right.value`; and the affine-scored value of the alignment
equals `score`.

The implementation MUST use a three-state `O(m · n)` dynamic program with
tables `M` (ending in a match/substitution), `X` (ending in a gap in
`right`), and `Y` (ending in a gap in `left`), where opening a gap or
switching gap direction costs `-a` and extending an existing gap costs `-b`:

- `M(i)(j) = Blosum62.score(s_i, t_j) + max(M(i-1)(j-1), X(i-1)(j-1), Y(i-1)(j-1))`;
- `X(i)(j) = max(M(i-1)(j) - a, X(i-1)(j) - b, Y(i-1)(j) - a)`;
- `Y(i)(j) = max(M(i)(j-1) - a, Y(i)(j-1) - b, X(i)(j-1) - a)`;
- boundaries `M(0)(0) = 0`, `X(i)(0) = -(a + b·(i-1))` for `i >= 1`,
  `Y(0)(j) = -(a + b·(j-1))` for `j >= 1`, all other boundary states `-∞`;
- the score is `max(M(m)(n), X(m)(n), Y(m)(n))`, recovered with one optimal
  alignment by traceback.

#### Scenario: Canonical Rosalind GAFF sample score
- **WHEN** `AffineGapAlignment.compute` is called with `PRTEINS` and `PRTWPSEIN`
- **THEN** the result `score` is `8` — the exact Rosalind published output

#### Scenario: Canonical Rosalind GAFF sample alignment
- **WHEN** `AffineGapAlignment.compute` is called with `PRTEINS` and `PRTWPSEIN`
- **THEN** `augmentedLeft` is `PRT---EINS` and `augmentedRight` is `PRTWPSEIN-`

#### Scenario: The returned alignment is structurally valid
- **WHEN** `AffineGapAlignment.compute` is called with `PRTEINS` and `PRTWPSEIN`
- **THEN** `augmentedLeft` and `augmentedRight` have equal length, no column holds `-` in both rows, stripping `-` from each recovers `PRTEINS` and `PRTWPSEIN` respectively, and the affine-scored value of the alignment equals `score`

#### Scenario: Identical strings score the sum of their self-substitution values with no gaps
- **WHEN** `AffineGapAlignment.compute` is called with `PRTEINS` and `PRTEINS`
- **THEN** the `score` is `36` (P/P 7 + R/R 5 + T/T 5 + E/E 5 + I/I 4 + N/N 6 + S/S 4) and both augmented strings equal `PRTEINS`

#### Scenario: An empty left scores a single affine gap spanning the right
- **WHEN** `AffineGapAlignment.compute` is called with an empty left and `MEANLY` right
- **THEN** the `score` is `-16` (`-(11 + (6 - 1))`), `augmentedLeft` is six `-` symbols, and `augmentedRight` is `MEANLY`

#### Scenario: An empty right scores a single affine gap spanning the left
- **WHEN** `AffineGapAlignment.compute` is called with `PRTEINS` left and an empty right
- **THEN** the `score` is `-17` (`-(11 + (7 - 1))`), `augmentedLeft` is `PRTEINS`, and `augmentedRight` is seven `-` symbols

#### Scenario: Two empty strings score zero
- **WHEN** `AffineGapAlignment.compute` is called with two empty strings
- **THEN** the `score` is `0` and both augmented strings are empty

#### Scenario: A single matched pair scores its BLOSUM62 diagonal value
- **WHEN** `AffineGapAlignment.compute` is called with `W` and `W`
- **THEN** the `score` is `11` and both augmented strings are `W`

#### Scenario: A single mismatched pair scores its BLOSUM62 off-diagonal value
- **WHEN** `AffineGapAlignment.compute` is called with `A` and `R`
- **THEN** the `score` is `-1` and both augmented strings are single characters with no gaps

#### Scenario: The gap penalty grows with gap length (affine, not constant)
- **WHEN** `AffineGapAlignment.compute` is called with `A` and `AA`, and with `A` and `AAA`
- **THEN** the scores are `-7` and `-8` respectively — extending the single gap by one symbol costs exactly the extension penalty `b = 1`, distinguishing affine from a constant gap

#### Scenario: The score is symmetric in its arguments
- **WHEN** `AffineGapAlignment.compute` is called with `(PRTEINS, PRTWPSEIN)` and with `(PRTWPSEIN, PRTEINS)`
- **THEN** both calls return the same `score`
