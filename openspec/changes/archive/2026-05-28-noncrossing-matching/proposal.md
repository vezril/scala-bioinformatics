## Why

Rosalind problem 35 ("Catalan Numbers and RNA Secondary Structures", CAT) is the next step beyond the now-archived PMCH (spec 34). Where PMCH counts *all* perfect matchings of basepair edges (yielding the closed-form `(#A)! · (#C)!`), CAT restricts to **noncrossing** perfect matchings — the matchings that correspond to *pseudoknot-free* RNA secondary structures, which is what biologists actually want to enumerate. The count is no longer a closed-form factorial: it follows the Catalan recurrence `c_n = Σ_{k=1..n} c_{k-1} · c_{n-k}` adapted to the constraint that bonded positions must be one of the basepair types (`A`-`U` or `C`-`G`). Adding it gives the framework its first dynamic-programming-on-RNA primitive, completes Rosalind problem 35, and lays the foundation for the rest of the Rosalind secondary-structure track (Motzkin numbers, partial matchings, etc.).

## What Changes

- Add a new validated domain type `bio.domain.nucleic.NoncrossingMatchingProblem` wrapping an `RnaString` plus precomputed `auCount: Int` (= `#A == #U`) and `cgCount: Int` (= `#C == #G`). The smart constructor enforces:
  - `length(value) <= 300` (Rosalind CAT cap, double PMCH's 80 bp because the algorithm is `O(n^3)` not `O(n!)`),
  - `#A == #U`, else `UnpairedAU(aCount, uCount)`,
  - `#C == #G`, else `UnpairedCG(cCount, gCount)`.
  The empty RNA string is accepted (the empty matching is the only noncrossing perfect matching → `1`).
- Add a new algorithm object `bio.algorithms.nucleic.NoncrossingMatching` exposing `count(problem: NoncrossingMatchingProblem): Int`. Returns the noncrossing-matching count modulo `1 000 000` (the Rosalind output spec). Implementation: bottom-up `O(n^2)` interval DP filling `dp(i)(j)` = matchings on the inclusive substring `s[i..j]`, recurring `dp(i)(j) = Σ_k dp(i+1)(k-1) · dp(k+1)(j)` for every `k ∈ {i+1, i+3, ..., j}` whose `s(k)` is a basepair partner of `s(i)`.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `NoncrossingMatchingProblemSpec`: accepts canonical sample `AUAU`; accepts the empty string; accepts the maximum 300-char input; rejects 301-char input as `ExceedsMaxLength`; rejects unbalanced AU `"AAU"` as `UnpairedAU(2, 1)`; rejects unbalanced CG `"CCG"` as `UnpairedCG(2, 1)`; companion `apply` and `copy` leak-proofness.
  - `NoncrossingMatchingSpec`: canonical Rosalind sample `AUAU → 2`; empty string → `1`; single `AU → 1`; single `CG → 1`; `AUAUAU → 5` (Catalan `C(3)`); fully-nested `AAAAUUUU → 1` (the only noncrossing all-nested layout); mixed `AUCG → 1`; `CGCG → 2`; modulo behaviour `("AU" * 14) → 674440` (= `C(14) mod 1 000 000`).
- No changes to existing capabilities; no breaking changes. `RnaString` is reused as-is (cap `10 000` — far above this spec's 300).

## Capabilities

### New Capabilities
- `noncrossing-matching`: Counts the noncrossing perfect matchings of basepair edges in the bonding graph of a balanced RNA string, modulo `1 000 000`. Includes the validated `NoncrossingMatchingProblem` input bundle (length, balanced-AU, balanced-CG checks) and the `NoncrossingMatching.count` `O(n^2)`-cell, `O(n)`-transition dynamic-programming algorithm.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/nucleic/NoncrossingMatchingProblem.scala`
  - `src/main/scala/bio/domain/nucleic/NoncrossingMatchingProblemError.scala`
  - `src/main/scala/bio/algorithms/nucleic/NoncrossingMatching.scala`
  - `src/test/scala/bio/domain/nucleic/NoncrossingMatchingProblemSpec.scala`
  - `src/test/scala/bio/algorithms/nucleic/NoncrossingMatchingSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Slots into the existing `bio.{algorithms,domain}.nucleic` family alongside the closely related `perfect-matching` (spec 34 — PMCH) which uses the same `Validated RNA + balanced-AU/CG` input bundle pattern.
