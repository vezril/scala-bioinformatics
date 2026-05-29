## Context

The Rosalind alignment ladder so far operates on *pairwise* sequences:
- EDIT (spec 40) — Levenshtein distance via 2-D DP.
- EDTA (spec 41) — Levenshtein traceback for one optimal alignment.
- GLOB (spec 42) — Needleman-Wunsch under BLOSUM62 with linear gap penalty.

MULT (spec 43) lifts alignment to *four* simultaneous DNA strings. The score is summed over all `C(4, 2) = 6` augmented-string pairs under a simple linear scheme:
- matched symbols (including matched gap-vs-gap columns): `0`;
- mismatched symbols (including any gap-vs-nongap): `-1`.

The Rosalind constraint pins this to exactly four strings each ≤ 10 bp — a deliberate, small upper bound because the obvious algorithm is `N`-dimensional DP whose work scales as `O((n+1)^N · (2^N − 1))`. At `N = 4, n = 10` this is `11^4 · 15 ≈ 220k` cell-transitions, each requiring 6 pair-comparisons — trivially fast.

Canonical Rosalind sample:
```
Input:                Output:
>Rosalind_7           -18
ATATCCG               ATAT-CCG
>Rosalind_35          -T---CCG
TCCG                  ATGTACTG
>Rosalind_23          ATGT-CTG
ATGTACTG
>Rosalind_44
ATGTCTG
```

Hand-verified column-by-column: the six pair-wise mismatch counts sum to `3 + 3 + 2 + 5 + 4 + 1 = 18`, so the total score is `-18` ✓.

## Goals / Non-Goals

**Goals:**
- Validated `MultipleAlignmentProblem(strings: Vector[DnaString])` smart constructor enforcing *exactly* 4 strings each ≤ 10 bp.
- Output ADT `MultipleAlignment(score: Int, augmentedStrings: Vector[String])` carrying the score plus the four augmented strings (each containing the original characters interleaved with `-` gap symbols).
- Algorithm object `MultipleAlignment.align(problem): MultipleAlignment` returning one optimal alignment via 4-D DP + traceback.
- Empty strings accepted (empty/empty/empty/empty ⇒ score `0`, four empty augmented strings).
- TDD: Red → Green → Refactor with `sbt test` green at the end.

**Non-Goals:**
- No support for variable `N` (not 4) — Rosalind pins `N = 4`. A future "generalised multiple alignment" change can lift this.
- No protein-string variant (Rosalind MULT uses DNA strings; BLOSUM62 / scoring matrices are spec 42 territory and would couple two unrelated concerns).
- No streaming / sparse-DP optimisation — the table is tiny.
- No FASTA parsing inside the algorithm (the runner under `bio.problems.MULTProb` handles I/O).
- `MultipleAlignment` (output) is *not* `sealed abstract` — it's a result value carrier with no cross-field invariant beyond what the producing algorithm guarantees. Same convention as `EditAlignment`.

## Decisions

**1. Hardcode `N = 4` strings.**
- Rationale: Rosalind constrains exactly 4 strings, and the move set (`2^N − 1 = 15` non-empty subsets) and the pair count (`C(N, 2) = 6`) are baked into the algorithm's loop structure. Building for variable `N` would force runtime allocation of an N-dimensional table and dynamic subset iteration — significant complexity for no current consumer. A future "n-way alignment" change can refactor when justified.

**2. Encode the DP table as a flat `Array[Int]` of size `(n1+1)(n2+1)(n3+1)(n4+1)` with manual index arithmetic.**
- Rationale: nested `Array[Array[...]]` of depth 4 would force four pointer chases per access. A flat `Array[Int]` plus `idx(i1, i2, i3, i4) = ((i1 * (n2+1) + i2) * (n3+1) + i3) * (n4+1) + i4` is one allocation and one multiply-add chain per access. At ~15k cells this matters less for performance than for clarity — it also avoids the depth-4 boilerplate. Pair this with a second `Array[Byte]` of the same shape storing the chosen subset (1..15) for traceback.

**3. Move set: 15 non-empty subsets of `{0, 1, 2, 3}` enumerated as `Int` bitmasks `1..15`.**
- Rationale: bitmask is the natural representation. `(mask & (1 << k)) != 0` is the membership predicate. The "empty subset" (mask 0) is excluded — it corresponds to an all-gap column, which is degenerate and never improves the score.

**4. Per-column score: sum over the 6 unordered pairs `(j, k)` with `0 ≤ j < k < 4`.**
- For each pair, the contribution is:
  - if both `j` and `k` consume a character (i.e., both bits set in `mask`): `0` if `s_j[i_j-1] == s_k[i_k-1]`, else `-1`.
  - if exactly one of `j`, `k` is in `mask`: `-1` (gap vs non-gap).
  - if neither is in `mask`: `0` (gap-vs-gap match).
- Rationale: this exactly implements Rosalind's spec scoring scheme.

**5. DP recurrence (maximising):**
- `dp(0, 0, 0, 0) = 0`.
- For every other cell `(i_0, i_1, i_2, i_3)`, iterate `mask ∈ 1..15`. The move is *legal* if for every `k` with `mask` bit `k` set, `i_k > 0` (we need a character to consume). The transition score is `dp(i_0 - δ_0, …, i_3 - δ_3) + columnScore(mask, (s_k[i_k - 1])_{k ∈ mask})`. Take the max.
- The *first cell with i > 0 in some dimension* may have multiple legal moves (e.g., `(1, 0, 0, 0)` only allows `mask = 1`); illegal moves are skipped.

**6. Iteration order: nested loops over `i_0 = 0..n_0`, `i_1 = 0..n_1`, etc., topological-sort-friendly because `dp(predecessor) < dp(current)` in lexicographic order whenever `mask ≠ 0`.**
- Rationale: simplest correct order. No need for explicit topological sort because every legal predecessor has strictly smaller index in every dimension (or equal-and-smaller in at least one).

**7. Traceback: from `(n_0, n_1, n_2, n_3)` back to `(0, 0, 0, 0)`, following the `chosenMask` table.**
- At each step, decode the stored mask into the column (character or `-` per row), prepend to a per-row `StringBuilder`, and decrement the indices according to the mask bits.
- At the end, reverse each builder once.
- Returns plain `Vector[String]` (augmented strings) because each may contain `-` gap symbols, which are not valid DNA characters.

**8. Validation: `WrongNumberOfStrings` and `StringTooLong` errors.**
- Rationale: Rosalind constrains *exactly* 4 strings each ≤ 10 bp. The smart constructor enforces these caps explicitly with first-failure-wins: count check before length check, and the length check reports the *first* offending index (preserving the predictable error ordering pattern of the codebase). Empty strings are accepted; only `> 10` triggers `StringTooLong`.

**9. Place under `bio.{domain,algorithms}.analysis`.**
- Rationale: matches `SharedMotif` (LCSM) and `SharedSplicedMotif` (LCSQ) — multi-DNA-string comparative algorithms. Inputs are `DnaString`s, outputs are `Vector[String]` of augmented strings.

## Risks / Trade-offs

- **Multiple optimal alignments exist; tests pin the canonical Rosalind output for the published sample.** → For the canonical input we pin `(-18, ATAT-CCG, -T---CCG, ATGTACTG, ATGT-CTG)` because that's what Rosalind publishes and what our subset-iteration order naturally produces (subsets iterated `1..15` ascending; max chosen by `>` so first encountered ties retain). For other inputs we test invariants (score matches sum-of-pair-Hamming, lengths equal, gap-stripping recovers originals). Multiple equally-valid alignments can exist; the spec accepts any one.
- **The 4-D index arithmetic is error-prone.** → Mitigated by extracting `private def idx(i0, i1, i2, i3): Int` and unit-testing it indirectly via the algorithm's small-input scenarios. Empty/empty/empty/empty and one-character cases will pin down off-by-one bugs.
- **The cap `n = 10` is a Rosalind constraint, not algorithmic.** → Even doubling it to 20 (`21^4 · 15 ≈ 2.9M` ops) would still be fast. The cap exists to keep the worst-case work bounded and prevent accidental quadratic-in-N blowup. We enforce it via the smart constructor and document why.
