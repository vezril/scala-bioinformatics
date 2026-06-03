## Why

Rosalind problem LGIS ("Longest Increasing Subsequence") gives a permutation π of `{1, …, n}` (`n ≤ 10000`) and asks for a longest *increasing* subsequence and a longest *decreasing* subsequence of π. It is the classic patience-sorting LIS problem with reconstruction; the decreasing subsequence is just the LIS computed under the reversed (greater-than) order. The framework has no permutation domain type yet, so this also introduces a reusable validated `Permutation`.

## What Changes

- Introduce a validated `Permutation` domain type wrapping a `Vector[Int]` that is a permutation of `{1, …, n}` (where `n` is its length, `n ≤ 10000`).
- Introduce a `PermutationError` ADT for the new invariants (too long; not a permutation of `1..n`).
- Introduce a `MonotonicSubsequences` result type holding the increasing and decreasing subsequences, with a `format` rendering each on its own line (space-separated).
- Introduce a `LongestSubsequences` algorithm computing a longest increasing and a longest decreasing subsequence via O(n log n) patience sorting with predecessor reconstruction.
- Add an `LGISProb` runner reading `n` and the permutation from `lgis_data.txt` and printing both subsequences through `IO`.

## Capabilities

### New Capabilities
- `longest-increasing-subsequence`: Given a permutation of `{1, …, n}`, return a longest increasing subsequence and a longest decreasing subsequence (Rosalind LGIS).

### Modified Capabilities
<!-- None. LGIS adds a new capability and a new reusable Permutation type without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.combinatorics`): `Permutation`, `PermutationError`, `MonotonicSubsequences` (result).
- **New algorithm** (`bio.algorithms.combinatorics.LongestSubsequences`) — O(n log n) patience-sorting LIS, run twice (ascending and descending order) with reconstruction.
- **New runner** (`bio.problems.LGISProb`) reading `src/main/scala/resources/lgis_data.txt`.
- **Tests**: new specs under `bio.domain.combinatorics` and `bio.algorithms.combinatorics`. No existing tests change.
