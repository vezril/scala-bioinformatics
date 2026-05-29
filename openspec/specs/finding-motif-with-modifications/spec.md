# finding-motif-with-modifications Specification

## Purpose

Locates a motif `t` inside a longer text string `s` while tolerating
substitutions, insertions, and deletions, by computing a *fitting
alignment* (Rosalind spec 48 — SIMS, "Finding a Motif with
Modifications"). A fitting alignment aligns a *substring* `r ⊆ s` against
**all** of `t` to maximise the *mismatch score* (`+1` per matched symbol,
`-1` per mismatched, inserted, or deleted symbol). It is the "semi-global"
member of the alignment taxonomy — global with respect to the motif, local
with respect to the text — complementing the global (GLOB/EDTA) and local
(LOCA, Smith-Waterman) alignments already in the framework. Provides a
validated `FittingAlignmentProblem` input bundle (length caps on both
DNA strings), a `FittingAlignment` output ADT carrying the score and the
two augmented strings, and the `FittingAlignment.align` algorithm.

## Requirements

### Requirement: Validated FittingAlignmentProblem input bundle

The system SHALL provide a validated domain type
`bio.domain.analysis.FittingAlignmentProblem` constructed only through a
smart constructor
`FittingAlignmentProblem.from(text: DnaString, motif: DnaString): Either[FittingAlignmentProblemError, FittingAlignmentProblem]`.
The smart constructor MUST enforce, in this order, first-failure-wins:

1. `text.value.length <= 10000`, else `TextTooLong(length, max)`.
2. `motif.value.length <= 1000`, else `MotifTooLong(length, max)`.

Empty `text` and/or empty `motif` MUST be accepted. The constructed value
MUST expose `text: DnaString` and `motif: DnaString`. The case class MUST
be `sealed abstract` so the synthesised `apply` and `copy` cannot leak
around the smart constructor.

#### Scenario: Accepts the canonical Rosalind SIMS sample
- **WHEN** `FittingAlignmentProblem.from` is called with the 97 nt text `GCAAACCATAAGCCCTACGTGCCGCCTGTTTAAACTCGCGAACTGAATCTTCTGCTTCACGGTGAAAGTACCACAATGGTATCACACCCCAAGGAAAC` and the motif `GCCGTCAGGCTGGTGTCCG`
- **THEN** it returns `Right(problem)` where both inputs round-trip into the wrapper

#### Scenario: Accepts two empty strings
- **WHEN** `FittingAlignmentProblem.from` is called with two empty `DnaString`s
- **THEN** it returns `Right(problem)` where both fields are empty

#### Scenario: Accepts an empty text with a non-empty motif
- **WHEN** `FittingAlignmentProblem.from` is called with empty text and `GATTACA` motif
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts a non-empty text with an empty motif
- **WHEN** `FittingAlignmentProblem.from` is called with `GATTACA` text and empty motif
- **THEN** it returns `Right(problem)`

#### Scenario: Accepts text at the 10000-nt and motif at the 1000-nt upper bounds
- **WHEN** `FittingAlignmentProblem.from` is called with `"A" * 10000` text and `"A" * 1000` motif
- **THEN** it returns `Right(problem)`

#### Scenario: Rejects a 10001-nt text as TextTooLong(10001, 10000)
- **WHEN** `FittingAlignmentProblem.from` is called with a 10001-nt text and a short motif
- **THEN** it returns `Left(FittingAlignmentProblemError.TextTooLong(10001, 10000))`

#### Scenario: Rejects a 1001-nt motif as MotifTooLong(1001, 1000)
- **WHEN** `FittingAlignmentProblem.from` is called with a short text and a 1001-nt motif
- **THEN** it returns `Left(FittingAlignmentProblemError.MotifTooLong(1001, 1000))`

#### Scenario: Reports TextTooLong first when both sides exceed their caps
- **WHEN** `FittingAlignmentProblem.from` is called with a 10001-nt text and a 1001-nt motif
- **THEN** it returns `Left(FittingAlignmentProblemError.TextTooLong(10001, 10000))`

#### Scenario: Companion apply does not leak
- **WHEN** code attempts to construct via `FittingAlignmentProblem(text, motif)`
- **THEN** compilation fails (the synthesised apply is hidden behind the smart constructor)

#### Scenario: copy does not leak
- **WHEN** code attempts to mutate via `problem.copy(motif = other)`
- **THEN** compilation fails (the synthesised copy is hidden behind the smart constructor)

### Requirement: FittingAlignment output ADT

The system SHALL provide a domain type
`bio.domain.analysis.FittingAlignment` carrying:

- `score: Int` — the maximum fitting-alignment mismatch score (may be
  negative).
- `augmentedText: String` — a substring of `problem.text.value` with `-`
  gap symbols inserted, as it appears in the optimal alignment.
- `augmentedMotif: String` — the full `problem.motif.value` with `-` gap
  symbols inserted, as it appears in the optimal alignment.

`FittingAlignment` MUST be a plain `final case class` (free `apply`,
`copy`, equality, and pattern-matching — no smart constructor). The
augmented strings MAY contain `-` gap characters and are therefore plain
`String`s, not `DnaString`s.

#### Scenario: Constructs with named fields
- **WHEN** code calls `FittingAlignment(score = 5, augmentedText = "ACCATAAGCCCTACGTG-CCG", augmentedMotif = "GCCGTCAGGC-TG-GTGTCCG")`
- **THEN** the resulting value exposes those three fields and is value-equal to another instance with the same fields

### Requirement: FittingAlignment.align returns the maximum fitting-alignment score with a recovered augmented alignment

The system SHALL provide an algorithm object
`bio.algorithms.analysis.FittingAlignment` with a method
`align(problem: FittingAlignmentProblem): FittingAlignment` that returns
the maximum *fitting alignment* score between a substring of
`problem.text` and **all** of `problem.motif` under the *mismatch score*
(`+1` per matched symbol, `-1` per mismatched, inserted, or deleted
symbol), plus one optimal augmented alignment achieving it.

The implementation MUST use the classical `O(m · n)` dynamic-programming
approach with traceback:

- `dp(i)(0) = 0` for every `i` (free to begin the text substring anywhere);
- `dp(0)(j) = -j` (the empty text substring pays one gap per motif symbol);
- `dp(i)(j) = max(dp(i-1)(j-1) + (if text(i-1) == motif(j-1) then +1 else -1), dp(i-1)(j) - 1, dp(i)(j-1) - 1)`;
- the score is `max over i of dp(i)(n)` where `n = motif.length`; traceback
  starts from the smallest `i` attaining that maximum and stops when the
  motif index reaches `0`.

The returned `FittingAlignment` MUST satisfy:

1. `augmentedText.length == augmentedMotif.length`.
2. No alignment column contains a `-` in both rows.
3. Stripping `-` from `augmentedMotif` recovers the **entire**
   `problem.motif.value`.
4. Stripping `-` from `augmentedText` yields a (possibly empty) contiguous
   substring of `problem.text.value`.
5. The mismatch score of the aligned pair (counting `+1` per equal
   non-gap column and `-1` per mismatched-or-gap column) equals `score`.
6. When `motif` is empty, the result MUST be `FittingAlignment(0, "", "")`.

#### Scenario: Canonical Rosalind SIMS sample yields score 5
- **WHEN** `FittingAlignment.align` is called with text `GCAAACCATAAGCCCTACGTGCCGCCTGTTTAAACTCGCGAACTGAATCTTCTGCTTCACGGTGAAAGTACCACAATGGTATCACACCCCAAGGAAAC` and motif `GCCGTCAGGCTGGTGTCCG`
- **THEN** the result has `score == 5`, `augmentedText.length == augmentedMotif.length`, no column has gaps in both rows, stripping `-` from `augmentedMotif` equals the full motif, and stripping `-` from `augmentedText` is a contiguous substring of the text

#### Scenario: A clean motif occurrence scores one point per motif symbol
- **WHEN** `FittingAlignment.align` is called with text `TTGATTACATT` and motif `GATTACA`
- **THEN** the result is `FittingAlignment(7, "GATTACA", "GATTACA")`

#### Scenario: Identical text and motif score the full length with no gaps
- **WHEN** `FittingAlignment.align` is called with text `ACGT` and motif `ACGT`
- **THEN** the result is `FittingAlignment(4, "ACGT", "ACGT")`

#### Scenario: Empty motif yields score 0 with empty augmented strings
- **WHEN** `FittingAlignment.align` is called with text `GATTACA` and an empty motif
- **THEN** the result is `FittingAlignment(0, "", "")`

#### Scenario: Empty text charges one gap per motif symbol
- **WHEN** `FittingAlignment.align` is called with empty text and motif `ACG`
- **THEN** the result is `FittingAlignment(-3, "---", "ACG")`

#### Scenario: The motif is always fully consumed
- **WHEN** `FittingAlignment.align` is called with text `GCAAACCATAAGCCCTACGTGCCGCCTGTTTAAACTCGCGAACTGAATCTTCTGCTTCACGGTGAAAGTACCACAATGGTATCACACCCCAAGGAAAC` and motif `GCCGTCAGGCTGGTGTCCG`
- **THEN** `result.augmentedMotif.replace("-", "")` equals the original motif string

#### Scenario: The recovered text is a contiguous substring of the input text
- **WHEN** `FittingAlignment.align` is called with text `GCAAACCATAAGCCCTACGTGCCGCCTGTTTAAACTCGCGAACTGAATCTTCTGCTTCACGGTGAAAGTACCACAATGGTATCACACCCCAAGGAAAC` and motif `GCCGTCAGGCTGGTGTCCG`
- **THEN** `result.augmentedText.replace("-", "")` is contained in the original text string as a contiguous substring
