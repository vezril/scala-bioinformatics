## 1. SplicedMotifProblem ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/analysis/SplicedMotifProblemSpec.scala` covering: accepts the canonical Rosalind sample (`ACGTACGTGACG`, `GTA`); accepts empty source + empty target; accepts non-empty source + empty target; accepts both at 1000-char upper bound; rejects 1001-char source as `SourceTooLong(1001, 1000)`; rejects 1001-char target as `TargetTooLong(1001, 1000)`; companion `apply` and `copy` leak-proofness via `assertDoesNotCompile`. Use a private `dna(s: String): DnaString` helper. Run `sbt Test/compile` and confirm Red.
- [x] 1.2 Create `src/main/scala/bio/domain/analysis/SplicedMotifProblemError.scala` as a `sealed trait` with cases `SourceTooLong(length: Int, max: Int)` and `TargetTooLong(length: Int, max: Int)`.
- [x] 1.3 Create `src/main/scala/bio/domain/analysis/SplicedMotifProblem.scala` as `sealed abstract case class SplicedMotifProblem(source: DnaString, target: DnaString)` with companion `from(source: DnaString, target: DnaString): Either[SplicedMotifProblemError, SplicedMotifProblem]` enforcing the 1000-char caps. Validation order: source length → target length.
- [x] 1.4 Run `sbt testOnly bio.domain.analysis.SplicedMotifProblemSpec` and confirm Green.

## 2. SplicedMotif algorithm (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/algorithms/analysis/SplicedMotifSpec.scala` covering: canonical Rosalind sample → `Some(Vector(3, 4, 5))` (greedy answer); empty target → `Some(Vector.empty)`; source equals target `ACGT` → `Some(Vector(1, 2, 3, 4))`; no-solution (source `AAA`, target `AAAA`) → `None`; target at end of source (`ACGT`, `T`) → `Some(Vector(4))`; empty source + empty target → `Some(Vector.empty)`; empty source + non-empty target → `None`; repeated target chars (`AACGAACG`, `AAAA`) → `Some(Vector(1, 2, 5, 6))`. Use a private `fixture(source: String, target: String): SplicedMotifProblem` helper. Run `sbt Test/compile` and confirm Red.
- [x] 2.2 Create `src/main/scala/bio/algorithms/analysis/SplicedMotif.scala` exposing `def find(problem: SplicedMotifProblem): Option[Vector[Int]]`. Implementation: single-pass two-pointer walk maintaining `i` over `source.value` and `j` over `target.value`; on character match, record `i + 1` and advance both pointers; otherwise advance only `i`. After the loop, return `Some(indices)` iff `j == target.value.length`; else `None`. Scaladoc cites Rosalind SSEQ, the leftmost-greedy convention (and that the Rosalind published `3 8 10` is also valid), the `O(|s| + |t|)` complexity, and contrasts with the substring-search sister `MotifLocations` (SUBS, spec 9).
- [x] 2.3 Run `sbt testOnly bio.algorithms.analysis.SplicedMotifSpec` and confirm Green.

## 3. Refactor & Final Verification

- [x] 3.1 Re-read both new source files and tests; tighten Scaladoc, ensure naming and validation order match the rest of the framework, ensure no stray imports.
- [x] 3.2 Run `sbt test` and confirm the full suite passes with zero regressions.
