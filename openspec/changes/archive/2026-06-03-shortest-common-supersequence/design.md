## Context

SCSP ("Interleaving Two Motifs") asks for a shortest common supersequence of two DNA strings `s` and `t`: the shortest string that contains both as subsequences. Its length is `|s| + |t| − LCS(s,t)`, and it is reconstructed by interleaving the two strings around their longest common subsequence.

The alignment problems live in `bio.{domain,algorithms}.analysis` and follow the convention of wrapping two `DnaString`s with per-field length validation. The framework permits imperative DP (`var`/`while`/`Array`) for alignment-family algorithms while keeping the public signature pure and total.

## Goals / Non-Goals

**Goals:**
- Validated `SupersequenceProblem(s, t)` (each ≤ 1000 bp) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `ShortestCommonSupersequence.build(problem): Supersequence`.
- Result type with `format: String` (the supersequence).
- Correct O(m·n) reconstruction within the 1000×1000 table budget.

**Non-Goals:**
- Enumerating all shortest common supersequences — Rosalind accepts any one.
- Scaling beyond 1000 bp — the full DP table needed for backtracking is `O(m·n)`; the cap keeps it at ≤ 1M cells.

## Decisions

**1. SCS-length DP plus backtrack.**
`dp(i)(j)` is the SCS length of `s[0..i)` and `t[0..j)`: `dp(i)(0) = i`, `dp(0)(j) = j`; on a match `dp(i)(j) = dp(i-1)(j-1) + 1`, else `dp(i)(j) = 1 + min(dp(i-1)(j), dp(i)(j-1))`. The full table is then backtracked from `(m,n)`: a match emits the shared character once (move diagonally); otherwise the move with the smaller SCS length emits its character (from `s` going up, from `t` going left); when one index reaches 0 the remainder of the other string is emitted. The result has length `dp(m)(n) = |s| + |t| − LCS(s,t)`. Verified on the sample (`ATCTGAT`, `TGCATA` → length 9, e.g. `ATGCATGAT`).

**2. Imperative DP table; pure tail-recursive backtrack.**
The table is filled with `var`/`while` over an `Array.ofDim[Int](m+1, n+1)` (the alignment-family exception, matching `EditDistance`'s style). The backtrack is a `@tailrec` function over the table that prepends characters to an immutable `List[Char]`, so it allocates no mutable buffer. The public `build` signature is pure and total.

**3. Validation rules and order (first-failure-wins).**
`SupersequenceProblem.from(s, t)` checks `s.value.length <= 1000` then `t.value.length <= 1000`, each failing with `SequenceTooLong(length, 1000)` for the first offending sequence. Empty sequences are accepted (the SCS of `""` and `t` is `t`).

**4. Naming and placement.**
`SupersequenceProblem`, `SupersequenceProblemError`, and the `Supersequence` result live in `bio.domain.analysis`; the algorithm `ShortestCommonSupersequence.build` in `bio.algorithms.analysis`. Result (`Supersequence`) and algorithm (`ShortestCommonSupersequence`) names are distinct, so no alias is needed. The runner reads two plain lines (not FASTA), matching `scsp_data.txt`.

## Risks / Trade-offs

- **[O(m·n) table memory]** → the 1000-bp cap bounds the table at ≤ 1M `Int`s (~4 MB); fine. (Length-only SCS needs O(min) space, but backtracking the actual string needs the full table.)
- **[Multiple shortest supersequences]** → Rosalind accepts any; the backtrack's deterministic tie-break (prefer the `s` move on equal SCS length) yields one valid SCS, possibly differing from the sample text but of the same minimal length and a supersequence of both. Tests assert those properties rather than byte-equality.
- **[Empty / identical / disjoint inputs]** → SCS of `""`/`t` is `t`; of identical strings is the string itself; of disjoint strings is their concatenation (length `|s|+|t|`); all covered by scenarios.
- **[Imperative DP]** → confined to the table fill; the backtrack and public signature are pure, consistent with the project's other alignment algorithms.
