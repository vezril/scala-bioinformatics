## Why

Rosalind problem ORF ("Open Reading Frames") asks, given a DNA string, for every distinct candidate protein that can be translated from an open reading frame across all six reading frames (three on the strand itself, three on its reverse complement). It is the first problem that combines the project's existing nucleic and protein machinery — reverse complement, transcription, and the genetic code — into a single multi-frame search, extending the framework toward real gene-finding workflows.

## What Changes

- Introduce a validated `OpenReadingFrameProblem` domain type wrapping a `DnaString` of length at most 1000 bp.
- Introduce an `OpenReadingFrames` result type holding the distinct candidate protein strings, with a Rosalind-style `format` (one protein per line).
- Introduce an `OpenReadingFrames` algorithm that enumerates the six reading frames, locates every ORF (start codon `AUG` through the first in-frame stop codon), translates each to a protein, and returns the distinct set.
- Add an `ORFProb` runner that reads a single FASTA DNA record from `orf_data.txt`, solves the problem, and prints the candidate proteins through `IO`.
- Reuse existing infrastructure unchanged: `DnaReverseComplement.reverseComplement`, `RnaTranscription.transcribe`, `GeneticCode.translate`/`CodonOutcome`, `Codon`, `ProteinString.fromAminoAcids`, and `FastaFileReader`.

## Capabilities

### New Capabilities
- `open-reading-frames`: Locate every distinct candidate protein translatable from an open reading frame across the six reading frames of a DNA string (Rosalind ORF).

### Modified Capabilities
<!-- None. ORF composes existing capabilities (transcription, reverse complement, genetic-code translation) without changing their requirements. -->

## Impact

- **New domain types** (`bio.domain.protein`): `OpenReadingFrameProblem`, `OpenReadingFrameProblemError`, `OpenReadingFrames` (result).
- **New algorithm** (`bio.algorithms.protein.OpenReadingFrames`).
- **New runner** (`bio.problems.ORFProb`) reading `src/main/scala/resources/orf_data.txt`.
- **Reused, unchanged**: `bio.algorithms.nucleic.{DnaReverseComplement, RnaTranscription}`, `bio.domain.protein.{GeneticCode, CodonOutcome, Codon, ProteinString, AminoAcid}`, `bio.domain.nucleic.{DnaString, RnaString, RnaNucleotide}`, `bio.parsing.{FastaFileReader, FastaRecord, FastaError}`.
- **Tests**: new specs under `bio.domain.protein` and `bio.algorithms.protein`. No existing tests change.
