## Context

Rosalind problem 37 (SSEQ) asks: given two DNA strings `s` and `t` (each `≤ 1 kbp`), return one collection of 1-indexed positions `(i_1, i_2, ..., i_m)` such that `1 ≤ i_1 < i_2 < ... < i_m ≤ |s|` and `s[i_k] == t[k]` for every `k`. That is, `t` appears as a *subsequence* (in order, not necessarily contiguous) of `s`. The spec promises a solution exists for the supplied dataset; it explicitly notes "if multiple solutions exist, you may return any one".

The canonical Rosalind sample:
```
s = ACGTACGTGACG  (length 12)
t = GTA           (length 3)
```
The published answer is `3 8 10`. A simpler *leftmost-greedy* walk produces `3 4 5` — also valid (`s[3]=G, s[4]=T, s[5]=A`). Both are correct; we adopt the leftmost-greedy rule for determinism.

The framework already hosts `bio.{algorithms,domain}.analysis` with `MotifLocations` (substring search) — this slots in there as the subsequence variant.

## Goals / Non-Goals

**Goals:**
- Provide a validated `SplicedMotifProblem` ADT enforcing both length caps (`≤ 1000`) so the algorithm can assume the Rosalind input contract.
- Provide `bio.algorithms.analysis.SplicedMotif.find` returning `Option[Vector[Int]]` of leftmost-greedy 1-indexed positions, or `None` when `t` is not a subsequence of `s`.
- TDD coverage at both layers, including the canonical Rosalind sample (our greedy answer), the empty-target case, the `s == t` case, the no-solution case, and the single-char-at-end-of-`s` case.

**Non-Goals:**
- Enumerating *all* valid index collections — the spec only asks for one.
- Returning the published Rosalind answer `3 8 10` instead of the greedy `3 4 5`. Both are spec-permitted; the greedy answer is what tests assert.
- Generalisation to alphabets beyond `{A, C, G, T}`. The wrapper uses `DnaString`; an RNA or protein variant would be a separate spec.
- FASTA-aware file ingestion. The Rosalind input is FASTA-wrapped; a problem runner can compose `FastaFileReader` with the algorithm.

## Decisions

**1. Greedy leftmost-match algorithm, `O(|s| + |t|)`.**

Walk a single pointer `i` through `s` and a single pointer `j` through `t`. On `s(i) == t(j)`, record `i + 1` (1-indexed) and advance both pointers; otherwise advance only `i`. Terminate when either pointer exhausts its string. If `j == t.length` at termination, `t` is a subsequence; return the recorded positions. Else return `None`. **Alternative considered:** suffix-automaton or `next[i][c]` precomputed table (rejected: the input is `≤ 1 kbp`; the greedy single-pass walk runs in microseconds and is dead simple to verify).

**2. Return type: `Option[Vector[Int]]`.**

`Some(indices)` when `t` is a subsequence of `s`; `None` when it isn't. The framework's pattern is to surface "no match" as `None` rather than throwing or returning an empty `Vector` (which would be ambiguous with the empty-target case). Mirrors `MotifLocations.find`'s "empty result = no matches" convention by adding the `Option` wrapper for the *non-existence* case specifically — empty `t` legitimately yields `Some(Vector.empty)`. **Alternative considered:** `Either[NoSubsequence.type, Vector[Int]]` (rejected: `Option` is the canonical "may not exist" type in Scala stdlib; there's only one failure mode).

**3. 1-indexed output to match Rosalind.**

Internally the algorithm walks 0-indexed; on each match we record `i + 1`. Same convention as `MotifLocations` and the rest of the Rosalind catalogue. Documented inline.

**4. Empty `t` accepted, returns `Some(Vector.empty)`.**

The empty string is a subsequence of every string (vacuously — no characters to match). The greedy algorithm's exit condition (`j == t.length`) is `true` immediately when `t.length == 0`, so this falls out naturally. **Alternative considered:** rejecting empty `t` (rejected: degenerate cases that "just work" are a feature, not a footgun; mirrors `MotifLocations.find` which also accepts empty inputs gracefully).

**5. Validation order: source length cap → target length cap.**

`s` first because it's the larger structure; first-failure-wins. Errors carry the offending length.

**6. Place under `bio.{algorithms,domain}.analysis`.**

Same family as `MotifLocations`, `HammingDistance`, `RandomMatch`, `FailureArray`, `GeneticCharacterTable`. The "string-analysis" subdomain. Co-located so readers find SUBS (substring) and SSEQ (subsequence) together.

## Risks / Trade-offs

- **[Risk]** Tests asserting the published Rosalind answer `3 8 10` would fail against the greedy algorithm (which emits `3 4 5`). → **Mitigation:** the spec scenarios assert `Some(Vector(3, 4, 5))` and Scaladoc on `SplicedMotif.find` calls out the leftmost-greedy convention explicitly, noting that the Rosalind published answer is a *different* valid output.
- **[Risk]** A future spec wants the *rightmost* subsequence positions for some reason → **Mitigation:** the leftmost-greedy logic is a private helper inside `SplicedMotif`; a `findRightmost` variant could share the same skeleton with reversed iteration. Not implemented now.
- **[Trade-off]** Single-pass greedy returns *one* solution. Enumerating all valid subsequences is a different problem (combinatorial explosion: `s = "AAAA", t = "AA"` has `C(4,2) = 6` valid index pairs); the framework doesn't need that for SSEQ.
- **[Trade-off]** No memoization or precomputation. At `|s| ≤ 1000` the cost is irrelevant. A future spec might want repeated queries on the same `s` — the standard fix is the `next[i][c]` table, deferred.
