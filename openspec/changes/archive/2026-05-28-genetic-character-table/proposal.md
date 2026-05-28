## Why

Rosalind problem 33 ("Creating a Character Table from Genetic Strings") takes a collection of aligned, *characterizable* DNA strings (at most two distinct symbols per column) and emits the character-table representation of the SNPs they encode. Each column with both possible symbols and a non-trivial partition of the rows becomes a nontrivial character; trivial columns (every row the same, or only one row diverging) are filtered out. This is the natural sequel to the now-archived `character-table` (CSTR, spec 32) — same output format, completely different input: where CSTR derives characters from the *edges of a phylogenetic tree*, this spec derives them from the *positions of an aligned sequence matrix*. Adding it gives the framework both halves of the Rosalind "character table" pair and completes spec 33 — the next problem after the now-archived CSTR (spec 32).

## What Changes

- Add a new validated domain type `bio.domain.analysis.GeneticCharacterTableProblem` wrapping a `Vector[DnaString]`. The smart constructor enforces:
  - `1 <= size <= 100` (Rosalind cap),
  - every string has the same length `1 <= length <= 300` (so column-wise iteration is well-defined),
  - every column has at most 2 distinct symbols ("characterizable" — failing this rejects the input).
  The constructed value precomputes `length: Int` (the common row length) so the algorithm doesn't need to re-derive it.
- Add a new algorithm object `bio.algorithms.analysis.GeneticCharacterTable` exposing `compute(problem: GeneticCharacterTableProblem): Vector[String]`. For each column index `j` in `0 until problem.length`:
  - if the column has only one distinct symbol, skip (no character),
  - else partition the row indices by symbol. If either partition has size `< 2`, the character is trivial (a single row diverges) — skip,
  - else emit the row: bit `i` is `'1'` iff row `i` has the *same* symbol as row `0` at column `j`. This matches the Rosalind sample's column-1 row `"10110"` and column-2 row `"10100"` exactly.
  Rows are emitted in **column-input order** (column `0`, then column `1`, …) — a deterministic choice within the spec's "any order" license.
- Add ScalaTest coverage (Red-Green-Refactor) at both layers:
  - `GeneticCharacterTableProblemSpec`: accepts the canonical 5×8 Rosalind sample; accepts size-100 / length-300 at the upper boundary; rejects size 101 as `TooManyStrings`; rejects length-301 as `StringTooLong`; rejects mismatched row lengths as `InconsistentLength`; rejects a 3-symbol column as `NonCharacterizable`; rejects an empty input as `EmptyInput`; `apply`/`copy` leak-proofness.
  - `GeneticCharacterTableSpec`: canonical Rosalind sample → `Vector("10110", "10100")`; an all-identical matrix yields `Vector.empty`; a matrix with a single nontrivial column yields one row; an all-trivial-column matrix yields `Vector.empty`; the row-length invariant — every emitted row has length `problem.size`.
- No changes to existing capabilities; no breaking changes. `DnaString` is reused as-is (with the 100 kbp cap raised in the prior `failure-array` change — far above this spec's 300 bp).

## Capabilities

### New Capabilities
- `genetic-character-table`: Computes the character-table representation of an aligned, characterizable DNA matrix. Includes the validated `GeneticCharacterTableProblem` input bundle (size, length, characterizability checks) and the `GeneticCharacterTable.compute` column-wise algorithm.

### Modified Capabilities
<!-- None — purely additive. -->

## Impact

- New files:
  - `src/main/scala/bio/domain/analysis/GeneticCharacterTableProblem.scala`
  - `src/main/scala/bio/domain/analysis/GeneticCharacterTableProblemError.scala`
  - `src/main/scala/bio/algorithms/analysis/GeneticCharacterTable.scala`
  - `src/test/scala/bio/domain/analysis/GeneticCharacterTableProblemSpec.scala`
  - `src/test/scala/bio/algorithms/analysis/GeneticCharacterTableSpec.scala`
- No public API changes to existing modules.
- No new third-party dependencies.
- Slots into the existing `bio.{algorithms,domain}.analysis` family alongside `motif-finding`, `hamming-distance`, `random-string-matching`, `failure-array` — all "column-wise / position-wise string analysis" siblings.
