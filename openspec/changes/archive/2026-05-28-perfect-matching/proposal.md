## Why

Rosalind problem 34 ("Perfect Matchings and RNA Secondary Structures", PMCH) takes an RNA string with balanced base counts (`#A == #U` and `#C == #G`) and asks for the total number of perfect matchings of *basepair edges* in its bonding graph — i.e. how many ways the molecule can pair up so that every A is bonded to some U and every C is bonded to some G. The closed-form answer is `(#A)! · (#C)!`, because choosing the matching factors into an independent permutation of the U-partners of the A's times an independent permutation of the G-partners of the C's. Adding it gives the framework its first *RNA-bonding-graph* feature, lays the foundation for the rest of the Rosalind RNA-secondary-structure track (matchings, Catalan numbers, Motzkin numbers), and completes spec 34 — the next problem after the now-archived `genetic-character-table` (spec 33).

## What Changes

- Add a new validated domain type `bio.domain.nucleic.PerfectMatchingProblem` wrapping an `RnaString` plus the precomputed `auCount: Int` (= `#A == #U`) and `cgCount: Int` (= `#C == #G`). The smart constructor enforces:
  - `length(value) <= 80` (Rosalind cap),
  - `#A == #U`, else `UnpairedAU(aCount, uCount)`,
  - `#C == #G`, else `UnpairedCG(cCount, gCount)`.
  The empty RNA string is accepted (it produces the empty bonding graph, with exactly one perfect matching — the empty matching — under `0! · 0! = 1`).
- Add a new algorithm object `bio.algorithms.nucleic.PerfectMatching` exposing `count(problem: PerfectMatchingProblem): BigInt`. Returns `factorial(problem.auCount) * factorial(problem.cgCount)`. **`BigInt`** because the worst case `40! ≈ 8.16 × 10^47` overflows `Long` — and Rosalind expects an exact integer, no modulo.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `PerfectMatchingProblemSpec`: accepts the canonical Rosalind sample (`AGCUAGUCAU`); accepts the empty string with `auCount = 0`, `cgCount = 0`; accepts the maximum-length 80-char input (40 `A`s + 40 `U`s); rejects an 81-char input; rejects unbalanced AU (`"AAU"` → `UnpairedAU(2, 1)`); rejects unbalanced CG (`"CCG"` → `UnpairedCG(2, 1)`); companion `apply` and `copy` leak-proofness.
  - `PerfectMatchingSpec`: canonical Rosalind sample → `BigInt(12)`; empty string → `BigInt(1)`; single `AU` pair → `BigInt(1)`; single `CG` pair → `BigInt(1)`; `"AAUU"` → `BigInt(2)`; `"CCGG"` → `BigInt(2)`; 40-pair maximum (`"AU" * 40`) → `BigInt("815915283247897734345611269596115894272000000000")` (= `40!`).
- No changes to existing capabilities; no breaking changes. `RnaString` is reused as-is (cap 10 000 — far above this spec's 80).

## Capabilities

### New Capabilities
- `perfect-matching`: Counts the perfect matchings of basepair edges in the bonding graph of a balanced RNA string. Includes the validated `PerfectMatchingProblem` input bundle (length, balanced-AU, balanced-CG checks) and the `PerfectMatching.count` closed-form algorithm.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/nucleic/PerfectMatchingProblem.scala`
  - `src/main/scala/bio/domain/nucleic/PerfectMatchingProblemError.scala`
  - `src/main/scala/bio/algorithms/nucleic/PerfectMatching.scala`
  - `src/test/scala/bio/domain/nucleic/PerfectMatchingProblemSpec.scala`
  - `src/test/scala/bio/algorithms/nucleic/PerfectMatchingSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Slots into the existing `bio.{algorithms,domain}.nucleic` family alongside `dna-nucleotides`, `dna-reverse-complement`, `rna-sequence` (the RNA transcription algorithm), and the count/sequence ADTs.
