## Why

Rosalind problem ITWV ("Finding Disjoint Motifs in a Gene") asks, for a text DNA string `s` and a collection of `n ≤ 10` pattern strings, which *pairs* of patterns can be **interwoven** into `s` — i.e. whether some contiguous substring of `s` is a shuffle (interleaving) of the two patterns as disjoint subsequences that together cover the substring exactly. The answer is the `n × n` 0/1 matrix `M` with `M[j][k] = 1` iff patterns `j` and `k` can be interwoven into `s`. It is the pairwise, "disjoint coverage" companion to SCSP ("Interleaving Two Motifs"), and is solved by the classic interleaving-string DP run over every start window of `s`.

## What Changes

- Introduce a validated `InterwovenMotifProblem` domain type wrapping the text `DnaString` (≤ 10 kbp) and a `Vector` of pattern `DnaString`s (`n ≤ 10`, each ≤ 10 bp).
- Introduce an `InterwovenMotifProblemError` ADT for the new invariants (too many patterns, text too long, pattern too long).
- Introduce an `InterwovenMotifMatrix` result type holding the `n × n` 0/1 matrix, with a `format` rendering each row space-separated and rows newline-joined.
- Introduce an `InterwovenMotifs` algorithm computing the matrix: for each pattern pair, an interleaving-string DP scanned over every start position of `s` decides interweavability.
- Add an `ITWVProb` runner reading text + patterns from `itwv_data.txt` and printing the matrix through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.DnaString`.

## Capabilities

### New Capabilities
- `disjoint-motifs-interweaving`: For a text DNA string and up to 10 short patterns, decide for every pair whether the two patterns can be interwoven (shuffled, as disjoint subsequences covering a contiguous substring) into the text, returning the pairwise 0/1 matrix (Rosalind ITWV).

### Modified Capabilities
<!-- None. ITWV adds a new capability and reuses DnaString without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.analysis`): `InterwovenMotifProblem`, `InterwovenMotifProblemError`, `InterwovenMotifMatrix` (result).
- **New algorithm** (`bio.algorithms.analysis.InterwovenMotifs`) — interleaving-string DP over all start windows, one decision per pattern pair.
- **New runner** (`bio.problems.ITWVProb`) reading `src/main/scala/resources/itwv_data.txt`.
- **Reused, unchanged**: `bio.domain.nucleic.DnaString`.
- **Tests**: new specs under `bio.domain.analysis` and `bio.algorithms.analysis`. No existing tests change.
