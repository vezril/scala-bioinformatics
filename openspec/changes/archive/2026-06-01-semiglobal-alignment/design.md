## Context

The project implements a family of pairwise alignment algorithms in
`bio.algorithms.analysis` and `bio.algorithms.protein` (global, local, fitting,
overlap, affine-gap). Each follows the same shape: a validated domain bundle, a result
type, and a pure/total `align` function whose signature exposes no `Either` because a
valid input always has a defined optimal alignment. By established codebase precedent
(`FittingAlignment`, `OverlapAlignment`, `GlobalAlignment`), the alignment-family
algorithms use an **imperative dynamic-programming kernel** internally
(`var`/`while`/`Array`) for the O(m·n) score table — the one sanctioned exception to
the "no mutable state" rule; the function signature remains pure and total.

Rosalind SMGB ("Semiglobal Alignment") asks for the best alignment of all of `s`
against all of `t` in which gaps at the **leading or trailing ends of either string
are free** (do not contribute to the score). Scoring is match `+1`, substitution `-1`,
gap `-1`. Input is two DNA strings (each ≤ 10 kbp) in FASTA; output is the optimal
score followed by the two aligned strings.

This is the **symmetric** free-end-gap regime. Contrast the nearest sibling,
overlap alignment (OAP), which is **one-sided**: a suffix of `s` against a prefix of
`t`. In semiglobal alignment, *both* strings are fully present in the alignment; only
the gap runs at the very start and very end are free.

Canonical sample: `s = CAGCACTTGGATTCTCGG`, `t = CAGCGTGG` → score `4`, e.g.
`CAGCA-CTTGGATTCTCGG` / `---CAGCGTGG--------`.

## Goals / Non-Goals

**Goals:**
- A validated `SemiglobalAlignmentProblem` bundle (two `DnaString`s, each ≤ 10000 bp),
  first-failure-wins (`s` then `t`), constructed only via a smart constructor.
- A pure, total `SemiglobalAlignment.align` computing the optimal semiglobal score and
  one optimal pair of augmented strings.
- A `SemiglobalAlignment` result type with a `format` rendering score + two strings on
  separate lines.
- A `SMGBProb` IO runner reading two FASTA records, printing the formatted result or a
  descriptive error.

**Non-Goals:**
- No affine gaps; no general global/local/fitting/overlap alignment (already covered).
- No enumeration of all co-optimal alignments — Rosalind accepts any one optimum, so a
  single deterministic traceback suffices.
- No changes to existing alignment code or shared types.

## Decisions

### Decision: Free end gaps on both strings via zero borders + max over last row/column

With `s` on the rows (length `m`) and `t` on the columns (length `n`):

- **Initialization** `dp(i)(0) = 0` and `dp(0)(j) = 0` for all `i, j` — a *leading*
  gap run in either string is free (consuming a prefix of one string against gaps in
  the other costs nothing).
- **Recurrence (interior)**
  `dp(i)(j) = max( dp(i-1)(j-1) + matchScore(s[i-1], t[j-1]), dp(i-1)(j) - 1, dp(i)(j-1) - 1 )`
  with `matchScore = +1` on equality, `-1` otherwise; `gap = -1`.
- **Answer** = the maximum cell over the **last row** `dp(m)(j)` *and* the **last
  column** `dp(i)(n)` — a *trailing* gap run in either string is free, so the scored
  path may end anywhere along the bottom or right edge, with the rest of the corner
  reached by free trailing gaps. The best cell is chosen deterministically (scan the
  last row left→right then the last column top→bottom, keeping the first strict
  maximum).

**Alternative considered**: penalizing end gaps (ordinary global / Needleman–Wunsch).
Rejected — that is the GLOB problem, already implemented; the zero borders and
edge-maximum are exactly what make the end gaps free.

### Decision: Traceback emits both strings fully, with free leading/trailing gap runs

Build the alignment right-to-left into two reversed `StringBuilder`s:

1. **Trailing free gaps** from the corner `(m, n)` to the best cell `(bi, bj)`:
   - if `bi == m` and `bj < n`: emit `t[n-1 … bj]` against `-` in `s` (free);
   - if `bj == n` and `bi < m`: emit `s[m-1 … bi]` against `-` in `t` (free).
   (Only one of these runs; neither runs when the best cell is the corner.)
2. **Core scored traceback** from `(bi, bj)` while `i > 0 && j > 0`, tie-break
   **diagonal > up > left** (matching `OverlapAlignment`/`FittingAlignment`):
   diagonal emits `s[i-1]`/`t[j-1]`; up emits `s[i-1]`/`-`; left emits `-`/`t[j-1]`.
3. **Leading free gaps** once an edge is hit: while `j > 0` emit `-`/`t[j-1]`; then
   while `i > 0` emit `s[i-1]`/`-` (both free).

Reverse both builders at the end. Because every character of `s` and `t` is emitted
(as a match/substitution or against a free gap), the augmented strings, with gap
symbols removed, reproduce `s` and `t` exactly.

### Decision: Domain & result types mirror the overlap-alignment siblings

- `SemiglobalAlignmentProblemError`: `sealed trait` with
  `final case class STooLong(length: Int, max: Int)` and
  `final case class TTooLong(length: Int, max: Int)`.
- `SemiglobalAlignmentProblem`: `sealed abstract case class SemiglobalAlignmentProblem(s: DnaString, t: DnaString)`;
  `from(s, t)` first-failure-wins `STooLong` (s.length > 10000) then `TTooLong`; empty
  strings accepted; constructed via anonymous subclass; no public `apply`/`copy`.
- `SemiglobalAlignment`: `final case class SemiglobalAlignment(score: Int, augmentedS: String, augmentedT: String)`
  with `def format: String = s"$score\n$augmentedS\n$augmentedT"`.

### Decision: Runner reads FASTA via the existing reader

`SMGBProb.solve(): IO[Unit]` reads `smgb_data.txt` with `FastaFileReader.read`, takes
the first two records as `s` and `t`, validates into `SemiglobalAlignmentProblem`, runs
`align`, and prints `result.format`. On any FASTA/validation error (or fewer than two
records) it prints a descriptive message rather than throwing — mirroring `OAPProb`.

## Risks / Trade-offs

- **[Multiple co-optimal alignments]** → Rosalind accepts any optimum. Tests assert
  *invariants* (score equals the known optimum; gap-stripped `augmentedS` equals `s`
  and `augmentedT` equals `t`; equal augmented lengths; no column with gaps in both
  rows; recomputed score matches) rather than a fixed string, plus an exact score check
  against the canonical sample.
- **[O(m·n) memory for 10 kbp inputs]** → a 10000×10000 `Int` table is large but
  within the precedent set by the other alignment algorithms here; SMGB's Rosalind
  datasets are well under the worst case. No change to the established approach.
- **[Imperative kernel]** → confined to `align`'s body; the public signature is pure
  and total, consistent with every other alignment algorithm in the project.
