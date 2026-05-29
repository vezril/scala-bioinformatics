## Context

GLOB (spec 42) established the global Needleman-Wunsch + BLOSUM62 + linear gap penalty (-5) pattern; LOCA (spec 47) is its *local* cousin. The classical *Smith-Waterman* algorithm differs from Needleman-Wunsch in three ways:

1. **0-clamp at every cell.** `dp(i)(j) = max(0, diag, up, left)` — every cell can either extend a positive-scoring alignment or "start fresh" with score 0. This means negative-scoring partial alignments are simply abandoned rather than dragged forward.
2. **Boundary conditions are zeros, not gap-penalty multiples.** `dp(0)(j) = dp(i)(0) = 0`. There's no charge for "starting later" in either string.
3. **Traceback starts from the global max cell, not the bottom-right.** Walk back through the moves that achieved each value until the cell value drops to 0; at that point, the local alignment is complete.

The substitution scoring uses *PAM250* (Point Accepted Mutation, scaled to 250 evolutionary distance) — a canonical 20 × 20 symmetric integer matrix sourced from BioPython / NCBI reference. Gap penalty is linear -5 (same as GLOB).

**Output contains the substrings, not augmented (gapped) strings.** During traceback, we emit `s(i-1)` into the left substring only when the DP move *consumed* a character from `s` (i.e., diagonal or up), and `t(j-1)` into the right substring only when the move consumed from `t` (diagonal or left). The result: two strings whose lengths typically differ by the number of indel events in the optimum.

Canonical Rosalind sample:
```
s = MEANLYPRTEINSTRING (18 aa)
t = PLEASANTLYEINSTEIN (18 aa)
Output: score = 23
  r = LYPRTEINSTRIN (13 chars, positions 5..17 of s)
  u = LYEINSTEIN    (10 chars, positions 8..17 of t)
```

Hand-verified alignment of `r` vs `u` (under PAM250 + gap -5):
```
L Y P R T E I N S T R I N    (r)
L Y - - - E I N S T E I N    (u, gaps inserted to align)
6+10-5-5-5+4+5+2+2+3-1+5+2 = 23 ✓
```

## Goals / Non-Goals

**Goals:**
- `Pam250.score(a, b): Int` — total, symmetric, indexed by the canonical NCBI 20-amino-acid ordering.
- Validated `LocalAlignmentProblem(left, right)` smart constructor with first-failure-wins 1000-aa cap pattern (mirrors EDIT / EDTA / GLOB).
- Output ADT `LocalAlignment(score, leftSubstring, rightSubstring)` — plain `final case class`, no smart constructor.
- Algorithm `LocalAlignment.compute(problem): LocalAlignment` runs Smith-Waterman + traceback returning the maximum local-alignment score under PAM250 substitution + linear gap penalty -5, plus the two recovered substrings.
- Empty inputs accepted: empty/empty → `LocalAlignment(0, "", "")`. Empty/non-empty → score 0, empty substrings.
- TDD: Red → Green → Refactor with `sbt test` green at the end.

**Non-Goals:**
- No traceback for *augmented* (gapped) alignment strings. The output is plain substrings (the spec asks for "substrings r and u"). The gapped form is reconstructible from `(r, u)` by re-running Needleman-Wunsch on them but is not part of LOCA's contract.
- No affine-gap or score-shifting variants (Rosalind GAFF / GCON / SMGB territory — separate changes).
- No parameterisation of the scoring matrix or gap penalty in the public signature — PAM250 and `-5` are hardcoded per the spec.
- No reuse of `GlobalAlignmentScore.compute`. The recurrence has different boundary conditions (zeros vs. linear penalty), a different cell formula (0-clamp), and a different post-DP step (traceback from global max). The duplication is intentional.
- No streaming / sparse optimisation. At the Rosalind cap (`m, n ≤ 1000`), the DP table is `~10^6` `Int` cells — ~4 MB — trivially fine.

## Decisions

**1. Encode PAM250 as a dense `Array[Array[Int]]` indexed by amino-acid canonical ordinal — exactly the `Blosum62` pattern.**
- Rationale: O(1) lookup with no boxing. The matrix has 400 cells × 4 bytes ≈ 1.6 KB. Same canonical NCBI ordering as BLOSUM62 (A R N D C Q E G H I L K M F P S T W Y V). A separate `ordinal: Map[AminoAcid, Int]` resolves at lookup time.

**2. PAM250 source values: canonical BioPython / NCBI 20 × 20 matrix.**
- Rationale: standard reference. Symmetric: `score(a, b) == score(b, a)` for every pair. Validated with reference values from the literature (`A/A = 2`, `W/W = 17`, `C/C = 12`, `Y/Y = 10`, `A/R = -2`, `L/L = 6`, etc.).

**3. Hardcode the linear gap penalty as `private val Gap = -5` inside `LocalAlignment`.**
- Rationale: matches Rosalind LOCA's spec wording verbatim. Same convention as `GlobalAlignmentScore.Gap`.

**4. Classical `O(m · n)` Smith-Waterman DP, maximising with 0-clamp.**
- Rationale: textbook. Recurrence:
  - `dp(0)(j) = dp(i)(0) = 0`;
  - `dp(i)(j) = max(0, dp(i-1)(j-1) + Pam250(s(i-1), t(j-1)), dp(i-1)(j) - 5, dp(i)(j-1) - 5)`.
- Track the running global max `(maxScore, maxI, maxJ)` during the fill; that's the traceback start.

**5. Traceback tie-break: diagonal > up > left, stop when cell value is 0.**
- Rationale: deterministic and consistent with EDTA's convention. At each step, the move that achieved the cell value wins; on ties, diagonal first. Stop when `dp(i)(j) == 0` (the local alignment has ended).
- Emit per move:
  - **diagonal**: append `s(i-1)` to `r`, append `t(j-1)` to `u`, decrement both indices.
  - **up**: append `s(i-1)` to `r` only (gap in `u` — but we don't emit the gap into output), decrement `i`.
  - **left**: append `t(j-1)` to `u` only (gap in `r`), decrement `j`.
- Reverse both `StringBuilder`s once at the end.

**6. Pre-resolve each character to its `AminoAcid` once before the DP loop (mirrors `GlobalAlignmentScore`'s `toAminoAcids`).**
- Rationale: avoids re-parsing each cell in the hot loop. Same convention as GLOB.

**7. Return `Int` for the score, `String` for the substrings.**
- Rationale: the score is bounded by `1000 × max(Pam250) = 1000 × 17 = 17_000`, well within `Int` range. The substrings cannot exceed the originals' length (1000 aa). Plain `String` for the substrings is correct because they don't contain `-` gap symbols (LOCA reports the unmodified substring, not the augmented alignment).

**8. Place under `bio.{domain,algorithms}.protein`.**
- Rationale: matches `EditDistance`, `EditDistanceAlignment`, `GlobalAlignmentScore`. Inputs are protein strings.

**9. Output ADT is a plain `final case class` (not `sealed abstract`).**
- Rationale: same convention as `EditAlignment` and `MultipleAlignment` — result records with no cross-field invariants beyond what their components carry.

## Risks / Trade-offs

- **PAM250 matrix correctness is critical.** → Mitigated by scenario tests covering well-known reference values (`A/A`, `W/W`, `C/C`, `Y/Y`, `L/L`, cross-substitution sample values), a 400-pair symmetry check, and the canonical Rosalind sample `(MEANLYPRTEINSTRING, PLEASANTLYEINSTEIN) → 23`.
- **Multiple optimal `(r, u)` substring pairs may exist for the same input.** Rosalind permits any. → Tests pin the *score* to 23 and verify the *invariants* (`r` is a substring of `s`, `u` is a substring of `t`, the alignment-score of `r` vs `u` re-computes to 23). The published Rosalind substrings (`LYPRTEINSTRIN` / `LYEINSTEIN`) are one valid answer; whether our deterministic traceback produces exactly that pair depends on tie-breaks. If it does, great; if not, we still pass the invariant check.
- **Traceback emits characters in reverse via `StringBuilder` and reverses once at the end** — same pattern as LCSQ, EDTA, MULT. Avoids `O(L²)` string concatenation.
- **Memory: one DP table of `Int` at the cap is ~4 MB.** Acceptable. → No special handling required.
- **PAM250 vs PAM30 / PAM70 / BLOSUM45 / etc. — the spec hardcodes PAM250.** A future change can lift the matrix behind a `ScoringScheme` interface when justified by a second consumer.
