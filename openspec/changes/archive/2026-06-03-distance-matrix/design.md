## Context

PDST ("Creating a Distance Matrix") takes `n ≤ 10` equal-length DNA strings (each ≤ 1 kbp, FASTA) and returns the `n × n` p-distance matrix `D`, where `D[i][j] = d_p(s_i, s_j)` is the proportion of positions at which `s_i` and `s_j` differ — i.e. their Hamming distance divided by the common length. The diagonal is `0`, and the matrix is symmetric. Rosalind allows an absolute error of `0.001`, so plain `Double` arithmetic with 5-decimal formatting is sufficient.

The project already has `HammingDistance.between(a, b): Either[HammingError, Int]` (the count of differing positions) and `FastaFileReader` for multi-record DNA FASTA. PDST is a phylogenetic-distance computation, so the types live in `bio.domain.analysis` / `bio.algorithms.analysis` beside the tree/alignment work.

## Goals / Non-Goals

**Goals:**
- Validated `DistanceMatrixProblem(strings)` (≤ 10 equal-length strings, each ≤ 1000 bp) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `PDistanceMatrix.compute(problem): DistanceMatrix`.
- Result type with `format: String` (5-decimal values, space-separated, rows newline-joined).
- Match the canonical Rosalind sample within `0.001`.

**Non-Goals:**
- Other distance functions — only the p-distance.
- Strings of unequal length — rejected by validation (p-distance is undefined for them).

## Decisions

**1. Pairwise p-distance via `HammingDistance`.**
`D[i][j] = HammingDistance.between(s_i, s_j) / L` as a `Double`, where `L` is the common length. Because the problem validates equal lengths, the `Either` from `HammingDistance` is always `Right`; the algorithm treats an unexpected `Left` defensively (mapping to `0.0`, unreachable). The diagonal is `0.0` (`hamming(s_i, s_i) = 0`); the matrix is computed for every `(i, j)` directly (the symmetry is incidental, the `n²` fill is trivial at `n ≤ 10`). When `L = 0` (all-empty strings) the distance is `0.0` (no positions to differ).

**2. Validation and first-failure-wins ordering.**
`DistanceMatrixProblem.from(strings)` enforces, in order: `strings.size ≤ 10` (`TooManyStrings(count, 10)`), then each string length `≤ 1000` (`StringTooLong(length, 1000)`, first offender), then all strings share one length (`UnequalLengths(lengths)`, carrying every length). Character validity (`A`,`C`,`G`,`T`) is owned upstream by `DnaString`. An empty string list is accepted (degenerate `0 × 0` matrix); a single string yields the `1 × 1` matrix `[[0.0]]`.

**3. Result rendering at 5 decimals.**
`DistanceMatrix(rows: Vector[Vector[Double]])` with `format` rendering each value via the `f"$v%.5f"` interpolator (e.g. `0.40000`), entries space-separated, rows joined by `\n`. A `0 × 0` matrix formats to the empty string. (5-decimal output matches the sample and stays well inside the allowed `0.001` error.)

**4. Pure functional fill.**
The matrix is built with `Vector.tabulate(n, n)(...)` over the input vector — no mutation. The public `compute` signature is pure and total.

**5. Naming and placement.**
`DistanceMatrixProblem`, `DistanceMatrixProblemError`, and the `DistanceMatrix` result live in `bio.domain.analysis`; the algorithm `PDistanceMatrix.compute` in `bio.algorithms.analysis`. Result (`DistanceMatrix`) and algorithm (`PDistanceMatrix`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Floating-point formatting]** → 5-decimal `Double` formatting reproduces the sample exactly and is within the allowed `0.001`; no `BigDecimal` needed.
- **[Empty / singleton inputs]** → empty list → `0 × 0`; single string → `[[0.0]]`; all-empty strings → zero matrix (no divide-by-zero); covered by scenarios.
- **[Unequal lengths]** → rejected with `UnequalLengths` (p-distance is only defined for equal-length strings); covered by a scenario.
- **[`HammingDistance` `Either`]** → always `Right` given the equal-length invariant; the defensive `0.0` branch is unreachable in practice.
