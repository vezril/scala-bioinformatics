## 1. RandomMatchProblemError ADT (TDD)

- [x] 1.1 Write `RandomMatchProblemErrorSpec` covering `DnaTooLong(150, 100)` field exposure and `TooManyGcContents(25, 20)` field exposure; run, observe red
- [x] 1.2 Implement `bio.domain.analysis.RandomMatchProblemError` sealed trait with `DnaTooLong(length: Int, max: Int)` and `TooManyGcContents(size: Int, max: Int)` cases; run all tests green
- [x] 1.3 Refactor pass: scaladoc describing both error cases

## 2. RandomMatchProblem bundle (TDD)

- [x] 2.1 Write `RandomMatchProblemSpec` covering all 9 scenarios from the spec: Rosalind sample accepted; empty DNA accepted; empty GC array accepted; upper-bound DNA length 100 accepted; upper-bound GC count 20 accepted; DNA length 101 rejected (`DnaTooLong(101, 100)`); GC count 21 rejected (`TooManyGcContents(21, 20)`); validation order (DNA length before GC count); `assertDoesNotCompile` for direct `RandomMatchProblem(dna, gcContents)`; run, observe red
- [x] 2.2 Implement `bio.domain.analysis.RandomMatchProblem` as `sealed abstract case class RandomMatchProblem(dna: DnaString, gcContents: Vector[Probability])` with `from` smart constructor enforcing `MaxDnaLength = 100` then `MaxGcContents = 20`; run all tests green
- [x] 2.3 Refactor pass: scaladoc describing both validation rules, the validation order (DNA first), and the `sealed abstract case class` pattern

## 3. RandomMatch.logProbabilities algorithm (TDD)

- [x] 3.1 Write `RandomMatchSpec` covering all 6 scenarios from the spec: Rosalind sample produces `Vector(-5.737, -5.217, -5.263, -5.360, -5.958, -6.628, -7.009)` within 0.001 absolute error; empty DNA → `Vector(0.0, 0.0, 0.0)` for three GC values; empty GC array → `Vector.empty`; `gc=0` with `G` → `Vector(Double.NegativeInfinity)`; `gc=1` with `A` → `Vector(Double.NegativeInfinity)`; single `G` at `gc=0.5` → `Vector(d)` with `|d - log10(0.25)| < 1e-9`; result length equals `gcContents.size` (7 in/7 out); run, observe red
- [x] 3.2 Implement `bio.algorithms.analysis.RandomMatch.logProbabilities(problem)` per design.md: outer `.map` over `problem.gcContents`, inner `foldLeft` over `problem.dna.value` summing `Math.log10` of per-character symbol probability (`G`/`C` → `x/2`, `A`/`T` → `(1−x)/2`); run all tests green
- [x] 3.3 Refactor pass: scaladoc describing the sum-of-logs choice (underflow-safe), the symbol-probability table, and the `Double.NegativeInfinity` behavior at `gc=0` / `gc=1` edges

## 4. Whole-suite verification

- [x] 4.1 Run `sbt test` and confirm all existing suites plus all new tests pass with no warnings
- [x] 4.2 Verify only additive changes (`git status` should show only new files under `bio/domain/analysis/`, `bio/algorithms/analysis/`, and matching test directories)
