## Why

Rosalind problem SPEC ("Inferring Protein from Spectrum") asks us to reconstruct a
protein from its **prefix spectrum** — the collection of cumulative prefix weights of a
weighted protein string. The gap between two consecutive prefix weights is the
monoisotopic mass of a single residue, so the protein is recovered by matching each
consecutive difference to the closest amino-acid mass. This is the project's first
mass-spectrometry-flavoured capability and the first to need the **monoisotopic mass
table**, a fundamental reference that further spectrum problems (PRTM, FULL, CONV) will
reuse.

## What Changes

- Extend the existing `AminoAcid` ADT with a `monoisotopicMass: Double` property on each
  of the 20 residues plus a companion `closestByMass(target: Double): AminoAcid` lookup
  (deterministic first-match on ties, e.g. the isobaric `I`/`L` pair). This is purely
  additive — translation behaviour is unchanged.
- Add a validated `PrefixSpectrum` domain bundle wrapping the list `L` of prefix weights
  (`n ≤ 100` positive reals), constructed through a smart constructor returning
  `Either[PrefixSpectrumError, PrefixSpectrum]` with first-failure-wins validation
  (empty, then over-long, then first non-positive weight).
- Add a `PrefixSpectrumError` ADT with `EmptySpectrum`, `TooManyWeights`, and
  `NonPositiveWeight` cases.
- Add an `InferredProtein` result type carrying the reconstructed `ProteinString`, with a
  `format` method rendering the protein's letters.
- Add a pure, total `InferProteinFromSpectrum.infer` algorithm: take consecutive
  differences of the prefix weights and map each to the closest amino acid by
  monoisotopic mass, yielding a protein of length `n − 1`.
- Add a `SPECProb` IO runner that reads the newline-separated weights from
  `spec_data.txt`, validates them into a `PrefixSpectrum`, infers the protein, and prints
  the result through `IO`; invalid input prints an error rather than throwing. Wire it
  into `Main`.

## Capabilities

### New Capabilities
- `protein-from-spectrum`: Validates a list of prefix weights and reconstructs a protein
  string of length `n − 1` whose prefix spectrum matches the input, by mapping each
  consecutive weight difference to the closest amino acid in the monoisotopic mass table.

### Modified Capabilities
- `protein-translation` (additive only): its `AminoAcid` alphabet gains a
  `monoisotopicMass` property and a `closestByMass` lookup. No existing behaviour,
  signature, or output changes — the new members are purely additional, so no
  translation spec scenarios are affected.

## Impact

- Extended domain type in `bio.domain.protein`: `AminoAcid` (adds `monoisotopicMass` and
  `closestByMass`).
- New domain types in `bio.domain.protein`: `PrefixSpectrum`, `PrefixSpectrumError`,
  `InferredProtein`.
- New algorithm in `bio.algorithms.protein`: `InferProteinFromSpectrum`.
- New IO runner `bio.problems.SPECProb`, wired into `bio.Main`.
- Reads `src/main/scala/resources/spec_data.txt` (one weight per line).
- No changes to existing algorithms, runners, or specs beyond the additive `AminoAcid`
  extension noted above.
