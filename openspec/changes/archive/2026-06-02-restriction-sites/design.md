## Context

REVP is a pure DNA-scanning problem. The framework already provides every needed
primitive in `bio.domain.nucleic` / `bio.algorithms.nucleic`: `DnaString` (validated
A/C/G/T, `unsafeFrom` for trusted substrings), `DnaNucleotide.complement`, and
`DnaReverseComplement.reverseComplement`. The FASTA reader (`FastaFileReader` +
`FastaRecord`) already yields a `DnaString` per record. Input is a single FASTA DNA
string (≤ 1 kbp); the canonical sample `TCAATGCATGCGGGTCTATATGCAT` yields eight
position/length pairs.

## Goals / Non-Goals

**Goals:**
- Model the bounded REVP input as a validated problem type.
- Locate every reverse palindrome of length 4–12 and report `(position, length)`
  pairs matching the Rosalind sample.
- Reuse `DnaReverseComplement` rather than re-implementing complementation.
- Keep the algorithm pure FP (no `var`/`while`/`Array`) — REVP is a simple scan,
  not an alignment-family DP.

**Non-Goals:**
- No methylation modeling, no enzyme database, no odd-length sites (a reverse
  palindrome is always even-length).
- No performance tuning beyond the ≤ 1000 bp × 5 lengths scan.

## Decisions

- **`RestrictionSiteProblem` as a `sealed abstract case class`** wrapping a
  `DnaString`, built only via `from(dna): Either[RestrictionSiteProblemError,
  RestrictionSiteProblem]`. Validation rejects a sequence longer than 1000 bp
  (`SequenceTooLong(length, max)`); the empty sequence is accepted (it simply has
  no sites). The abstract+sealed pattern blocks synthesized `apply`/`copy`, proven
  with `assertDoesNotCompile`. This mirrors `ProteinMassProblem`.
- **`RestrictionSiteProblemError`** ADT with the single case
  `SequenceTooLong(length: Int, max: Int)`.
- **`RestrictionSite`** is a plain `final case class(position: Int, length: Int)`
  — a 1-based start position and an even length. No invariant beyond construction.
- **`RestrictionSites` result** is a plain `final case class(sites:
  Vector[RestrictionSite])` with `format` rendering each site as `"$position
  $length"`, one per line, joined by `\n`. The empty result formats to `""`.
- **Even lengths only.** A reverse palindrome must have even length (its two
  central bases must be complementary), so the scan considers lengths
  `4, 6, 8, 10, 12` only — never odd lengths.
- **Algorithm** `RestrictionSites.locate` aliases the domain result as `Result`
  and uses a `for` comprehension over `position` (ascending) then `length`
  (ascending), guarded by `position + length <= n`, emitting a site when
  `reverseComplement(sub).value == sub.value`. Iteration order yields results
  sorted by `(position, length)` — deterministic and matching the sample. The
  substring is lifted with `DnaString.unsafeFrom` because it is a slice of an
  already-validated `DnaString` (documented invariant; no re-validation).
- **`REVPProb` runner** reads `revp_data.txt` via `FastaFileReader`, takes the
  first record's `DnaString`, validates it into a `RestrictionSiteProblem`, locates
  the sites, and prints `format`. Missing record / FASTA / validation errors are
  printed via `IO.println`, never thrown (consistent with SMGB/SPEC/CONV runners).

## Risks / Trade-offs

- [Output ordering ambiguity] → Rosalind accepts any order; we pin `(position,
  length)` ascending via iteration order so tests are deterministic.
- [Re-validating substrings] → Avoided with `unsafeFrom`; the slice of a valid
  `DnaString` is itself valid by construction.
- [Performance: 1000 × 5 substring checks] → Trivial; no optimization needed.
