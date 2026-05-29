## Context

Rosalind KSIM ("Finding All Similar Motifs") is an *approximate string
matching* problem: given a motif `s` (≤ 5 kbp) and a genome `t` (≤ 50 kbp)
and an edit-distance budget `k` (1 ≤ k ≤ 50), report **every** substring
`t′ = t[i..j]` of `t` with edit distance `d_E(s, t′) ≤ k`. Edit distance
is the standard Levenshtein metric — insertion, deletion, and substitution
each cost 1. Each hit is encoded as a pair `(location, length)` where
`location` is the 1-based start position of `t′` in `t`.

The framework already has the two endpoints of this spectrum:

- **KMP** — *exact* substring search (zero edits, all occurrences).
- **EDIT / EDTA** — global *edit distance* between two whole strings (one
  number / one alignment, no search).
- **SIMS** — fitting alignment: the single *best* approximate placement of
  a motif (one optimum, max score).

KSIM is the missing piece: *all* approximate placements within an
edit-distance budget. Note the metric differs from SIMS — KSIM counts
**edits** (minimise; threshold `≤ k`), whereas SIMS maximises a +1/-1
mismatch score. They are related but not identical, so KSIM gets its own
DP rather than reusing the SIMS alignment.

## Goals / Non-Goals

**Goals:**

- Provide a validated `SimilarMotifsProblem` input bundle (sealed abstract
  case class, smart constructor, caps) consistent with the existing
  `*Problem` ADTs.
- Provide a plain `SimilarMotif` output ADT carrying `(location, length)`.
- Implement approximate string matching that returns **all** `(location,
  length)` pairs with `d_E(s, t′) ≤ k`, sorted by `(location, length)`.
- Reproduce the canonical Rosalind KSIM sample exactly:
  `k=2, s=ACGTAG, t=ACGGATCGGCATCGT → (1,4) (1,5) (1,6)`.
- Stay tractable at the Rosalind cap (50 kbp × 5 kbp) via banding to the
  `|len − m| ≤ k` length window.

**Non-Goals:**

- Reporting the alignments / edit scripts themselves — only the
  `(location, length)` coordinates are required.
- The +1/-1 mismatch score of SIMS — KSIM uses unit-cost edit distance.
- Overlap removal or "maximal hit" deduplication — Rosalind wants **every**
  qualifying substring, including nested ones sharing a start (the sample
  returns `(1,4) (1,5) (1,6)` — three lengths at the same location).
- Sub-quadratic specialised algorithms (suffix automata, bit-parallel
  Myers). The banded two-pass DP below is correct and fast enough.

## Decisions

### 1. Subdomain `analysis`, names `SimilarMotif` / `SimilarMotifs`

Place the algorithm in `bio.algorithms.analysis` and the domain types in
`bio.domain.analysis`, alongside the DNA string-analysis capabilities
already there (SIMS `FittingAlignment`, OSYM `IsolatedSymbols`). The output
ADT is singular `SimilarMotif` (one hit) and the algorithm object is plural
`SimilarMotifs` (finds all hits) — distinct names, so no import aliasing is
needed (unlike LOCA/SIMS/OSYM where the output and algorithm share a name).

### 2. Input bundle fields `k` / `motif` / `genome`

The inputs are asymmetric: `motif` (`s`) is searched for; `genome` (`t`)
is searched within. Name them after the problem statement
("…representing a motif", "…representing a genome").

Caps, first-failure-wins (k first — it is the cheapest and most likely
user error):

1. `1 <= k <= 50`, else `KOutOfRange(k, 1, 50)`.
2. `motif.value.length <= 5000`, else `MotifTooLong(length, 5000)`.
3. `genome.value.length <= 50000`, else `GenomeTooLong(length, 50000)`.

Empty `motif` and/or empty `genome` are accepted by the constructor
(`DnaString` already permits the empty string, and both caps are ≤ its
100 000 limit); the algorithm handles them as documented edge cases. `k`
is a plain `Int`; the constructor rejects `k < 1` and `k > 50`.

### 3. Edit distance = unit-cost Levenshtein

`d_E(x, y)` is the minimum number of single-character insertions,
deletions, and substitutions to turn `x` into `y`. Standard recurrence
`D[a][b] = min(D[a-1][b-1] + (x_a≠y_b), D[a-1][b] + 1, D[a][b-1] + 1)`.

### 4. Two-pass algorithm: forward prune, backward enumerate

Let `m = |s|`, `n = |t|`.

**Pass 1 — forward approximate matching (find valid end columns).**
Compute `D[a][b]` = the minimum edit distance between the motif prefix
`s[0..a)` and **any** substring of `t` ending exactly at genome column `b`
(free start in `t`):

- `D[0][b] = 0` for all `b` — the empty motif-prefix matches the empty
  substring at any end, for free (this is what makes the start free).
- `D[a][0] = a` — only the empty genome prefix is available; delete all
  `a` motif chars.
- `D[a][b] = min(D[a-1][b-1] + (s[a-1]≠t[b-1]), D[a-1][b] + 1, D[a][b-1] + 1)`.

The **valid end columns** are `E = { b ∈ [1, n] : D[m][b] ≤ k }`. Every
qualifying substring `t[i..j]` necessarily has `D[m][j] ≤ d_E(s, t[i..j]) ≤ k`,
so it ends at some `b ∈ E` — the pass is a complete, sound filter on end
positions. (It does **not** by itself give start positions or enumerate
nested hits; that is Pass 2.)

**Pass 2 — per-end backward DP (enumerate exact lengths).**
A substring with `d_E(s, t′) ≤ k` satisfies `|len(t′) − m| ≤ k`, so its
length lies in the window `L ∈ [max(1, m − k), m + k]`. For each valid end
column `b ∈ E`, compute exact edit distances of the **whole** motif against
the genome suffixes ending at `b` for each candidate length, via a backward
table on the reversed inputs:

- `B[a][c]` = `d_E(s[m−a..m), t[b−c..b))` — the edit distance between the
  last `a` motif characters and the last `c` genome characters before `b`.
- `B[0][c] = c`, `B[a][0] = a`, standard recurrence on `s` reversed and
  the genome read right-to-left from `b`.
- For each candidate length `c = L` in `[max(1, m − k), min(b, m + k)]`,
  if `B[m][c] ≤ k`, emit `SimilarMotif(location = b − c + 1, length = c)`
  (`location` is 1-based).

Because `B[m][c]` is the *exact* distance for the substring `t[b−c..b)`,
Pass 2 emits precisely the qualifying hits (no false positives), including
multiple lengths sharing a start (the sample's `(1,4) (1,5) (1,6)`).

**Result ordering.** Collect all emitted hits and sort by `(location,
length)` ascending before returning, so the output is deterministic and
matches Rosalind's row order.

### 5. Banding to the `|len − m| ≤ k` window

Both passes only need DP cells within `k` of the relevant diagonal:
distances strictly greater than `k` can never recover to `≤ k` along a
monotone path, so cells with `|a − b'| > k` (where `b'` is the offset from
the diagonal) can be treated as `+∞` and skipped. This bounds Pass 1 to
`O(n · k)` and each Pass-2 end to `O((m + k) · k)`. At the Rosalind cap
(`n = 50 000`, `m = 5 000`, `k = 50`) Pass 1 is ~`2.5 · 10^6` cells and the
number of valid ends is typically small, keeping the whole run well within
seconds. A naïve unbanded `O(n · m)` forward table (~`2.5 · 10^8` cells) is
also correct but is the fallback, not the chosen path.

### 6. Empty-input and boundary short-circuits

- `genome` empty (`n == 0`): no non-empty substring exists ⇒ return
  `Nil`.
- `motif` empty (`m == 0`): `d_E("", t′) = len(t′)`, so every substring of
  length `1..k` qualifies. This is a documented (if degenerate) case; the
  general DP handles it, and the constructor permits it. The canonical
  Rosalind inputs are always non-empty.
- `motif` longer than `genome`: still well-defined — e.g. `s = ACGT`,
  `t = ACG`, `k = 1` ⇒ `(1, 3)` (one deletion). The length window simply
  caps candidate lengths at `min(b, m + k)`.

## Risks / Trade-offs

- **[Output size]** → The result list can be large when `k` is generous
  and the motif is short (many overlapping/nested hits). This is inherent
  to the problem (Rosalind wants them all); we return a `List` and let the
  runner stream it. No artificial cap is imposed.

- **[Banding correctness]** → Banding relies on the monotonicity of
  edit-distance paths (a cell `> k` cannot lead to a final cell `≤ k`).
  This is standard for thresholded edit distance; the tests pin the
  canonical sample, a small multi-hit case, a "no hits" case, and a
  brute-force edit-distance oracle cross-check on assorted inputs to guard
  the band boundaries. If banding is ever in doubt, the unbanded full table
  is a drop-in correctness oracle.

- **[Two metrics in one subdomain]** → KSIM uses edit distance while its
  neighbour SIMS uses the +1/-1 mismatch score. To avoid confusion the
  algorithm docstring states the metric explicitly and the types are
  named for the *task* (`SimilarMotif(s)`), not the metric.

- **[Memory]** → Pass 1's banded forward table is `O(n · k)` ints
  (~`2.5 · 10^6` at the cap, ~10 MB). Pass 2 allocates a small
  `O((m + k) · k)` table per valid end. Both are comfortably within a
  normal JVM heap. Documented, not further optimised.

## Migration Plan

Purely additive; no migration. New files under `bio.domain.analysis`,
`bio.algorithms.analysis`, `bio.problems`, plus a one-line wiring change in
`bio/Main.scala`. Rollback = delete the new files and the wiring line.

## Open Questions

None.
