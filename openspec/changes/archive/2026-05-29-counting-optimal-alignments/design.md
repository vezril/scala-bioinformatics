## Context

CTEA (spec 46) is a one-step extension of EDIT (spec 40):

- EDIT returns the integer Levenshtein distance `d_E(s, t)`.
- CTEA returns the count of *distinct* alignments `(s', t')` whose Hamming distance equals `d_E(s, t)` — i.e., the size of the equivalence class of optimal alignments, modulo `134_217_727`.

The algorithmic trick is well-known. Alongside the standard cost table `dp(i)(j) = d_E(s[0..i), t[0..j))`, fill a parallel count table `cnt(i)(j) = number of optimal alignments of s[0..i) and t[0..j)`. The recurrences run in lock-step:

- `dp(i)(j)` follows the classical Levenshtein recurrence (`dp(i-1)(j-1) + δ`, `dp(i-1)(j) + 1`, `dp(i)(j-1) + 1`, take min).
- `cnt(i)(j) = (sum over winning moves of cnt(predecessor)) mod 134_217_727`, where a move is "winning" iff its cost-extension equals `dp(i)(j)`.

Three moves can win simultaneously at any cell. The modulus `134_217_727 = 2^27 - 1` is small enough that three additions of values bounded by `2^27 - 1` produce at most `3 · (2^27 - 1) ≈ 4 · 10^8`, well within signed `Int` range. So plain `Int` arithmetic suffices — no `Long` casts needed if we mod between additions, but folding mod at the end is also safe.

Canonical Rosalind sample (`PLEASANTLY` / `MEANLY`) returns `4` ✓ (verified by Rosalind).

## Goals / Non-Goals

**Goals:**
- Validated `OptimalAlignmentCountProblem(left, right)` smart constructor matching the EDIT bundle's first-failure-wins 1000-aa cap pattern.
- Algorithm `OptimalAlignmentCount.compute(problem): Int` returning the count modulo `134_217_727`.
- Empty inputs accepted: empty/empty → `1`; empty + length-n → `1`; vice versa → `1`.
- Identical strings → `1` (only the identity alignment is optimal).
- TDD: Red → Green → Refactor with `sbt test` green at the end.

**Non-Goals:**
- No enumeration of the optimal alignments themselves — only the count.
- No support for non-unit substitution / gap costs (those would change which moves are "winning"). Pure Levenshtein metric, same as EDIT.
- No reuse of `EditDistance.compute`. The recurrence is the same, but threading the count accumulator into a separate algorithm keeps both clean. Code duplication is ~15 lines; not worth the abstraction.
- No streaming or rolling-array optimisation — the full `(m+1) × (n+1)` table is fine at the 1000 aa cap (`~10^6` cells of `Int` × 2 tables ≈ 8 MB).

## Decisions

**1. Separate `OptimalAlignmentCountProblem` bundle (vs. reusing `EditDistanceProblem`).**
- Rationale: matches every other Rosalind problem in the codebase. Algorithm signature is self-documenting; the bundle is the obvious single source of truth for that capability. Mirrors the LCSM/LCSQ/SSEQ/EDIT/EDTA separation even when input shapes overlap.

**2. `sealed abstract case class` to suppress `apply` / `copy` leakage.**
- Rationale: established convention. Forces all construction through the smart constructor so invariants (length cap) cannot be bypassed.

**3. First-failure-wins ordering: `LeftTooLong` before `RightTooLong`.**
- Rationale: identical to EDIT / EDTA / GLOB — deterministic, predictable.

**4. Parallel cost + count DP over two `Array[Array[Int]]` tables.**
- Rationale: textbook. The cost table drives the "which moves win" decision; the count table accumulates the number of optimal predecessors. Both are filled in the same nested-while loop.

**5. Modulus `134_217_727 = 2^27 - 1` applied at every accumulation step (defensive).**
- Rationale: even though three adds of `< 2^27` values fit in `Int`, modding after each add keeps values clearly bounded and matches the spec's "modulo 134_217_727" wording verbatim. Inner loop cost is negligible.

**6. Recurrence boundary conditions:**
- `dp(0)(0) = 0`, `cnt(0)(0) = 1` (one way to align two empty prefixes — the empty alignment).
- `dp(i)(0) = i`, `cnt(i)(0) = 1` (only delete; one path).
- `dp(0)(j) = j`, `cnt(0)(j) = 1` (only insert; one path).

**7. Inner recurrence:**
- Let `matchFree = (s(i-1) == t(j-1))`.
- `diag = dp(i-1)(j-1) + (if matchFree then 0 else 1)`.
- `up   = dp(i-1)(j) + 1`.
- `left = dp(i)(j-1) + 1`.
- `dp(i)(j) = min(diag, up, left)`.
- `cnt(i)(j) = ((if diag == dp(i)(j) then cnt(i-1)(j-1) else 0) + (if up == dp(i)(j) then cnt(i-1)(j) else 0) + (if left == dp(i)(j) then cnt(i)(j-1) else 0)) mod M`.

**8. Return `Int` (signed).**
- Rationale: values are bounded by `M - 1 < 2^27 - 1`, well within signed `Int` range. Matches the natural type for "count modulo a small prime-like number".

**9. Place under `bio.{domain,algorithms}.protein`.**
- Rationale: matches `EditDistance`, `EditDistanceAlignment`, `GlobalAlignmentScore`. Inputs are protein strings.

## Risks / Trade-offs

- **The modulus `134_217_727` is NOT prime** (`134_217_727 = (2^27 - 1) = 7 × 73 × 262657`). Counting via modular addition is still well-defined and the spec just asks for the residue, so this poses no correctness issue. → Documented in Scaladoc.
- **Memory: two tables of `Int` at the cap is ~8 MB.** → Fine for the JVM; well below typical heap. Rolling-array optimisation could halve this if ever needed.
- **Code duplication of the DP loop relative to `EditDistance`.** → Acceptable: ~15 lines, with one structural difference (parallel count table). Generalising prematurely (introducing a `CostScheme + Counting` interface) would couple two simple algorithms before a clear shared interface exists. Defer.
- **Canonical sample `PLEASANTLY / MEANLY → 4` is the only Rosalind-provided datapoint.** → Mitigated by spec scenarios covering well-known reference values (empty inputs, identical strings, single-character cases including the 2-way count for `A` vs `AA`, the 1-way count for distinct-single-chars).
