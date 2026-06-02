## Context

The project already implements a family of pairwise alignment algorithms in
`bio.algorithms.analysis` and `bio.algorithms.protein` (global, local, fitting,
affine-gap). Each follows the same shape: a validated domain bundle, a result type,
and a pure/total `align` function whose signature exposes no `Either` because a valid
input always has a defined optimal alignment. By established codebase precedent
(`FittingAlignment`, `GlobalAlignment`, etc.), the alignment-family algorithms use an
**imperative dynamic-programming kernel** internally (`var`/`while`/`Array`) for the
O(m·n) score table — this is the one sanctioned exception to the "no mutable state"
rule, because the function signature remains pure and total.

Rosalind OAP ("Overlap Alignment") asks for an *overlap alignment*: the
highest-scoring alignment of a **suffix of `s`** against a **prefix of `t`**. Scoring
is match `+1`, substitution `-2`, linear gap `-2`. Input is two DNA strings (each
≤ 10 kbp) in FASTA; output is the optimal score followed by the two aligned strings.

Canonical sample: `s = CTAAGGGATTCCGGTAATTAGACAG`, `t = ATAGACCATATGTCAGTGACTGTGTAA`
→ score `1`, with one optimal alignment `ATTAGAC-AG` / `AT-AGACCAT`.

## Goals / Non-Goals

**Goals:**
- A validated `OverlapAlignmentProblem` bundle (two `DnaString`s, each ≤ 10000 bp),
  first-failure-wins (`s` then `t`), constructed only via a smart constructor.
- A pure, total `OverlapAlignment.align` computing the optimal overlap-alignment
  score and one optimal pair of augmented strings.
- An `OverlapAlignment` result type with a `format` rendering score + two strings on
  separate lines.
- An `OAPProb` IO runner reading two FASTA records, printing the formatted result or
  a descriptive error.

**Non-Goals:**
- No affine gaps, no general local/global alignment (already covered elsewhere).
- No enumeration of all co-optimal alignments — Rosalind accepts any one optimum, so
  a single deterministic traceback suffices.
- No changes to existing alignment code or shared types.

## Decisions

### Decision: Overlap = suffix-of-`s` vs prefix-of-`t`, expressed via free end gaps

With `s` on the rows (length `m`) and `t` on the columns (length `n`), the overlap
DP uses asymmetric initialization:

- `dp(i)(0) = 0` for all `i` — the alignment may **start anywhere in `s`** for free
  (the skipped prefix of `s` is not penalized), so a suffix of `s` can begin at any
  row.
- `dp(0)(j) = -2 * j` — with an empty `s` portion, consuming `j` characters of `t`'s
  prefix costs `j` gaps. (`t`'s prefix must be *consumed*, not skipped, because the
  result is a prefix of `t`.)

Recurrence (interior):
`dp(i)(j) = max( dp(i-1)(j-1) + matchScore(s[i-1], t[j-1]), dp(i-1)(j) + gap, dp(i)(j-1) + gap )`
with `matchScore = +1` on equality, `-2` otherwise; `gap = -2`.

**Answer**: the maximum over the **final row** `dp(m)(j)` for `j ∈ 0..n`. Taking the
final row forces the suffix of `s` to be consumed to the end of `s`; allowing any `j`
lets the prefix of `t` end anywhere. The argmax `j` is chosen as the **smallest** `j`
achieving the maximum, for determinism.

**Alternative considered**: scanning the final *column* instead — that would compute
a fitting/prefix-of-`s` variant, not overlap. Rejected.

### Decision: Traceback to column 0, free skip not emitted

From `(m, jStar)`, walk back while `j > 0`:
- diagonal if `dp(i)(j) == dp(i-1)(j-1) + matchScore` (emit `s[i-1]`, `t[j-1]`),
- up if `dp(i)(j) == dp(i-1)(j) + gap` (emit `s[i-1]`, `-`),
- left otherwise (`dp(i)(j) == dp(i)(j-1) + gap`; emit `-`, `t[j-1]`).

Stop when `j == 0`: the remaining skipped prefix of `s` is the free start and is
**not** emitted (the aligned suffix begins at the current row). When `i == 0` is
reached with `j > 0`, the move is forced **left** (only `t` remains). Tie-break order
is **diagonal > up > left**, matching the `FittingAlignment` precedent. Strings are
built reversed in a `StringBuilder` and reversed at the end.

### Decision: Score is always ≥ 0

Because `dp(m)(0) = 0` is always a candidate in the final-row scan, the optimal
overlap score is never negative — the empty overlap (no characters aligned) is always
available. This differs from fitting alignment, whose score can be negative. The
disjoint-alphabet edge case (`AAAA` vs `TTTT`) therefore yields score `0` with empty
augmented strings.

### Decision: Domain & result types follow the analysis-package conventions

- `OverlapAlignmentProblemError`: `sealed trait` with `final case class STooLong(length: Int, max: Int)`
  and `final case class TTooLong(length: Int, max: Int)`.
- `OverlapAlignmentProblem`: `sealed abstract case class OverlapAlignmentProblem(s: DnaString, t: DnaString)`;
  `from(s, t): Either[OverlapAlignmentProblemError, OverlapAlignmentProblem]`,
  first-failure-wins `STooLong` (s.length > 10000) then `TTooLong`; empty strings
  accepted; constructed via anonymous subclass `new OverlapAlignmentProblem(s, t) {}`.
  No public `apply`/`copy`.
- `OverlapAlignment`: `final case class OverlapAlignment(score: Int, augmentedS: String, augmentedT: String)`
  with `def format: String = s"$score\n$augmentedS\n$augmentedT"`.

### Decision: Runner reads FASTA via the existing reader

`OAPProb.solve(): IO[Unit]` reads `oap_data.txt` with `FastaFileReader.read`, takes
the first two records as `s` and `t`, validates into `OverlapAlignmentProblem`, runs
`align`, and prints `result.format`. On any FASTA/validation error (or fewer than two
records) it prints a descriptive message rather than throwing.

## Risks / Trade-offs

- **[Multiple co-optimal alignments]** → Rosalind accepts any optimum. Tests assert
  *invariants* (score equals the known optimum; gap-stripped `augmentedS` is a suffix
  of `s`; gap-stripped `augmentedT` is a prefix of `t`; recomputed alignment score
  matches; equal augmented lengths; no column with gaps in both rows) rather than a
  fixed string, plus an exact score check against the canonical sample.
- **[O(m·n) memory for 10 kbp inputs]** → a 10000×10000 `Int` table is ~400 MB, which
  is large but within the precedent set by the other alignment algorithms here; OAP's
  Rosalind datasets are well under the worst case. No change to the established
  approach.
- **[Imperative kernel]** → confined to `align`'s body; the public signature is pure
  and total, consistent with every other alignment algorithm in the project.
