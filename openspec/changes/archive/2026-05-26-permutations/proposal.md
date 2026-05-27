## Why

Spec 15 of the project brief — "Enumerating Gene Orders" — is the framework's first *combinatorial enumeration* problem. The Rosalind framing (orderings of `n` genes) is a thin wrapper over a pure-math primitive: enumerate every permutation of `{1, 2, ..., n}`. This change introduces a new `combinatorics` subdomain (under both `bio.domain` and `bio.algorithms`) to house this and future enumeration-style primitives (combinations, subsets, partitions). Adding a small subdomain now — rather than co-locating with `genetics` — keeps the subdomain organization honest: pure-math primitives don't belong inside a biology-named folder.

## What Changes

- **NEW** `PermutationLength` domain type in a new `bio.domain.combinatorics` subdomain — `sealed abstract case class PermutationLength(value: Int)` with smart constructor `from(value: Int): Either[PermutationLengthError, PermutationLength]` enforcing `1 <= value <= 7`
- **NEW** `PermutationLengthError` sealed ADT in `bio.domain.combinatorics` — cases `NonPositive(value: Int)` and `ExceedsMaximum(value: Int, max: Int)` (where `max = 7`)
- **NEW** `Permutations` algorithm object in a new `bio.algorithms.combinatorics` subdomain — `enumerate(length: PermutationLength): Vector[Vector[Int]]` returning all `length.value!` permutations of `{1, ..., length.value}`. The result is total and ordered by Scala stdlib's `Iterable.permutations` (lexicographic).
- **NO** wrapper type for individual permutations. Each permutation is a `Vector[Int]` — by construction it contains every value in `1..n` exactly once; no validation beyond the algorithm's own guarantee.
- **NO** modifications to existing capabilities. Permutations is a pure-math primitive that doesn't interact with the existing biology subdomains.

## Capabilities

### New Capabilities

- `permutations`: The `PermutationLength` validated input type, the `PermutationLengthError` ADT, and the `Permutations.enumerate(length): Vector[Vector[Int]]` algorithm producing every permutation of `{1, ..., length.value}`. The result count is `length.value!`. Establishes the framework's `combinatorics` subdomain.

### Modified Capabilities

None.

## Impact

- New subdomain `bio.domain.combinatorics` (directory created on first file)
- New subdomain `bio.algorithms.combinatorics`
- New files: `PermutationLength.scala`, `PermutationLengthError.scala`, `Permutations.scala`
- New test files mirroring each
- No changes to existing files
- No new external dependencies
- All existing 271 tests continue passing
