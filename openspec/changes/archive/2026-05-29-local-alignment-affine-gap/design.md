## Context

Rosalind LAFF asks for the maximum *local* alignment score of two protein
strings under BLOSUM62 with an affine gap penalty (gap-open 11, gap-extend 1),
plus one optimal pair of aligned substrings. The codebase already contains the
two ingredients separately:

- **LOCA** (`bio.algorithms.protein.LocalAlignment`) — Smith-Waterman *local*
  alignment with a *linear* gap (single DP table, 0-clamp, traceback emitting
  contiguous substrings of the inputs). Result ADT
  `LocalAlignment(score, leftSubstring, rightSubstring)`.
- **GAFF** (`bio.algorithms.protein.AffineGapAlignment`) — *global* alignment
  with an *affine* gap via a three-state (Gotoh) DP `M`/`X`/`Y` and traceback
  to augmented strings (with `-` gap symbols).

LAFF is the intersection: a three-state affine DP made *local* by 0-clamping the
match state and recovering substrings (not gap-augmented strings). The Rosalind
cap is 10,000 amino acids per string.

## Goals / Non-Goals

**Goals:**
- A validated input bundle `LocalAffineAlignmentProblem` (two `ProteinString`s,
  each ≤ 10,000 aa) with a first-failure-wins smart constructor.
- A result ADT `LocalAffineAlignment(score, leftSubstring, rightSubstring)`
  mirroring LOCA's output shape (plain `String` substrings — contiguous regions
  of the inputs, no gap symbols).
- A `compute` algorithm producing the score and one optimal substring pair,
  reproducing `PLEASANTLY` / `MEANLY` → `12` / `LEAS` / `MEAN`.
- A runner `LAFFProb` wired into `Main`.

**Non-Goals:**
- Linear-space (Hirschberg) optimisation — out of scope (see risk below).
- Enumerating *all* optimal alignments — the spec permits any one.
- Returning gap-augmented strings — LAFF reports the aligned substrings only.

## Decisions

### Naming: `LocalAffineAlignment*`, LOCA-family output shape
Follow the established LOCA naming and the GAFF/LOCA convention of a result ADT
named identically to the algorithm object but living in `bio.domain.protein`,
while the algorithm lives in `bio.algorithms.protein`. So:
- `bio.domain.protein.LocalAffineAlignmentProblem` (input bundle, sealed
  abstract case class),
- `bio.domain.protein.LocalAffineAlignmentProblemError` (sealed trait +
  `LeftTooLong`/`RightTooLong`),
- `bio.domain.protein.LocalAffineAlignment` (plain `final case class` result:
  `score: Int`, `leftSubstring: String`, `rightSubstring: String`),
- `bio.algorithms.protein.LocalAffineAlignment` (`compute(problem): result`).

*Alternative considered:* reusing GAFF's augmented-string output. Rejected —
LAFF reports substrings of the inputs (like LOCA), not gap-augmented rows.

### Local affine three-state DP (Gotoh + Smith-Waterman 0-clamp)
Three `(m+1) × (n+1)` `Int` tables over prefixes `s[0..i)`, `t[0..j)`:
- `M(i)(j)` — best *local* score of an alignment ending in a match/substitution
  column at `(i, j)`, **clamped at 0** (a local alignment may always restart);
- `X(i)(j)` — best score ending in a gap in `t` (run of `s` opposite `-`);
- `Y(i)(j)` — best score ending in a gap in `s` (`-` opposite run of `t`).

With `a = 11` (open) and `b = 1` (extend):
```
M(i)(j) = max(0, Blosum62.score(s_i, t_j) + max(M(i-1,j-1), X(i-1,j-1), Y(i-1,j-1)))
X(i)(j) = max(M(i-1,j) - a, X(i-1,j) - b, Y(i-1,j) - a)
Y(i)(j) = max(M(i,j-1) - a, Y(i,j-1) - b, X(i,j-1) - a)
```
Only the match state is 0-clamped; the gap states `X`/`Y` are *not* clamped,
because a local alignment never begins or ends with a gap, and an internal gap
must carry its accrued (negative) penalty until the next match either rescues it
or the surrounding `M` restarts at 0.

**Boundaries** use `NegInf = Int.MinValue / 4` (headroom so `NegInf - a` cannot
underflow). Unlike GAFF, the LAFF boundaries are *not* the cumulative gap costs:
local alignments cannot start with a gap, so
- `M(i)(0) = M(0)(j) = M(0)(0) = 0`;
- `X(i)(0) = NegInf` for all `i`, `X(0)(j) = NegInf`;
- `Y(0)(j) = NegInf` for all `j`, `Y(i)(0) = NegInf`.

The answer is `max` over **all** `M(i)(j)` cells (Smith-Waterman tracks the
running maximum, not just the corner). Track `maxScore`, `maxI`, `maxJ`.

*Alternative considered:* a single-table local affine DP. Rejected — affine
penalties require the three-state decomposition to distinguish open vs. extend.

### Traceback to substrings
From `(maxI, maxJ)` in state `M`, walk back accumulating characters into two
reversed builders, **stopping when a 0-clamped match cell is reached**
(`state == M && M(bi)(bj) == 0`). Emit `s` char in `M` and `X` steps; emit `t`
char in `M` and `Y` steps (gap columns contribute to only one substring, so the
substrings remain contiguous regions of the inputs). Reverse once at the end.
Tie-break mirrors GAFF (prefer match/diagonal, then extend, then open/switch),
which yields the canonical `LEAS` / `MEAN`.

### 10,000-aa cap and empty inputs
`MaxLength = 10000` per the Rosalind spec; `from` is first-failure-wins
(`LeftTooLong` before `RightTooLong`). An empty `s` or `t` yields
`LocalAffineAlignment(0, "", "")` (no positive-scoring local alignment exists).

## Risks / Trade-offs

- **[Memory at the cap]** Three full `Int` tables at `10000 × 10000` ≈ 3 × 400 MB
  ≈ 1.2 GB worst case. → Mitigation: accept the readable full-table design,
  consistent with the project's established choice (GCON/GAFF) of clarity over
  linear-space. Real Rosalind LAFF datasets are far below the cap; the cap is a
  validation guard, not a sizing target. Linear-space (Hirschberg) is a possible
  future optimisation but is out of scope here.
- **[Tie-break sensitivity]** "Any optimal alignment" is accepted by Rosalind,
  but the test suite pins the canonical `LEAS` / `MEAN`. → Mitigation: reuse the
  GAFF tie-break order, which already reproduces canonical published alignments;
  add an invariant test that both substrings are contiguous infixes of their
  inputs so the suite does not over-fit a single tie-break.
