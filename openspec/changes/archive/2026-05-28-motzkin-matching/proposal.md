## Why

Rosalind problem 36 ("Motzkin Numbers and RNA Secondary Structures", MOTZ) is the natural sequel to the now-archived CAT (spec 35). Where CAT counts noncrossing **perfect** matchings — every node must be bonded — MOTZ counts **all** noncrossing matchings, including partial ones where some nodes are left unbonded. Biologically, this captures every pseudoknot-free secondary structure (not just the "fully paired" ones), which is what real RNA structure prediction enumerates. Mathematically, the recurrence gains one extra term per cell — `M(i, j) = M(i+1, j) + Σ_k ...` where the new `M(i+1, j)` term represents "leave position `i` unpaired". Adding it gives the framework its third RNA-bonding-graph capability (the all-matchings / noncrossing-perfect / noncrossing-partial trio) and completes Rosalind problem 36 — the next problem after the now-archived CAT (spec 35).

## What Changes

- Add a new validated domain type `bio.domain.nucleic.MotzkinMatchingProblem` wrapping an `RnaString`. The smart constructor enforces **only** the length cap (`length <= 300`); unlike PMCH (spec 34) and CAT (spec 35), MOTZ does **not** require balanced AU/CG counts because partial matchings allow any number of unbonded nodes. The empty RNA string is accepted (the empty matching alone counts as `1`).
- Add a new algorithm object `bio.algorithms.nucleic.MotzkinMatching` exposing `count(problem: MotzkinMatchingProblem): Int`. Returns the noncrossing-matching count modulo `1 000 000`. Implementation: bottom-up `O(n²)`-cell interval DP filling `M(i)(j)` = matchings on the inclusive substring `s[i..j]`, recurring `M(i)(j) = M(i+1)(j) + Σ_k M(i+1)(k-1) · M(k+1)(j)` for every `k ∈ {i+1, ..., j}` whose `s(k)` is a basepair partner of `s(i)`. Unlike CAT, the parity constraint on `(k - i)` is gone (since partial matchings don't require even-length partitions), and the unmatched-`i` term `M(i+1)(j)` is added.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `MotzkinMatchingProblemSpec`: accepts canonical sample `AUAU`; accepts the empty string; accepts the maximum 300-char input; accepts an *unbalanced* string (e.g. `"A"` — would be rejected by CAT but accepted here); accepts an odd-length string; rejects 301-char input as `ExceedsMaxLength(301, 300)`; companion `apply` and `copy` leak-proofness.
  - `MotzkinMatchingSpec`: canonical Rosalind sample `AUAU → 7`; empty input → `1`; single `A → 1` (only the empty matching); `AU → 2` (empty + one pair); `AAAA → 1` (no pairs possible); `AAAU → 4` (3 individual A-U bonds + empty); `AUCG → 4` (4 subsets of two independent pairs); `CGCG → 7` (Catalan-equivalent on CG alphabet).
- No changes to existing capabilities; no breaking changes. `RnaString` is reused as-is (cap `10 000` — far above this spec's 300).

## Capabilities

### New Capabilities
- `motzkin-matching`: Counts the noncrossing matchings (not necessarily perfect) of basepair edges in the bonding graph of an RNA string, modulo `1 000 000`. Includes the validated `MotzkinMatchingProblem` input bundle (length cap only — no balance constraint) and the `MotzkinMatching.count` `O(n³)` interval dynamic-programming algorithm with the extra "leave position `i` unpaired" term per cell.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/nucleic/MotzkinMatchingProblem.scala`
  - `src/main/scala/bio/domain/nucleic/MotzkinMatchingProblemError.scala`
  - `src/main/scala/bio/algorithms/nucleic/MotzkinMatching.scala`
  - `src/test/scala/bio/domain/nucleic/MotzkinMatchingProblemSpec.scala`
  - `src/test/scala/bio/algorithms/nucleic/MotzkinMatchingSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Slots into the existing `bio.{algorithms,domain}.nucleic` family alongside the closely related `perfect-matching` (PMCH, spec 34) and `noncrossing-matching` (CAT, spec 35) — the three RNA-bonding-graph counting variants now form a complete progression: all matchings → noncrossing perfect → noncrossing partial.
