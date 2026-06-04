## Context

MPRT ("Finding a Protein Motif") locates the N-glycosylation motif `N{P}[ST]{P}` in proteins fetched from UniProt by access ID. The motif shorthand is a per-position language: a bare residue `X` means exactly `X`, `[XY…]` means any listed residue, and `{X…}` means any residue *except* those listed. The deliverable per protein is the list of 1-based start positions where the four-position pattern matches (overlaps count).

The pure, deterministic core — parsing the shorthand and scanning a sequence — is fully unit-testable. Fetching sequences from UniProt is a network side effect handled in the runner via `IO.blocking` and the JDK `HttpURLConnection` (no new library), mirroring how other runners isolate file I/O. UniProt sequences can include non-standard residues (`U`, `X`, `B`, `Z`), so the matcher operates on a raw residue `String` rather than the 20-code `ProteinString`.

## Goals / Non-Goals

**Goals:**
- A `MotifElement` ADT (`OneOf` / `NoneOf`) with a per-residue `matches`.
- A `ProteinMotif` domain type with a total `parse(shorthand): Either[ProteinMotifError, ProteinMotif]`.
- A `ProteinMotifError` ADT for parse failures.
- A pure, total `MotifSearch.findLocations(motif, protein): Vector[Int]` (1-based).
- A `MotifLocations` result with `format`.

**Non-Goals:**
- A full PROSITE-pattern engine (quantifiers `x(n)`, wildcards `x`, anchors) — only the `[…]`/`{…}`/literal subset MPRT needs.
- Unit-testing the UniProt fetch — it is a runner-only side effect.
- Validating residues against the 20 standard codes in the matcher (UniProt has more).

## Decisions

**1. Model each motif position as a `MotifElement`.**
`sealed trait MotifElement { def matches(c: Char): Boolean }` with `OneOf(residues: Set[Char])` (matches `c ∈ residues`) and `NoneOf(residues: Set[Char])` (matches `c ∉ residues`). A literal residue `X` parses to `OneOf(Set('X'))`, `[ST]` to `OneOf(Set('S','T'))`, `{P}` to `NoneOf(Set('P'))`. So `N{P}[ST]{P}` → `[OneOf{N}, NoneOf{P}, OneOf{S,T}, NoneOf{P}]`.

**2. Recursive-descent parser producing `Either`.**
`ProteinMotif.parse` scans left to right: `[` reads until `]` → `OneOf`; `{` reads until `}` → `NoneOf`; a letter → `OneOf(Set(letter))`; a stray `]`/`}` or non-letter → `UnexpectedCharacter(ch, index)`; an unclosed `[`/`{` → `UnterminatedGroup(start)`; empty input → `EmptyMotif`. Pure, total, returns the validated `ProteinMotif`.

**3. Scan every start position for matches.**
`MotifSearch.findLocations(motif, protein)` returns, for `m = motif.length` and `L = protein.length`, every `start ∈ [0, L−m]` (1 ≤ start+1) where `motif.elements(k).matches(protein(start+k))` holds for all `k`, reported as 1-based positions. Overlapping matches are all included. `O(L · m)`, pure functional via `filter`/`forall`.

**4. `ProteinMotif` as a `sealed abstract case class`.**
Constructed only via `parse` (or an internal factory), so the smart-constructor invariant (the elements came from a valid shorthand) cannot be bypassed; `apply`/`copy` are blocked.

**5. Result rendering.**
`MotifLocations(id: String, positions: Vector[Int])` with `format = s"$id\n${positions.mkString(" ")}"`. The runner emits one `MotifLocations` block per protein that has at least one match (proteins with no match are omitted, per the problem).

**6. Runner network boundary.**
`MPRTProb` reads the IDs (≤ 15), and for each fetches `https://rest.uniprot.org/uniprotkb/{accession}.fasta` (stripping any `_ENTRY_ORG` suffix to the bare accession) via `IO.blocking`. It concatenates the FASTA sequence lines, runs `MotifSearch`, and prints the matching blocks. Network/parse failures print a message rather than throwing.

Redirects are followed **manually**: an ID may be a *secondary* accession (from a merged/demerged entry), which the REST endpoint resolves to the primary entry via an HTTP redirect. `HttpURLConnection`'s built-in follower ignores `303 See Other` and refuses cross-protocol (http↔https) hops — both of which occur for secondary accessions — so `httpGet` follows up to 5 redirects by hand, resolving relative `Location` headers against the request URL. (Relying on the built-in follower silently drops secondary-ID entries and yields wrong results.)

**7. Naming and placement.**
`MotifElement`, `ProteinMotif`, `ProteinMotifError`, and `MotifLocations` live in `bio.domain.protein`; the algorithm `MotifSearch.findLocations` in `bio.algorithms.protein`. Result (`MotifLocations`) and algorithm (`MotifSearch`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Network dependence]** → the runner makes live UniProt calls, so its output depends on UniProt availability and current data; the pure core is fully deterministic and unit-tested, and the runner degrades gracefully on failure.
- **[Non-standard residues]** → the matcher uses a raw `String`, so `U`/`X`/`B`/`Z` in UniProt sequences are handled without `ProteinString` validation errors.
- **[ID suffixes]** → IDs like `P07204_TRBM_HUMAN` are reduced to the accession `P07204` before querying the REST endpoint.
- **[Overlapping matches]** → all start positions are reported (the scan does not skip past a match), matching the Rosalind requirement.
- **[Parser scope]** → only `[…]`/`{…}`/literal are supported; that is exactly the MPRT shorthand. Other PROSITE constructs are out of scope.
