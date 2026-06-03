## Why

Rosalind problem FULL ("Inferring Peptide from Full Spectrum") reconstructs a peptide of length `n` from a list `L` of `2n+3` masses: the parent mass plus the `2n+2` b-ion and y-ion masses (complements guaranteed present). Because consecutive prefix (b-ion) masses differ by exactly one residue mass, the peptide is recovered by walking the prefix-ion series. It extends the protein mass-spectrometry family (PRTM, SPEC, CONV, PRSM) with full-spectrum peptide inference.

## What Changes

- Introduce a validated `FullSpectrumProblem` domain type wrapping the mass list `L` (`Vector[Double]`), whose size must be `2n+3` for some `n ≥ 1` and whose values are positive.
- Introduce a `FullSpectrumProblemError` ADT for the new invariants (invalid size, non-positive mass).
- Introduce an `InferredPeptide` result type holding the reconstructed peptide string, with a `format` returning it verbatim.
- Introduce an `InferPeptide` algorithm: drop the parent mass, sort the `2n+2` ions, and greedily extend the prefix series — from the smallest ion, repeatedly take the next ion whose gap from the current prefix matches an amino-acid residue mass — yielding the `n` residues.
- Add a `FULLProb` runner reading `L` from `full_data.txt` and printing the inferred peptide through `IO`.
- Reuse existing infrastructure: `bio.domain.protein.AminoAcid`.

## Capabilities

### New Capabilities
- `peptide-from-full-spectrum`: Reconstruct a length-`n` peptide from the full b-ion/y-ion spectrum `L` by walking its prefix-ion series (Rosalind FULL).

### Modified Capabilities
<!-- None. FULL adds a new capability and reuses AminoAcid without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.protein`): `FullSpectrumProblem`, `FullSpectrumProblemError`, `InferredPeptide` (result).
- **New algorithm** (`bio.algorithms.protein.InferPeptide`).
- **New runner** (`bio.problems.FULLProb`) reading `src/main/scala/resources/full_data.txt`.
- **Reused, unchanged**: `bio.domain.protein.AminoAcid`.
- **Tests**: new specs under `bio.domain.protein` and `bio.algorithms.protein`. No existing tests change.
