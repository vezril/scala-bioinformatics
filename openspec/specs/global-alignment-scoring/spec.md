# global-alignment-scoring Specification

## Purpose

Computes the maximum global alignment score between two protein strings
under BLOSUM62 substitution scoring and a linear gap penalty of `-5`
(Rosalind spec 42 — GLOB, "Global Alignment with Scoring Matrix"). This
is the classical Needleman-Wunsch algorithm, the foundation for every
downstream alignment variant (Smith-Waterman local alignment, affine
gap penalties, PAM-matrix scoring). Provides a `Blosum62` substitution
matrix lookup, a validated `GlobalAlignmentScoreProblem` input bundle
(length caps on both strings), and the `GlobalAlignmentScore.compute`
algorithm returning the integer maximum score.

## Requirements

### Requirement: Blosum62 substitution-score matrix

The system SHALL provide an object `bio.algorithms.protein.Blosum62` exposing a total function `score(a: AminoAcid, b: AminoAcid): Int` that returns the BLOSUM62 substitution score for the pair `(a, b)`. The implementation MUST use the canonical NCBI BLOSUM62 20 × 20 matrix.

The scoring function MUST be symmetric: for every pair `(a, b)`, `score(a, b) == score(b, a)`.

#### Scenario: Self-substitution scores match canonical reference values
- **WHEN** `Blosum62.score` is called with the same amino acid on both sides
- **THEN** `score(A, A)` is `4`, `score(W, W)` is `11`, `score(C, C)` is `9`, `score(M, M)` is `5`, `score(G, G)` is `6`

#### Scenario: Cross-substitution scores match canonical reference values
- **WHEN** `Blosum62.score` is called with two different amino acids
- **THEN** `score(A, R)` is `-1`, `score(W, C)` is `-2`, `score(L, M)` is `2`, `score(P, Y)` is `-3`, `score(N, D)` is `1`

#### Scenario: The matrix is symmetric for every pair
- **WHEN** `Blosum62.score(a, b)` is compared to `Blosum62.score(b, a)` for every pair of amino acids in `AminoAcid.all`
- **THEN** every pair satisfies `score(a, b) == score(b, a)`

### Requirement: Validated GlobalAlignmentScoreProblem input bundle

The system SHALL provide a validated domain type `bio.domain.protein.GlobalAlignmentScoreProblem` constructed only through a smart constructor `GlobalAlignmentScoreProblem.from(left: ProteinString, right: ProteinString): Either[GlobalAlignmentScoreProblemError, GlobalAlignmentScoreProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, max)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, max)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value MUST expose `left: ProteinString` and `right: ProteinString`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind GLOB sample
- **WHEN** `GlobalAlignmentScoreProblem.from` is called with `PLEASANTLY` and `MEANLY`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `GlobalAlignmentScoreProblem.from` is called with two empty `ProteinString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `GlobalAlignmentScoreProblem.from` is called with empty left and `MEANLY` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `GlobalAlignmentScoreProblem.from` is called with `PLEASANTLY` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-character upper bound
- **WHEN** `GlobalAlignmentScoreProblem.from` is called with `"A" * 1000` and `"A" * 1000`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-character left as LeftTooLong(1001, 1000)
- **WHEN** `GlobalAlignmentScoreProblem.from` is called with a 1001-char left and a short right
- **THEN** it returns `Left(GlobalAlignmentScoreProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-character right as RightTooLong(1001, 1000)
- **WHEN** `GlobalAlignmentScoreProblem.from` is called with a short left and a 1001-char right
- **THEN** it returns `Left(GlobalAlignmentScoreProblemError.RightTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `GlobalAlignmentScoreProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: GlobalAlignmentScore.compute returns the maximum Needleman-Wunsch alignment score

The system SHALL provide an algorithm object `bio.algorithms.protein.GlobalAlignmentScore` with a method `compute(problem: GlobalAlignmentScoreProblem): Int` that returns the maximum global alignment score between `problem.left` and `problem.right` using BLOSUM62 substitution scoring and a linear gap penalty of `-5` (i.e., each gap symbol contributes `-5` to the score).

The implementation MUST use the classical `O(m · n)` Needleman-Wunsch dynamic-programming approach: fill a `(m + 1) × (n + 1)` table `dp(i)(j) = max alignment score of left[0..i)` and `right[0..j)` using:

- `dp(0)(0) = 0`;
- `dp(i)(0) = -5 * i` (all gaps on the right);
- `dp(0)(j) = -5 * j` (all gaps on the left);
- `dp(i)(j) = max(dp(i-1)(j-1) + Blosum62.score(left(i-1), right(j-1)), dp(i-1)(j) - 5, dp(i)(j-1) - 5)`.

The result MUST be `dp(m)(n)`. When `left` is empty, the result MUST be `-5 * right.value.length`; when `right` is empty, the result MUST be `-5 * left.value.length`; when both are empty, the result MUST be `0`.

#### Scenario: Canonical Rosalind GLOB sample (PLEASANTLY / MEANLY → 8)
- **WHEN** `GlobalAlignmentScore.compute` is called with `PLEASANTLY` and `MEANLY`
- **THEN** the result is `8`

#### Scenario: Identical strings score the sum of self-substitution values
- **WHEN** `GlobalAlignmentScore.compute` is called with `MEANLY` and `MEANLY`
- **THEN** the result is `M(5) + E(5) + A(4) + N(6) + L(4) + Y(7) == 31`

#### Scenario: Empty left scores -5 × length of right
- **WHEN** `GlobalAlignmentScore.compute` is called with empty left and `MEANLY` right
- **THEN** the result is `-30`

#### Scenario: Empty right scores -5 × length of left
- **WHEN** `GlobalAlignmentScore.compute` is called with `PLEASANTLY` left and empty right
- **THEN** the result is `-50`

#### Scenario: Both empty returns 0
- **WHEN** `GlobalAlignmentScore.compute` is called with two empty protein strings
- **THEN** the result is `0`

#### Scenario: Single-letter self-substitution returns the BLOSUM62 diagonal entry
- **WHEN** `GlobalAlignmentScore.compute` is called with `W` and `W`
- **THEN** the result is `11`

#### Scenario: Single-letter cross-substitution returns the BLOSUM62 off-diagonal entry
- **WHEN** `GlobalAlignmentScore.compute` is called with `A` and `R`
- **THEN** the result is `-1`

#### Scenario: Single-letter insertion (`A` vs `AC`) prefers extension over gap-then-mismatch
- **WHEN** `GlobalAlignmentScore.compute` is called with `A` and `AC`
- **THEN** the result is `Blosum62.score(A, A) + (-5) == -1`

#### Scenario: The alignment score is symmetric in its arguments
- **WHEN** `GlobalAlignmentScore.compute` is called with `(PLEASANTLY, MEANLY)` and with `(MEANLY, PLEASANTLY)`
- **THEN** both calls return the same integer score (BLOSUM62 is symmetric)
