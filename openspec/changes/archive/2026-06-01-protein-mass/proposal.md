## Why

Rosalind problem PRTM ("Calculating Protein Mass") asks for the total monoisotopic
weight of a protein string — the sum of its residues' monoisotopic masses. It is the
direct companion of the just-added SPEC capability (which *differences* a prefix
spectrum); PRTM simply *sums* the residue masses. Both rest on the monoisotopic mass
table now carried by the `AminoAcid` alphabet, so this capability is small, high-value,
and reuses existing infrastructure.

## What Changes

- Extend the existing `AminoAcid` ADT with a companion `fromChar(c: Char): Option[AminoAcid]`
  lookup (derived from `all`, the single source of truth) so algorithms can lift a
  validated protein character to its residue. This is purely additive — no existing
  behaviour changes.
- Add a validated `ProteinMassProblem` domain bundle wrapping a `ProteinString` of length
  `≤ 1000` aa, constructed through a smart constructor returning
  `Either[ProteinMassProblemError, ProteinMassProblem]`. Empty proteins are accepted.
- Add a `ProteinMassProblemError` ADT with a `ProteinTooLong` case.
- Add a `ProteinMass` result type carrying the total monoisotopic mass, with a `format`
  method rendering the mass to three decimal places.
- Add a pure, total `ProteinMass.calculate` algorithm that lifts each residue via
  `AminoAcid.fromChar` and sums their monoisotopic masses.
- Add a `PRTMProb` IO runner that reads the protein string from `prtm_data.txt`,
  validates it into a `ProteinMassProblem`, computes the total mass, and prints the
  formatted result through `IO`; invalid input prints an error rather than throwing. Wire
  it into `Main`.

## Capabilities

### New Capabilities
- `protein-mass`: Validates a protein string of length `≤ 1000` aa and computes its total
  monoisotopic mass (the sum of its residues' monoisotopic masses), rendering the result
  to three decimal places.

### Modified Capabilities
- `protein-translation` (additive only): its `AminoAcid` alphabet gains a `fromChar`
  lookup. No existing behaviour, signature, or output changes — the new member is purely
  additional, so no translation spec scenarios are affected.

## Impact

- Extended domain type in `bio.domain.protein`: `AminoAcid` (adds `fromChar`).
- New domain types in `bio.domain.protein`: `ProteinMassProblem`,
  `ProteinMassProblemError`, `ProteinMass`.
- New algorithm in `bio.algorithms.protein`: `ProteinMass`.
- New IO runner `bio.problems.PRTMProb`, wired into `bio.Main`.
- Reads `src/main/scala/resources/prtm_data.txt` (one protein string).
- No changes to existing algorithms, runners, or specs beyond the additive `AminoAcid`
  extension noted above.
