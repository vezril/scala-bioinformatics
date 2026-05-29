## Context

Rosalind problem 39 (LCSQ) is the textbook Longest Common Subsequence problem applied to DNA. Given two DNA strings `s` and `t` (each `≤ 1 kbp`), return *one* longest sequence `u` such that `u` appears in order — not necessarily contiguous — in both `s` and `t`. Multiple LCSes typically exist for the same pair; the spec permits any.

The canonical Rosalind sample (`AACCTTGG`, `ACACTGTGA`) has LCS length 6. The published sample answer is `"AACTGG"`; other valid length-6 answers include `"AACTTG"` and `"ACCTTG"`. Any of these is correct.

The framework already hosts `bio.{algorithms,domain}.analysis` with:
- `MotifLocations` (SUBS, spec 9) — one-pattern *substring* search.
- `SplicedMotif` (SSEQ, spec 37) — one-pattern *subsequence* search.
- `SharedMotif` (LCSM, spec 38) — multi-string longest common *substring*.

LCSQ rounds out the matrix: two-string longest common *subsequence*. Slots into the same subdomain.

## Goals / Non-Goals

**Goals:**
- Provide a validated `SharedSplicedMotifProblem` ADT enforcing both 1000-char caps so the algorithm can assume the Rosalind input contract.
- Provide `bio.algorithms.analysis.SharedSplicedMotif.find` returning *one* LCS as `String`, with a deterministic backtracking convention so tests can assert specific values where the LCS is unique.
- TDD coverage at both layers, including the canonical Rosalind sample (property-based: length 6 AND subsequence-of-both), uniqueness-friendly small cases, empty-input variants, no-shared-character, and identical-strings cases.

**Non-Goals:**
- Enumerating *all* LCSes (combinatorial explosion possible).
- Reproducing Rosalind's specific published `"AACTGG"`. The spec allows any valid LCS; our convention produces one deterministic answer that may differ from the published one. Both are correct per Rosalind's grader.
- Hirschberg's linear-space variant. At Rosalind cap (`m, n ≤ 1000` → 4 MB for the `Int` DP array) the standard `O(m · n)` space is fine; saving space adds complexity we don't need.
- Edit-distance / Hamming-distance generalisations. Those are separate specs.

## Decisions

**1. Algorithm: classical `O(m · n)` DP + backtracking.**

Fill a `(m+1) × (n+1)` `Array[Array[Int]]` where `dp(i)(j)` is the LCS length of `left[0..i)` and `right[0..j)`. Recurrence:
- `dp(0)(_) = dp(_)(0) = 0`;
- if `left(i-1) == right(j-1)`: `dp(i)(j) = dp(i-1)(j-1) + 1`;
- else: `dp(i)(j) = max(dp(i-1)(j), dp(i)(j-1))`.

After filling, backtrack from `(m, n)`:
- On match: append `left(i-1)` and decrement both indices.
- On mismatch: move "up" (decrement `i`) if `dp(i-1)(j) >= dp(i)(j-1)`, else move "left".

Append to a `StringBuilder` and reverse at end. Total `O(m · n)` time, `O(m · n)` space. At Rosalind cap: 10⁶ cells, milliseconds.

**Alternative considered:** Hirschberg's algorithm (`O(m·n)` time, `O(min(m, n))` space) — rejected as unnecessary at our scale. **Alternative considered:** suffix tree / sparse DP — same verdict.

**2. Backtracking tie-break: prefer "up" (`>=` not strict).**

When `left(i-1) != right(j-1)` and `dp(i-1)(j) == dp(i)(j-1)`, advance the source pointer (`i--`). Deterministic, simple, the textbook default. **Alternative considered:** prefer "left" on ties (rejected: equally valid but not the canonical textbook convention; either pick would have served).

**3. Return type: bare `String`, not `Option[String]`.**

The empty string `""` is always a valid LCS (every string contains the empty string as a subsequence). Bare `String` is simpler; `Option` would only differentiate "no LCS exists" — but that case never arises. Mirrors `SharedMotif.find` (LCSM, spec 38) which also returns bare `String`.

**4. Validation order: left length cap → right length cap.**

Left first because it's the "primary" input from a naming standpoint; first-failure-wins. Symmetric naming (`LeftTooLong` / `RightTooLong`) since the LCS problem is symmetric in its two inputs — no source/target distinction like SSEQ.

**5. Empty input short-circuit.**

If either `left` or `right` is empty, the LCS is `""` and we return immediately without allocating the DP table.

**6. Place under `bio.{algorithms,domain}.analysis`.**

Same family as `SharedMotif` (LCSM, the substring variant), `SplicedMotif` (SSEQ, the one-pattern subsequence search), and the broader string-analysis subdomain. Co-located so readers find the LCSM (substring) and LCSQ (subsequence) pair together.

## Risks / Trade-offs

- **[Risk]** Tests asserting Rosalind's published `"AACTGG"` would fail against our deterministic convention (which produces a different valid LCS — likely `"AACTTG"`). → **Mitigation:** the canonical-sample test is **property-based** (length 6 AND subsequence-of-both) rather than asserting a specific string. Small uniqueness-friendly cases (e.g. `ACG, TCG → "CG"`) cover specific-value assertions.
- **[Risk]** `O(m · n)` memory at the worst case is 1000 · 1000 · 4 bytes = 4 MB. → **Mitigation:** well within JVM defaults; if a future spec requires larger inputs, swap to Hirschberg.
- **[Trade-off]** Reconstructing the LCS via backtracking duplicates the comparison logic from the forward fill. Could be DRYed via a shared "decide direction" helper. At this scale not worth the indirection.
- **[Trade-off]** The `(m+1) × (n+1)` array uses one row of zeros as sentinels. Slight memory waste; the alternative (special-casing `i = 0` and `j = 0` in the inner loop) is uglier and not faster.
