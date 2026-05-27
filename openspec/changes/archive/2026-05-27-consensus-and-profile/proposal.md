## Why

Rosalind problem 18 ("Consensus and Profile") asks us to compute, for a collection of equal-length DNA strings in FASTA format, a 4×n profile matrix (per-column nucleotide counts) and a consensus string (the most-common nucleotide at each column). This is the framework's first algorithm that emits a *structured tabular* result rather than a single number, list, or graph, and the first that requires a hard equal-length cross-constraint across multiple input records. It seeds a new `bio.{domain,algorithms}.matrix` subdomain — the natural home for the upcoming class of matrix-shaped outputs (profile matrices today, distance/scoring matrices later).

## What Changes

- Introduce a new subdomain `bio.domain.matrix` (and matching `bio.algorithms.matrix`) for matrix-shaped domain types and the algorithms that produce them.
- Add a validated `ConsensusProfileProblem` parameter bundle in `bio.domain.matrix` (smart constructor enforces non-empty input and equal-length records).
- Add a `ConsensusProfileProblemError` ADT (`EmptyInput`, `LengthMismatch(lengths: Vector[Int])`).
- Add a `ProfileMatrix` domain type in `bio.domain.matrix` wrapping `Map[DnaNucleotide, Vector[Int]]`. Public accessors `a`, `c`, `g`, `t` return the per-column count vectors (total over the 4-letter alphabet). Smart constructor enforces "all four keys present, all four vectors of equal length".
- Add a `ConsensusProfileResult(consensus: DnaString, profile: ProfileMatrix)` result type in `bio.domain.matrix`.
- Add the `ConsensusProfile.compute(problem): ConsensusProfileResult` algorithm in `bio.algorithms.matrix`. Total over validated input. Tie-break rule for consensus: alphabetical (A < C < G < T) on max count per column.
- No modifications to existing capabilities. FASTA parsing and `DnaNucleotide` are reused unchanged.

## Capabilities

### New Capabilities
- `consensus-and-profile`: The `ConsensusProfileProblem` validated bundle, the `ConsensusProfileProblemError` ADT, the `ProfileMatrix` domain type with its construction error, the `ConsensusProfileResult` pair, and the `ConsensusProfile.compute` algorithm computing the profile matrix and (alphabetically-tie-broken) consensus string for a collection of equal-length DNA records.

### Modified Capabilities
<!-- none — purely additive -->

## Impact

- New packages populated: `bio.domain.matrix` (new — first occupants: `ConsensusProfileProblem`, `ConsensusProfileProblemError`, `ProfileMatrix`, `ProfileMatrixError`, `ConsensusProfileResult`), `bio.algorithms.matrix` (new — first occupant: `ConsensusProfile`).
- No new SBT dependencies.
- New test suites: `ConsensusProfileProblemSpec`, `ProfileMatrixSpec`, `ConsensusProfileResultSpec`, `ConsensusProfileSpec`.
- No breaking changes — purely additive. Existing `bio.domain.analysis` and `bio.parsing` types are reused as-is.
