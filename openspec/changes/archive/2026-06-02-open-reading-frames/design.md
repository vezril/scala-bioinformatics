## Context

The project already implements every primitive ORF requires:
- `bio.domain.nucleic.DnaString` / `RnaString` (validated value classes; `.value`, `from`, `unsafeFrom`).
- `bio.algorithms.nucleic.DnaReverseComplement.reverseComplement(dna: DnaString): DnaString` (total).
- `bio.algorithms.nucleic.RnaTranscription.transcribe(dna: DnaString): RnaString` (total; T→U).
- `bio.domain.protein.Codon(first, second, third: RnaNucleotide)` and `GeneticCode.translate(c: Codon): CodonOutcome` where `CodonOutcome` is `AminoAcidProduct(aa: AminoAcid)` or `Stop` (case object). `AminoAcid.M` is the product of the start codon `AUG`.
- `bio.domain.protein.ProteinString.fromAminoAcids(aas: Seq[AminoAcid]): ProteinString` (total) and `.value`.
- `bio.parsing.{FastaFileReader, FastaRecord(id, dna: DnaString), FastaError}`.

ORF is the first capability to compose nucleic + protein machinery for a multi-frame search. No existing requirement changes.

## Goals / Non-Goals

**Goals:**
- Validated `OpenReadingFrameProblem` (DnaString ≤ 1000 bp) via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage.
- Pure, total `OpenReadingFrames.find(problem): OpenReadingFrames` enumerating all six frames and returning the **distinct** candidate proteins.
- Reuse existing transcription, reverse-complement, and genetic-code machinery — no reimplementation of codon translation.
- Result type with a `format: String` (one protein per line; empty result → empty string).
- `ORFProb` runner reading a single FASTA record from `orf_data.txt`, printing via `IO`.

**Non-Goals:**
- Reporting ORF positions/frames/strand (Rosalind asks only for distinct protein strings).
- Any output ordering guarantee from Rosalind ("any order"); we nonetheless return a deterministic order for testability.
- Handling more than one FASTA record (ORF input is a single DNA string).

## Decisions

**1. Six frames = forward strand (offsets 0,1,2) + reverse complement (offsets 0,1,2).**
The reverse complement is computed once via `DnaReverseComplement.reverseComplement`; each strand is transcribed to RNA via `RnaTranscription.transcribe`, then scanned at the three frame offsets. Rationale: reuses total, tested primitives; matches the problem's definition of six reading frames exactly.

**2. Work at the `CodonOutcome` level per frame, not via `RnaTranslation.translate`.**
`RnaTranslation.translate` halts at the first stop and succeeds even when no stop exists, so it cannot, on its own, distinguish "ORF terminated by a stop" from "ran off the end without a stop." Instead, for each frame we build the vector of complete codons (stepping by 3 from the offset, ignoring a trailing 1–2 nt remainder), map each through `GeneticCode.translate` to a `CodonOutcome`, then locate ORFs over that outcome vector. Alternative considered: regex over the protein string — rejected as it bypasses the ADT/codon machinery the framework mandates.

**3. ORF extraction is pure FP (no `var`/`while`).**
For each index `i` where the outcome is `AminoAcidProduct(M)` (a start codon `AUG`), take the maximal run of `AminoAcidProduct` from `i` and check that it is immediately followed by a `Stop`. Implemented with `drop(i)` + `span`: the `before` run is the amino acids, and a non-empty `after` (head `Stop`) confirms a valid ORF. If no stop follows (run reaches the end), the start codon yields **no** candidate. Each valid run is assembled via `ProteinString.fromAminoAcids`. This is a non-alignment problem, so pure FP applies (per framework convention).

**4. Distinctness and determinism.**
Candidate proteins are deduplicated with `.distinct` on `ProteinString` (a case class — value equality). Rosalind permits any order; to keep the runner output and tests deterministic we sort the distinct results (by `value`). Nested/overlapping ORFs (an inner `AUG` inside a longer ORF) naturally each produce their own candidate.

**5. Placement in the `protein` packages.**
`OpenReadingFrameProblem`, `OpenReadingFrameProblemError`, and the `OpenReadingFrames` result live in `bio.domain.protein`; the algorithm in `bio.algorithms.protein`. The problem wraps a `DnaString` (imported from `bio.domain.nucleic`) but the capability's output is proteins, so the protein package is the natural home — consistent with `RnaTranslation`. The algorithm aliases the domain result via `import bio.domain.protein.{OpenReadingFrames => Result}` to resolve the object/result name clash (same pattern as CONV/REVP).

**6. MaxLength = 1000.** Matches the Rosalind ORF constraint (≤ 1 kbp), mirroring `RestrictionSiteProblem`.

## Risks / Trade-offs

- **[A start codon with no downstream in-frame stop is silently dropped]** → Correct per the ORF definition (an ORF must end at a stop). The `span`+non-empty-`after` check enforces this; covered by an explicit edge-case scenario.
- **[Reverse-strand ORFs missed if reverse complement applied incorrectly]** → Reuse the tested `DnaReverseComplement`; the canonical sample's expected output includes reverse-strand proteins (`MGMTPRLGLESLLE`, `MTPRLGLESLLE`), which the sample scenario verifies end-to-end.
- **[Duplicate proteins from different frames inflate output]** → `.distinct` deduplicates; an explicit scenario covers a protein discoverable from two positions collapsing to one.
- **[Trailing partial codon at frame end]** → Stepping by 3 and only forming complete codons ignores the 1–2 nt remainder, matching biological frame semantics.
