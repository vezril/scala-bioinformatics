## Why

Rosalind problem SIGN ("Enumerating Oriented Gene Orderings") gives a positive integer `n ≤ 6` and asks for the total number of *signed* permutations of length `n` (each of the `n!` orderings of `{1, …, n}` with each element independently `+` or `−`, so `n! · 2ⁿ` in total) followed by a list of all of them. It is the oriented (signed) extension of PERM ("Enumerating Gene Orders"), so it reuses the project's permutation generator and adds the `2ⁿ` sign assignments.

## What Changes

- Introduce a validated `SignedPermutationProblem` domain type wrapping the length `n` (1 … 6).
- Introduce a `SignedPermutationProblemError` ADT for the invariants (non-positive length; length over the cap).
- Introduce a `SignedPermutations` result type holding the list of signed permutations, exposing the `count` and a `format` (count on the first line, then one permutation per line).
- Introduce a `SignedPermutationEnumeration` algorithm that, for each base permutation of `{1, …, n}`, emits every `2ⁿ` sign assignment.
- Add a `SIGNProb` runner reading `n` from `sign_data.txt` and printing the count and permutations through `IO`.
- Reuse existing infrastructure: `bio.algorithms.combinatorics.Permutations` and `bio.domain.combinatorics.PermutationLength`.

## Capabilities

### New Capabilities
- `signed-permutations`: Enumerate all signed permutations of length `n` (every ordering of `{1, …, n}` with each element signed `±`), reporting the total count and the list (Rosalind SIGN).

### Modified Capabilities
<!-- None. SIGN adds a new capability and reuses the permutation generator without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.combinatorics`): `SignedPermutationProblem`, `SignedPermutationProblemError`, `SignedPermutations` (result).
- **New algorithm** (`bio.algorithms.combinatorics.SignedPermutationEnumeration`) — base permutations × `2ⁿ` sign assignments, reusing `Permutations.enumerate`.
- **New runner** (`bio.problems.SIGNProb`) reading `src/main/scala/resources/sign_data.txt`.
- **Reused, unchanged**: `bio.algorithms.combinatorics.Permutations`, `bio.domain.combinatorics.PermutationLength`.
- **Tests**: new specs under `bio.domain.combinatorics` and `bio.algorithms.combinatorics`. No existing tests change.
