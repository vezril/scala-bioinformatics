## 1. PerfectMatchingProblem ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/nucleic/PerfectMatchingProblemSpec.scala` covering: accepts the canonical Rosalind sample `AGCUAGUCAU` with `auCount == 3` and `cgCount == 2`; accepts the empty RNA string with both counts `0`; accepts 80-char input `"AU" * 40`; rejects 81-char input as `ExceedsMaxLength(81, 80)`; rejects `"AAU"` as `UnpairedAU(2, 1)`; rejects `"CCG"` as `UnpairedCG(2, 1)`; companion `apply` and `copy` leak-proofness via `assertDoesNotCompile`. Use a private `rna(s: String): RnaString` helper that calls `RnaString.from(s).getOrElse(sys.error(...))`. Run `sbt Test/compile` and confirm Red.
- [x] 1.2 Create `src/main/scala/bio/domain/nucleic/PerfectMatchingProblemError.scala` as a `sealed trait` with cases `ExceedsMaxLength(length: Int, max: Int)`, `UnpairedAU(aCount: Int, uCount: Int)`, `UnpairedCG(cCount: Int, gCount: Int)`.
- [x] 1.3 Create `src/main/scala/bio/domain/nucleic/PerfectMatchingProblem.scala` as `sealed abstract case class PerfectMatchingProblem(rna: RnaString, auCount: Int, cgCount: Int)` with companion `from(rna: RnaString): Either[PerfectMatchingProblemError, PerfectMatchingProblem]`. The companion's private `countSymbols(s: String)` does a single pass returning `(a, c, g, u)`. Validation order: length cap → AU balance → CG balance.
- [x] 1.4 Run `sbt testOnly bio.domain.nucleic.PerfectMatchingProblemSpec` and confirm Green.

## 2. PerfectMatching algorithm (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/algorithms/nucleic/PerfectMatchingSpec.scala` covering: canonical Rosalind sample → `BigInt(12)`; empty input → `BigInt(1)`; single `AU` → `BigInt(1)`; single `CG` → `BigInt(1)`; `AAUU` → `BigInt(2)`; `CCGG` → `BigInt(2)`; 40-pair maximum `"AU" * 40` → `BigInt("815915283247897734345611269596115894272000000000")` (= 40!). Use a private `fixture(s: String): PerfectMatchingProblem` helper. Run `sbt Test/compile` and confirm Red.
- [x] 2.2 Create `src/main/scala/bio/algorithms/nucleic/PerfectMatching.scala` exposing `def count(problem: PerfectMatchingProblem): BigInt = factorial(problem.auCount) * factorial(problem.cgCount)`. Private `factorial(n: Int): BigInt` via `(BigInt(1) to BigInt(n)).foldLeft(BigInt(1))(_ * _)`. Scaladoc cites Rosalind PMCH, the closed-form decoupling argument (`(#A)! · (#C)!`), and the `BigInt` rationale (worst case `40!` overflows `Long`).
- [x] 2.3 Run `sbt testOnly bio.algorithms.nucleic.PerfectMatchingSpec` and confirm Green.

## 3. Refactor & Final Verification

- [x] 3.1 Re-read both new source files and tests; tighten Scaladoc, ensure naming and validation order match the rest of the framework, ensure no stray imports.
- [x] 3.2 Run `sbt test` and confirm the full suite passes with zero regressions.
