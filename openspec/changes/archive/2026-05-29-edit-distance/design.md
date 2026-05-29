## Context

The Rosalind track has built up a 4-quadrant DNA pattern-search matrix (SUBS, SSEQ, LCSM, LCSQ) and a trio of RNA bonding-graph matching algorithms. The next algorithmic primitive is the *Levenshtein* (edit) distance between two protein strings — the minimum number of single-symbol substitutions, insertions, and deletions to transform `s` into `t`. This is the canonical introduction to alignment-style DP and the foundation for follow-on Rosalind problems (global/local/affine alignment, edit-distance alignment with traceback, etc.).

Existing analogues that shape conventions:
- `SharedSplicedMotifProblem` / `SharedSplicedMotif` (LCSQ, spec 39) — two-string DP, smart constructor with `LeftTooLong`/`RightTooLong` first-failure-wins caps, classical `O(m · n)` table.
- `RnaSplicingProblem` (spec 21) and `InferMRna` (spec 13) — established `bio.{domain,algorithms}.protein` conventions and the `sealed abstract case class` ADT pattern with `ProteinString`.

## Goals / Non-Goals

**Goals:**
- Validated `EditDistanceProblem(left: ProteinString, right: ProteinString)` smart constructor enforcing `length ≤ 1000` per side with deterministic first-failure-wins error ordering.
- Algorithm object `EditDistance.compute(problem): Int` returning the Levenshtein distance via the classical `O(m · n)` DP — substitutions, insertions, deletions each cost 1.
- Empty inputs are accepted and produce the natural distances (`d(∅, t) = |t|`, `d(s, ∅) = |s|`).
- Round-trip the canonical Rosalind sample `(PLEASANTLY, MEANLY) → 5`.
- TDD: Red → Green → Refactor with `sbt test` green at the end.

**Non-Goals:**
- No alignment reconstruction / traceback (Rosalind spec 41 territory — separate change).
- No affine gaps, scoring matrices (PAM/BLOSUM), or local alignment.
- No streaming or rolling-array optimisation (the `(m+1) × (n+1)` table is fine at the 1000 aa cap — 10⁶ cells, milliseconds).
- No FASTA parsing in the algorithm/domain layer (the runner under `bio.problems.EDITProb` handles I/O).

## Decisions

**1. Wrap two `ProteinString`s in a dedicated `EditDistanceProblem` bundle (vs. raw tuple).**
- Rationale: matches every other Rosalind problem in the codebase. Centralises validation (length caps, first-failure-wins error ordering) and makes the algorithm signature self-documenting. Same pattern as `SharedSplicedMotifProblem` / `RnaSplicingProblem`.

**2. `sealed abstract case class` to suppress `apply` / `copy` leakage.**
- Rationale: established convention. Forces all construction through the smart constructor so invariants (length cap) cannot be bypassed by `Problem(left, right)` or `problem.copy(...)`.

**3. First-failure-wins ordering: `LeftTooLong` before `RightTooLong`.**
- Rationale: identical to LCSQ's ordering — deterministic, predictable, mirrors the rest of the codebase's Rosalind cap validations.

**4. Classical `O(m · n)` 2-D `Array[Array[Int]]` DP, no rolling array.**
- Rationale: at the Rosalind cap (1000 × 1000 = 10⁶ cells, ~4 MB of `Int`s) this is trivially fast and uses ~10 ms of CPU. Keeps the implementation simple, mirrors the LCSQ implementation exactly (same table shape, same imperative-while-loops style), and leaves room for a future traceback feature reusing the same table. Rolling-array optimisation could be added later if benchmark numbers demand.

**5. Standard Levenshtein recurrence:**
- `dp(0)(j) = j`, `dp(i)(0) = i`;
- if `s(i-1) == t(j-1)`: `dp(i)(j) = dp(i-1)(j-1)`;
- else: `dp(i)(j) = 1 + min(dp(i-1)(j), dp(i)(j-1), dp(i-1)(j-1))` — corresponding to delete-from-s, insert-into-s, substitute.
- Rationale: textbook; Rosalind's grader accepts the integer answer with no ambiguity (unlike LCSQ where multiple valid reconstructions exist).

**6. Return `Int`, not `BigInt` or a wrapper type.**
- Rationale: the answer is bounded by `max(|s|, |t|) ≤ 1000`. `Int` is sufficient and matches the consumer's expectation. (No modulo arithmetic needed here, unlike PPER / SSET / INDC.)

**7. Place under `bio.{domain,algorithms}.protein` (not `analysis`).**
- Rationale: the inputs are protein strings, matching `RnaTranslation`, `InferMRna`, and `RnaSplicing`. The algorithm itself is generic Levenshtein, but the *typed inputs* are protein-specific in the Rosalind formulation. (If/when a DNA-edit-distance variant is added later, it can live under `nucleic` or share via a generic helper.)

## Risks / Trade-offs

- **Memory at the cap (~4 MB for 10⁶ Ints):** Fine for the JVM; well below typical heap. → No mitigation needed; rolling-array refactor stays available as a future optimisation.
- **No traceback returned:** Spec 40 asks only for the integer distance, but future alignment problems will need the table. → The implementation keeps the full DP table internally; later refactors can expose a `computeWithTable` overload without breaking the public `compute` API.
- **Pure-imperative loops sit oddly inside a Cats Effect codebase:** Mirrors the LCSQ implementation; the algorithm itself is referentially transparent (returns the same `Int` for the same input). → No mitigation; consistent with established conventions.
