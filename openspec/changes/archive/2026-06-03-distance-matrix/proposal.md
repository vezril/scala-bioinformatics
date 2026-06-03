## Why

Rosalind problem PDST ("Creating a Distance Matrix") gives `n ≤ 10` equal-length DNA strings (FASTA, each ≤ 1 kbp) and asks for the `n × n` p-distance matrix `D`, where `D[i][j]` is the *p-distance* `d_p(s_i, s_j)` — the proportion of positions at which `s_i` and `s_j` differ (i.e. their Hamming distance divided by length). It is a small, exact, closed-form matrix computation that reuses the existing `HammingDistance` algorithm per pair.

## What Changes

- Introduce a validated `DistanceMatrixProblem` domain type wrapping a `Vector` of equal-length `DnaString`s (`n ≤ 10`, each ≤ 1000 bp).
- Introduce a `DistanceMatrixProblemError` ADT for the new invariants (too many strings; a string too long; strings of unequal length).
- Introduce a `DistanceMatrix` result type holding the `n × n` matrix of `Double`s, with a `format` rendering each value to 5 decimal places, space-separated, rows newline-joined.
- Introduce a `PDistanceMatrix` algorithm computing `D[i][j] = hamming(s_i, s_j) / length`, reusing `HammingDistance`.
- Add a `PDSTProb` runner reading the FASTA records from `pdst_data.txt` and printing the matrix through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.DnaString`, `bio.algorithms.analysis.HammingDistance`, and `bio.parsing.FastaFileReader`.

## Capabilities

### New Capabilities
- `distance-matrix`: Given up to 10 equal-length DNA strings, return the p-distance matrix (pairwise proportion of differing symbols) (Rosalind PDST).

### Modified Capabilities
<!-- None. PDST adds a new capability and reuses DnaString/HammingDistance/FastaFileReader without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.analysis`): `DistanceMatrixProblem`, `DistanceMatrixProblemError`, `DistanceMatrix` (result).
- **New algorithm** (`bio.algorithms.analysis.PDistanceMatrix`) — an `O(n² · L)` pairwise p-distance fill reusing `HammingDistance`.
- **New runner** (`bio.problems.PDSTProb`) reading `src/main/scala/resources/pdst_data.txt` via `FastaFileReader`.
- **Reused, unchanged**: `bio.domain.nucleic.DnaString`, `bio.algorithms.analysis.HammingDistance`, `bio.parsing.FastaFileReader`.
- **Tests**: new specs under `bio.domain.analysis` and `bio.algorithms.analysis`. No existing tests change.
