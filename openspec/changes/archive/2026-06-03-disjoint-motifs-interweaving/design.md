## Context

ITWV ("Finding Disjoint Motifs in a Gene") takes a text DNA string `s` (≤ 10 kbp) and `n ≤ 10` pattern strings (each ≤ 10 bp) and asks, for every ordered pair `(j, k)`, whether patterns `j` and `k` can be **interwoven** into `s`: does some contiguous substring of `s` equal a shuffle of the two patterns, with the two patterns appearing as *disjoint* subsequences that together cover the substring exactly? The output is the `n × n` 0/1 matrix `M`.

"Interwoven" is exactly the classic *interleaving string* relation: a string `w` is an interleaving of `t` and `u` iff `|w| = |t| + |u|` and `w` can be split so that one disjoint subsequence is `t` and the other is `u`. ITWV adds two wrinkles over the textbook problem: the interleaving need only match *some contiguous window* of `s` (not all of `s`), and the answer is pairwise over a small pattern set. The problem references SCSP ("Interleaving Two Motifs"), so the types live alongside it in `bio.domain.analysis` / `bio.algorithms.analysis`.

## Goals / Non-Goals

**Goals:**
- Validated `InterwovenMotifProblem(text, patterns)` via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `InterwovenMotifs.compute(problem): InterwovenMotifMatrix`.
- Result type with `format: String` (rows of space-separated 0/1, newline-joined).
- Match the canonical Rosalind sample exactly.

**Non-Goals:**
- Reconstructing or printing the actual interleaving — only the 0/1 decision per pair.
- Handling more than the Rosalind caps (`n ≤ 10`, text ≤ 10 kbp, pattern ≤ 10 bp).

## Decisions

**1. Interleaving DP over every start window.**
For a fixed pattern pair `(t, u)` let `L = |t| + |u|`. Patterns `t` and `u` can be interwoven into `s` iff, for *some* start `p ∈ [0, |s| − L]`, the window `s[p .. p+L-1]` is an interleaving of `t` and `u`. For one window, fill a boolean table `dp(i)(j)` = "the first `i+j` window characters are an interleaving of `t[0..i-1]` and `u[0..j-1]`":
- `dp(0)(0) = true`;
- `dp(i)(j) = (i>0 && t(i-1) == s(p+i+j-1) && dp(i-1)(j)) || (j>0 && u(j-1) == s(p+i+j-1) && dp(i)(j-1))`.

The pair is interweavable iff `dp(|t|)(|u|)` is `true` for any `p`; scanning short-circuits on the first success. Cost per pair `O(|s| · |t| · |u|)` ≤ `10000 · 10 · 10 = 10⁶`; with `n ≤ 10` pairs that is well within budget.

**2. Symmetry — compute the upper triangle, mirror it.**
Interweavability is symmetric (`M[j][k] = M[k][j]`) because interleaving `t` with `u` is the same relation as interleaving `u` with `t`. Compute `M[j][k]` for `j ≤ k` and mirror to `M[k][j]`, halving the DP work. The diagonal `M[j][j]` (a pattern interwoven with itself) is computed normally and may be `1` (the sample has `M[1][1] = 1`).

**3. Imperative DP fill, pure signature (established alignment/DP precedent).**
The per-window interleaving table uses `var`/`while`/`Array[Boolean]`, exactly as the alignment family and the counting DPs (MOTZ/CAT) do; the public `compute` signature is pure and total, returning an `InterwovenMotifMatrix`.

**4. Validation and first-failure-wins ordering.**
`InterwovenMotifProblem.from(text, patterns)` enforces, in order: `patterns.size ≤ 10` (`TooManyPatterns(count, 10)`), `text.length ≤ 10000` (`TextTooLong(length, 10000)`), then each pattern length `≤ 10` (`PatternTooLong(length, 10)`, first offender wins). Character validity (`A`,`C`,`G`,`T`) is owned upstream by `DnaString`. An empty pattern list is accepted (degenerate `0 × 0` matrix, empty output).

**5. Result rendering.**
`InterwovenMotifMatrix(rows: Vector[Vector[Int]])` with `format` = each row's entries joined by a single space, rows joined by `\n`. A `0 × 0` matrix formats to the empty string.

**6. Naming and placement.**
`InterwovenMotifProblem`, `InterwovenMotifProblemError`, and the `InterwovenMotifMatrix` result live in `bio.domain.analysis` (beside the SCSP types); the algorithm `InterwovenMotifs.compute` in `bio.algorithms.analysis`. Result (`InterwovenMotifMatrix`) and algorithm (`InterwovenMotifs`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[Cubic-per-pair DP]** → at the Rosalind caps each pair is ≤ 10⁶ boolean ops and there are ≤ 55 unique pairs; total ≤ ~5·10⁷, fast. Symmetry mirroring keeps it tight.
- **[Empty patterns / empty window]** → `L = 0` (both patterns empty) makes the empty window a trivial interleaving (`dp(0)(0)` already `true`), so such a pair is interweavable; covered by reasoning, not expected in real datasets. An empty pattern *list* yields a `0 × 0` matrix.
- **[Self-pair semantics]** → `M[j][j]` legitimately can be `1` (e.g. `GT` & `GT` → `GGTT` inside the sample); the algorithm makes no special case for the diagonal.
- **[Imperative DP]** → confined to the per-window table fill, consistent with the alignment family; the public signature stays pure/total.
