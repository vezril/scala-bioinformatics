## ADDED Requirements

### Requirement: Validated EditDistanceProblem input bundle

The system SHALL provide a validated domain type `bio.domain.protein.EditDistanceProblem` constructed only through a smart constructor `EditDistanceProblem.from(left: ProteinString, right: ProteinString): Either[EditDistanceProblemError, EditDistanceProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, max)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, max)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value MUST expose `left: ProteinString` and `right: ProteinString`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind EDIT sample
- **WHEN** `EditDistanceProblem.from` is called with `PLEASANTLY` and `MEANLY`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `EditDistanceProblem.from` is called with two empty `ProteinString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `EditDistanceProblem.from` is called with empty left and `MEANLY` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `EditDistanceProblem.from` is called with `PLEASANTLY` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-character upper bound
- **WHEN** `EditDistanceProblem.from` is called with `"A" * 1000` and `"A" * 1000`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-character left as LeftTooLong(1001, 1000)
- **WHEN** `EditDistanceProblem.from` is called with a 1001-char left and a short right
- **THEN** it returns `Left(EditDistanceProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-character right as RightTooLong(1001, 1000)
- **WHEN** `EditDistanceProblem.from` is called with a short left and a 1001-char right
- **THEN** it returns `Left(EditDistanceProblemError.RightTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `EditDistanceProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: EditDistance.compute returns the Levenshtein distance

The system SHALL provide an algorithm object `bio.algorithms.protein.EditDistance` with a method `compute(problem: EditDistanceProblem): Int` that returns the Levenshtein distance between `problem.left` and `problem.right` — the minimum number of single-symbol substitutions, insertions, and deletions needed to transform `left` into `right`.

The implementation MUST use the classical `O(m · n)` dynamic-programming approach: fill a `(m + 1) × (n + 1)` table `dp(i)(j) = edit-distance of left[0..i)` and `right[0..j)` using the recurrence:

- `dp(0)(j) = j` and `dp(i)(0) = i`;
- if `left(i-1) == right(j-1)`: `dp(i)(j) = dp(i-1)(j-1)`;
- else: `dp(i)(j) = 1 + min(dp(i-1)(j), dp(i)(j-1), dp(i-1)(j-1))`.

The result MUST be `dp(m)(n)`. When `left` is empty, the result MUST be `right.value.length`; when `right` is empty, the result MUST be `left.value.length`; when both are empty, the result MUST be `0`.

#### Scenario: Canonical Rosalind EDIT sample (PLEASANTLY / MEANLY → 5)
- **WHEN** `EditDistance.compute` is called with `PLEASANTLY` and `MEANLY`
- **THEN** the result is `5`

#### Scenario: Identical strings return 0
- **WHEN** `EditDistance.compute` is called with `MEANLY` and `MEANLY`
- **THEN** the result is `0`

#### Scenario: Empty left returns the length of right
- **WHEN** `EditDistance.compute` is called with empty left and `MEANLY` right
- **THEN** the result is `6`

#### Scenario: Empty right returns the length of left
- **WHEN** `EditDistance.compute` is called with `PLEASANTLY` left and empty right
- **THEN** the result is `10`

#### Scenario: Both strings empty returns 0
- **WHEN** `EditDistance.compute` is called with two empty protein strings
- **THEN** the result is `0`

#### Scenario: Single substitution is distance 1
- **WHEN** `EditDistance.compute` is called with `A` and `M`
- **THEN** the result is `1`

#### Scenario: Single insertion is distance 1
- **WHEN** `EditDistance.compute` is called with `MEANLY` and `MEANLLY`
- **THEN** the result is `1`

#### Scenario: Single deletion is distance 1
- **WHEN** `EditDistance.compute` is called with `MEANLY` and `MEANL`
- **THEN** the result is `1`

#### Scenario: Completely disjoint strings of equal length require full substitution
- **WHEN** `EditDistance.compute` is called with `AAA` and `MMM`
- **THEN** the result is `3`
