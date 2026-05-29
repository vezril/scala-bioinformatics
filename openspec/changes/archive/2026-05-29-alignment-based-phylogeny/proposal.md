## Why

Rosalind spec 44 (ALPH — "Alignment-Based Phylogeny") combines the two strands of the Rosalind tier-2 track into a single problem: *given a rooted binary tree topology with named species at the leaves and a multiple alignment of those species' DNA sequences (already aligned, with `-` gap symbols allowed), assign DNA strings to the internal nodes so that the total per-edge Hamming distance is minimised*. This is the canonical *small-parsimony* problem (Sankoff / Fitch dynamic programming) and the gateway to every alignment-based phylogenetic-reconstruction exercise on the Rosalind track.

## What Changes

- Reuse the existing `bio.domain.graph.NewickTree` and `bio.parsing.NewickParser` for the tree input.
- Add a validated domain bundle `bio.domain.analysis.AlignmentBasedPhylogenyProblem` wrapping (a) the rooted binary `NewickTree` (every internal node labeled, every leaf labeled) and (b) a `Vector[NamedSequence]` of FASTA-style species alignments (all equal length, ≤ 300 bp, ≤ 500 leaves).
- Add a tiny output type `bio.domain.analysis.NamedSequence(label: String, sequence: String)` — used both for input alignment entries and for output internal-node assignments.
- Add an output ADT `bio.domain.analysis.AlignmentBasedPhylogeny(totalDistance: Int, internalAssignments: Vector[NamedSequence])` carrying the minimum total per-edge Hamming distance plus the assigned DNA strings for every internal (non-leaf) tree node.
- Add an algorithm object `bio.algorithms.analysis.AlignmentBasedPhylogeny` exposing `solve(problem): AlignmentBasedPhylogeny` that runs Sankoff small-parsimony per column over the 5-symbol alphabet `{A, C, G, T, -}`, then reconstructs internal-node strings via traceback.
- Add an error ADT `AlignmentBasedPhylogenyProblemError` covering: empty alignment, length mismatch across alignment rows, length > 300 bp, leaf count > 500, leaf names in tree not matched 1-to-1 with alignment names, missing internal-node labels, non-DNA-or-gap characters in alignment sequences.
- Wire a `bio.problems.ALPHProb` runner consistent with the other Prob-style entry points.

## Capabilities

### New Capabilities
- `alignment-based-phylogeny`: Validated input bundle for the Sankoff small-parsimony problem on a rooted binary `NewickTree` plus a multiple alignment, an `AlignmentBasedPhylogeny` output ADT, and the per-column 5-symbol DP + traceback algorithm returning one optimal internal-node assignment.

### Modified Capabilities
<!-- none -->

## Impact

- New files under `bio.domain.analysis` (problem + error ADT + output ADT + `NamedSequence`) and `bio.algorithms.analysis` (algorithm).
- New runner under `bio.problems` (`ALPHProb`).
- New spec test suites mirroring `MultipleAlignment` (MULT) and `EditDistanceAlignment` (EDTA) conventions.
- No changes to existing capabilities; purely additive. We *consume* `NewickTree` + `NewickParser` but do not modify them.
