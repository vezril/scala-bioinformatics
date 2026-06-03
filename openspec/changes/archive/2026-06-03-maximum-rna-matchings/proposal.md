## Why

Rosalind problem MMCH ("Maximum Matchings and RNA Secondary Structures") asks for the total number of *maximum* matchings of basepair edges in the bonding graph of an RNA string `s` (length ≤ 100). Unlike PMCH (which assumes a balanced string and counts *perfect* matchings), MMCH handles the general, unbalanced case: a maximum matching pairs as many bases as possible. Because the A-U and C-G subgraphs share no nodes, the count factors into a product of falling factorials, so the answer is a closed form (no search) — but the values overflow `Long`, so it is exact `BigInt`.

## What Changes

- Introduce a validated `MaximumMatchingProblem` domain type wrapping an `RnaString` (length ≤ 100) together with its four precomputed symbol counts.
- Introduce a `MaximumMatchingProblemError` ADT for the new invariant (sequence too long).
- Introduce a `MaximumMatchings` result type holding the exact count (`BigInt`), with a `format`.
- Introduce a `MaximumMatching` algorithm computing the maximum-matching count as `P(max(a,u), min(a,u)) · P(max(c,g), min(c,g))`, where `P(hi, lo)` is the falling factorial `hi · (hi-1) · … · (hi-lo+1)`.
- Add an `MMCHProb` runner reading the FASTA-formatted RNA string from `mmch_data.txt` and printing the count through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.RnaString`.

## Capabilities

### New Capabilities
- `maximum-rna-matchings`: Count the maximum matchings of basepair edges in the bonding graph of an (unbalanced) RNA string via a product of falling factorials over the A-U and C-G subgraphs (Rosalind MMCH).

### Modified Capabilities
<!-- None. MMCH adds a new capability and reuses RnaString without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.nucleic`): `MaximumMatchingProblem`, `MaximumMatchingProblemError`, `MaximumMatchings` (result).
- **New algorithm** (`bio.algorithms.nucleic.MaximumMatching`) — a closed-form `BigInt` product of falling factorials, mirroring `PerfectMatching` (PMCH) generalised to the unbalanced case.
- **New runner** (`bio.problems.MMCHProb`) reading `src/main/scala/resources/mmch_data.txt` (FASTA).
- **Reused, unchanged**: `bio.domain.nucleic.RnaString`.
- **Tests**: new specs under `bio.domain.nucleic` and `bio.algorithms.nucleic`. No existing tests change.
