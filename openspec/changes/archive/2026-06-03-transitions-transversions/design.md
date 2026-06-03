## Context

TRAN ("Transitions and Transversions") takes two equal-length DNA strings (≤ 1 kbp, FASTA) and returns `R(s1, s2)`, the ratio of transition substitutions to transversion substitutions across the mismatched positions. A *transition* swaps two purines (`A↔G`) or two pyrimidines (`C↔T`); a *transversion* swaps a purine for a pyrimidine (`A↔C`, `A↔T`, `G↔C`, `G↔T`). Mismatches are identified exactly as for Hamming distance. The sample ratio is `1.21428571429`.

The project already has `DnaString` and `FastaFileReader`. TRAN is a DNA substitution analysis, so the types live in `bio.domain.nucleic` / `bio.algorithms.nucleic`.

## Goals / Non-Goals

**Goals:**
- Validated `TransitionTransversionProblem(first, second)` (equal length, each ≤ 1000 bp) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `TransitionTransversionAnalysis.analyze(problem): TransitionTransversionRatio`.
- Result type exposing `ratio: Double` and `format: String`.
- Match the canonical sample (`1.21428571429`).

**Non-Goals:**
- Strings of unequal length — rejected by validation (the ratio is only defined for equal-length strings).
- Modelling the substitution as a typed event — counts suffice.

## Decisions

**1. Classify each mismatch as transition or transversion.**
Zip the two sequences, keep the mismatched positions (`x != y`), and count those that are transitions: a mismatch is a *transition* iff both bases are purines (`{A, G}`) or both are pyrimidines (`{C, T}`); otherwise it is a *transversion*. `transversions = mismatches − transitions`. `O(L)` single pass. As in `HammingDistance`, the comparison is on the underlying `Char`s (both inputs are validated `DnaString`s), classified via small purine/pyrimidine sets.

**2. Ratio with a defined zero case.**
`TransitionTransversionRatio(transitions: Int, transversions: Int)` computes `ratio = transitions / transversions` as a `Double`, defined as `0.0` when `transversions == 0` (avoids division-by-zero / `NaN` for degenerate inputs such as identical strings). Real Rosalind data always has transversions.

**3. Result rendering to 11 decimals.**
`format = f"$ratio%.11f"`, which reproduces the sample `1.21428571429` (the value `17/14 = 1.2142857142857…` rounded to 11 places). This is well inside Rosalind's allowed error.

**4. Validation and first-failure-wins ordering.**
`TransitionTransversionProblem.from(first, second)` enforces, in order: `first.length ≤ 1000` then `second.length ≤ 1000` (`SequenceTooLong(length, 1000)`), then equal length (`LengthMismatch(firstLength, secondLength)`). Character validity (`A`,`C`,`G`,`T`) is owned upstream by `DnaString`. Two equal empty strings are accepted (ratio `0.0`).

**5. Pure functional analysis.**
The mismatch classification uses `lazyZip`/`count` over the strings — no mutation. The public `analyze` signature is pure and total.

**6. Naming and placement.**
`TransitionTransversionProblem`, `TransitionTransversionProblemError`, and the `TransitionTransversionRatio` result live in `bio.domain.nucleic`; the algorithm `TransitionTransversionAnalysis.analyze` in `bio.algorithms.nucleic`. Result (`TransitionTransversionRatio`) and algorithm (`TransitionTransversionAnalysis`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Division by zero]** → handled by defining `ratio = 0.0` when `transversions == 0`; covered by an identical-strings scenario.
- **[Formatting precision]** → `%.11f` reproduces the sample exactly and exceeds the grader's tolerance; no `BigDecimal` needed.
- **[Unequal lengths]** → rejected with `LengthMismatch` (the ratio is undefined otherwise).
- **[Classification correctness]** → purine/pyrimidine set membership covers all four bases; verified on the sample and small hand cases (a pure-transition pair and a pure-transversion pair).
