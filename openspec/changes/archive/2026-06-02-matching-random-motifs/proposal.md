## Why

Rosalind problem RSTR ("Matching Random Motifs") asks: given a motif `s`, a count `N`, and a GC-content `x`, what is the probability that at least one of `N` independently generated random DNA strings (each the length of `s`, built with GC-content `x`) equals `s`? It extends the project's probabilistic-analysis capabilities (the PROB / "Introduction to Random Strings" family) from per-string match log-probabilities to a closed-form "at least one of N matches" probability, reusing the validated `Probability` type.

## What Changes

- Introduce a validated `RandomMotifProblem` domain type holding a motif (`DnaString`, ≤ 10 bp), a trial count `N` (1 ≤ N ≤ 100000), and a GC-content fraction (`Probability`, already validated to `[0,1]`).
- Introduce a `RandomMotifProblemError` ADT for the new invariants (motif too long, non-positive trials, too many trials).
- Introduce a `RandomMotifMatch` result type wrapping the computed probability, with a Rosalind-style `format` rounded to three decimals.
- Introduce a `MatchingRandomMotifs` algorithm computing `p = ∏ P(symbol | x)` (with `P(G)=P(C)=x/2`, `P(A)=P(T)=(1-x)/2`, dispatched on the `DnaNucleotide` ADT) and the answer `1 - (1 - p)^N`.
- Add an `RSTRProb` runner reading `N`, `x`, and the motif from `rstr_data.txt` and printing the probability through `IO`.
- Reuse existing infrastructure unchanged: `bio.domain.stats.Probability`, `bio.domain.nucleic.{DnaString, DnaNucleotide}`.

## Capabilities

### New Capabilities
- `matching-random-motifs`: Compute the probability that at least one of `N` random DNA strings (length |s|, GC-content `x`) equals a given motif `s` (Rosalind RSTR).

### Modified Capabilities
<!-- None. RSTR adds a new capability and reuses the existing Probability type without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.analysis`): `RandomMotifProblem`, `RandomMotifProblemError`, `RandomMotifMatch` (result).
- **New algorithm** (`bio.algorithms.analysis.MatchingRandomMotifs`).
- **New runner** (`bio.problems.RSTRProb`) reading `src/main/scala/resources/rstr_data.txt`.
- **Reused, unchanged**: `bio.domain.stats.Probability`, `bio.domain.nucleic.{DnaString, DnaNucleotide}`.
- **Tests**: new specs under `bio.domain.analysis` and `bio.algorithms.analysis`. No existing tests change.
