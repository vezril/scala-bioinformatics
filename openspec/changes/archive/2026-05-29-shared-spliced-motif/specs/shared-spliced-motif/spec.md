## ADDED Requirements

### Requirement: Validated SharedSplicedMotifProblem input bundle

The system SHALL provide a validated domain type `bio.domain.analysis.SharedSplicedMotifProblem` constructed only through a smart constructor `SharedSplicedMotifProblem.from(left: DnaString, right: DnaString): Either[SharedSplicedMotifProblemError, SharedSplicedMotifProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, max)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, max)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value MUST expose `left: DnaString` and `right: DnaString`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind LCSQ sample
- **WHEN** `SharedSplicedMotifProblem.from` is called with `AACCTTGG` and `ACACTGTGA`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `SharedSplicedMotifProblem.from` is called with two empty `DnaString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `SharedSplicedMotifProblem.from` is called with empty left and `ACGT` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `SharedSplicedMotifProblem.from` is called with `ACGT` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-character upper bound
- **WHEN** `SharedSplicedMotifProblem.from` is called with `"A" * 1000` and `"A" * 1000`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-character left as LeftTooLong(1001, 1000)
- **WHEN** `SharedSplicedMotifProblem.from` is called with a 1001-char left and a short right
- **THEN** it returns `Left(SharedSplicedMotifProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-character right as RightTooLong(1001, 1000)
- **WHEN** `SharedSplicedMotifProblem.from` is called with a short left and a 1001-char right
- **THEN** it returns `Left(SharedSplicedMotifProblemError.RightTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `SharedSplicedMotifProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: SharedSplicedMotif.find returns a longest common subsequence

The system SHALL provide an algorithm object `bio.algorithms.analysis.SharedSplicedMotif` with a method `find(problem: SharedSplicedMotifProblem): String` that returns *one* longest common subsequence of `problem.left` and `problem.right`.

The implementation MUST use the classical `O(m · n)` dynamic-programming approach: fill a `(m + 1) × (n + 1)` table `dp(i)(j) = LCS-length of left[0..i)` and `right[0..j)`, then backtrack from `dp(m)(n)` to reconstruct one LCS. On backtracking ties (`dp(i-1)(j) == dp(i)(j-1)` after a mismatch), the implementation MUST prefer the "up" direction (decrement `i`) for determinism.

When `left` or `right` is empty, the result MUST be `""`.

#### Scenario: Canonical Rosalind LCSQ sample (length-6 LCS)
- **WHEN** `SharedSplicedMotif.find` is called with `AACCTTGG` and `ACACTGTGA`
- **THEN** the result has length `6` AND is a subsequence of both inputs (multiple length-6 LCSes exist — Rosalind's published `"AACTGG"` is one valid answer; our deterministic convention produces a different but equally-valid one)

#### Scenario: Identical strings return the string itself
- **WHEN** `SharedSplicedMotif.find` is called with `ACGT` and `ACGT`
- **THEN** it returns `"ACGT"`

#### Scenario: Empty left returns the empty string
- **WHEN** `SharedSplicedMotif.find` is called with empty left and `ACGT` right
- **THEN** it returns `""`

#### Scenario: Empty right returns the empty string
- **WHEN** `SharedSplicedMotif.find` is called with `ACGT` left and empty right
- **THEN** it returns `""`

#### Scenario: No shared character returns the empty string
- **WHEN** `SharedSplicedMotif.find` is called with `ACG` and `TTT`
- **THEN** it returns `""`

#### Scenario: Single common character returns that character
- **WHEN** `SharedSplicedMotif.find` is called with `A` and `TA`
- **THEN** it returns `"A"`

#### Scenario: Unique two-character LCS `ACG`/`TCG` → `"CG"`
- **WHEN** `SharedSplicedMotif.find` is called with `ACG` and `TCG`
- **THEN** it returns `"CG"` (the unique length-2 LCS)
