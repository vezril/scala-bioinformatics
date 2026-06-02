## Why

Rosalind REVP ("Locating Restriction Sites") asks us to find every reverse
palindrome — a DNA substring equal to its reverse complement — of length 4 to 12,
reporting each one's 1-based position and length. Restriction enzymes recognize
exactly these sites, so this is a foundational DNA-analysis problem and the next
(spec 75) in the framework's progression.

## What Changes

- Add a `RestrictionSiteProblem` domain type: a validated wrapper over a
  `DnaString` capped at the REVP limit of 1 kbp (1000 bp), built only through a
  smart constructor returning `Either`.
- Add a `RestrictionSite` value type carrying a single palindrome's 1-based
  `position` and `length`, and a `RestrictionSites` result type wrapping the
  ordered collection with a `format` rendering the Rosalind two-column output.
- Add a pure `RestrictionSites.locate` algorithm that scans every start position
  and every even length 4–12, emitting a site wherever the substring equals its
  reverse complement (reusing the existing `DnaReverseComplement`).
- Add a `REVPProb` runner that reads the FASTA DNA string from `revp_data.txt`,
  solves the problem, and prints the position/length pairs through `IO`.

## Capabilities

### New Capabilities
- `restriction-sites`: validate a bounded DNA string, locate every reverse
  palindrome of length 4–12, and report each one's position and length.

### Modified Capabilities
<!-- None: this reuses existing DNA types/algorithms without changing their requirements. -->

## Impact

- New code: `bio.domain.nucleic.{RestrictionSiteProblem,
  RestrictionSiteProblemError, RestrictionSite, RestrictionSites}`,
  `bio.algorithms.nucleic.RestrictionSites`, `bio.problems.REVPProb`.
- New tests mirroring those packages under `src/test`.
- Reuses `DnaString`, `DnaNucleotide`, `DnaReverseComplement`, and the FASTA
  reader unchanged.
- `bio.Main` switches its active runner to `REVPProb`.
- Reads existing dataset `src/main/scala/resources/revp_data.txt`.
