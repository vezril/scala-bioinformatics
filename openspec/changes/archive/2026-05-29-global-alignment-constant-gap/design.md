## Context

Rosalind GCON ("Global Alignment with Constant Gap Penalty") is a *global*
protein alignment (both strings consumed end to end) scored under BLOSUM62,
but with a **constant** gap penalty: a gap — a maximal run of contiguous
insertions *or* contiguous deletions — costs a flat `5`, independent of how
many symbols it spans. Inserting or deleting 1 000 contiguous symbols is
penalised exactly as a single symbol.

The framework already has GLOB (spec 42), the same global alignment under
BLOSUM62 but with a *linear* gap penalty (`-5` per gap symbol), implemented
as a single-table Needleman-Wunsch DP returning an `Int`
(`GlobalAlignmentScore.compute`). GCON cannot reuse that single table: a
linear model needs only "best score so far," but a length-independent gap
model must distinguish *whether the previous column was already part of a
gap* (so an extension is free) from *whether a gap is being opened* (so it
costs `5`). That requires tracking the alignment's terminal state.

## Goals / Non-Goals

**Goals:**

- Provide a validated `ConstantGapAlignmentScoreProblem` input bundle
  (sealed abstract case class, smart constructor, 1 000-aa caps) consistent
  with `GlobalAlignmentScoreProblem`.
- Implement the three-state `O(m · n)` constant-gap DP under BLOSUM62 and
  return the maximum global alignment score as an `Int`.
- Reproduce the canonical Rosalind GCON sample: `PLEASANTLY` / `MEANLY` →
  `13`.

**Non-Goals:**

- Returning the alignment itself (augmented strings / traceback) — only the
  maximum score is required, mirroring GLOB.
- Affine gaps (separate open + per-symbol extend penalties). Constant gap
  is the special case extend = 0; the same three-state machinery
  generalises to affine later, but that is out of scope here.
- A configurable scoring matrix or gap constant — both are fixed by the
  Rosalind spec (BLOSUM62, gap = 5).

## Decisions

### 1. Subdomain `protein`, name `ConstantGapAlignmentScore`

Place the algorithm in `bio.algorithms.protein` and the domain types in
`bio.domain.protein`, exactly alongside `GlobalAlignmentScore` (GLOB) and
`LocalAlignment` (LOCA). The names mirror GLOB's `…Score` convention since,
like GLOB, the result is a single `Int` score with **no output ADT**:

- `bio.domain.protein.ConstantGapAlignmentScoreProblem`
- `bio.domain.protein.ConstantGapAlignmentScoreProblemError`
- `bio.algorithms.protein.ConstantGapAlignmentScore` (`compute(...): Int`).

### 2. Input bundle fields `left` / `right`

The two inputs are **symmetric** (both fully aligned; BLOSUM62 is
symmetric), so the score is invariant under swapping them — name them
`left` (`s`) and `right` (`t`), matching `GlobalAlignmentScoreProblem`.

Caps, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, 1000)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, 1000)`.

Empty `left` and/or empty `right` are accepted (`ProteinString` permits the
empty string).

### 3. Three-state (Gotoh-style) constant-gap DP

Build three `(m + 1) × (n + 1)` integer tables over `i ∈ [0, m]` (left
`s`) and `j ∈ [0, n]` (right `t`), where each cell is the best score of an
alignment of the prefixes `s[0..i)` and `t[0..j)` that **ends** in a given
terminal state:

- `M(i)(j)` — ends in a match/substitution column (`s[i-1]` aligned to
  `t[j-1]`);
- `X(i)(j)` — ends in a gap **in `t`** (a run of `s` symbols opposite `-`,
  i.e. deletions);
- `Y(i)(j)` — ends in a gap **in `s`** (a run of `t` symbols opposite `-`,
  i.e. insertions).

Let `c = 5` be the constant gap penalty. Recurrences (maximising):

```
M(i)(j) = Blosum62.score(s_i, t_j) + max(M(i-1)(j-1), X(i-1)(j-1), Y(i-1)(j-1))
X(i)(j) = max( M(i-1)(j) - c,   // open a new gap-in-t from a match state
               X(i-1)(j),       // extend the current gap-in-t (free)
               Y(i-1)(j) - c )  // switch from gap-in-s to a new gap-in-t
Y(i)(j) = max( M(i)(j-1) - c,   // open a new gap-in-s from a match state
               Y(i)(j-1),       // extend the current gap-in-s (free)
               X(i)(j-1) - c )  // switch from gap-in-t to a new gap-in-s
```

The crux versus GLOB: gap *extension* (`X(i-1)(j)`, `Y(i)(j-1)`) carries
**no** penalty — the `- c` is paid only when a gap is *opened* (from `M`)
or when the gap direction *switches* (the prior run has ended and a new,
distinct gap begins).

### 4. Boundary conditions and the `-∞` sentinel

States that are structurally impossible at a boundary are seeded with a
large negative sentinel `NegInf` (chosen as `Int.MinValue / 4` so that
adding a score or `-c` cannot underflow):

- `M(0)(0) = 0`; `M(i)(0) = NegInf` (i ≥ 1); `M(0)(j) = NegInf` (j ≥ 1) —
  a match column requires consuming one symbol from each side.
- `X(0)(0) = NegInf`; `X(i)(0) = -c` for `i ≥ 1` — aligning `s[0..i)` to an
  empty `t` is a *single* gap-in-t, hence `-c` regardless of `i`;
  `X(0)(j) = NegInf` (j ≥ 0, j ≥ 1 needs i ≥ 1).
- `Y(0)(0) = NegInf`; `Y(0)(j) = -c` for `j ≥ 1` — symmetric to `X`;
  `Y(i)(0) = NegInf`.

The answer is `max(M(m)(n), X(m)(n), Y(m)(n))`.

### 5. Score range and `Int` safety

Per matched pair BLOSUM62 contributes at most `+11`; gaps only ever
subtract. With `m, n ≤ 1000` the score lies well within `Int` range
(roughly `[-something small, 11 000]`; the constant-gap model makes large
negatives *less* extreme than GLOB's linear `-5000`). The `NegInf =
Int.MinValue / 4` sentinel leaves ample headroom for the single `+ score`
or `- c` applied per cell.

### 6. Empty-input behaviour (a constant-gap signature)

- both empty (`m = n = 0`): `M(0)(0) = 0` ⇒ score `0`.
- one side empty (`m = 0, n > 0` or vice versa): the whole non-empty side
  is a *single* gap ⇒ score `-5` (contrast GLOB's `-5n`). This is the
  clearest behavioural fingerprint of the constant-gap model and is pinned
  as a scenario.

## Risks / Trade-offs

- **[Three tables vs. one]** → Memory is `3 · (m+1)(n+1)` ints (~12 MB at
  the 1 000² cap) versus GLOB's single table. Acceptable; only the final
  corner is needed, so a row-rolling variant could cut this to `O(n)`, but
  the full tables are kept for parity with GLOB's readable style and
  because 12 MB is immaterial. Documented, not optimised.

- **[Sentinel arithmetic]** → Using `Int.MinValue` directly would overflow
  on `NegInf - c`. The `Int.MinValue / 4` choice is deliberate and
  documented; tests covering empty/short inputs exercise the boundary
  states that read the sentinel.

- **[Gap-direction switch cost]** → Charging `- c` on a `Y → X` (or
  `X → Y`) transition is essential: two adjacent gaps of opposite
  direction are two *distinct* gaps, so two penalties. Omitting it would
  under-count. Covered implicitly by the canonical sample (which has
  multiple gaps) and the `Int` reference values.

## Migration Plan

Purely additive; no migration. New files under `bio.domain.protein`,
`bio.algorithms.protein`, `bio.problems`, plus a one-line wiring change in
`bio/Main.scala`. Rollback = delete the new files and the wiring line.

## Open Questions

None.
