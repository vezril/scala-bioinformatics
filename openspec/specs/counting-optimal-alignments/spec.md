# counting-optimal-alignments Specification

## Purpose

Counts the number of *distinct* optimal alignments of two protein
strings under the standard Levenshtein metric (Rosalind spec 46 —
CTEA, "Counting Optimal Alignments"). The result is returned modulo
`134_217_727 = 2^27 - 1`. This is the counting counterpart to EDIT
(integer distance) and EDTA (one example alignment): instead of
finding the minimum or constructing an instance, we count the
equivalence class. Provides a validated `OptimalAlignmentCountProblem`
input bundle (length caps on both strings) and the
`OptimalAlignmentCount.compute` parallel cost + count DP algorithm.

## Requirements

### Requirement: Validated OptimalAlignmentCountProblem input bundle

The system SHALL provide a validated domain type `bio.domain.protein.OptimalAlignmentCountProblem` constructed only through a smart constructor `OptimalAlignmentCountProblem.from(left: ProteinString, right: ProteinString): Either[OptimalAlignmentCountProblemError, OptimalAlignmentCountProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, max)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, max)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value MUST expose `left: ProteinString` and `right: ProteinString`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind CTEA sample
- **WHEN** `OptimalAlignmentCountProblem.from` is called with `PLEASANTLY` and `MEANLY`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `OptimalAlignmentCountProblem.from` is called with two empty `ProteinString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `OptimalAlignmentCountProblem.from` is called with empty left and `MEANLY` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `OptimalAlignmentCountProblem.from` is called with `PLEASANTLY` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-character upper bound
- **WHEN** `OptimalAlignmentCountProblem.from` is called with `"A" * 1000` and `"A" * 1000`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-character left as LeftTooLong(1001, 1000)
- **WHEN** `OptimalAlignmentCountProblem.from` is called with a 1001-char left and a short right
- **THEN** it returns `Left(OptimalAlignmentCountProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-character right as RightTooLong(1001, 1000)
- **WHEN** `OptimalAlignmentCountProblem.from` is called with a short left and a 1001-char right
- **THEN** it returns `Left(OptimalAlignmentCountProblemError.RightTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `OptimalAlignmentCountProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: OptimalAlignmentCount.compute returns the count of optimal alignments modulo 134_217_727

The system SHALL provide an algorithm object `bio.algorithms.protein.OptimalAlignmentCount` with a method `compute(problem: OptimalAlignmentCountProblem): Int` that returns the count of distinct optimal alignments of `problem.left` and `problem.right` under the standard Levenshtein metric (matched chars contribute 0, substitutions / insertions / deletions each contribute 1), modulo `134_217_727 = 2^27 - 1`.

The implementation MUST use a parallel Levenshtein cost + count dynamic-programming approach: fill a `(m + 1) × (n + 1)` cost table `dp(i)(j) = d_E(left[0..i), right[0..j))` alongside a count table `cnt(i)(j) = number of optimal alignments`, using:

- `dp(0)(0) = 0`, `cnt(0)(0) = 1`;
- `dp(i)(0) = i`, `cnt(i)(0) = 1`;
- `dp(0)(j) = j`, `cnt(0)(j) = 1`;
- `dp(i)(j) = min(dp(i-1)(j-1) + δ, dp(i-1)(j) + 1, dp(i)(j-1) + 1)`, where `δ = 0` if `left(i-1) == right(j-1)` else `1`;
- `cnt(i)(j) = sum-over-winning-moves of cnt(predecessor), mod 134_217_727`, where a move is "winning" iff its cost-extension equals `dp(i)(j)`.

The result MUST be `cnt(m)(n)`. When both `left` and `right` are empty, the result MUST be `1`; when exactly one is empty, the result MUST be `1`.

#### Scenario: Canonical Rosalind CTEA sample (PLEASANTLY / MEANLY → 4)
- **WHEN** `OptimalAlignmentCount.compute` is called with `PLEASANTLY` and `MEANLY`
- **THEN** the result is `4`

#### Scenario: Identical strings return 1
- **WHEN** `OptimalAlignmentCount.compute` is called with `MEANLY` and `MEANLY`
- **THEN** the result is `1`

#### Scenario: Empty left returns 1
- **WHEN** `OptimalAlignmentCount.compute` is called with empty left and `MEANLY` right
- **THEN** the result is `1`

#### Scenario: Empty right returns 1
- **WHEN** `OptimalAlignmentCount.compute` is called with `PLEASANTLY` left and empty right
- **THEN** the result is `1`

#### Scenario: Both strings empty returns 1
- **WHEN** `OptimalAlignmentCount.compute` is called with two empty protein strings
- **THEN** the result is `1`

#### Scenario: Two distinct single characters yield exactly 1 optimal alignment (substitution only)
- **WHEN** `OptimalAlignmentCount.compute` is called with `A` and `M`
- **THEN** the result is `1` (only the single-substitution alignment is optimal; delete-then-insert and insert-then-delete each have cost 2 > 1)

#### Scenario: `A` vs `AA` yields exactly 2 optimal alignments
- **WHEN** `OptimalAlignmentCount.compute` is called with `A` and `AA`
- **THEN** the result is `2` (the inserted `A` can land on either side of the matched `A`: `A-` / `AA` or `-A` / `AA`)

#### Scenario: The count is symmetric in its arguments
- **WHEN** `OptimalAlignmentCount.compute` is called with `(PLEASANTLY, MEANLY)` and with `(MEANLY, PLEASANTLY)`
- **THEN** both calls return the same integer count
