## Context

Rosalind problem 38 (LCSM) asks: given a collection of `k` DNA strings (`k ≤ 100`, each `≤ 1 kbp`), return a *longest common substring* — a contiguous run of nucleotides that appears in *every* string in the collection, with maximum length. When multiple longest-equal substrings exist (a common case), the spec permits any; we adopt the lexicographically-smallest convention for deterministic tests.

The canonical Rosalind sample: `GATTACA, TAGACCA, ATACA`. At length 2, the substrings common to all three strings are `{AC, CA, TA}`. At length 3 nothing is common (verified by enumeration). The lex-smallest length-2 result is `"AC"` — matching Rosalind's published answer.

The framework already hosts `bio.{algorithms,domain}.analysis` with `MotifLocations` (one-pattern substring search, SUBS) and `SplicedMotif` (subsequence search, SSEQ) — this slots in there as the multi-string longest-common-substring variant.

## Goals / Non-Goals

**Goals:**
- Provide a validated `SharedMotifProblem` ADT enforcing the Rosalind input contract (`1 ≤ k ≤ 100`, each string `≤ 1000`) so the algorithm can assume well-formed input.
- Provide `bio.algorithms.analysis.SharedMotif.find` returning the *lex-smallest longest* common substring as `String` (deterministic for tests; the spec allows any valid answer).
- TDD coverage at both layers, including the canonical Rosalind sample, single-string, identical-strings, no-shared-character, empty-string-in-collection, single-shared-char, and one-shared-run-with-distractors cases.

**Non-Goals:**
- Enumerating *all* longest common substrings. The spec asks for *one*.
- Returning a "leftmost" or "structurally meaningful" answer. Lex-smallest is a deterministic, easy-to-test convention; nothing biological hinges on it.
- Suffix-tree / suffix-array algorithms. At `k ≤ 100, n ≤ 1000` the binary-search + set-intersection algorithm runs in milliseconds; the asymptotically-optimal `O(N)` suffix-tree approach is overkill and adds significant complexity.
- FASTA-aware file ingestion. The Rosalind input is FASTA-wrapped; a problem runner can compose `FastaFileReader` with the algorithm.
- Generalisation beyond DNA. The wrapper uses `DnaString`; an RNA or protein variant is a separate spec.

## Decisions

**1. Algorithm: binary search on length + `Set[String]` intersection per length.**

For a candidate length `L`, build a `Set[String]` of every length-`L` substring of each input string. Intersect all the sets. If the intersection is non-empty, `L` is feasible — try a larger `L`. Else try smaller. Binary search converges in `⌈log₂ |shortest|⌉` iterations.

Per iteration: each of `k` strings has at most `n` substrings of length `L`, and building / intersecting sets is `O(L · k · n)` time. Total: `O(L · k · n · log n)`. At the Rosalind cap (`k = 100, n = 1000`) the worst case is roughly `10 · 100 · 1000 · 1000 = 10⁹` raw character ops — but in practice the lengths tested are far smaller (the answer is usually short) and Scala's `String.substring` plus `Set` operations run in hashing constants. Well under a second.

**Alternative considered:** generalized suffix tree (rejected: ~200 LOC vs ~20 LOC; not needed at this scale). **Alternative considered:** rolling-hash + set-of-hashes per length (rejected: marginal speedup for considerable complexity; the simple `String`-keyed `Set` is fast enough and trivially correct).

**2. Tie-break: lexicographically-smallest substring at the maximum feasible length.**

When multiple length-`L_max` substrings are common to all strings, return `intersected.min` (lex order). This makes tests deterministic and reproduces Rosalind's published sample answer `"AC"` (smaller than `"CA"` and `"TA"`). The spec explicitly permits any valid answer.

**3. Validation order: empty-collection → per-string length cap.**

Lower-cost check first (empty test is `O(1)`); per-string scan is `O(k)`. First-failure-wins.

The collection-size *upper* cap (`k ≤ 100`) is enforced in the same step as the empty check: `if (size < 1) EmptyCollection else if (size > 100) TooManyStrings else ...`.

**4. Empty strings within the collection short-circuit to `""`.**

If any input string is empty, no non-empty common substring can exist. The algorithm detects this in the binary-search initialisation (`shortest.length == 0`) and returns `""` immediately. **Alternative considered:** rejecting empty inputs in the wrapper (rejected: the natural mathematical answer for "LCS of `{"", "ACGT"}`" is `""`; rejecting would be over-strict).

**5. Output type: bare `String`, not `Option[String]`.**

Because the empty string `""` is always a valid common substring (every string contains the empty string), the result is always defined. `Option` would only differentiate "no LCS exists" — but that case never arises. Bare `String` is simpler. Contrasts with `SplicedMotif.find: Option[Vector[Int]]` where `None` *does* signal "no match" (when target isn't a subsequence of source).

**6. Place under `bio.{algorithms,domain}.analysis`.**

Same family as `MotifLocations` (SUBS), `SplicedMotif` (SSEQ), `HammingDistance`, `RandomMatch`, `FailureArray`, `GeneticCharacterTable`. Co-located so readers find the SUBS (one-pattern substring), SSEQ (subsequence), and LCSM (multi-string common substring) trio together.

## Risks / Trade-offs

- **[Risk]** A pathological worst-case input (many similar strings near the 1000-char cap) could push the binary-search loop's set construction into the high tens of millions of character ops. → **Mitigation:** at the Rosalind cap this is still <1 second in JVM; if a future spec needs more headroom, swap in the hash-based variant.
- **[Risk]** The lex-smallest tie-break is *not* the convention of the suffix-tree-based DP solutions (which typically return the *first-encountered* one in their traversal). Cross-referencing with another implementation could produce different (but equally valid) outputs. → **Mitigation:** Scaladoc explicitly states the lex-smallest convention and cites the spec's "any valid solution" permission.
- **[Trade-off]** Set-based comparison hashes each substring twice (once during construction, once during lookup). For `L ~ 500` and `k = 100`, that's ~50 000 sub-string allocations per binary-search iteration. Negligible at this scale but worth noting; a rolling-hash variant would avoid the allocations entirely.
- **[Trade-off]** `String.substring` in Java/Scala 2.13 copies the underlying char array, doubling memory traffic per substring extraction. Acceptable at our scale; if profiling ever flags this, swap to `String.regionMatches` for compares without allocation.
