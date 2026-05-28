## 1. SharedMotifProblem ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/analysis/SharedMotifProblemSpec.scala` covering: accepts the canonical 3-string Rosalind sample (`GATTACA`, `TAGACCA`, `ATACA`); accepts a single-string collection; accepts 100 strings at the upper boundary; accepts a collection containing an empty string; rejects empty collection as `EmptyCollection`; rejects 101 strings as `TooManyStrings(101, 100)`; rejects a 1001-char string as `StringTooLong(0, 1001, 1000)`; companion `apply` and `copy` leak-proofness via `assertDoesNotCompile`. Use a private `dna(s: String): DnaString` helper. Run `sbt Test/compile` and confirm Red.
- [x] 1.2 Create `src/main/scala/bio/domain/analysis/SharedMotifProblemError.scala` as a `sealed trait` with cases `EmptyCollection`, `TooManyStrings(count: Int, max: Int)`, `StringTooLong(index: Int, length: Int, max: Int)`.
- [x] 1.3 Create `src/main/scala/bio/domain/analysis/SharedMotifProblem.scala` as `sealed abstract case class SharedMotifProblem(sequences: Vector[DnaString])` with companion `from(sequences: Vector[DnaString]): Either[SharedMotifProblemError, SharedMotifProblem]` running the 3-step validation (empty → upper-cap → per-string-length). Use `iterator.zipWithIndex.collectFirst` for short-circuit per-string scan.
- [x] 1.4 Run `sbt testOnly bio.domain.analysis.SharedMotifProblemSpec` and confirm Green.

## 2. SharedMotif algorithm (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/algorithms/analysis/SharedMotifSpec.scala` covering: canonical Rosalind sample → `"AC"`; single-string collection → that string; two identical strings → that string; no shared character → `""`; collection containing an empty string → `""`; only single-char LCS exists (`ACGT, ATAT`) → `"A"`; one shared length-3 run with distractors (`CCGTAGG, AAGTACC, TTGTAGT`) → `"GTA"`. Use a private `fixture(strings: String*): SharedMotifProblem` helper. Run `sbt Test/compile` and confirm Red.
- [x] 2.2 Create `src/main/scala/bio/algorithms/analysis/SharedMotif.scala` exposing `def find(problem: SharedMotifProblem): String`. Implementation: short-circuit to `""` if any input is empty; otherwise binary-search length `L` over `1..|shortest|`; for each candidate `L`, build `Set[String]` of length-`L` substrings of each input and intersect; if non-empty, raise `lo`; else lower `hi`. Track the best non-empty intersection seen so far and return `intersection.min` (lex-smallest) at the end. Scaladoc cites Rosalind LCSM, the binary-search + set-intersection approach, the lex-smallest tie-break rationale (and how it reproduces the canonical sample `"AC"`), and contrasts with the single-pattern `MotifLocations` (SUBS, spec 9) and the subsequence `SplicedMotif` (SSEQ, spec 37).
- [x] 2.3 Run `sbt testOnly bio.algorithms.analysis.SharedMotifSpec` and confirm Green.

## 3. Refactor & Final Verification

- [x] 3.1 Re-read both new source files and tests; tighten Scaladoc, ensure naming and validation order match the rest of the framework, ensure no stray imports.
- [x] 3.2 Run `sbt test` and confirm the full suite passes with zero regressions.
