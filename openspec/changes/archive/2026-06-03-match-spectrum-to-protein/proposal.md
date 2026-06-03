## Why

Rosalind problem PRSM ("Matching a Spectrum to a Protein") asks, given several candidate protein strings and a multiset `R` representing the complete spectrum of some unknown protein, which candidate best matches `R` — the one maximising the multiplicity of the spectral convolution `R⊖S[s_k]`, where `S[s]` is the multiset of all prefix and suffix weights of `s`. It combines the project's protein-mass and spectral-convolution capabilities (PRTM, CONV) into a best-match search.

## What Changes

- Introduce a validated `SpectrumMatchProblem` domain type wrapping the candidate proteins (`Vector[ProteinString]`) and the target spectrum `R` (`Vector[Double]` of positive numbers).
- Introduce a `SpectrumMatchProblemError` ADT for the new invariants (no candidates, empty spectrum, non-positive mass).
- Introduce a `SpectrumMatch` result type holding the winning multiplicity and protein, with a Rosalind-style `format` (multiplicity on one line, protein on the next).
- Introduce a `MatchSpectrum` algorithm computing, per candidate, the complete spectrum `S[s_k]` (cumulative prefix and suffix monoisotopic weights) and the maximum multiplicity of `R⊖S[s_k]` (the most frequent difference, bucketed to 5 decimals), then choosing the candidate with the greatest multiplicity.
- Add a `PRSMProb` runner reading `n`, the proteins, and `R` from `prsm_data.txt` and printing the best match through `IO`.
- Reuse existing infrastructure: `bio.domain.protein.{AminoAcid, ProteinString}`.

## Capabilities

### New Capabilities
- `match-spectrum-to-protein`: Find the candidate protein whose complete spectrum best matches a target spectrum `R` (maximum multiplicity of `R⊖S[s_k]`), returning that multiplicity and protein (Rosalind PRSM).

### Modified Capabilities
<!-- None. PRSM adds a new capability and reuses AminoAcid / ProteinString without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.protein`): `SpectrumMatchProblem`, `SpectrumMatchProblemError`, `SpectrumMatch` (result).
- **New algorithm** (`bio.algorithms.protein.MatchSpectrum`).
- **New runner** (`bio.problems.PRSMProb`) reading `src/main/scala/resources/prsm_data.txt`.
- **Reused, unchanged**: `bio.domain.protein.{AminoAcid, ProteinString}`.
- **Tests**: new specs under `bio.domain.protein` and `bio.algorithms.protein`. No existing tests change.
