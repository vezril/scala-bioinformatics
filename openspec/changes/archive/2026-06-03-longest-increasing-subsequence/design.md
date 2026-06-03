## Context

LGIS ("Longest Increasing Subsequence") takes a permutation π of `{1, …, n}` (`n ≤ 10000`) and returns a longest increasing subsequence and a longest decreasing subsequence. With `n` up to 10000 the quadratic DP (10⁸ ops) is borderline, so the standard O(n log n) *patience sorting* with predecessor reconstruction is the right algorithm. A longest *decreasing* subsequence of π is exactly a longest *increasing* subsequence of π under the reversed order (`>` instead of `<`), so the same routine handles both by parameterising the comparison.

The framework has no permutation type yet; LGIS introduces a reusable `Permutation` in `bio.domain.combinatorics`, beside the existing permutation-family types (`PermutationLength`, `PartialPermutationProblem`).

## Goals / Non-Goals

**Goals:**
- Validated `Permutation` (a permutation of `{1, …, n}`, `n ≤ 10000`) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `LongestSubsequences.find(permutation): MonotonicSubsequences`.
- Result type with `format: String` (two lines: increasing, then decreasing).
- O(n log n) time.

**Non-Goals:**
- Returning *all* longest subsequences — one of each suffices (Rosalind accepts any longest).
- Supporting arbitrary integer sequences — the domain is permutations of `1..n`.

## Decisions

**1. Patience sorting with predecessor reconstruction, comparison-parameterised.**
Maintain `tails`, where `tails(l)` holds the index (into π) of the smallest possible tail value of an increasing subsequence of length `l+1` seen so far. For each position `i`, binary-search `tails` for the first slot whose value is not `< π(i)` (for the increasing run; `not >` for the decreasing run), place `i` there, and record `pred(i)` = the index stored in the previous slot. The longest subsequence is recovered by walking `pred` back from the index in the last filled slot, then reversing. Running both passes (one with `<`, one with `>`) yields the increasing and decreasing answers. `O(n log n)` per pass.

**2. Decreasing = increasing under reversed order.**
Rather than a separate routine, the LIS core takes a `lessThan: (Int, Int) => Boolean`. The increasing pass uses `_ < _`; the decreasing pass uses `_ > _`. This keeps one tested algorithm and guarantees the two passes stay consistent.

**3. Imperative DP internals, pure signature (established algorithmic precedent).**
The patience-sorting arrays (`tails`, `pred`) use `var`/`while`/`Array[Int]` with a manual binary search, exactly as the alignment/DP family does; the public `find` signature is pure and total, returning a `MonotonicSubsequences`.

**4. Validation.**
`Permutation.from(values)` enforces, with first-failure-wins ordering: `values.length ≤ 10000` (`TooLong(length, 10000)`), then that `values` is a permutation of `{1, …, length}` — i.e. sorted it equals `1..length` (`NotAPermutation(values)`). The empty vector is accepted (the empty permutation of `{1..0}`; both subsequences are empty).

**5. Result rendering.**
`MonotonicSubsequences(increasing: Vector[Int], decreasing: Vector[Int])` with `format` = the increasing values space-separated on line 1, the decreasing values space-separated on line 2 (joined by `\n`).

**6. Naming and placement.**
`Permutation`, `PermutationError`, and the `MonotonicSubsequences` result live in `bio.domain.combinatorics`; the algorithm `LongestSubsequences.find` in `bio.algorithms.combinatorics`. Result (`MonotonicSubsequences`) and algorithm (`LongestSubsequences`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Tie-breaking vs. the sample]** → multiple longest subsequences can exist; the sample shows `5 4 2` for the decreasing run, but a valid algorithm may emit `5 4 3` (also length 3). Rosalind accepts any longest, so the algorithm tests assert *validity* (correct length, strict monotonicity, genuine subsequence) rather than exact equality with the sample's decreasing line — the PRSM tie precedent.
- **[n = 10000 performance]** → O(n log n) patience sorting handles the cap in well under a second; the quadratic DP is deliberately avoided.
- **[Empty / singleton permutations]** → empty → both subsequences empty; singleton → both are that one element; covered by scenarios.
- **[Imperative internals]** → confined to the patience-sorting core; the public signature stays pure/total, consistent with the alignment/DP family.
