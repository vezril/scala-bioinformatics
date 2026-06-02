## Context

LING ("Linguistic Complexity of a Genome") computes `lc(s) = sub(s) / m(a,n)`, where `sub(s)` is the number of distinct substrings of `s` and `m(a,n)` is the maximum number of distinct substrings any length-`n` string over an `a`-letter alphabet could have. For DNA, `a = 4`. The hint ("Why does this follow Encoding Suffix Trees?") points at suffix structures: counting distinct substrings is their canonical application.

Unlike SUFF (capped at 1 kbp), LING allows `s` up to **100 kbp**, so the naive O(n²) suffix-tree builder from SUFF is not used here. The project already has `bio.domain.nucleic.DnaString`; LING is a genome metric, sibling to GC-content, so it lives in `bio.{domain,algorithms}.analysis`.

## Goals / Non-Goals

**Goals:**
- `LinguisticComplexityProblem` wrapping a `DnaString` `s`. The 100 kbp bound is already enforced by `DnaString` (its own `MaxLength` is 100000), so the problem carries no extra invariant and is a plain wrapper (precedent: `SpectralConvolutionProblem`).
- Pure, total `LinguisticComplexityAnalysis.compute(problem): LinguisticComplexity` that scales to 100 kbp.
- Result type with `format: String` rounding `lc(s)` to three decimals (sample `0.875`).
- Functional implementation (immutable structures, tail recursion / folds), no `var`/`while`/mutable collections.

**Non-Goals:**
- Reusing the O(n²) SUFF suffix-tree builder (too slow at the 100 kbp bound).
- A general alphabet — `a` is fixed at 4 (DNA).
- Per-length breakdown output — only the final ratio is returned.

## Decisions

**1. `sub(s)` via suffix array + LCP, not an explicit suffix tree.**
The number of distinct substrings of `s` equals `n(n+1)/2 − Σ LCP[i]`, where `LCP` is the longest-common-prefix array of the sorted suffixes. This is the suffix-array dual of "sum of suffix-tree edge lengths"; it is O(n log n)-ish and avoids materialising the tree, so it scales to 100 kbp. (The suffix-tree relationship the hint alludes to is exactly this identity — distinct substrings = total edge-label length of the suffix tree.)

**2. Suffix array by prefix doubling (pure FP).**
The suffix array is built by prefix doubling: ranks start from character codes; each round sorts indices by `(rank(i), rank(i+k))` and recomputes ranks, doubling `k` until all ranks are distinct. Implemented with immutable `Vector`s and `sortBy`/`foldLeft` (O(n log² n)). The inverse permutation gives `rank(i)` = the suffix's position in the array.

**3. LCP sum by Kasai's algorithm (pure FP).**
Kasai computes LCP in O(n) by scanning suffixes in text order, carrying the previous match length `h`. Since only `Σ LCP` is needed, it is a `foldLeft` over `0 until n` threading `(h, sum)`; the per-step match extension is a `@tailrec` counter starting at `h`. No array is materialised.

**4. `m(4,n)` with overflow-safe powers.**
`m(a,n) = Σ_{k=1}^{n} min(a^k, n−k+1)`. `4^k` overflows `Long` past `k ≈ 31`, but `min(4^k, n−k+1)` is bounded by `n−k+1 ≤ n`. The power is computed with a capped tail-recursion that stops multiplying once it exceeds the position count, so it never overflows. The running total uses `Long` (for `n = 100 000`, both `sub(s)` and `m` are ≈ 5·10⁹, beyond `Int`).

**5a. No problem-level length validation.**
`DnaString`'s own maximum length is 100000 bp — exactly the Rosalind LING bound — so a DNA string exceeding 100 kbp cannot be constructed in the first place. `LinguisticComplexityProblem` therefore needs no length check or error type; it is a plain `final case class` wrapping the validated `DnaString`, mirroring `SpectralConvolutionProblem`.

**5. Total function; empty input.**
`n(n+1)/2`, `Σ LCP`, and `m` are all well-defined for `n ≥ 1`. For the empty string (`n = 0`), `m = 0`, so `lc` is undefined; the algorithm returns `0.0` rather than dividing by zero (defensive — Rosalind inputs are non-empty). `lc` is otherwise a `Double` in `(0, 1]`.

**6. Naming and placement.**
`LinguisticComplexityProblem`, `LinguisticComplexityProblemError`, and the `LinguisticComplexity` result live in `bio.domain.analysis`; the algorithm `LinguisticComplexityAnalysis.compute` in `bio.algorithms.analysis`. Result (`LinguisticComplexity`) and algorithm (`LinguisticComplexityAnalysis`) names are distinct, so no `=> Result` alias is needed.

## Risks / Trade-offs

- **[Prefix-doubling correctness]** → De-risked by tests that cross-check `sub(s)` against a brute-force distinct-substring set count on small strings, plus the exact sample (`ATTTGGATT → 0.875`).
- **[Performance at 100 kbp]** → O(n log² n) suffix array + O(n) Kasai is comfortably fast; no O(n²) step. The realistic dataset is far smaller.
- **[Floating-point rounding]** → `lc` rendered with `f"%.3f"`; Rosalind grades to 3 decimals. `35/40 = 0.875` is exact.
- **[Empty input]** → returns `0.0` (documented boundary); covered by a scenario.
- **[Integer overflow]** → `sub(s)` and `m` use `Long`; the power computation is capped to avoid overflow.
