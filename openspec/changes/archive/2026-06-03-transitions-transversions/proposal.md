## Why

Rosalind problem TRAN ("Transitions and Transversions") gives two equal-length DNA strings (≤ 1 kbp, FASTA) and asks for their transition/transversion ratio `R(s1, s2)` — the number of *transition* substitutions (purine↔purine `A↔G`, or pyrimidine↔pyrimidine `C↔T`) divided by the number of *transversion* substitutions (every other mismatch), counting mismatches exactly as for Hamming distance. It is a small, exact analysis reusing the project's `DnaString` and FASTA reader.

## What Changes

- Introduce a validated `TransitionTransversionProblem` domain type wrapping two equal-length `DnaString`s (each ≤ 1000 bp).
- Introduce a `TransitionTransversionProblemError` ADT for the invariants (a sequence too long; the two lengths differing).
- Introduce a `TransitionTransversionRatio` result type holding the transition and transversion counts, exposing the `ratio` and a `format`.
- Introduce a `TransitionTransversionAnalysis` algorithm that classifies each mismatched position as a transition or transversion and reports the counts.
- Add a `TRANProb` runner reading the two FASTA records from `tran_data.txt` and printing the ratio through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.DnaString` and `bio.parsing.FastaFileReader`.

## Capabilities

### New Capabilities
- `transitions-transversions`: Compute the transition/transversion ratio between two equal-length DNA strings (Rosalind TRAN).

### Modified Capabilities
<!-- None. TRAN adds a new capability and reuses DnaString/FastaFileReader without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.nucleic`): `TransitionTransversionProblem`, `TransitionTransversionProblemError`, `TransitionTransversionRatio` (result).
- **New algorithm** (`bio.algorithms.nucleic.TransitionTransversionAnalysis`) — an `O(L)` mismatch classification over the two sequences.
- **New runner** (`bio.problems.TRANProb`) reading `src/main/scala/resources/tran_data.txt` via `FastaFileReader`.
- **Reused, unchanged**: `bio.domain.nucleic.DnaString`, `bio.parsing.FastaFileReader`.
- **Tests**: new specs under `bio.domain.nucleic` and `bio.algorithms.nucleic`. No existing tests change.
