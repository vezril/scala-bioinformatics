## Why

Spec 10 of the project brief — "Counting Point Mutations" — introduces the framework's first *pairwise* DNA comparison: the Hamming distance between two equal-length sequences. Unlike spec 9's motif finding (where the two inputs play asymmetric roles — text vs pattern), this is the first algorithm where both inputs are co-equal participants in the comparison. It also introduces the framework's first *required precondition* between two `DnaString` inputs (equal length), modeled as an explicit error rather than a convention.

Hamming distance is the foundation for spec problems that follow: approximate motif matching, distance-based clustering, error-correcting codes for sequencing, and SNP-counting analyses.

## What Changes

- **NEW** `HammingDistance` algorithm object in `bio.algorithms.analysis`
- **NEW** `HammingDistance.between(a: DnaString, b: DnaString): Either[HammingError, Int]` — returns the count of positions at which the two sequences differ. Requires `a.value.length == b.value.length`; otherwise returns `Left(HammingError.LengthMismatch(a.value.length, b.value.length))`.
- **NEW** `HammingError` sealed ADT in `bio.domain.analysis` with case `LengthMismatch(lengthA: Int, lengthB: Int)`
- **NO** wrapper type for the result — bare `Int` distance, consistent with `MotifLocations` returning bare `Vector[Int]`

## Capabilities

### New Capabilities

- `hamming-distance`: The `HammingDistance.between(a: DnaString, b: DnaString): Either[HammingError, Int]` algorithm (in `bio.algorithms.analysis`) and the `HammingError` sealed ADT (in `bio.domain.analysis`). Returns the number of differing positions when the inputs are equal length; returns `Left(LengthMismatch(...))` otherwise. Two empty DnaStrings have distance 0.

### Modified Capabilities

None.

## Impact

- New file: `src/main/scala/bio/algorithms/analysis/HammingDistance.scala`
- New file: `src/main/scala/bio/domain/analysis/HammingError.scala`
- New test files: `src/test/scala/bio/algorithms/analysis/HammingDistanceSpec.scala` and `src/test/scala/bio/domain/analysis/HammingErrorSpec.scala`
- No changes to existing files
- No new external dependencies
- All existing 200 tests continue passing
