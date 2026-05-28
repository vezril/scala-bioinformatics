## 1. MotzkinMatchingProblem ADT (Red phase first)

- [x] 1.1 Write `src/test/scala/bio/domain/nucleic/MotzkinMatchingProblemSpec.scala` covering: accepts the canonical sample `AUAU`; accepts the empty RNA string; accepts the 300-char input `"A" * 300`; accepts a single-character `A`; accepts an unbalanced AU `"AAU"` (which CAT would reject); accepts an odd-length `"AUC"`; rejects 301-char input as `ExceedsMaxLength(301, 300)`; companion `apply` and `copy` leak-proofness via `assertDoesNotCompile`. Use a private `rna(s: String): RnaString` helper. Run `sbt Test/compile` and confirm Red.
- [x] 1.2 Create `src/main/scala/bio/domain/nucleic/MotzkinMatchingProblemError.scala` as a `sealed trait` with the single case `ExceedsMaxLength(length: Int, max: Int)`.
- [x] 1.3 Create `src/main/scala/bio/domain/nucleic/MotzkinMatchingProblem.scala` as `sealed abstract case class MotzkinMatchingProblem(rna: RnaString)` with companion `from(rna: RnaString): Either[MotzkinMatchingProblemError, MotzkinMatchingProblem]` enforcing only the length cap (300). Scaladoc cites Rosalind MOTZ and explicitly notes the *broader* input contract vs PMCH/CAT — no balance check, odd lengths OK.
- [x] 1.4 Run `sbt testOnly bio.domain.nucleic.MotzkinMatchingProblemSpec` and confirm Green.

## 2. MotzkinMatching algorithm (Red phase first)

- [x] 2.1 Write `src/test/scala/bio/algorithms/nucleic/MotzkinMatchingSpec.scala` covering: canonical sample `AUAU → 7`; empty input → `1`; single character `A → 1`; single pair `AU → 2`; all-A `AAAA → 1`; three-A-one-U `AAAU → 4`; mixed `AUCG → 4`; `CGCG → 7`. Use a private `fixture(s: String): MotzkinMatchingProblem` helper. Run `sbt Test/compile` and confirm Red.
- [x] 2.2 Create `src/main/scala/bio/algorithms/nucleic/MotzkinMatching.scala` exposing `def count(problem: MotzkinMatchingProblem): Int`. Implementation: short-circuit empty input to `1`; otherwise allocate a 2D `Array[Int]` of size `n × n`; define `get(i, j)` returning `1` for `j < i`; fill by increasing interval length `len = 1, 2, ..., n` over `i = 0..n-len`. For each cell `(i, j)` with `j = i + len - 1`, sum `get(i+1, j)` (unbonded-`i` term) plus, for each `k` in `i+1..j` such that `isPair(s(i), s(k))`, `get(i+1, k-1).toLong * get(k+1, j)`. Take the result `% Mod` and cast to `Int`. Scaladoc cites Rosalind MOTZ, the recurrence, the unbonded-`i` extra term, the absence of the parity constraint vs CAT, the `Long`-mod trick, and cross-references both sister algorithms (`PerfectMatching` and `NoncrossingMatching`).
- [x] 2.3 Run `sbt testOnly bio.algorithms.nucleic.MotzkinMatchingSpec` and confirm Green.

## 3. Refactor & Final Verification

- [x] 3.1 Re-read both new source files and tests; tighten Scaladoc to (a) cite Rosalind problem 36 (MOTZ) and the Motzkin-flavoured recurrence with the unbonded-`i` term, (b) explicitly contrast with the sister CAT algorithm (perfect matching: no unbonded-`i` term, parity constraint), and (c) note that the values produced are *not* literal Motzkin numbers `M_n` (which assume the complete graph `K_n`) but the RNA-constrained variant. No stray imports.
- [x] 3.2 Run `sbt test` and confirm the full suite passes with zero regressions.
