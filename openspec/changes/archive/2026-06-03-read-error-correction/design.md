## Context

CORR ("Error Correction in Reads") takes up to 1000 equal-length DNA reads (≤ 50 bp, FASTA) and returns the single-symbol corrections for the reads that were mis-sequenced. The dataset guarantees a clean dichotomy: a read is **correct** if it (or its reverse complement) occurs at least twice; otherwise it is **incorrect** — it occurs exactly once and is at Hamming distance 1 from exactly one correct read (or that read's reverse complement). A read and its reverse complement represent the same physical molecule, so support is counted across both strands.

The project already provides `DnaReverseComplement.reverseComplement(dna): DnaString` and `HammingDistance.between(a, b): Either[HammingError, Int]`, and `FastaFileReader` for multi-record DNA FASTA. CORR is a sequence-analysis task, so the types live in `bio.domain.analysis` / `bio.algorithms.analysis`.

## Goals / Non-Goals

**Goals:**
- Validated `ReadCorrectionProblem(reads)` (≤ 1000 equal-length reads, each ≤ 50 bp) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `ReadErrorCorrection.correct(problem): ReadCorrections`.
- A `Correction` type (`old->new`) and a `ReadCorrections` result, both with `format`.

**Non-Goals:**
- Multi-error correction — exactly one single-nucleotide substitution per incorrect read (the Rosalind guarantee).
- Reads of unequal length — rejected by validation (Hamming distance requires equal length).

## Decisions

**1. Strand-aware support counting.**
Build `counts: Map[String, Int]` over the read strings. For a read `r` with reverse complement `rc`, its *support* is `counts(r) + counts(rc)` when `rc ≠ r`, else `counts(r)`. A read is **correct** iff `support(r) ≥ 2`, **incorrect** iff `support(r) == 1`.

**2. Correct forms include both strands.**
The set of `correctForms: Set[String]` collects, for every distinct correct read `r`, both `r` and `rc(r)`. An incorrect read may legitimately be corrected to the reverse complement of a correct read (the sample corrects `GAGGA -> GATGA`, where `GATGA = rc(TCATC)`), so both strands must be candidates.

**3. One correction per incorrect read, in input order.**
For each read in input order with `support == 1`, find a `correctForm` at Hamming distance exactly 1 and emit `Correction(read, form)`. Incorrect reads occur exactly once, so iterating input order (then `distinct`) yields one correction each, matching the sample ordering. Rosalind accepts any order regardless.

**4. Reuse `DnaReverseComplement`; inline Hamming on equal-length strings.**
Reverse complements come from `DnaReverseComplement.reverseComplement`. Hamming distance is computed inline on the equal-length read strings (`a.lazyZip(b).count(_ != _)`) — all forms share one length (validated, and reverse-complement preserves length), so this is total and avoids the `Either` wrapper.

**5. Validation and first-failure-wins ordering.**
`ReadCorrectionProblem.from(reads)` enforces, in order: `reads.size ≤ 1000` (`TooManyReads(count, 1000)`), each read length `≤ 50` (`ReadTooLong(length, 50)`, first offender), then all reads of equal length (`UnequalLengths(lengths)`). Character validity (`A`,`C`,`G`,`T`) is owned upstream by `DnaString`. An empty read list is accepted (no corrections).

**6. Result rendering.**
`Correction(oldRead: String, newRead: String)` with `format = s"$oldRead->$newRead"`. `ReadCorrections(corrections: Vector[Correction])` with `format` = each correction's `format` joined by `\n` (empty result → empty string).

**7. Naming and placement.**
`ReadCorrectionProblem`, `ReadCorrectionProblemError`, `Correction`, and `ReadCorrections` live in `bio.domain.analysis`; the algorithm `ReadErrorCorrection.correct` in `bio.algorithms.analysis`. Result (`ReadCorrections`) and algorithm (`ReadErrorCorrection`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Reverse-complement palindromes]** → when `rc(r) == r`, support is `counts(r)` alone (not doubled); handled explicitly so a self-complementary read is not spuriously promoted to correct.
- **[Tie in correct forms]** → the Rosalind dataset guarantees a unique Hamming-1 correct read per incorrect read, so `find` is deterministic in practice; tests compare correction *sets* (order-independent) to stay robust.
- **[Empty / all-correct inputs]** → empty list → no corrections; a dataset with only duplicated reads → no corrections; a read plus its reverse complement (e.g. `AAA`/`TTT`) → both correct, no corrections; covered by scenarios.
- **[Unequal lengths]** → rejected with `UnequalLengths` (Hamming distance is only defined for equal-length reads).
