## Context

Specs 40 (EDIT) and 41 (EDTA) introduced Levenshtein DP and traceback for protein strings: unit-cost substitutions, unit-cost gaps, minimisation. Spec 42 (GLOB) is the next conceptual layer — *biological* scoring via the BLOSUM62 amino-acid substitution matrix combined with a *linear gap penalty* of −5, maximised rather than minimised. This is Needleman-Wunsch in its canonical form and is the foundation for every downstream alignment variant on the Rosalind track (LCSV, SMGB, GAFF, GCON, LOCA, EDTA-style traceback over scored alignments).

BLOSUM62 is a fixed 20 × 20 integer matrix (symmetric, diagonal values represent self-substitution scores). Its values are well-known and standard — sourced from the NCBI distribution.

## Goals / Non-Goals

**Goals:**
- A total `Blosum62.score(a: AminoAcid, b: AminoAcid): Int` function backed by the canonical NCBI BLOSUM62 matrix, symmetric for any pair of the 20 amino acids.
- Validated `GlobalAlignmentScoreProblem(left, right)` smart constructor with first-failure-wins 1000-aa cap pattern (mirrors EDIT / EDTA).
- Algorithm object `GlobalAlignmentScore.compute(problem): Int` returning the maximum global alignment score using BLOSUM62 + linear gap penalty −5 via classical Needleman-Wunsch `O(m · n)` DP.
- Empty inputs accepted: empty/empty ⇒ 0; empty + length-n ⇒ −5n; vice versa.
- TDD: Red → Green → Refactor with `sbt test` green at the end.

**Non-Goals:**
- No traceback / alignment reconstruction (Rosalind spec 43+ territory — separate change). Only the integer maximum score is returned.
- No affine-gap penalty (Rosalind spec 47+); no scoring matrix other than BLOSUM62; no local alignment (Smith-Waterman).
- No parameterisation of the scoring matrix or the gap penalty in the algorithm's public signature. They are hardcoded constants of this capability (matching Rosalind's spec wording). A future refactor can lift them into a `ScoringScheme` ADT when justified by a second consumer.
- No reuse of `EditDistance.compute`. The recurrence direction (max vs min), per-step cost (BLOSUM62 lookup vs unit cost), and gap weight (−5 vs −1) all differ. Separate DP loop.
- No streaming / rolling-array optimisation — the full `(m+1) × (n+1)` table is fine at the 1000-aa cap.

## Decisions

**1. Encode BLOSUM62 as a dense `Array[Array[Int]]` indexed by amino-acid ordinal, exposed via `Blosum62.score(a, b)`.**
- Rationale: `O(1)` lookup with no `Map[(AminoAcid, AminoAcid), Int]` boxing overhead. The 20 amino acids have a fixed ordinal via `AminoAcid.all` (canonical order used codebase-wide), so we precompute an index `ordinal: AminoAcid => Int` once at object init. The matrix has 400 cells of `Int` — ~1.6 KB. Constant-time `score` keeps the inner DP loop tight.
- Alternative considered: `Map[(AminoAcid, AminoAcid), Int]`. Rejected for the hot-path call from inside the inner loop; we'd pay tuple allocation per lookup at the 10⁶-cell cap, which is wasteful.

**2. BLOSUM62 source values: NCBI canonical 20 × 20 matrix.**
- Rationale: standard reference. We use the same matrix Rosalind uses. The matrix is symmetric (`score(a, b) == score(b, a)`); we will store both halves for direct lookup rather than canonicalising on every call.

**3. Hardcode the linear gap penalty as a `private val Gap = -5` inside `GlobalAlignmentScore`.**
- Rationale: matches the Rosalind spec wording verbatim and keeps the public signature minimal. Lifting it later behind a `ScoringScheme(gap: Int, score: (AminoAcid, AminoAcid) => Int)` is a straightforward refactor when a second consumer arrives.

**4. Classical `O(m · n)` Needleman-Wunsch DP, maximising.**
- Rationale: textbook. Build `dp(i)(j) = max alignment score of left[0..i)` and `right[0..j)`:
  - `dp(0)(0) = 0`;
  - `dp(i)(0) = -5 * i` (all gaps on right);
  - `dp(0)(j) = -5 * j` (all gaps on left);
  - `dp(i)(j) = max(dp(i-1)(j-1) + BLOSUM62(left(i-1), right(j-1)), dp(i-1)(j) + (-5), dp(i)(j-1) + (-5))`.
- Result: `dp(m)(n)`. Returns `Int` (signed — scores can be negative).

**5. Map raw `Char` from `ProteinString.value` to `AminoAcid` once per cell access.**
- Rationale: simpler than re-parsing each cell. Build a `private val charToAa: Map[Char, AminoAcid]` (or `Array[AminoAcid]` indexed by `code - 'A'`) once at `Blosum62` init. Inner-loop lookup is `O(1)`.

**6. Separate `GlobalAlignmentScoreProblem` bundle (vs reusing `EditDistanceProblem`).**
- Rationale: consistent with every Rosalind problem in the codebase. Algorithm signature is self-documenting; the bundle is the obvious single source of truth for that capability. Mirrors the LCSM/LCSQ/SSEQ/EDIT/EDTA separation even when input shapes overlap.

**7. Place under `bio.{domain,algorithms}.protein`.**
- Rationale: the inputs and the BLOSUM62 matrix are protein-specific. Matches `EditDistance`, `EditDistanceAlignment`, `RnaTranslation`.

## Risks / Trade-offs

- **Returning `Int` (signed) when the alignment score may be negative.** → Documented in Scaladoc. At the 1000-aa cap with BLOSUM62 values bounded in [−4, 11] and gap penalty −5, the score lies in [−5000, 11000], well within `Int` range.
- **No traceback returned.** → Rosalind GLOB asks only for the integer score; the DP table is built internally but not exposed. Future refactor can expose `computeWithTable` for downstream alignment-reconstruction features without breaking the public `compute` API.
- **Code duplication of the DP loop relative to `EditDistance`.** → Acceptable: ~15 lines, with three differences (max vs min, BLOSUM62 lookup vs unit cost, gap penalty −5 vs −1). Generalising prematurely (introducing a `ScoringScheme` parameter shared by both) would couple two simple algorithms before a clear shared interface exists. Defer.
- **BLOSUM62 matrix correctness is critical.** → Mitigated by spec scenarios covering well-known reference values (`score(A, A) == 4`, `score(W, W) == 11`, `score(C, C) == 9`, `score(A, R) == -1`, plus symmetry checks).
