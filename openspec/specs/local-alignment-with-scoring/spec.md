# local-alignment-with-scoring Specification

## Purpose

Computes the maximum local-alignment score between two protein strings
under PAM250 substitution scoring and a linear gap penalty of `-5`
(Rosalind spec 47 — LOCA, "Local Alignment with Scoring Matrix").
Returns the maximum score plus the two substrings of the originals that
achieve it. This is the classical *Smith-Waterman* algorithm and the
local counterpart to GLOB's Needleman-Wunsch. Provides a `Pam250`
substitution matrix lookup, a validated `LocalAlignmentProblem` input
bundle (length caps on both strings), a `LocalAlignment` output ADT, and
the `LocalAlignment.compute` algorithm.

## Requirements

### Requirement: Pam250 substitution-score matrix

The system SHALL provide an object `bio.algorithms.protein.Pam250` exposing a total function `score(a: AminoAcid, b: AminoAcid): Int` that returns the PAM250 substitution score for the pair `(a, b)`. The implementation MUST use the canonical 20 × 20 PAM250 matrix.

The scoring function MUST be symmetric: for every pair `(a, b)`, `score(a, b) == score(b, a)`.

#### Scenario: Self-substitution scores match canonical reference values
- **WHEN** `Pam250.score` is called with the same amino acid on both sides
- **THEN** `score(A, A)` is `2`, `score(W, W)` is `17`, `score(C, C)` is `12`, `score(Y, Y)` is `10`, `score(L, L)` is `6`, `score(F, F)` is `9`, `score(P, P)` is `6`

#### Scenario: Cross-substitution scores match canonical reference values
- **WHEN** `Pam250.score` is called with two different amino acids
- **THEN** `score(A, R)` is `-2`, `score(W, C)` is `-8`, `score(L, M)` is `4`, `score(F, Y)` is `7`, `score(I, V)` is `4`, `score(L, I)` is `2`

#### Scenario: The matrix is symmetric for every pair
- **WHEN** `Pam250.score(a, b)` is compared to `Pam250.score(b, a)` for every pair of amino acids in `AminoAcid.all`
- **THEN** every pair satisfies `score(a, b) == score(b, a)`

### Requirement: Validated LocalAlignmentProblem input bundle

The system SHALL provide a validated domain type `bio.domain.protein.LocalAlignmentProblem` constructed only through a smart constructor `LocalAlignmentProblem.from(left: ProteinString, right: ProteinString): Either[LocalAlignmentProblemError, LocalAlignmentProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, max)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, max)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value MUST expose `left: ProteinString` and `right: ProteinString`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind LOCA sample
- **WHEN** `LocalAlignmentProblem.from` is called with `MEANLYPRTEINSTRING` and `PLEASANTLYEINSTEIN`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `LocalAlignmentProblem.from` is called with two empty `ProteinString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `LocalAlignmentProblem.from` is called with empty left and `MEANLY` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `LocalAlignmentProblem.from` is called with `PLEASANTLY` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-character upper bound
- **WHEN** `LocalAlignmentProblem.from` is called with `"A" * 1000` and `"A" * 1000`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-character left as LeftTooLong(1001, 1000)
- **WHEN** `LocalAlignmentProblem.from` is called with a 1001-char left and a short right
- **THEN** it returns `Left(LocalAlignmentProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-character right as RightTooLong(1001, 1000)
- **WHEN** `LocalAlignmentProblem.from` is called with a short left and a 1001-char right
- **THEN** it returns `Left(LocalAlignmentProblemError.RightTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `LocalAlignmentProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: LocalAlignment output ADT

The system SHALL provide a domain type `bio.domain.protein.LocalAlignment` carrying:

- `score: Int` — the maximum local-alignment score.
- `leftSubstring: String` — a substring of `problem.left.value` that participated in the optimum.
- `rightSubstring: String` — a substring of `problem.right.value` that participated in the optimum.

`LocalAlignment` MUST be a plain `final case class` (free `apply`, `copy`, equality, and pattern-matching — no smart constructor). The substrings are plain `String`s without `-` gap characters; they are the original contiguous regions of the input strings, *not* augmented alignment strings.

#### Scenario: Constructs with named fields
- **WHEN** code calls `LocalAlignment(score = 23, leftSubstring = "LYPRTEINSTRIN", rightSubstring = "LYEINSTEIN")`
- **THEN** the resulting value exposes those three fields and is value-equal to another instance with the same fields

### Requirement: LocalAlignment.compute returns the maximum Smith-Waterman score with recovered substrings

The system SHALL provide an algorithm object `bio.algorithms.protein.LocalAlignment` with a method `compute(problem: LocalAlignmentProblem): LocalAlignment` that returns the maximum local-alignment score between `problem.left` and `problem.right` under PAM250 substitution scoring + a linear gap penalty of `-5`, plus the two substrings of the originals that achieve it.

The implementation MUST use the classical Smith-Waterman `O(m · n)` dynamic-programming approach with traceback from the global-max cell:

- `dp(0)(j) = dp(i)(0) = 0`;
- `dp(i)(j) = max(0, dp(i-1)(j-1) + Pam250.score(left(i-1), right(j-1)), dp(i-1)(j) - 5, dp(i)(j-1) - 5)`;
- Track the running global max `(maxScore, maxI, maxJ)` during the fill.
- Traceback from `(maxI, maxJ)` following the move that achieved each cell value, preferring **up > left > diagonal** on ties (preferring indels over substitution on cost-ties keeps the alignment compact and matches the canonical Rosalind sample's published output). Stop when the current cell's value is `0`. Emit characters per move:
  - **diagonal**: append `left(i-1)` to the left builder, append `right(j-1)` to the right builder, decrement both indices.
  - **up**: append `left(i-1)` to the left builder only, decrement `i`.
  - **left**: append `right(j-1)` to the right builder only, decrement `j`.
- Reverse both builders to recover the substrings in input order.

The returned `LocalAlignment` MUST satisfy:

1. `score >= 0`.
2. `leftSubstring` is a (possibly empty) contiguous substring of `problem.left.value`.
3. `rightSubstring` is a (possibly empty) contiguous substring of `problem.right.value`.
4. When `left` or `right` is empty, the result MUST be `LocalAlignment(0, "", "")`.

#### Scenario: Canonical Rosalind LOCA sample (score 23 with published substrings)
- **WHEN** `LocalAlignment.compute` is called with `MEANLYPRTEINSTRING` and `PLEASANTLYEINSTEIN`
- **THEN** the result is `LocalAlignment(23, "LYPRTEINSTRIN", "LYEINSTEIN")` — the exact Rosalind published output

#### Scenario: Two empty strings yield score 0 with empty substrings
- **WHEN** `LocalAlignment.compute` is called with two empty protein strings
- **THEN** the result is `LocalAlignment(0, "", "")`

#### Scenario: Empty left yields score 0 with empty substrings
- **WHEN** `LocalAlignment.compute` is called with empty left and `MEANLY` right
- **THEN** the result is `LocalAlignment(0, "", "")`

#### Scenario: Empty right yields score 0 with empty substrings
- **WHEN** `LocalAlignment.compute` is called with `PLEASANTLY` left and empty right
- **THEN** the result is `LocalAlignment(0, "", "")`

#### Scenario: Identical strings yield the sum of PAM250 self-substitution values
- **WHEN** `LocalAlignment.compute` is called with `MEANLY` and `MEANLY`
- **THEN** the result has `score == M(6) + E(4) + A(2) + N(2) + L(6) + Y(10) == 30` and both substrings equal `MEANLY`

#### Scenario: Single-letter self-substitution returns the PAM250 diagonal entry
- **WHEN** `LocalAlignment.compute` is called with `W` and `W`
- **THEN** the result has `score == 17` and both substrings equal `W`

#### Scenario: Single-letter cross-substitution yields max(0, off-diagonal) — possibly 0 if the off-diagonal is negative
- **WHEN** `LocalAlignment.compute` is called with `A` and `R` (PAM250 `A/R = -2 < 0`)
- **THEN** the result has `score == 0` and both substrings are empty (no positive-scoring local alignment exists)

#### Scenario: The score is symmetric in its arguments (PAM250 is symmetric)
- **WHEN** `LocalAlignment.compute` is called with `(MEANLYPRTEINSTRING, PLEASANTLYEINSTEIN)` and with `(PLEASANTLYEINSTEIN, MEANLYPRTEINSTRING)`
- **THEN** both calls return the same integer score
