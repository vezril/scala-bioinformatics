## Context

Rosalind SIMS ("Finding a Motif with Modifications") is a *fitting
alignment* problem: given a long string `s` (the text, ≤ 10 kbp) and a
short motif `t` (≤ 1 kbp), find a substring `r ⊆ s` whose alignment
against **all** of `t` maximizes the *mismatch score* — `+1` for a matched
symbol, `-1` for every mismatched, inserted, or deleted symbol.

The framework already has three alignment variants:

- **GLOB / EDTA** — *global*: align all of `s` against all of `t`
  (boundary row/col carry full gap penalties; traceback from the
  bottom-right corner).
- **LOCA** — *local* (Smith-Waterman): align the best substring of `s`
  against the best substring of `t` (0-clamp everywhere; traceback from
  the global-max cell).

Fitting alignment is the missing fourth quadrant in the standard
global/local/semi-global taxonomy: *all of one input, a substring of the
other*. It is "semi-global" — global with respect to `t`, local with
respect to `s`.

## Goals / Non-Goals

**Goals:**

- Provide a validated `FittingAlignmentProblem` input bundle (sealed
  abstract case class, smart constructor, length caps) consistent with the
  existing `*Problem` ADTs.
- Provide a plain `FittingAlignment` output ADT carrying the score and the
  two augmented strings.
- Implement the classical `O(m · n)` fitting-alignment DP + traceback
  under the +1/-1 mismatch score, returning the maximum score plus one
  optimal augmented alignment.
- Reproduce the canonical Rosalind SIMS sample **score** (5) and satisfy
  the structural invariants of a valid fitting alignment.

**Non-Goals:**

- Reproducing the *exact* augmented strings from the Rosalind sample.
  Multiple optimal alignments exist and Rosalind explicitly accepts any
  one; pinning exact strings would couple the test to a fragile tie-break
  choice (cf. the MULT decision). We pin the score + invariants instead.
- Affine / constant gap penalties, or scoring matrices (PAM/BLOSUM). SIMS
  uses a flat +1/-1 unit mismatch score.
- Reporting *all* substring start positions or multiple optimal alignments.

## Decisions

### 1. Subdomain `analysis`, name `FittingAlignment`

Place the algorithm in `bio.algorithms.analysis` and the domain types in
`bio.domain.analysis`, matching the DNA-based alignment capabilities
already there (`MultipleAlignment`, `SharedSplicedMotif`). The output ADT
and algorithm object share the name `FittingAlignment` (resolved via
import aliasing at call sites), exactly as `LocalAlignment` does for LOCA.

*Alternative considered:* the `protein` subdomain (where GLOB/LOCA/EDTA
live). Rejected — those use `ProteinString`; SIMS operates on
`DnaString`, so `analysis` (the DNA-alignment home) is the better fit.

### 2. Input bundle field names `text` / `motif`

The two inputs are **asymmetric** (unlike LCS/LCSQ): `t` must be fully
consumed; `s` contributes only a substring. Name them `text` (`s`) and
`motif` (`t`) — mirroring the `source`/`target` asymmetry of
`SplicedMotifProblem` rather than the symmetric `left`/`right` of
`SharedSplicedMotifProblem`.

Caps, first-failure-wins:

1. `text.value.length <= 10000`, else `TextTooLong(length, 10000)`.
2. `motif.value.length <= 1000`, else `MotifTooLong(length, 1000)`.

Empty `text` and/or empty `motif` are accepted (`DnaString` already
permits the empty string; both fit under its 100 000 cap).

### 3. Fitting-alignment DP recurrence

Build `dp(i)(j)` over `i ∈ [0, m]` (text `s`) and `j ∈ [0, n]` (motif
`t`), where `dp(i)(j)` is the best mismatch score of a fitting alignment
that consumes the first `j` characters of `t` and ends at character `i`
of `s`.

Boundary conditions — the crux of *fitting* alignment:

- `dp(i)(0) = 0` for **all** `i` — starting the `s`-substring anywhere is
  free (we may skip any prefix of `s` at no cost). This is the local
  freedom on the `s` side.
- `dp(0)(j) = dp(0)(j-1) - 1 = -j` — aligning an empty `s`-substring
  against the first `j` motif characters costs `j` gaps. This is the
  global constraint on the `t` side (all of `t` must be paid for).

Recurrence:

```
matchScore = if s(i-1) == t(j-1) then +1 else -1
dp(i)(j) = max(
  dp(i-1)(j-1) + matchScore,   // diagonal: match / substitution
  dp(i-1)(j)   - 1,            // up:   gap in motif (unmatched s char)
  dp(i)(j-1)   - 1             // left: gap in text  (unmatched t char)
)
```

Note there is **no 0-clamp** (that would make it local, LOCA-style) and
**no full-gap top row** for `s` (that would make it global, GLOB-style).

### 4. Answer extraction from the last *column*

Because `t` must be fully consumed but `s`-substring may end anywhere, the
optimum is the maximum over the final column:

```
maxScore = max over i ∈ [0, m] of dp(i)(n)
maxI     = the smallest i attaining maxScore
```

Choosing the **smallest** `maxI` guarantees the recovered `s`-substring
has no trailing unmatched characters (any larger `i` with the same score
would have come through a `-1` up-move, so a strictly-larger cell would
exist — contradicting the max). Traceback therefore never emits a trailing
gap-in-motif.

### 5. Traceback → augmented strings, stop at `j == 0`

From `(maxI, n)`, walk back choosing the move that produced each cell,
**tie-break diagonal > up > left**, until `j == 0`:

- **diagonal**: append `s(i-1)` to the text builder, `t(j-1)` to the motif
  builder; `i--`, `j--`.
- **up**: append `s(i-1)` to the text builder, `-` to the motif builder;
  `i--`.
- **left**: append `-` to the text builder, `t(j-1)` to the motif builder;
  `j--`.
- On the border `i == 0` (empty text remaining) the move is forced left.

Stop as soon as `j == 0`: the remaining skipped prefix of `s` is *not*
emitted (free start). Reverse both builders to recover input order. The
augmented strings are plain `String` (they contain `-`, not a valid DNA
base), as with `EditAlignment`.

Resulting guarantees:

1. `augmentedText.length == augmentedMotif.length`;
2. no column has `-` in both rows;
3. stripping `-` from `augmentedMotif` recovers the **entire** `t`;
4. stripping `-` from `augmentedText` is a **contiguous substring** of `s`;
5. the mismatch score of the augmented pair equals `score`.

### 6. Empty-input short-circuits

- `motif` empty (`n == 0`): return `FittingAlignment(0, "", "")` (the
  empty motif fits the empty substring at score 0).
- `text` empty with non-empty `motif` (`m == 0`, `n > 0`): the only
  fitting alignment is all-gaps — `FittingAlignment(-n, "-" * n, t)`.
  The general traceback's forced-left border handles this, but we note it
  explicitly as a scenario.

## Risks / Trade-offs

- **[Exact sample strings not pinned]** → The canonical scenario asserts
  `score == 5` plus the five structural invariants, not the literal
  `ACCATAAGCCCTACGTG-CCG` / `GCCGTCAGGC-TG-GTGTCCG`. This keeps the test
  robust against tie-break choices while still proving correctness
  (Rosalind accepts any optimum). A separate non-canonical scenario pins a
  tiny exact alignment where only one optimum exists.

- **[Memory at the cap]** → A full `Array[Array[Int]]` of `(m+1)·(n+1)`
  cells is `~10001 · 1001 ≈ 10^7` ints ≈ 40 MB at the Rosalind maximum.
  Acceptable on a normal JVM; traceback needs the full table so a
  row-rolling optimization is not applicable. Documented, not optimized.

- **[Score can be negative]** → Unlike LOCA (0-clamped, `score ≥ 0`), a
  fitting alignment must pay for all of `t`, so a poor match yields a
  negative score (e.g. empty text ⇒ `-n`). The output ADT and invariants
  do **not** assert `score >= 0`.

## Migration Plan

Purely additive; no migration. New files under `bio.domain.analysis`,
`bio.algorithms.analysis`, `bio.problems`, plus a one-line wiring change in
`bio/Main.scala`. Rollback = delete the new files and the wiring line.

## Open Questions

None.
