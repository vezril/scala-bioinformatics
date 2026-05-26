## 1. HammingError ADT

- [x] 1.1 Create `src/main/scala/bio/domain/analysis/HammingError.scala` with `package bio.domain.analysis`, `sealed trait HammingError`, and case `final case class LengthMismatch(lengthA: Int, lengthB: Int)`
- [x] 1.2 Run `sbt compile` — confirm clean compile

## 2. HammingDistance algorithm (TDD)

- [x] 2.1 Write failing tests in `src/test/scala/bio/algorithms/analysis/HammingDistanceSpec.scala` for `HammingDistance.between` covering: Rosalind sample (`GAGCCTACTAACGGGAT` / `CATCGTAATGACGGCCT` → `Right(7)`), identical strings (`ACGT` / `ACGT` → `Right(0)`), two empty DnaStrings → `Right(0)`, all-different (`AAAA` / `TTTT` → `Right(4)`), single-difference (`ACGT` / `ACGA` → `Right(1)`), symmetry (`between(a, b) == between(b, a)` for an equal-length pair), length-mismatch non-empty (`ACGT` / `ACGTAA` → `Left(LengthMismatch(4, 6))`), empty + non-empty → `Left(LengthMismatch(0, 2))`, non-empty + empty → `Left(LengthMismatch(2, 0))`
- [x] 2.2 Run `sbt test` — confirm tests fail (Red) because `HammingDistance` does not yet exist
- [x] 2.3 Implement `src/main/scala/bio/algorithms/analysis/HammingDistance.scala` in `bio.algorithms.analysis`. Signature: `def between(a: DnaString, b: DnaString): Either[HammingError, Int]`. Logic: if `a.value.length != b.value.length` return `Left(HammingError.LengthMismatch(a.value.length, b.value.length))`; otherwise return `Right(a.value.lazyZip(b.value).count { case (x, y) => x != y })`. Imports: `bio.domain.nucleic.DnaString`, `bio.domain.analysis.HammingError`. No `var`, no mutable collections.
- [x] 2.4 Run `sbt test` — confirm `HammingDistanceSpec` passes (Green)
- [x] 2.5 Refactor: confirm no `var`, idiomatic FP style (single-expression body acceptable); algorithm completes in a single pass over the inputs
- [x] 2.6 Run `sbt test` — confirm all tests still pass after refactor

## 3. Final Verification

- [x] 3.1 Run `sbt clean compile` — zero errors, zero warnings
- [x] 3.2 Run `sbt test` — all tests pass (count higher than 200)
- [x] 3.3 Verify the new files reside at `src/main/scala/bio/domain/analysis/HammingError.scala` and `src/main/scala/bio/algorithms/analysis/HammingDistance.scala` with the expected `package` declarations
- [x] 3.4 Verify the algorithm dispatches on `Char` inequality directly (not through `DnaNucleotide.fromChar`), per the design decision — inputs are pre-validated `DnaString`s
