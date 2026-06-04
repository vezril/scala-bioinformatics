## Why

Rosalind problem MPRT ("Finding a Protein Motif") gives up to 15 UniProt access IDs and asks, for each protein possessing the N-glycosylation motif `N{P}[ST]{P}`, to output its ID and the 1-based locations where the motif occurs. The motif shorthand uses `[XY]` ("X or Y") and `{X}` ("any residue except X"). The pure, testable core is (1) parsing the motif shorthand into an ADT and (2) finding all match locations in a protein sequence; fetching the sequences from UniProt is an `IO` side effect at the runner boundary.

## What Changes

- Introduce a `MotifElement` ADT modelling one motif position: `OneOf(residues)` (a literal residue or a `[…]` group) or `NoneOf(residues)` (a `{…}` negation).
- Introduce a `ProteinMotif` domain type (a sequence of `MotifElement`s) with a `parse` that turns the shorthand (e.g. `N{P}[ST]{P}`) into the ADT.
- Introduce a `ProteinMotifError` ADT for parse failures (empty motif; unexpected character; unterminated group).
- Introduce a `MotifLocations` result type holding a protein ID and its 1-based match positions, with a `format`.
- Introduce a `MotifSearch` algorithm finding every 1-based start position where the motif matches a protein sequence (overlaps included).
- Add an `MPRTProb` runner reading the IDs from `mprt_data.txt`, fetching each protein's sequence from UniProt over HTTP (`IO.blocking` + JDK `HttpURLConnection`, no new dependency), applying the motif, and printing each matching protein's ID and locations.

## Capabilities

### New Capabilities
- `protein-motif`: Parse a protein-motif shorthand (`[XY]` / `{X}`) into a matcher and find all 1-based locations of the motif in a protein sequence (Rosalind MPRT, N-glycosylation motif `N{P}[ST]{P}`).

### Modified Capabilities
<!-- None. MPRT adds a new capability without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.protein`): `MotifElement`, `ProteinMotif`, `ProteinMotifError`, `MotifLocations` (result).
- **New algorithm** (`bio.algorithms.protein.MotifSearch`) — an `O(L · m)` scan over start positions.
- **New runner** (`bio.problems.MPRTProb`) reading `src/main/scala/resources/mprt_data.txt` and fetching sequences from UniProt via JDK HTTP.
- **Reused, unchanged**: none required for the pure core (the matcher works over raw residue strings to tolerate non-standard UniProt residues such as `U`/`X`).
- **Tests**: new specs under `bio.domain.protein` and `bio.algorithms.protein`. No existing tests change. (Network fetching is a runner-only concern, not unit-tested, consistent with file-I/O runners.)
