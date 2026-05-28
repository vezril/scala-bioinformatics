## 1. FailureArrayProblem ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/analysis/FailureArrayProblemSpec.scala` covering: accepts a 21-char DNA (`CAGCATGGTATCACAGCAGAG`); accepts a single-character DNA (`A`); rejects an empty `DnaString` as `EmptySequence`; companion `apply` does not leak (`assertDoesNotCompile`); `copy` does not leak (`assertDoesNotCompile`). Run `sbt Test/compile` and confirm Red.
- [x] 1.2 Create `src/main/scala/bio/domain/analysis/FailureArrayProblemError.scala` as a `sealed trait` with case `EmptySequence`.
- [x] 1.3 Create `src/main/scala/bio/domain/analysis/FailureArrayProblem.scala` as `sealed abstract case class FailureArrayProblem(dna: DnaString)` with companion `from(dna: DnaString): Either[FailureArrayProblemError, FailureArrayProblem]` enforcing non-empty `dna.value`.
- [x] 1.4 Run `sbt testOnly bio.domain.analysis.FailureArrayProblemSpec` and confirm Green.

## 2. FailureArray algorithm (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/algorithms/analysis/FailureArraySpec.scala` covering: canonical Rosalind sample `CAGCATGGTATCACAGCAGAG → Vector(0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 3, 4, 5, 3, 0, 0)`; single character `A → Vector(0)`; all-same `AAAAA → Vector(0, 1, 2, 3, 4)`; no-overlap `ACGT → Vector(0, 0, 0, 0)`; periodic `ACACACAC → Vector(0, 0, 1, 2, 3, 4, 5, 6)`; two-character `AA → Vector(0, 1)` and `AT → Vector(0, 0)`. Use a private `fixture(s: String): FailureArrayProblem` helper that constructs `DnaString.from(s)` then `FailureArrayProblem.from(_)` and `getOrElse(sys.error(...))`. Run `sbt Test/compile` and confirm Red.
- [x] 2.2 Create `src/main/scala/bio/algorithms/analysis/FailureArray.scala` exposing `def compute(problem: FailureArrayProblem): Vector[Int]`. Implementation: two-pointer KMP table-build over the underlying `String`, building a mutable `Array[Int]` for performance, then converting `.toVector` at the end. Scaladoc references Rosalind problem code (KMP) and the 0-indexed → Rosalind 1-indexed correspondence.
- [x] 2.3 Run `sbt testOnly bio.algorithms.analysis.FailureArraySpec` and confirm Green.

## 3. Refactor & Final Verification

- [x] 3.1 Re-read both new source files and tests; tighten Scaladoc, ensure naming and validation order match the rest of the framework, ensure no stray imports.
- [x] 3.2 Run `sbt test` and confirm the full suite passes with zero regressions.
