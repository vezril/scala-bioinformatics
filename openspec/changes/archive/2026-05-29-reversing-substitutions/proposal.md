## Why

Rosalind spec 45 (RSUB — "Identifying Reversing Substitutions") asks us to find every *reversing substitution* in a fully-labelled rooted binary tree of DNA sequences: a pair of parent-child edges `(s, t)` and `(v, w)` on a root-down path where `s[i] → t[i]` is the first substitution at position `i`, and `v[i] → w[i]` reverses it back to the original (`w[i] == s[i]`), with the intermediate sub-path from `t` down to `v` preserving the substituted value `t[i] == u[i]` (no other change at this position in between). It's a per-position tree walk: gateway to evolutionary-substitution analysis and the prerequisite for downstream RSUB-style phylogenetic inference exercises on the Rosalind track.

## What Changes

- Reuse `bio.domain.graph.NewickTree`, `bio.parsing.NewickParser`, and `bio.domain.analysis.NamedSequence` (introduced for ALPH).
- Add a validated domain bundle `bio.domain.analysis.ReversingSubstitutionsProblem` wrapping (a) a fully-labelled rooted binary `NewickTree` and (b) a `Vector[NamedSequence]` of equal-length DNA strings (alphabet `{A, C, G, T}`, no gaps; ≤ 400 bp per sequence; ≤ 100 sequences total) whose labels match *every* tree node — leaves and internals.
- Add an output ADT `bio.domain.analysis.ReversingSubstitution(firstChangeSpecies, reversionSpecies, position, originalSymbol, substitutedSymbol, revertedSymbol)` representing one reversing substitution found in the tree.
- Add an algorithm object `bio.algorithms.analysis.ReversingSubstitutions` exposing `findAll(problem: ReversingSubstitutionsProblem): Vector[ReversingSubstitution]` that enumerates every reversing substitution by scanning each column independently and walking each candidate "first substitution edge" `(s, t)` downward.
- Add an error ADT `ReversingSubstitutionsProblemError` covering: empty alignment, row-length mismatch, sequence too long, invalid character (non-ACGT), too many sequences, non-binary internal node, missing internal-node label, missing leaf label, node-label mismatch between tree and alignment.
- Wire a `bio.problems.RSUBProb` runner consistent with the other Prob-style entry points, including file-based input loading.

## Capabilities

### New Capabilities
- `reversing-substitutions`: Validated input bundle for a fully-labelled rooted binary tree + DNA alignment, a `ReversingSubstitution` output record, and the per-column tree-walk algorithm enumerating every reversing substitution.

### Modified Capabilities
<!-- none -->

## Impact

- New files under `bio.domain.analysis` (problem + error ADT + output ADT) and `bio.algorithms.analysis` (algorithm).
- New runner under `bio.problems` (`RSUBProb`) reading from `src/main/scala/resources/rsub_data.txt` (mirroring the ALPHProb file-loading pattern, including the inline FASTA-style parser).
- New spec test suites mirroring the conventions used for `AlignmentBasedPhylogeny` (ALPH).
- No changes to existing capabilities; purely additive. The Sankoff parsimony algorithm from spec 44 is untouched — RSUB asks a *different* question (enumerate reversions, given fully-labelled internal nodes) and runs a different per-column walk.
