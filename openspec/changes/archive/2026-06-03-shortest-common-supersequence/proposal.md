## Why

Rosalind problem SCSP ("Interleaving Two Motifs") asks for a shortest common supersequence (SCS) of two DNA strings `s` and `t` — the shortest string containing both as subsequences. The SCS is built from the longest common subsequence: matched (LCS) characters appear once, and the rest of each string is interleaved around them. It extends the alignment family with the SCS dynamic program.

## What Changes

- Introduce a validated `SupersequenceProblem` domain type wrapping two `DnaString`s (each ≤ 1000 bp, keeping the O(m·n) table tractable).
- Introduce a `SupersequenceProblemError` ADT for the new invariant (sequence too long).
- Introduce a `Supersequence` result type holding the shortest common supersequence, with a `format` returning it verbatim.
- Introduce a `ShortestCommonSupersequence` algorithm: an O(m·n) SCS-length dynamic program followed by a backtrack that merges `s` and `t`, emitting matched characters once and the rest in order.
- Add an `SCSPProb` runner reading `s` and `t` from `scsp_data.txt` and printing a shortest common supersequence through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.DnaString`.

## Capabilities

### New Capabilities
- `shortest-common-supersequence`: Compute a shortest common supersequence of two DNA strings (Rosalind SCSP).

### Modified Capabilities
<!-- None. SCSP adds a new capability and reuses DnaString without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.analysis`): `SupersequenceProblem`, `SupersequenceProblemError`, `Supersequence` (result).
- **New algorithm** (`bio.algorithms.analysis.ShortestCommonSupersequence`) with an internal SCS-length DP and backtrack.
- **New runner** (`bio.problems.SCSPProb`) reading `src/main/scala/resources/scsp_data.txt`.
- **Reused, unchanged**: `bio.domain.nucleic.DnaString`.
- **Tests**: new specs under `bio.domain.analysis` and `bio.algorithms.analysis`. No existing tests change.
