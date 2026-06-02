## Why

Rosalind problem SUFF ("Encoding Suffix Trees") asks, given a DNA string `s`, for the substrings of `s$` that label the edges of its suffix tree. Where LREP consumed a pre-built suffix tree, SUFF requires **constructing** the suffix tree from scratch — the project's first suffix-tree builder, completing the suffix-tree pair in the graph family.

## What Changes

- Introduce a validated `SuffixTreeProblem` domain type wrapping the DNA string `s` (`DnaString`, length ≤ 1000 bp); the terminator `$` is appended internally to form `s$`.
- Introduce a `SuffixTreeProblemError` ADT for the new invariant (sequence too long).
- Introduce a `SuffixTreeEncoding` result type holding the edge-label substrings (`Vector[String]`), with a Rosalind-style `format` (one label per line, any order).
- Introduce a `SuffixTreeConstruction` algorithm that builds the suffix tree of `s$` by naive suffix insertion with edge splitting, and returns the edge labels.
- Add a `SUFFProb` runner reading `s` (with or without a trailing `$`) from `suff_data.txt` and printing the edge labels through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.DnaString`.

## Capabilities

### New Capabilities
- `suffix-tree-encoding`: Construct the suffix tree of `s$` for a DNA string `s` and return the substrings of `s$` labelling its edges (Rosalind SUFF).

### Modified Capabilities
<!-- None. SUFF adds a new capability and does not change any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.graph`): `SuffixTreeProblem`, `SuffixTreeProblemError`, `SuffixTreeEncoding` (result).
- **New algorithm** (`bio.algorithms.graph.SuffixTreeConstruction`).
- **New runner** (`bio.problems.SUFFProb`) reading `src/main/scala/resources/suff_data.txt`.
- **Reused, unchanged**: `bio.domain.nucleic.DnaString`.
- **Tests**: new specs under `bio.domain.graph` and `bio.algorithms.graph`. No existing tests change.
