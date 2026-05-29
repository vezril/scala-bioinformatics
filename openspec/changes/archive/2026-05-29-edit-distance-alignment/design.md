## Context

Spec 40 (EDIT — Levenshtein distance) just landed: `bio.algorithms.protein.EditDistance.compute` returns the integer distance using a full `(m+1) × (n+1)` `Array[Array[Int]]` DP table. The implementation deliberately deferred traceback for spec 41 — exactly this change.

EDTA (spec 41) asks for both the distance *and* one *optimal alignment* — two augmented strings `s'` and `t'` where:
- `s'` and `t'` have equal length;
- each is the original string with `-` gap symbols inserted;
- no column has gaps in both rows;
- the Hamming distance `d_H(s', t')` equals the edit distance `d_E(s, t)`.

For the canonical Rosalind sample `(PRETTY, PRTTEIN) → 4`, one valid optimal alignment is:
```
PRETTY--
PR-TTEIN
```
Multiple optimal alignments may exist; the grader accepts any one.

## Goals / Non-Goals

**Goals:**
- Validated `EditDistanceAlignmentProblem(left, right)` smart constructor matching the EDIT bundle's first-failure-wins 1000-aa cap pattern.
- Algorithm object `EditDistanceAlignment.align(problem): EditAlignment` returning one optimal alignment.
- `EditAlignment` output ADT carries `distance: Int`, `augmentedLeft: String`, `augmentedRight: String`. Plain `String` (not `ProteinString`) because the augmented strings contain `-`, which is not a valid amino-acid code.
- Empty inputs accepted: empty/empty ⇒ `("", "", 0)`; empty left + non-empty right ⇒ aligns all-gaps on the left; vice versa.
- TDD: Red → Green → Refactor with `sbt test` green at the end.

**Non-Goals:**
- No affine-gap scoring (Rosalind spec 47+); no PAM/BLOSUM substitution scoring; no local alignment.
- No enumeration of *all* optimal alignments (just return one canonical).
- No reuse of `EditDistance.compute`. Building the alignment requires the full DP table, so reusing only the integer return would force a wasteful second pass. Both algorithms keep their own self-contained DP loop.
- No streaming / rolling-array optimisation — we need the full `(m+1) × (n+1)` table for traceback.
- `EditAlignment` is *not* `sealed abstract` — it's a simple value carrier with no invariants beyond what its components guarantee. (Compare: `EditDistanceAlignmentProblem` *is* `sealed abstract` because the smart-constructor cap-check is a real invariant.)

## Decisions

**1. Separate `EditDistanceAlignmentProblem` bundle (vs. reusing `EditDistanceProblem`).**
- Rationale: the inputs are structurally identical, but every Rosalind problem in the codebase gets its own typed bundle so the algorithm signature is self-documenting and the smart constructor is the obvious single source of truth for that capability. Mirrors the LCSM/LCSQ/SSEQ separation even when shapes overlap.

**2. `EditAlignment` as a plain `final case class` (not `sealed abstract`).**
- Rationale: it's a result type, not a validated input. There is no invariant we are enforcing beyond the natural arity. The two `String`s may legitimately contain `-`, so wrapping them in `ProteinString` would be wrong (and would fail validation). Using a plain case class gives free `copy`, equality, and pattern-matching.

**3. Classical `O(m · n)` Levenshtein DP table + greedy traceback.**
- Rationale: textbook. Build `dp(i)(j) = edit distance of left[0..i)` and `right[0..j)` exactly as in `EditDistance.compute`, then walk back from `(m, n)` to `(0, 0)`.

**4. Traceback tie-break: match > up > left > diagonal-sub, deterministic.**
- Rationale: at each step `(i, j)` (with `i > 0` and `j > 0`) we ask which neighbour realised the value:
  - if `s(i-1) == t(j-1)` and `dp(i)(j) == dp(i-1)(j-1)`: **diagonal-match** — free; emit `s(i-1)` over `t(j-1)`, decrement both.
  - else if `dp(i)(j) == dp(i-1)(j) + 1`: **up** (delete) — emit `s(i-1)` over `-`, decrement `i`.
  - else if `dp(i)(j) == dp(i)(j-1) + 1`: **left** (insert) — emit `-` over `t(j-1)`, decrement `j`.
  - else (forced): **diagonal-sub** — emit `s(i-1)` over `t(j-1)`, decrement both.
- On the borders (`i == 0` ⇒ only-left, `j == 0` ⇒ only-up), the choice is forced.
- *Why prefer indels over substitution on cost-ties?* It pushes gaps toward consistent ends of the alignment (forming contiguous gap runs rather than scattered single substitutions) and matches the canonical published Rosalind sample for `(PRETTY, PRTTEIN)` → `PRETTY--` / `PR-TTEIN`. Hand-tracing the table confirms: at `(6, 7)` the optimal moves are left (cost-tied with sub) and at `(3, 2)` the optimal move is up — with `match > up > left > sub` the traceback walks exactly to Rosalind's published alignment. The spec permits *any* optimal alignment, so determinism is purely a quality-of-life choice for tests and diffs.

**5. Traceback uses `StringBuilder` × 2 and reverses at the end.**
- Rationale: matches the LCSQ implementation pattern (build backwards, reverse once). Avoids `O(n²)` string concatenation. Returns plain `String` for both augmented strings.

**6. Return `EditAlignment` rather than a `(Int, String, String)` tuple.**
- Rationale: ADTs over tuples is a project-wide convention. Named fields read better at call sites and resist parameter-order bugs.

**7. Place under `bio.{domain,algorithms}.protein`.**
- Rationale: same package as `EditDistance` — the inputs are protein strings and the result is a protein-strings-with-gaps alignment.

## Risks / Trade-offs

- **Multiple optimal alignments exist; tests must accept any valid one (or pin to our deterministic choice).** → For the canonical Rosalind sample we pin to `PRETTY--` / `PR-TTEIN` (which our tie-break produces and which Rosalind documents). For property-style tests we verify invariants — equal length, no double-gap columns, gap-removal recovers the originals, and Hamming distance equals `distance`.
- **No reuse of `EditDistance.compute`.** → Acceptable code duplication: the recurrence is ~10 lines, traceback needs the table anyway, and reusing only `compute`'s integer return would force a wasteful second pass. A future refactor could expose a private `buildTable` helper if the duplication becomes painful. Not now.
- **`String` (not `ProteinString`) for the augmented outputs.** → Required because `-` is not a valid amino-acid code. Documented in Scaladoc.
