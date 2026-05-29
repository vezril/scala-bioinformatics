## Context

Rosalind GAFF ("Global Alignment with Scoring Matrix and Affine Gap
Penalty") is a *global* protein alignment (both strings consumed end to end)
scored under BLOSUM62, but with an **affine** gap penalty: a gap of length
`L` costs `a + b·(L − 1)`, where `a = 11` is the gap-opening penalty (charged
once for the first gap symbol) and `b = 1` is the gap-extension penalty
(charged for each subsequent symbol). GAFF generalises the two gap models we
already have:

- **GLOB** (spec 42) — *linear* gap, `−5` per symbol, single-table
  Needleman-Wunsch returning an `Int`.
- **GCON** (spec 51) — *constant* gap, flat `−5` per gap, three-state
  (Gotoh) DP returning an `Int`. Constant gap = affine with extension `b = 0`.

Affine gap (`a > 0`, `b > 0`, `a ≥ b`) is exactly the three-state machinery
GCON already established (`M`/`X`/`Y` tables), with `extend` set to `b`
instead of `0`. The new wrinkle is the **output**: GAFF must return *one
optimal alignment* — two augmented strings with `-` gap symbols — not merely
the score. That requires keeping the full DP tables and a **traceback**, just
like EDTA (spec 41, `EditDistanceAlignment`) does for the single-table
Levenshtein DP. GAFF is therefore the intersection of GCON's three-state DP
and EDTA's traceback-to-augmented-strings.

The GAFF cap is **100 aa** per side (smaller than GLOB/GCON's 1000 aa).

## Goals / Non-Goals

**Goals:**

- Provide a validated `AffineGapAlignmentProblem` input bundle (sealed
  abstract case class, smart constructor, 100-aa caps) consistent with
  `GlobalAlignmentScoreProblem` / `ConstantGapAlignmentScoreProblem`.
- Provide an `AffineGapAlignment` *result ADT* (plain `final case class`)
  carrying the `score` and the two augmented strings, mirroring
  `EditAlignment` (EDTA) and `LocalAlignment` (LOCA).
- Implement the three-state `O(m · n)` affine-gap DP under BLOSUM62
  (`a = 11`, `b = 1`) **with traceback**, returning the maximum global
  alignment score and one optimal alignment.
- Reproduce the canonical Rosalind GAFF sample: `PRTEINS` / `PRTWPSEIN` →
  score `8`, with a valid optimal alignment.

**Non-Goals:**

- Enumerating *all* optimal alignments (only one is required).
- A configurable scoring matrix or gap constants — all fixed by the Rosalind
  spec (BLOSUM62, `a = 11`, `b = 1`).
- Reusing GCON's algorithm: GCON returns only an `Int` (no tables retained,
  no traceback). GAFF keeps its own self-contained three-state DP plus
  traceback, exactly as EDTA keeps its own DP rather than reusing
  `EditDistance.compute`.

## Decisions

### 1. Subdomain `protein`, names mirror LOCA's "result-ADT" family

GLOB and GCON return a bare `Int` and are named `…Score`. GAFF returns a
*structured result* (score + two strings), so it follows the LOCA/EDTA
naming family instead — a result ADT named for the alignment, living
alongside an identically named algorithm object in a different package
(exactly as `bio.domain.protein.LocalAlignment` coexists with
`bio.algorithms.protein.LocalAlignment`):

- `bio.domain.protein.AffineGapAlignmentProblem` (input bundle);
- `bio.domain.protein.AffineGapAlignmentProblemError` (`LeftTooLong` /
  `RightTooLong`);
- `bio.domain.protein.AffineGapAlignment` (result ADT: `score`,
  `augmentedLeft`, `augmentedRight`);
- `bio.algorithms.protein.AffineGapAlignment` with
  `compute(problem): AffineGapAlignment`.

`compute` is chosen as the method name for consistency with the
score-returning alignment family (`GlobalAlignmentScore.compute`,
`ConstantGapAlignmentScore.compute`, `LocalAlignment.compute`).

### 2. Input bundle fields `left` / `right`, 100-aa caps

Caps, first-failure-wins (matching GLOB/GCON exactly, but at 100 not 1000):

1. `left.value.length <= 100`, else `LeftTooLong(length, 100)`.
2. `right.value.length <= 100`, else `RightTooLong(length, 100)`.

Empty `left` and/or empty `right` are accepted (`ProteinString` permits the
empty string).

### 3. Result ADT is a plain `final case class`

`AffineGapAlignment(score: Int, augmentedLeft: String, augmentedRight: String)`
is a plain `final case class` (free `apply`/`copy`/equality) — it is a
result value with no cross-field invariant beyond what the producing
algorithm guarantees, exactly like `EditAlignment` and `LocalAlignment`. The
augmented strings are plain `String` (not `ProteinString`) because they
contain the `-` gap symbol, which is not a valid amino-acid code.

Algorithm-guaranteed invariants (documented on the ADT, not enforced by it):

- `augmentedLeft.length == augmentedRight.length`;
- no column has `-` in both rows;
- stripping `-` from `augmentedLeft` recovers `problem.left.value`; likewise
  for `augmentedRight` / `problem.right.value`;
- the affine-scored value of the alignment equals `score`.

### 4. Three-state (Gotoh) affine-gap DP

Build three `(m + 1) × (n + 1)` integer tables over `i ∈ [0, m]` (left `s`)
and `j ∈ [0, n]` (right `t`); each cell holds the best score of an alignment
of the prefixes `s[0..i)` and `t[0..j)` that **ends** in a given state:

- `M(i)(j)` — ends in a match/substitution column;
- `X(i)(j)` — ends in a gap **in `t`** (a run of `s` symbols opposite `-`,
  i.e. deletions);
- `Y(i)(j)` — ends in a gap **in `s`** (a run of `t` symbols opposite `-`,
  i.e. insertions).

Let `a = 11` (open) and `b = 1` (extend). Recurrences (maximising):

```
M(i)(j) = Blosum62.score(s_i, t_j) + max(M(i-1)(j-1), X(i-1)(j-1), Y(i-1)(j-1))
X(i)(j) = max( M(i-1)(j) - a,   // open a new gap-in-t from a match state
               X(i-1)(j) - b,   // extend the current gap-in-t
               Y(i-1)(j) - a )  // switch from gap-in-s to a new gap-in-t
Y(i)(j) = max( M(i)(j-1) - a,   // open a new gap-in-s from a match state
               Y(i)(j-1) - b,   // extend the current gap-in-s
               X(i)(j-1) - a )  // switch from gap-in-t to a new gap-in-s
```

The only difference from GCON is that gap *extension* now costs `b = 1`
rather than `0`, and the gap-open / direction-switch cost is `a = 11` rather
than `5`. (GCON is the special case `a = b = 5`… no: GCON is `a = 5, b = 0`.)

### 5. Boundary conditions and the `-∞` sentinel

States structurally impossible at a boundary are seeded with `NegInf =
Int.MinValue / 4` (headroom so `NegInf - a` cannot underflow):

- `M(0)(0) = 0`; `M(i)(0) = NegInf` (i ≥ 1); `M(0)(j) = NegInf` (j ≥ 1) —
  a match column requires one symbol from each side.
- `X(0)(0) = NegInf`; `X(i)(0) = -(a + b·(i-1))` for `i ≥ 1` — aligning
  `s[0..i)` to an empty `t` is a *single* gap-in-t of length `i`;
  `X(0)(j) = NegInf`.
- `Y(0)(0) = NegInf`; `Y(0)(j) = -(a + b·(j-1))` for `j ≥ 1` — symmetric;
  `Y(i)(0) = NegInf`.

The score is `max(M(m)(n), X(m)(n), Y(m)(n))`.

### 6. Traceback to augmented strings

Keep all three full tables. Start at `(m, n)` in whichever state attains the
score, then walk back to `(0, 0)`, emitting one column per step into two
reversed `StringBuilder`s (reverse once at the end, as EDTA does):

- In state `M` at `(i, j)`: emit `(s[i-1], t[j-1])`; step to `(i-1, j-1)`;
  next state = the argmax predecessor among `M/X/Y` at `(i-1, j-1)`.
- In state `X` at `(i, j)`: emit `(s[i-1], '-')`; step to `(i-1, j)`; next
  state determined by which term realised `X(i)(j)` (`M-a` → `M`, `X-b` →
  `X`, `Y-a` → `Y`).
- In state `Y` at `(i, j)`: emit `('-', t[j-1])`; step to `(i, j-1)`; next
  state determined analogously (`M-a` → `M`, `Y-b` → `Y`, `X-a` → `X`).

At the borders the move is forced (`i = 0` ⇒ stay in `Y` consuming `t`;
`j = 0` ⇒ stay in `X` consuming `s`).

**Tie-break order.** When several predecessors tie, prefer in this order:
**match/diagonal → extend (stay in the current gap state) → open/switch**.
Preferring to *stay* in a gap state keeps a single contiguous gap contiguous
(an affine model favours one long gap over several short ones), and
preferring the diagonal on ties pushes gaps toward one side. This
deterministic order reproduces the canonical published sample alignment
`PRT---EINS` / `PRTWPSEIN-`. Rosalind accepts *any* optimal alignment, so the
hard contract is the **score** plus the **structural validity** of the
returned alignment (equal length, no double gaps, gap-strip round-trips to
the inputs, recomputed affine score equals `score`); the exact published
strings are pinned only for the canonical sample, where the tie-break above
yields them.

### 7. Score range and `Int` safety

Per matched pair BLOSUM62 contributes at most `+11`; gaps only subtract. With
`m, n ≤ 100` the score lies well within `Int` range. `NegInf =
Int.MinValue / 4` leaves ample headroom for the single `+ score` or `- a`
applied per cell.

## Risks / Trade-offs

- **[Three tables + traceback vs. score-only]** → Memory is
  `3 · (m+1)(n+1)` ints (~120 KB at the 100² cap) plus two `StringBuilder`s.
  Trivial at this cap; full tables are required for traceback anyway.

- **[Tie-break must reproduce the published alignment]** → The canonical
  sample pins the exact strings, so the documented tie-break order
  (match → extend → open/switch) is load-bearing for that one scenario. If
  TDD reveals a different order reproduces the published alignment, the
  tie-break is adjusted; the score and structural-validity scenarios are
  tie-break-independent and remain the primary contract.

- **[Sentinel arithmetic]** → Using `Int.MinValue` directly would overflow on
  `NegInf - a`. `Int.MinValue / 4` is deliberate and documented; empty/short
  inputs exercise the boundary states that read the sentinel.

- **[Gap-direction switch costs a full open]** → Charging `-a` (not `-b`) on
  a `Y → X` / `X → Y` transition is correct: two adjacent gaps of opposite
  direction are two *distinct* gaps, each opened. Covered by the canonical
  sample (which has two gaps) and the recomputed-score invariant.

## Migration Plan

Purely additive; no migration. New files under `bio.domain.protein`,
`bio.algorithms.protein`, `bio.problems`, plus a one-line wiring change in
`bio/Main.scala`. Rollback = delete the new files and the wiring line.

## Open Questions

None.
