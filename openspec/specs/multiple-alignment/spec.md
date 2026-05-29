# multiple-alignment Specification

## Purpose

Computes one optimal multiple alignment of four DNA strings under the
linear scoring scheme `match = 0`, `mismatch = -1`, summed over all
`C(4, 2) = 6` augmented-string pairs (Rosalind spec 43 — MULT,
"Multiple Alignment"). This is the first Rosalind problem requiring
N-dimensional DP + traceback and the natural follow-on to the pairwise
alignment ladder (EDIT, EDTA, GLOB). Provides a validated four-string
input bundle (each ≤ 10 bp), a `MultipleAlignment` output ADT, and the
`MultipleAlignment.align` 4-dimensional DP + traceback algorithm.

## Requirements

### Requirement: Validated MultipleAlignmentProblem input bundle

The system SHALL provide a validated domain type `bio.domain.analysis.MultipleAlignmentProblem` constructed only through a smart constructor `MultipleAlignmentProblem.from(strings: Vector[DnaString]): Either[MultipleAlignmentProblemError, MultipleAlignmentProblem]`. The smart constructor MUST enforce, in this order, first-failure-wins:

1. `strings.size == 4`, else `WrongNumberOfStrings(actual, expected = 4)`.
2. For every index `i` in `0..3` (ascending), `strings(i).value.length <= 10`, else `StringTooLong(index = i, length, max = 10)` for the *first* offending index.

Empty strings (length 0) MUST be accepted. The constructed value MUST expose `strings: Vector[DnaString]`. The case class MUST be `sealed abstract` so the synthesised `apply` and `copy` cannot leak around the smart constructor.

#### Scenario: Accepts the canonical Rosalind MULT sample
- **WHEN** `MultipleAlignmentProblem.from` is called with the four DNA strings `ATATCCG`, `TCCG`, `ATGTACTG`, `ATGTCTG`
- **THEN** it returns `Right(problem)` where the four inputs round-trip into the wrapper in the supplied order

#### Scenario: Accepts four empty strings
- **WHEN** `MultipleAlignmentProblem.from` is called with four empty `DnaString`s
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts four strings at the 10 bp upper bound
- **WHEN** `MultipleAlignmentProblem.from` is called with four 10-character DNA strings
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects fewer than 4 strings as WrongNumberOfStrings
- **WHEN** `MultipleAlignmentProblem.from` is called with three DNA strings
- **THEN** it returns `Left(MultipleAlignmentProblemError.WrongNumberOfStrings(3, 4))`

#### Scenario: Rejects more than 4 strings as WrongNumberOfStrings
- **WHEN** `MultipleAlignmentProblem.from` is called with five DNA strings
- **THEN** it returns `Left(MultipleAlignmentProblemError.WrongNumberOfStrings(5, 4))`

#### Scenario: Rejects an 11-character string as StringTooLong with the first offending index
- **WHEN** `MultipleAlignmentProblem.from` is called with four DNA strings where the second (index 1) is 11 characters and the others are short
- **THEN** it returns `Left(MultipleAlignmentProblemError.StringTooLong(1, 11, 10))`

#### Scenario: Reports the first offending index when multiple strings exceed the cap
- **WHEN** `MultipleAlignmentProblem.from` is called with four DNA strings where the second and fourth are both 11 characters
- **THEN** it returns `Left(MultipleAlignmentProblemError.StringTooLong(1, 11, 10))` (the first offending index)

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `MultipleAlignmentProblem(strings)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(strings = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: MultipleAlignment output ADT

The system SHALL provide a domain type `bio.domain.analysis.MultipleAlignment` carrying:

- `score: Int` — the maximum multiple-alignment score (sum over the six pairs of pairwise Hamming costs; matched columns including matched gap-vs-gap score `0`, all other pairs in a column score `-1`).
- `augmentedStrings: Vector[String]` — exactly four augmented strings, each the original input with `-` gap symbols inserted as needed.

The augmented strings MAY contain the `-` gap symbol and therefore are plain `String`, NOT `DnaString`. `MultipleAlignment` MUST be a plain `final case class` (free `apply`, `copy`, equality, and pattern-matching — no smart constructor).

The following invariants MUST hold for any `MultipleAlignment` returned by `MultipleAlignment.align`:

1. `augmentedStrings.size == 4`.
2. All four augmented strings have equal length.
3. No column has gap symbols in *every* row (the all-gap column is never optimal).
4. Stripping `-` from `augmentedStrings(k)` recovers `problem.strings(k).value` for every `k` in `0..3`.
5. `score` equals the sum over all `C(4, 2) = 6` ordered pairs `(j, k)` with `j < k` of `-1` per column position where `augmentedStrings(j)(p) != augmentedStrings(k)(p)`.

#### Scenario: Constructs with named fields
- **WHEN** code calls `MultipleAlignment(score = -18, augmentedStrings = Vector("ATAT-CCG", "-T---CCG", "ATGTACTG", "ATGT-CTG"))`
- **THEN** the resulting value has those fields exposed and is value-equal to another instance with the same fields

### Requirement: MultipleAlignment.align returns one optimal 4-string alignment

The system SHALL provide an algorithm object `bio.algorithms.analysis.MultipleAlignment` with a method `align(problem: MultipleAlignmentProblem): MultipleAlignment` that returns one optimal alignment of the four DNA strings under the linear scoring scheme `match = 0`, `mismatch = -1` (with `-` gap symbols also counted as a normal character in the per-pair Hamming distance — i.e., gap-vs-non-gap is a mismatch, gap-vs-gap is a match).

The implementation MUST use a 4-dimensional `O((n_0 + 1)(n_1 + 1)(n_2 + 1)(n_3 + 1) · 15)` dynamic-programming approach with traceback:

- Build a flat DP table `dp[i_0][i_1][i_2][i_3] = max alignment score over the prefixes`, with `dp[0][0][0][0] = 0`.
- For every other cell, iterate over the 15 non-empty subsets `mask ∈ 1..15` of `{0, 1, 2, 3}`. A subset is *legal* if for every `k` with bit `k` set, `i_k > 0`. The transition score is `dp[predecessor] + columnScore(mask, charsByIndex)` where `predecessor` decrements `i_k` by 1 for every `k` in `mask`, and `columnScore` is the sum over the 6 pairs as defined above.
- Take the max over legal moves. Record the `mask` that achieved the max for traceback.
- After filling, traceback from `(n_0, n_1, n_2, n_3)` to `(0, 0, 0, 0)`, building each row's augmented string in reverse via `StringBuilder`s, then reverse once at the end.

The returned `MultipleAlignment` MUST satisfy all five invariants in the `MultipleAlignment` Requirement above. When all four strings are empty, the result MUST be `MultipleAlignment(0, Vector("", "", "", ""))`.

#### Scenario: Canonical Rosalind MULT sample (score -18 with valid alignment)
- **WHEN** `MultipleAlignment.align` is called with the four DNA strings `ATATCCG`, `TCCG`, `ATGTACTG`, `ATGTCTG`
- **THEN** the result has `score == -18` and the five `MultipleAlignment` invariants hold (Rosalind permits any optimal alignment; the published sample `Vector("ATAT-CCG", "-T---CCG", "ATGTACTG", "ATGT-CTG")` is one valid answer, our deterministic mask-iteration may produce a different but equally-optimal one)

#### Scenario: All four strings empty yields the degenerate empty alignment
- **WHEN** `MultipleAlignment.align` is called with four empty DNA strings
- **THEN** it returns `MultipleAlignment(0, Vector("", "", "", ""))`

#### Scenario: All four strings identical scores 0 with identity alignment
- **WHEN** `MultipleAlignment.align` is called with four copies of `ACGT`
- **THEN** it returns `MultipleAlignment(0, Vector("ACGT", "ACGT", "ACGT", "ACGT"))`

#### Scenario: One non-empty string with three empties scores -3 × length (gap penalty against all three empty rows)
- **WHEN** `MultipleAlignment.align` is called with `ACGT` and three empty strings
- **THEN** the score equals `-12` (4 columns × 3 mismatched pairs per column), and the alignment is `Vector("ACGT", "----", "----", "----")`

#### Scenario: Alignment invariants hold for arbitrary input
- **WHEN** `MultipleAlignment.align` is called with four DNA strings of differing lengths within the cap
- **THEN** the returned alignment satisfies: exactly 4 rows, equal lengths across rows, no all-gap column, stripping `-` from each row recovers the corresponding original input, and the score equals the sum of `-1 × (mismatch column count)` over all 6 pairs
