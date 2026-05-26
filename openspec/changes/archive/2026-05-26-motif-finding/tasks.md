## 1. MotifLocations algorithm (TDD)

- [x] 1.1 Write failing tests in `src/test/scala/bio/algorithms/analysis/MotifLocationsSpec.scala` for `MotifLocations.find` covering: Rosalind sample (`GATATATGCATATACTT` / `ATAT` → `Vector(2, 4, 10)`), motif at start (`ACGTACGT` / `AC` → first element is `1`), motif at end (`ACGT` / `GT` → `Vector(3)`), overlapping all-matches (`AAAA` / `AA` → `Vector(1, 2, 3)`), text equal to motif (`ACGT` / `ACGT` → `Vector(1)`), no match (`AAAA` / `GG` → empty), motif longer than text (`AC` / `ACGT` → empty), empty text + non-empty motif (empty / `AC` → empty), empty motif (`ACGT` / empty → empty), empty motif + empty text (empty / empty → empty)
- [x] 1.2 Run `sbt test` — confirm tests fail (Red) because `MotifLocations` does not yet exist
- [x] 1.3 Implement `src/main/scala/bio/algorithms/analysis/MotifLocations.scala` in `bio.algorithms.analysis`. Signature: `def find(text: DnaString, motif: DnaString): Vector[Int]`. Implementation: if `motif.value.isEmpty` return `Vector.empty`; otherwise iterate `i` from `0` to `text.value.length - motif.value.length` inclusive, keeping indices where `text.value.startsWith(motif.value, i)` is true, mapping each kept index to `i + 1`, collecting into a `Vector[Int]`. No `var`, no mutable collections. Import `bio.domain.nucleic.DnaString`.
- [x] 1.4 Run `sbt test` — confirm `MotifLocationsSpec` passes (Green)
- [x] 1.5 Refactor: confirm no `var`, no mutable collections, no unnecessary intermediate allocations; the sliding-window scan is expressed via `Iterator`/`Range` + `filter` + `map`
- [x] 1.6 Run `sbt test` — confirm all tests still pass after refactor

## 2. Final Verification

- [x] 2.1 Run `sbt clean compile` — zero errors, zero warnings
- [x] 2.2 Run `sbt test` — all tests pass (count higher than 190)
- [x] 2.3 Verify `MotifLocations.find` returns 1-based positions consistently across all test cases (no off-by-one regressions)
- [x] 2.4 Verify the file lives at `src/main/scala/bio/algorithms/analysis/MotifLocations.scala` and its `package` declaration is `bio.algorithms.analysis`
