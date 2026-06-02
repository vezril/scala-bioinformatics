## Why

Rosalind problem LING ("Linguistic Complexity of a Genome") measures how repetitive a DNA string is: `lc(s) = sub(s) / m(a,n)`, the ratio of the distinct substrings actually observed in `s` to the maximum number theoretically possible. It is the natural sequel to the suffix-tree problems (SUFF/LREP) — counting distinct substrings is a classic suffix-structure application — and adds the project's first genome-complexity metric.

## What Changes

- Introduce a `LinguisticComplexityProblem` domain type wrapping the DNA string `s` (`DnaString`); the 100 kbp bound is already guaranteed by `DnaString`, so it carries no extra invariant.
- Introduce a `LinguisticComplexity` result type holding the ratio, with a Rosalind-style `format` rounded to three decimals.
- Introduce a `LinguisticComplexityAnalysis` algorithm computing `lc(s) = sub(s) / m(4,n)` where `sub(s)` (distinct substrings) is obtained via a suffix array + LCP (`sub(s) = n(n+1)/2 − ΣLCP`) and `m(4,n) = Σ_{k=1}^{n} min(4^k, n−k+1)`.
- Add a `LINGProb` runner reading `s` from `ling_data.txt` and printing `lc(s)` through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.DnaString`.

## Capabilities

### New Capabilities
- `linguistic-complexity`: Compute the linguistic complexity `lc(s)` of a DNA string — the ratio of its distinct substrings to the maximum possible (Rosalind LING).

### Modified Capabilities
<!-- None. LING adds a new capability and does not change any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.analysis`): `LinguisticComplexityProblem`, `LinguisticComplexity` (result).
- **New algorithm** (`bio.algorithms.analysis.LinguisticComplexityAnalysis`).
- **New runner** (`bio.problems.LINGProb`) reading `src/main/scala/resources/ling_data.txt`.
- **Reused, unchanged**: `bio.domain.nucleic.DnaString`.
- **Tests**: new specs under `bio.domain.analysis` and `bio.algorithms.analysis`. No existing tests change.
