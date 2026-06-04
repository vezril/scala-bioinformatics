## Context

SIGN ("Enumerating Oriented Gene Orderings") takes `n ≤ 6` and returns the count of signed permutations of length `n` (an ordering of `{1, …, n}` with each element independently signed `+`/`−`) followed by the full list. The count is `n! · 2ⁿ` (for `n = 2`: `2 · 4 = 8`). This is PERM (which enumerates the `n!` unsigned orderings) crossed with the `2ⁿ` sign assignments.

The project already provides `Permutations.enumerate(length: PermutationLength): Vector[Vector[Int]]` (the `n!` base permutations) and `PermutationLength` (validates `1 ≤ n ≤ 7`). SIGN reuses these and lives beside them in `bio.{domain,algorithms}.combinatorics`.

## Goals / Non-Goals

**Goals:**
- Validated `SignedPermutationProblem(n)` (`1 ≤ n ≤ 6`) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `SignedPermutationEnumeration.enumerate(problem): SignedPermutations`.
- Result exposing `count: Int` and `format: String` (count line then one permutation per line).
- Produce exactly the `n! · 2ⁿ` signed permutations.

**Non-Goals:**
- A canonical ordering of the output — Rosalind accepts any order.
- `n > 6` — rejected by validation (the Rosalind cap; output grows as `n! · 2ⁿ`).

## Decisions

**1. Base permutations × sign assignments.**
Reuse `Permutations.enumerate` for the `n!` base orderings (constructing a `PermutationLength` from the already-validated `n`, which is guaranteed to succeed since `n ≤ 6 < 7`). For each base permutation, emit every sign assignment: enumerate the `2ⁿ` bitmasks, mapping bit `i` to `−1` (set) or `+1` (clear), and multiply element-wise. The full result is `perms.flatMap(p => signCombos.map(signs => p ⊙ signs))`, of size `n! · 2ⁿ`.

**2. Count derived from the list.**
`SignedPermutations.count = permutations.size` (= `n! · 2ⁿ`), keeping the reported count and the listed permutations trivially consistent.

**3. Validation.**
`SignedPermutationProblem.from(n)` validates `n ≥ 1` (`NonPositive(n)`) then `n ≤ 6` (`ExceedsMaximum(n, 6)`). This mirrors `PermutationLength` with the tighter SIGN cap of 6 (since `2ⁿ` makes the output grow faster than PERM's).

**4. Result rendering.**
`SignedPermutations(permutations: Vector[Vector[Int]])` with `format` = the `count` on the first line followed by each permutation rendered space-separated, all joined by `\n`.

**5. Pure functional enumeration.**
Bitmask generation and the element-wise products use `map`/`flatMap`/`lazyZip` over immutable `Vector`s — no mutation. The public `enumerate` signature is pure and total.

**6. Naming and placement.**
`SignedPermutationProblem`, `SignedPermutationProblemError`, and the `SignedPermutations` result live in `bio.domain.combinatorics`; the algorithm `SignedPermutationEnumeration.enumerate` in `bio.algorithms.combinatorics`. Result (`SignedPermutations`) and algorithm (`SignedPermutationEnumeration`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Output size]** → at `n = 6` the result is `720 · 64 = 46 080` permutations — trivially fast; the cap of 6 keeps it bounded.
- **[Reuse vs inline generation]** → reusing `Permutations.enumerate` requires constructing a `PermutationLength`; since `n` is pre-validated to `1 … 6` this always succeeds (the unreachable failure branch falls back to an empty list).
- **[Order]** → output order is base-permutation-major, sign-minor; Rosalind accepts any order, so tests compare the result as a set.
- **[Edge cases]** → `n = 1` → 2 permutations (`-1`, `1`); the count always equals `n! · 2ⁿ`; covered by scenarios.
