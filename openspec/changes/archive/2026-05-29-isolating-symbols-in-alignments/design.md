## Context

Rosalind OSYM ("Isolating Symbols in Alignments") works over two DNA
strings `s` (length `m`) and `t` (length `n`) under the *mismatch score*
from SIMS: `+1` for a matched symbol, `-1` for every mismatched, inserted,
or deleted symbol. It asks for two numbers:

1. The maximum **global** alignment score of `s` and `t`.
2. The **sum of every element** of the matrix `M`, where `M[j][k]` is the
   maximum score of any global alignment of `s` and `t` that aligns `s[j]`
   *in the same column as* `t[k]` (a forced match/substitution column at
   `(j, k)`).

The canonical sample is `s = ATAGATA`, `t = ACAGGTA` ⇒ global score `3`,
matrix sum `-139`.

The framework already has the +1/-1 mismatch score (SIMS,
`bio.algorithms.analysis.FittingAlignment`) and Needleman-Wunsch global
alignment (GLOB, `bio.algorithms.protein.GlobalAlignmentScore` — but that
uses BLOSUM62 + gap `-5` on protein, not the unit mismatch score on DNA).

## Goals / Non-Goals

**Goals:**

- Provide a validated `IsolatedSymbolsProblem` bundle (sealed abstract case
  class, smart constructor, 1 000 bp caps) consistent with the existing
  `*Problem` ADTs.
- Provide a plain `IsolatedSymbols` output ADT carrying `globalScore: Int`
  and `matrixSum: Long`.
- Implement the forward + backward (prefix/suffix) `O(m · n)` DP under the
  +1/-1 mismatch score and compute the full `M`-matrix sum without
  materialising `M`.
- Reproduce the canonical OSYM sample exactly (`3`, `-139`).

**Non-Goals:**

- Materialising or returning the `M` matrix itself (only its sum is asked).
- Reusing BLOSUM/PAM scoring or arbitrary gap penalties — OSYM is fixed to
  the unit mismatch score.
- FASTA file parsing in the core algorithm (the runner uses inline
  `DnaString`s, matching `LOCAProb`/`SIMSProb`).

## Decisions

### 1. Subdomain `analysis`, name `IsolatedSymbols`

Place the algorithm in `bio.algorithms.analysis` and the domain types in
`bio.domain.analysis`, next to the SIMS fitting alignment whose +1/-1
mismatch score this reuses. Output ADT and algorithm object share the name
`IsolatedSymbols` (import-aliased at call sites), as `LocalAlignment` and
`FittingAlignment` already do.

### 2. Symmetric field names `left` / `right`

OSYM is symmetric: swapping `s` and `t` transposes `M` (same sum) and
preserves the global score. Name the inputs `left`/`right` (like
`SharedSplicedMotifProblem`), not the asymmetric `source`/`target`.

Caps, first-failure-wins:

1. `left.value.length <= 1000`, else `LeftTooLong(length, 1000)`.
2. `right.value.length <= 1000`, else `RightTooLong(length, 1000)`.

Empty `left` and/or empty `right` are accepted.

### 3. `matrixSum` is a `Long`, not an `Int`

`M` has `m · n` entries (up to `10^6`), each as negative as roughly
`-(m + n)` (≈ `-2000`). The worst-case sum approaches `-2 · 10^9`, which is
perilously close to `Int.MinValue` (`-2_147_483_648`). Use `Long` for
`matrixSum` to remove all overflow risk. `globalScore` stays `Int` (bounded
by `±max(m, n)`).

### 4. Forward + backward DP, then `M[j][k]` in O(1)

Two Needleman-Wunsch tables under the unit mismatch score:

- **Forward** `f(i)(j)` = best global score of `left[0..i)` vs `right[0..j)`:
  - `f(0)(0) = 0`, `f(i)(0) = -i`, `f(0)(j) = -j`;
  - `f(i)(j) = max(f(i-1)(j-1) + sc, f(i-1)(j) - 1, f(i)(j-1) - 1)`
    where `sc = if left(i-1) == right(j-1) then +1 else -1`.
- **Backward** `b(i)(j)` = best global score of `left[i..m)` vs `right[j..n)`:
  - `b(m)(n) = 0`, `b(i)(n) = -(m - i)`, `b(m)(j) = -(n - j)`;
  - `b(i)(j) = max(b(i+1)(j+1) + sc, b(i+1)(j) - 1, b(i)(j+1) - 1)`
    where `sc = if left(i) == right(j) then +1 else -1`.

Then:

- `globalScore = f(m)(n)` (equivalently `b(0)(0)`).
- For `j ∈ [0, m)`, `k ∈ [0, n)`:
  `M[j][k] = f(j)(k) + sc(left(j), right(k)) + b(j+1)(k+1)`
  — the best prefix alignment ending before `(j, k)`, the forced column at
  `(j, k)`, and the best suffix alignment starting after it.
- `matrixSum = Σ M[j][k]` accumulated in a `Long`; `M` is never stored.

This is `O(m · n)` time and `O(m · n)` memory (two int tables ≈ 8 MB at the
cap).

### 5. Empty-input behaviour

- Either input empty ⇒ `M` has no `(j, k)` entries, so `matrixSum = 0`.
- `globalScore` is `-(other length)` (all gaps), or `0` when both empty.

The general DP yields these naturally; the empty-`M` sum falls out of the
empty iteration range. No special-casing required beyond what the loops
already do.

## Risks / Trade-offs

- **[Sum overflow]** → Mitigated by typing `matrixSum` as `Long`
  (Decision 3).

- **[Memory at the cap]** → Two `(m+1)·(n+1)` int tables (~8 MB at
  `m = n = 1000`). Acceptable; both tables are needed for the O(1)
  per-cell `M` formula, so a row-rolling optimisation would force a second
  pass and is not worth it.

- **[Confusion with GLOB]** → GLOB (`GlobalAlignmentScore`) uses BLOSUM62 +
  gap `-5` on protein; OSYM uses the unit mismatch score on DNA. They are
  distinct enough that sharing code would add coupling for no gain — OSYM
  keeps its own self-contained forward/backward DP.

## Migration Plan

Purely additive; no migration. New files under `bio.domain.analysis`,
`bio.algorithms.analysis`, `bio.problems`, plus a one-line wiring change in
`bio/Main.scala`. Rollback = delete the new files and the wiring line.

## Open Questions

None.
