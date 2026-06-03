## Why

Rosalind problem MGAP ("Maximizing the Gap Symbols of an Optimal Alignment") asks, for two DNA strings and any scoring with `m>0, d<0, g<0`, for the maximum number of gap symbols across all maximum-score alignments. The answer is parameter-independent: a maximum-score alignment maximises matches and (to maximise gaps) avoids mismatches, so the gap count is `|s| + |t| − 2·LCS(s,t)`. It extends the alignment family with the longest-common-subsequence length and the gap-maximising identity.

## What Changes

- Introduce a validated `MaxGapProblem` domain type wrapping two `DnaString`s (each ≤ 5000 bp).
- Introduce a `MaxGapProblemError` ADT for the new invariant (sequence too long).
- Introduce a `MaxGapSymbols` result type holding the maximum gap count, with a `format` of the integer.
- Introduce a `MaximizeGapSymbols` algorithm computing `|s| + |t| − 2·LCS(s,t)`, where the longest-common-subsequence length is found by an O(n·m) dynamic program (the alignment-family imperative-DP exception applies; rolling two rows for O(min(n,m)) space).
- Add an `MGAPProb` runner reading two FASTA DNA records from `mgap_data.txt` and printing the maximum gap count through `IO`.
- Reuse existing infrastructure: `bio.parsing.FastaFileReader`, `bio.domain.nucleic.DnaString`.

## Capabilities

### New Capabilities
- `maximize-gap-symbols`: Compute the maximum number of gap symbols in any maximum-score alignment of two DNA strings (`m>0, d<0, g<0`), via `|s| + |t| − 2·LCS(s,t)` (Rosalind MGAP).

### Modified Capabilities
<!-- None. MGAP adds a new capability and reuses FastaFileReader / DnaString without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.analysis`): `MaxGapProblem`, `MaxGapProblemError`, `MaxGapSymbols` (result).
- **New algorithm** (`bio.algorithms.analysis.MaximizeGapSymbols`) with an internal rolling-row LCS-length DP.
- **New runner** (`bio.problems.MGAPProb`) reading `src/main/scala/resources/mgap_data.txt`.
- **Reused, unchanged**: `bio.parsing.FastaFileReader`, `bio.domain.nucleic.DnaString`.
- **Tests**: new specs under `bio.domain.analysis` and `bio.algorithms.analysis`. No existing tests change.
