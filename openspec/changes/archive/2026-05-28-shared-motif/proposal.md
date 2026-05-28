## Why

Rosalind problem 38 ("Finding a Shared Motif", LCSM) takes a collection of DNA strings and asks for a *longest common substring* — a contiguous run of nucleotides that appears in **every** string in the collection, with maximum length. This is the multi-string generalization of `motif-finding` (SUBS, spec 9): SUBS locates one fixed pattern in one source, LCSM searches for the longest pattern that *all* sources share. Biologically this is the workhorse for conserved-motif discovery (finding sequence motifs preserved across species). Adding it gives the framework its first *multi-string substring-search* primitive and completes Rosalind problem 38 — the next problem after the now-archived SSEQ (spec 37).

## What Changes

- Add a new validated domain type `bio.domain.analysis.SharedMotifProblem` wrapping a `Vector[DnaString]` collection of source DNA strings. The smart constructor enforces:
  - `1 <= size <= 100` (Rosalind cap; empty collection is rejected as `EmptyCollection`),
  - every string length `<= 1000` (the per-string Rosalind cap, first violator wins as `StringTooLong(index, length, max)`).
  Empty strings within the collection are accepted (they trivially short-circuit the result to `""`).
- Add a new algorithm object `bio.algorithms.analysis.SharedMotif` exposing `find(problem: SharedMotifProblem): String`. Returns a longest substring common to every string in the collection — choosing the *lexicographically-smallest* such substring when multiple longest matches exist (so tests are deterministic and the canonical Rosalind sample's `"AC"` is the result, beating `"CA"` and `"TA"`). Empty string `""` is returned when no character is shared (or when the collection contains an empty string).
- Algorithm shape: binary search over length `L` from `1..|shortest|`; for each `L`, build a `Set[String]` of length-`L` substrings of every input string and intersect. If the intersection is non-empty, `L` is feasible; else not. After binary search settles, return the lex-smallest substring at the maximum feasible `L`. Complexity `O(k · n · log n)` time, `O(k · n)` memory.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `SharedMotifProblemSpec`: accepts the canonical 3-string Rosalind sample; accepts a single string; accepts 100 strings at the upper boundary; accepts a collection containing an empty string; rejects an empty collection as `EmptyCollection`; rejects a 1001-char string as `StringTooLong(idx, 1001, 1000)`; companion `apply`/`copy` leak-proofness.
  - `SharedMotifSpec`: canonical Rosalind sample → `"AC"` (lex-smallest of `{AC, CA, TA}` at length 2); single string `"ACGT"` → `"ACGT"`; two identical strings → that string; two strings with no shared character → `""`; collection containing an empty string → `""`; collection where the LCS is a single character → that single character; collection with one shared run plus distractors → the shared run.
- No changes to existing capabilities; no breaking changes. `DnaString` is reused as-is.

## Capabilities

### New Capabilities
- `shared-motif`: Finds a longest common substring of a collection of DNA strings (Rosalind LCSM). Includes the validated `SharedMotifProblem` input bundle (collection-size and per-string length caps) and the `SharedMotif.find` `O(k · n · log n)` binary-search + set-intersection algorithm returning the lex-smallest longest common substring as `String`.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/analysis/SharedMotifProblem.scala`
  - `src/main/scala/bio/domain/analysis/SharedMotifProblemError.scala`
  - `src/main/scala/bio/algorithms/analysis/SharedMotif.scala`
  - `src/test/scala/bio/domain/analysis/SharedMotifProblemSpec.scala`
  - `src/test/scala/bio/algorithms/analysis/SharedMotifSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Slots into the existing `bio.{algorithms,domain}.analysis` family alongside `motif-finding` (SUBS, spec 9), `spliced-motif` (SSEQ, spec 37), `hamming-distance` (HAMM, spec 10), `random-string-matching` (PROB, spec 19), `failure-array` (KMP, spec 31), and `genetic-character-table` (spec 33) — the framework's "string-analysis" subdomain.
