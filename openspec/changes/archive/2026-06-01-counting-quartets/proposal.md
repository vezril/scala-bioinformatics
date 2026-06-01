## Why

Rosalind CNTQ ("Counting Quartets") asks for `q(T)`, the total number of quartets
consistent with an unrooted binary tree `T` on `n` taxa, modulo 1,000,000. Because
`T` is fully resolved, every 4-element subset of taxa induces exactly one
consistent quartet topology, so `q(T) = C(n, 4)` — a closed form depending only on
`n`. The framework already parses Newick trees but has no capability counting
consistent quartets.

## What Changes

- Add a new `counting-quartets` capability that, given a leaf count `n`
  (`4 ≤ n ≤ 5000`) and an unrooted binary tree `T` on `n` taxa in Newick format,
  returns `q(T) = C(n, 4) mod 1,000,000`.
- Introduce a validated `CountingQuartetsProblem` input bundle and its
  `CountingQuartetsProblemError` ADT (below minimum, exceeds maximum, leaf-count
  mismatch), constructed only through a first-failure-wins smart constructor that
  also verifies the parsed tree actually has `n` leaves.
- Add `CountingQuartets.count` computing `n(n−1)(n−2)(n−3)/24 mod 1,000,000` with
  overflow-safe `Long` arithmetic (the product of four consecutive integers is
  always divisible by 24).
- Add a `CNTQProb` runner that reads the sample dataset from
  `src/main/scala/resources/cntq_data.txt` (`n` on line 1, the Newick tree on line
  2), parses both, and prints the count; wire it into `bio.Main`.

## Capabilities

### New Capabilities
- `counting-quartets`: a validated `(n, tree)` input bundle and the algorithm
  computing the number of quartets consistent with an unrooted binary tree,
  `C(n, 4) mod 1,000,000`, per Rosalind CNTQ.

### Modified Capabilities
<!-- None — reuses the existing Newick parser/tree types without changing their requirements. -->

## Impact

- New domain types: `bio.domain.graph.CountingQuartetsProblem`,
  `bio.domain.graph.CountingQuartetsProblemError`.
- New algorithm: `bio.algorithms.graph.CountingQuartets`.
- New runner: `bio.problems.CNTQProb`; one line changed in `bio.Main`.
- Reuses existing `bio.parsing.NewickParser` and `bio.domain.graph.NewickTree`
  (no changes to either).
- New tests under `bio.domain.graph` and `bio.algorithms.graph`.
