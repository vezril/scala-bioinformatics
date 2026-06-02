## Why

Rosalind problem MREP ("Identifying Maximal Repeats") asks for every maximal repeat of a DNA string with length at least 20 — a repeated substring whose occurrences cannot be extended in either direction while still agreeing. It is the third member of the project's suffix-structure family (after SUFF and LREP) and the first to compute maximal repeats, applying Gusfield's classic suffix-tree characterisation.

## What Changes

- Introduce a validated `MaximalRepeatProblem` domain type wrapping a `DnaString` (≤ 1000 bp) and a minimum repeat length (≥ 1; the runner uses 20).
- Introduce a `MaximalRepeatProblemError` ADT for the new invariants (sequence too long, non-positive minimum length).
- Introduce a `MaximalRepeats` result type holding the maximal-repeat substrings (`Vector[String]`), with a `format` of one repeat per line.
- Introduce an `IdentifyMaximalRepeats` algorithm: build the suffix tree of `s$`, then return the path-labels of the **left-diverse internal nodes** (right-maximal because internal, left-maximal because their subtree leaves have ≥ 2 distinct preceding characters), filtered to length ≥ the minimum.
- Add an `MREPProb` runner reading `s` from `mrep_data.txt` and printing the maximal repeats (length ≥ 20) through `IO`.
- Reuse existing infrastructure: `bio.domain.nucleic.DnaString`.

## Capabilities

### New Capabilities
- `maximal-repeats`: Identify all maximal repeats of a DNA string of length at least a given minimum, via left-diverse internal nodes of its suffix tree (Rosalind MREP).

### Modified Capabilities
<!-- None. MREP adds a new capability and does not change any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.graph`): `MaximalRepeatProblem`, `MaximalRepeatProblemError`, `MaximalRepeats` (result).
- **New algorithm** (`bio.algorithms.graph.IdentifyMaximalRepeats`) with a private structured suffix-tree builder (the SUFF builder augmented to track leaf suffix positions).
- **New runner** (`bio.problems.MREPProb`) reading `src/main/scala/resources/mrep_data.txt`.
- **Reused, unchanged**: `bio.domain.nucleic.DnaString`.
- **Tests**: new specs under `bio.domain.graph` and `bio.algorithms.graph`. No existing tests change.
