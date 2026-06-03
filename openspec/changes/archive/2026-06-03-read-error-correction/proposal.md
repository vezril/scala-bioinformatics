## Why

Rosalind problem CORR ("Error Correction in Reads") gives up to 1000 equal-length DNA reads (≤ 50 bp, FASTA). Some carry a single-nucleotide sequencing error. A read is *correct* if it (or its reverse complement) appears in the dataset at least twice; an *incorrect* read appears exactly once and is at Hamming distance 1 from exactly one correct read (or its reverse complement). The task is to return every correction `old->new`. It is a small, exact analysis that reuses the project's `DnaReverseComplement` and `HammingDistance`.

## What Changes

- Introduce a validated `ReadCorrectionProblem` domain type wrapping a `Vector` of equal-length `DnaString`s (≤ 1000 reads, each ≤ 50 bp).
- Introduce a `ReadCorrectionProblemError` ADT for the new invariants (too many reads; a read too long; reads of unequal length).
- Introduce a `Correction` domain type (`oldRead -> newRead`) with a `format`, and a `ReadCorrections` result type holding the ordered collection of corrections with a `format`.
- Introduce a `ReadErrorCorrection` algorithm that classifies reads as correct (support ≥ 2 counting reverse complements) or incorrect, and maps each incorrect read to the Hamming-distance-1 correct form.
- Add a `CORRProb` runner reading the FASTA records from `corr_data.txt` and printing the corrections through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.DnaString`, `bio.algorithms.nucleic.DnaReverseComplement`, and `bio.parsing.FastaFileReader`.

## Capabilities

### New Capabilities
- `read-error-correction`: Given up to 1000 equal-length DNA reads, identify single-nucleotide sequencing errors and return each correction `old->new`, treating a read and its reverse complement as the same molecule (Rosalind CORR).

### Modified Capabilities
<!-- None. CORR adds a new capability and reuses existing infrastructure without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.analysis`): `ReadCorrectionProblem`, `ReadCorrectionProblemError`, `Correction`, `ReadCorrections` (result).
- **New algorithm** (`bio.algorithms.analysis.ReadErrorCorrection`) — support counting over reads and their reverse complements, then a Hamming-distance-1 match per incorrect read.
- **New runner** (`bio.problems.CORRProb`) reading `src/main/scala/resources/corr_data.txt` via `FastaFileReader`.
- **Reused, unchanged**: `bio.domain.nucleic.DnaString`, `bio.algorithms.nucleic.DnaReverseComplement`, `bio.parsing.FastaFileReader`.
- **Tests**: new specs under `bio.domain.analysis` and `bio.algorithms.analysis`. No existing tests change.
