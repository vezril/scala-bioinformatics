## Why

Rosalind problem EVAL ("Expected Number of Restriction Sites") asks, given a length `n`, a motif `s` of even length, and an array `A` of GC-content values, for the expected number of times `s` appears as a substring of a random length-`n` DNA string built with each GC-content in `A`. It rounds out the project's random-string-analysis family (PROB's log match-probabilities, RSTR's at-least-one-of-N probability) with the linearity-of-expectation count `(n - |s| + 1) · p`, reusing the validated `Probability` type and the same per-symbol GC model.

## What Changes

- Introduce a validated `ExpectedRestrictionSitesProblem` domain type holding a motif (`DnaString` of even length ≤ 10), a string length `n` (1 ≤ n ≤ 1,000,000), and an array of GC-content fractions (`Vector[Probability]`, size ≤ 20).
- Introduce an `ExpectedRestrictionSitesProblemError` ADT for the new invariants (motif too long, odd motif length, non-positive length, length too large, too many GC-contents).
- Introduce an `ExpectedRestrictionSites` result type holding the per-GC-content expected counts (`Vector[Double]`), with a Rosalind-style `format` (space-separated, three decimals each).
- Introduce an `ExpectedRestrictionSites` algorithm computing, for each GC-content `x`, `(max(0, n - |s| + 1)) · ∏ P(sⱼ | x)` with `P(G)=P(C)=x/2`, `P(A)=P(T)=(1-x)/2` (dispatched on the `DnaNucleotide` ADT).
- Add an `EVALProb` runner reading `n`, `s`, and `A` from `eval_data.txt` and printing the expected counts through `IO`.
- Reuse existing infrastructure unchanged: `bio.domain.stats.Probability`, `bio.domain.nucleic.{DnaString, DnaNucleotide}`.

## Capabilities

### New Capabilities
- `expected-restriction-sites`: Compute the expected number of occurrences of a motif `s` as a substring of a random length-`n` DNA string, for each GC-content in an array `A` (Rosalind EVAL).

### Modified Capabilities
<!-- None. EVAL adds a new capability and reuses the existing Probability type without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.analysis`): `ExpectedRestrictionSitesProblem`, `ExpectedRestrictionSitesProblemError`, `ExpectedRestrictionSites` (result).
- **New algorithm** (`bio.algorithms.analysis.ExpectedRestrictionSites`).
- **New runner** (`bio.problems.EVALProb`) reading `src/main/scala/resources/eval_data.txt`.
- **Reused, unchanged**: `bio.domain.stats.Probability`, `bio.domain.nucleic.{DnaString, DnaNucleotide}`.
- **Tests**: new specs under `bio.domain.analysis` and `bio.algorithms.analysis`. No existing tests change.
