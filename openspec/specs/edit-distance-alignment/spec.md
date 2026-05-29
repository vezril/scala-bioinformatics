# edit-distance-alignment Specification

## Purpose

Computes one optimal Levenshtein alignment of two protein strings —
the integer edit distance plus two augmented strings (the originals
with `-` gap symbols inserted) that realise it (Rosalind spec 41 —
EDTA, "Edit Distance Alignment"). Provides a validated
`EditDistanceAlignmentProblem` input bundle (length caps on both
strings), an `EditAlignment` output ADT, and the
`EditDistanceAlignment.align` classical `O(m · n)` DP + traceback
algorithm.

## Requirements

### Requirement: Validated EditDistanceAlignmentProblem input bundle

The system SHALL provide a validated domain type `bio.domain.protein.EditDistanceAlignmentProblem` constructed only through a smart constructor `EditDistanceAlignmentProblem.from(left: ProteinString, right: ProteinString): Either[EditDistanceAlignmentProblemError, EditDistanceAlignmentProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, max)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, max)`.

Empty `left` and/or empty `right` MUST be accepted. The constructed value MUST expose `left: ProteinString` and `right: ProteinString`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind EDTA sample
- **WHEN** `EditDistanceAlignmentProblem.from` is called with `PRETTY` and `PRTTEIN`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `EditDistanceAlignmentProblem.from` is called with two empty `ProteinString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty left with a non-empty right
- **WHEN** `EditDistanceAlignmentProblem.from` is called with empty left and `MEANLY` right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty left with an empty right
- **WHEN** `EditDistanceAlignmentProblem.from` is called with `PLEASANTLY` left and empty right
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts both strings at the 1000-character upper bound
- **WHEN** `EditDistanceAlignmentProblem.from` is called with `"A" * 1000` and `"A" * 1000`
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 1001-character left as LeftTooLong(1001, 1000)
- **WHEN** `EditDistanceAlignmentProblem.from` is called with a 1001-char left and a short right
- **THEN** it returns `Left(EditDistanceAlignmentProblemError.LeftTooLong(1001, 1000))`

#### Scenario: Rejects a 1001-character right as RightTooLong(1001, 1000)
- **WHEN** `EditDistanceAlignmentProblem.from` is called with a short left and a 1001-char right
- **THEN** it returns `Left(EditDistanceAlignmentProblemError.RightTooLong(1001, 1000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `EditDistanceAlignmentProblem(left, right)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(right = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: EditAlignment output ADT

The system SHALL provide a domain type `bio.domain.protein.EditAlignment` carrying the integer distance plus two augmented strings:

- `distance: Int` — the Levenshtein distance `d_E(left, right)`.
- `augmentedLeft: String` — the original `left.value` with `-` gap symbols inserted as needed.
- `augmentedRight: String` — the original `right.value` with `-` gap symbols inserted as needed.

The augmented strings MAY contain the `-` gap symbol and therefore are plain `String`, NOT `ProteinString`. `EditAlignment` MUST be a plain `final case class` (free `apply`, `copy`, equality, and pattern-matching — no smart constructor).

The following invariants MUST hold for any `EditAlignment` returned by `EditDistanceAlignment.align`:

1. `augmentedLeft.length == augmentedRight.length`.
2. No column has gap symbols in both rows — for every index `j`, `(augmentedLeft(j), augmentedRight(j)) != ('-', '-')`.
3. Removing all `-` characters from `augmentedLeft` recovers `problem.left.value`; removing all `-` characters from `augmentedRight` recovers `problem.right.value`.
4. The Hamming distance between `augmentedLeft` and `augmentedRight` equals `distance`.

#### Scenario: Constructs with named fields
- **WHEN** code calls `EditAlignment(distance = 4, augmentedLeft = "PRETTY--", augmentedRight = "PR-TTEIN")`
- **THEN** the resulting value has those three fields exposed and is value-equal to another instance with the same three fields

### Requirement: EditDistanceAlignment.align returns one optimal alignment

The system SHALL provide an algorithm object `bio.algorithms.protein.EditDistanceAlignment` with a method `align(problem: EditDistanceAlignmentProblem): EditAlignment` that returns one optimal alignment of `problem.left` and `problem.right`.

The implementation MUST use the classical `O(m · n)` Levenshtein dynamic-programming approach: fill a `(m + 1) × (n + 1)` table `dp(i)(j) = edit-distance of left[0..i)` and `right[0..j)` using:

- `dp(0)(j) = j` and `dp(i)(0) = i`;
- if `left(i-1) == right(j-1)`: `dp(i)(j) = dp(i-1)(j-1)`;
- else: `dp(i)(j) = 1 + min(dp(i-1)(j), dp(i)(j-1), dp(i-1)(j-1))`.

After filling, traceback from `(m, n)` to `(0, 0)`, preferring at each interior step (where `i > 0` and `j > 0`):

1. **diagonal-match** if `left(i-1) == right(j-1)` and `dp(i)(j) == dp(i-1)(j-1)` — emit `left(i-1)` over `right(j-1)`, decrement both;
2. **up (delete)** if `dp(i)(j) == dp(i-1)(j) + 1` — emit `left(i-1)` over `-`, decrement `i`;
3. **left (insert)** if `dp(i)(j) == dp(i)(j-1) + 1` — emit `-` over `right(j-1)`, decrement `j`;
4. **diagonal-substitution** otherwise (i.e., `dp(i)(j) == dp(i-1)(j-1) + 1`) — emit `left(i-1)` over `right(j-1)`, decrement both.

On the borders (`i == 0` ⇒ emit `-` over `right(j-1)` and decrement `j`; `j == 0` ⇒ emit `left(i-1)` over `-` and decrement `i`), the move is forced. This ordering — *match first, then prefer indels over substitution, with deletions (up) before insertions (left)* — produces the canonical Rosalind sample alignment and pushes gaps consistently toward the trailing end.

The returned `EditAlignment` MUST satisfy all four invariants in the `EditAlignment` Requirement above. When `left` is empty and `right` is empty, the result MUST be `EditAlignment(0, "", "")`.

#### Scenario: Canonical Rosalind EDTA sample (PRETTY / PRTTEIN)
- **WHEN** `EditDistanceAlignment.align` is called with `PRETTY` and `PRTTEIN`
- **THEN** it returns `EditAlignment(4, "PRETTY--", "PR-TTEIN")`

#### Scenario: Identical strings produce a gap-free identity alignment
- **WHEN** `EditDistanceAlignment.align` is called with `MEANLY` and `MEANLY`
- **THEN** it returns `EditAlignment(0, "MEANLY", "MEANLY")`

#### Scenario: Empty left aligns as all gaps over right
- **WHEN** `EditDistanceAlignment.align` is called with empty left and `MEANLY` right
- **THEN** it returns `EditAlignment(6, "------", "MEANLY")`

#### Scenario: Empty right aligns as left over all gaps
- **WHEN** `EditDistanceAlignment.align` is called with `PLEASANTLY` left and empty right
- **THEN** it returns `EditAlignment(10, "PLEASANTLY", "----------")`

#### Scenario: Both empty yields the degenerate empty alignment
- **WHEN** `EditDistanceAlignment.align` is called with two empty protein strings
- **THEN** it returns `EditAlignment(0, "", "")`

#### Scenario: Single substitution (A vs M) prefers the diagonal move
- **WHEN** `EditDistanceAlignment.align` is called with `A` and `M`
- **THEN** it returns `EditAlignment(1, "A", "M")`

#### Scenario: Single insertion (MEANLY vs MEANLLY) emits one gap on the left
- **WHEN** `EditDistanceAlignment.align` is called with `MEANLY` and `MEANLLY`
- **THEN** the returned alignment has `distance == 1`, `augmentedLeft.length == 7`, `augmentedRight == "MEANLLY"`, and removing `-` from `augmentedLeft` recovers `MEANLY`

#### Scenario: Single deletion (MEANLY vs MEANL) emits one gap on the right
- **WHEN** `EditDistanceAlignment.align` is called with `MEANLY` and `MEANL`
- **THEN** the returned alignment has `distance == 1`, `augmentedRight.length == 6`, `augmentedLeft == "MEANLY"`, and removing `-` from `augmentedRight` recovers `MEANL`

#### Scenario: Alignment invariants hold (PLEASANTLY / MEANLY)
- **WHEN** `EditDistanceAlignment.align` is called with `PLEASANTLY` and `MEANLY`
- **THEN** the returned alignment satisfies: equal lengths, no double-gap column, stripping `-` from each side recovers the original input, and Hamming distance between the two augmented strings equals `distance`
