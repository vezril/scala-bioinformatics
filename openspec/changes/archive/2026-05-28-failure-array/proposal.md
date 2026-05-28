## Why

Rosalind problem 31 ("Speeding Up Motif Finding", KMP) introduces the *failure array* — the cornerstone of the Knuth-Morris-Pratt string-matching algorithm. The failure array `P` of a string `s` has `P[k] = length of the longest proper prefix of s[1..k] that is also a suffix of s[1..k]`. This is the canonical "longest proper prefix-suffix" (LPS) table that turns naive O(n·m) substring matching into O(n+m). Adding it gives the framework a foundational text-processing primitive that future motif-search optimisations (and any KMP-based alignment work) can build on, while completing Rosalind problem 31 — the next problem after the now-fully-archived NWCK (spec 30).

## What Changes

- Add a new validated domain type `bio.domain.analysis.FailureArrayProblem` wrapping a non-empty `DnaString`. Smart constructor returning `Either` enforces non-emptiness (the Rosalind input is FASTA, but the underlying string itself must be non-empty for the failure array to be defined).
- Add a new algorithm object `bio.algorithms.analysis.FailureArray` exposing `compute(problem: FailureArrayProblem): Vector[Int]`. Returns the 1-indexed failure array of length `n` (where `n == problem.dna.value.length`). `P[0]` (Rosalind's `P[1]`) is `0` by convention; subsequent entries are computed by the classic O(n) KMP table-build recurrence.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `FailureArrayProblemSpec`: accepts a non-empty DNA string; rejects empty; rejects via `DnaString.from` for invalid alphabet (defence-in-depth — the wrapper takes a pre-validated `DnaString`, but the spec asserts the smart constructor accepts only the validated form).
  - `FailureArraySpec`: canonical Rosalind sample `CAGCATGGTATCACAGCAGAG → 0 0 0 1 2 0 0 0 0 0 0 1 2 1 2 3 4 5 3 0 0`; single-character input `A → 0`; all-same input `AAAAA → 0 1 2 3 4`; no-self-overlap input `ACGT → 0 0 0 0`; periodic input `ABABABAB`-equivalent on the DNA alphabet (`ACACACAC → 0 0 1 2 3 4 5 6`); two-character cases `AA` and `AT`.
- No changes to existing capabilities; no breaking changes.

## Capabilities

### New Capabilities
- `failure-array`: Computes the KMP failure array (longest proper prefix-suffix table) for a DNA string. Includes the validated `FailureArrayProblem` input bundle and the `FailureArray.compute` O(n) algorithm.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/analysis/FailureArrayProblem.scala`
  - `src/main/scala/bio/domain/analysis/FailureArrayProblemError.scala`
  - `src/main/scala/bio/algorithms/analysis/FailureArray.scala`
  - `src/test/scala/bio/domain/analysis/FailureArrayProblemSpec.scala`
  - `src/test/scala/bio/algorithms/analysis/FailureArraySpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Builds on the established `bio.{algorithms,domain}.analysis` subdomain conventions used by `motif-finding` and `random-string-matching`.
