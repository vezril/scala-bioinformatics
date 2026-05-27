## 1. WrightFisherExpectationProblemError ADT (TDD)

- [x] 1.1 Write `WrightFisherExpectationProblemErrorSpec` (package `bio.domain.genetics`) covering field-exposure for all 3 cases: `NonPositiveN(0)`, `NExceedsMaximum(1000001, 1000000)`, `TooManyProbabilities(21, 20)`; run, observe red
- [x] 1.2 Implement `bio.domain.genetics.WrightFisherExpectationProblemError` sealed trait with `NonPositiveN(value: Int)`, `NExceedsMaximum(value: Int, max: Int)`, `TooManyProbabilities(size: Int, max: Int)` cases; run all tests green
- [x] 1.3 Refactor pass: scaladoc describing each case and the `TooManyProbabilities` naming choice (mirrors `MExceedsTotalAlleles` from spec 26's descriptive style)

## 2. WrightFisherExpectationProblem bundle (TDD)

- [x] 2.1 Write `WrightFisherExpectationProblemSpec` covering all 12 scenarios from the spec: 5 accept (Rosalind sample; minimum n=1 with single-p; upper-bound n=1000000 with empty p; upper-bound p size 20; empty p); 3 reject (n=0; n=1000001; p.size=21); 2 validation-order (n-lower before n-upper; n-upper before p-size); 1 compile-error scenario; run, observe red
- [x] 2.2 Implement `bio.domain.genetics.WrightFisherExpectationProblem` as `sealed abstract case class WrightFisherExpectationProblem(n: Int, p: Vector[Probability])` with `from(n, p)` enforcing `1 <= n <= 1_000_000` then `p.size <= 20`; run all tests green
- [x] 2.3 Refactor pass: scaladoc describing each validation rule, the 3-stage validation order, the "empty p is allowed" decision, and the `sealed abstract case class` pattern

## 3. WrightFisher.expectedFrequencies algorithm (TDD)

- [x] 3.1 Write `WrightFisherExpectationSpec` (package `bio.algorithms.genetics`, separate from the existing `WrightFisherSpec` to keep test files focused per Rosalind problem) covering all 7 scenarios: Rosalind sample (17, [0.1, 0.2, 0.3]) → [1.7, 3.4, 5.1] within 0.001; empty p → empty output; p=[0.0] yields 0.0; p=[1.0] yields n exactly; upper-bound n=1000000 with p=[0.5] yields exactly 500000.0; result length matches input length (using p.size=20); monotonically non-decreasing for sorted ascending p. Run, observe red
- [x] 3.2 Extend the existing `bio.algorithms.genetics.WrightFisher` object with a new `expectedFrequencies(problem: WrightFisherExpectationProblem): Vector[Double]` method implemented as `problem.p.map(prob => problem.n.toDouble * prob.value)`; run all tests green (both `WrightFisherSpec` and `WrightFisherExpectationSpec` should pass)
- [x] 3.3 Refactor pass: scaladoc on the new method describing the closed-form `E[Bin(n,p)] = n * p`, the bare-`Vector[Double]` output choice (expected counts can exceed 1), and the fact that this method shares an object with `atLeast` without sharing state

## 4. Whole-suite verification

- [x] 4.1 Run `sbt test` and confirm all existing suites plus the new tests pass with no warnings — in particular verify the existing `WrightFisherSpec` (for `atLeast`) still passes unchanged
- [x] 4.2 Verify the modification to `WrightFisher.scala` is purely additive: `git diff src/main/scala/bio/algorithms/genetics/WrightFisher.scala` should show only the new `expectedFrequencies` method and any required new imports, with the existing `atLeast` body and `binomialPmf` helper untouched
- [x] 4.3 Verify `git status` shows: 2 new source files under `bio/domain/genetics/`, 1 modified `WrightFisher.scala`, 3 new test files (`WrightFisherExpectationProblemErrorSpec`, `WrightFisherExpectationProblemSpec`, `WrightFisherExpectationSpec`)
