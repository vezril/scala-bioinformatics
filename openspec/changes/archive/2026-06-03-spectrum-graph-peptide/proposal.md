## Why

Rosalind problem SGRA ("Using the Spectrum Graph to Infer Peptides") builds the spectrum graph of a list `L` of masses — a node per mass, a directed edge `u→v` whenever `v−u` equals an amino-acid residue mass — and asks for the longest protein matching it. A matching protein is exactly the edge-label sequence of a path through this DAG, so the answer is the longest path. It rounds out the protein mass-spectrometry family (PRTM, SPEC, CONV, PRSM, FULL) with a graph/longest-path formulation.

## What Changes

- Introduce a validated `SpectrumGraphProblem` domain type wrapping the mass list `L` (`Vector[Double]`, size ≤ 100, all positive).
- Introduce a `SpectrumGraphProblemError` ADT for the new invariants (too many masses, non-positive mass).
- Introduce a `SpectrumGraphPeptide` result type holding the longest matching protein, with a `format` returning it verbatim.
- Introduce a `SpectrumGraph` algorithm that builds the DAG (residue-mass-gap edges between value-sorted nodes) and returns the longest path's edge-label string, via a memoised longest-path dynamic program.
- Add an `SGRAProb` runner reading `L` from `sgra_data.txt` and printing the longest matching protein through `IO`.
- Reuse existing infrastructure: `bio.domain.protein.AminoAcid`.

## Capabilities

### New Capabilities
- `spectrum-graph-peptide`: Infer the longest protein matching the spectrum graph of a mass list `L` — the longest path in the DAG of residue-mass-gap edges (Rosalind SGRA).

### Modified Capabilities
<!-- None. SGRA adds a new capability and reuses AminoAcid without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.protein`): `SpectrumGraphProblem`, `SpectrumGraphProblemError`, `SpectrumGraphPeptide` (result).
- **New algorithm** (`bio.algorithms.protein.SpectrumGraph`).
- **New runner** (`bio.problems.SGRAProb`) reading `src/main/scala/resources/sgra_data.txt`.
- **Reused, unchanged**: `bio.domain.protein.AminoAcid`.
- **Tests**: new specs under `bio.domain.protein` and `bio.algorithms.protein`. No existing tests change.
