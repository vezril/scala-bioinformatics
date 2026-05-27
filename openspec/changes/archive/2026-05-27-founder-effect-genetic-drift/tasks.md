## 1. WrightFisherFixationProblemError ADT (TDD)

- [x] 1.1 Write `WrightFisherFixationProblemErrorSpec` (package `bio.domain.genetics`) covering field-exposure for all 6 cases: `NonPositiveN(0)`, `NExceedsMaximum(101, 100)`, `NonPositiveM(0)`, `MExceedsMaximum(101, 100)`, `TooManyRecessiveCounts(101, 100)`, `RecessiveCountOutOfRange(2, 9, 8)`; run, observe red
- [x] 1.2 Implement `bio.domain.genetics.WrightFisherFixationProblemError` sealed trait with the 6 final case classes; run all tests green
- [x] 1.3 Refactor pass: scaladoc describing each case and the `RecessiveCountOutOfRange(index, value, max)` payload convention

## 2. WrightFisherFixationProblem bundle (TDD)

- [x] 2.1 Write `WrightFisherFixationProblemSpec` covering all 17 scenarios from the spec: 5 accept (Rosalind sample; minimum n=1,m=1,empty A; upper-bound n=100,m=100,|A|=100; boundary A[j]=2n; boundary A[j]=0); 8 reject (n=0; n=101; m=0; m=101; |A|=101; A[j]=-1 below range; A[j]>2n above range; first-offender-in-input-order); 2 validation-order (n-lower first; m-upper before A scan); 1 compile-error scenario; run, observe red
- [x] 2.2 Implement `bio.domain.genetics.WrightFisherFixationProblem` as `sealed abstract case class` with `from(n, m, recessiveCounts)` enforcing `1 <= n <= 100 → 1 <= m <= 100 → |A| <= 100 → first A[j] not in [0, 2n]`; run all tests green
- [x] 2.3 Refactor pass: scaladoc describing each validation rule, the 6-stage validation order, the "n must come first because A[j] range depends on 2n" reasoning, and the `sealed abstract case class` pattern

## 3. WrightFisher.fixationLogProbs algorithm (TDD)

- [x] 3.1 Write `WrightFisherFixationSpec` (package `bio.algorithms.genetics`, separate from `WrightFisherSpec` and `WrightFisherExpectationSpec`) covering all 7 scenarios: Rosalind sample (N=4, m=3, A=[0,1,2]) → exact 3×3 matrix within 1e-9 per element; A[j]=0 → 0.0 at every generation; A[j]=2n with N=1,m=1 → `Double.NegativeInfinity`; analytic spot-check (N=1, m=1, A=[1]) → `log10(0.25)` within 1e-9; output dimensions match input (m=5, |A|=7 → 5×7); empty A → m empty inner vectors; monotonically non-decreasing in generation per factor (A=[1] with m=3). Run, observe red
- [x] 3.2 Extend the existing `bio.algorithms.genetics.WrightFisher` object with a new method `fixationLogProbs(problem: WrightFisherFixationProblem): Vector[Vector[Double]]` per design.md: build transition matrix via existing `binomialPmf`, for each factor walk a one-hot distribution at state `2n − A[j]` forward `m` generations recording `Math.log10(dist(2n))` each step, then transpose factor-first results into the m × k matrix layout the spec requires; run all tests green (existing `WrightFisherSpec`, `WrightFisherExpectationSpec`, and the new `WrightFisherFixationSpec` all pass)
- [x] 3.3 Refactor pass: scaladoc on the new method describing the per-factor inner loop + transpose, the reuse of `binomialPmf`, the `Math.log10(0.0) = NegativeInfinity` behavior for the all-recessive absorbing state, and the fact that this is the third sibling method on `WrightFisher`

## 4. Whole-suite verification

- [x] 4.1 Run `sbt test` and confirm all existing suites plus the new tests pass with no warnings — verify the existing `WrightFisherSpec` (for `atLeast`) and `WrightFisherExpectationSpec` (for `expectedFrequencies`) still pass unchanged
- [x] 4.2 Verify the modification to `WrightFisher.scala` is purely additive: `git diff src/main/scala/bio/algorithms/genetics/WrightFisher.scala` should show only the import widening and the new `fixationLogProbs` method, with the existing `atLeast`, `expectedFrequencies`, and `binomialPmf` bodies untouched
- [x] 4.3 Verify `git status` shows: 2 new source files under `bio/domain/genetics/`, 1 modified `WrightFisher.scala`, 3 new test files (`WrightFisherFixationProblemErrorSpec`, `WrightFisherFixationProblemSpec`, `WrightFisherFixationSpec`)
