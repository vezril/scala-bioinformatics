## Context

Rosalind problem 33 asks: given a collection of `≤ 100` aligned, *characterizable* DNA strings (each column has at most two distinct symbols), each of length `≤ 300`, return the character-table representation of the SNPs. Each "informative" column — where both symbols appear and neither side has only one row — encodes a binary character; the rest are trivial and excluded.

The canonical Rosalind sample is a 5×8 matrix:
```
ATGCTACC
CGTTTACC
ATTCGACC
AGTCTCCC
CGTCTATC
```
Of the 8 columns, only columns 0 and 1 split the 5 rows non-trivially (sizes 3/2 and 2/3 respectively). The expected output assigns `'1'` to the rows whose symbol at that column matches row 0's symbol, giving `"10110"` for column 0 (where row 0 is `A`) and `"10100"` for column 1 (where row 0 is `T`).

The framework already hosts `bio.{algorithms,domain}.analysis` for column-wise / position-wise string analysis (`MotifLocations`, `HammingDistance`, `RandomMatch`, `FailureArray`). This new feature is a clean drop-in there.

## Goals / Non-Goals

**Goals:**
- Provide a validated `GeneticCharacterTableProblem` ADT enforcing the Rosalind input contract (size, length, characterizability) so the algorithm can assume well-formed input.
- Provide `bio.algorithms.analysis.GeneticCharacterTable.compute` returning the nontrivial character rows in a deterministic order (column-input order).
- TDD coverage at both layers, including canonical Rosalind sample, structural edge cases (all-identical matrix, single-nontrivial-column matrix, all-trivial matrix), boundary-cap inputs, and four validation-failure cases.

**Non-Goals:**
- File ingestion. The Rosalind input is a plain newline-separated list of DNA strings — straightforward to read in a `Prob` runner via `IO.blocking` + `linesIterator`.
- Generic FASTA support. This spec's input is bare DNA strings without identifiers; FASTA wrapping is orthogonal.
- Generalisation to a non-DNA alphabet. The spec is explicit about DNA — reuse `DnaString` rather than introducing a more general `AlignedSequence` type.
- Rearrangement of rows / alternate row orderings. We pick column-input order and ship.

## Decisions

**1. Wrap `Vector[DnaString]`, not a raw 2D array or matrix type.**

Reuses the framework's validated DNA-sequence ADT (alphabet-validated by `DnaString.from`) and keeps the input bundle declarative — one field. Column iteration is then just `(0 until length).map(j => sequences.map(_.value.charAt(j)))`, no separate matrix abstraction needed. **Alternative considered:** a custom `AlignedDnaMatrix` ADT (rejected: introduces a sibling type that doesn't compose with the rest of the framework's `DnaString`-based algorithms).

**2. Validate characterizability eagerly in the smart constructor.**

The "≤ 2 distinct symbols per column" property is the defining feature of "characterizable" in the spec. Checking it once at construction (returning `NonCharacterizable(columnIndex, distinctCount)` on failure) means the algorithm can assume the property holds and the assertion lives in one place. The check is `O(rows × cols)` — at the upper-bound 100 × 300 that's 30 000 character reads, microseconds. **Alternative considered:** failing inside the algorithm on a non-characterizable column (rejected: scatters precondition logic across two files).

**3. Validation order: `EmptyInput` → size cap → length-cap (first oversized string) → consistent-length → characterizability.**

Each check assumes the previous holds. First-failure-wins. The order is "global → per-row → cross-row", which means error messages reference the most specific defect possible. The error type itself names the offending index where applicable.

**4. Row encoding: "match-row-0 ⇒ `1`".**

For each nontrivial column `j`, the bit at position `i` is `'1'` iff `sequence(i).charAt(j) == sequence(0).charAt(j)`. This is deterministic, matches the Rosalind sample, and side-steps the spec's noted "1/0 assignment is arbitrary" ambiguity. **Alternative considered:** lex-smaller-symbol gets `'1'` (rejected: less natural for SNP data and would not match the published sample row `"10100"` for column `T/G`).

**5. Row order: column-input order.**

Matches the Rosalind sample (column 0's row appears before column 1's row in the expected output) and is the most natural left-to-right reading of the input. **Alternative considered:** lex-sorting the output rows (rejected: less informative — the row order tells the reader *which column* generated it; alphabetic order destroys that signal).

**6. Place under `bio.{algorithms,domain}.analysis`.**

This is column-wise string analysis. `bio.{algorithms,domain}.graph` hosts the tree-based `character-table` (spec 32); the two character-table specs intentionally live in different subdomains because the *operation* differs (tree-edge-split enumeration vs column-wise symbol partitioning) even though the *output* shape is the same.

## Risks / Trade-offs

- **[Risk]** The choice of row-encoding rule ("match row 0") differs from spec 32's rule ("lex-first taxon side gets `0`"). Both are spec-permitted. A reader inspecting both modules might expect symmetry. → **Mitigation:** Scaladoc on `GeneticCharacterTable.compute` explicitly states the rule and cites the Rosalind sample as the rationale; the sister spec 32 algorithm's Scaladoc states *its* rule similarly. The two rules are genuinely different problems' conventions.
- **[Risk]** A row-length of 0 (empty `DnaString`) would slip through `DnaString.from` (which allows empty strings) — could cause an empty result without a meaningful character-table. → **Mitigation:** the smart constructor rejects `length == 0` via the length-cap path (lower bound `1`). Documented in Scaladoc.
- **[Trade-off]** No file-ingestion helper in this change. A caller's `Prob` runner can do `Files.readAllLines(p).map(DnaString.from)` itself — no need for a custom reader (the format is line-trivial). Future extension if a runner gets repetitive.
- **[Trade-off]** O(rows × cols) characterizability check at construction even when the input is known-clean. Acceptable at the 100 × 300 cap; would warrant a "trusted" constructor if scaled up.
