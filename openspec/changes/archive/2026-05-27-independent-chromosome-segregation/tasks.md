## 1. ChromosomePairsError ADT (TDD)

- [x] 1.1 Write `ChromosomePairsErrorSpec` (package `bio.domain.genetics`) covering all 3 scenarios: `NonPositive(0)` field exposure; `NonPositive(-5)` field exposure; `ExceedsMaximum(51, 50)` field exposure (both `value` and `max`); run, observe red
- [x] 1.2 Implement `bio.domain.genetics.ChromosomePairsError` sealed trait with `NonPositive(value: Int)` and `ExceedsMaximum(value: Int, max: Int)` cases; run all tests green
- [x] 1.3 Refactor pass: scaladoc describing both error cases

## 2. ChromosomePairs value type (TDD)

- [x] 2.1 Write `ChromosomePairsSpec` covering all 8 scenarios from the spec: accept n=1 (minimum); accept n=5 (Rosalind sample); accept n=50 (upper bound); reject n=0 as NonPositive; reject n=-3 as NonPositive; reject n=51 as ExceedsMaximum; validation order (lower bound first); `assertDoesNotCompile` for direct `ChromosomePairs(5)`; run, observe red
- [x] 2.2 Implement `bio.domain.genetics.ChromosomePairs` as `sealed abstract case class ChromosomePairs(value: Int)` with `from` smart constructor enforcing `1 <= value <= 50` (lower bound first); run all tests green
- [x] 2.3 Refactor pass: scaladoc describing both validation rules, validation order, the haploid-pair-count meaning, and the `sealed abstract case class` pattern

## 3. IndependentSegregation.logProbs algorithm (TDD)

- [x] 3.1 Write `IndependentSegregationSpec` (package `bio.algorithms.genetics`) covering all 7 scenarios: Rosalind sample n=5 → canonical 10-element vector within 0.001 abs error; n=1 → [log10(0.75), log10(0.25)] within 1e-9; result length = 2*n (using n=7 → length 14); last entry at n=50 ≈ -30.103 (within 1e-6 of `-100*log10(2)`); first entry at n=10 ≈ `log10(1 - 0.5^20)` (within 1e-6); upper bound n=50 yields a length-100 vector with every entry ≤ 0; entries monotonically non-increasing for n=5. Run, observe red
- [x] 3.2 Implement `bio.algorithms.genetics.IndependentSegregation.logProbs(pairs: ChromosomePairs): Vector[Double]` per design.md: PMF recurrence (`pmf(k+1) = pmf(k) * (2n-k) / (k+1)` starting from `pmf(0) = Math.pow(0.5, 2n)`), upper-tail right-to-left sweep using a local `Array[Double]`, then element-wise `Math.log10`; run all tests green
- [x] 3.3 Refactor pass: scaladoc describing the PMF recurrence (avoiding `C(2n,k)` blowup), the right-to-left upper-tail sweep (numerical stability vs. `1 - lower-tail`), the `Math.pow(0.5, 2n)` exactness, and the local `Array` choice for the running accumulator

## 4. Whole-suite verification

- [x] 4.1 Run `sbt test` and confirm all existing suites plus the new tests pass with no warnings
- [x] 4.2 Verify only additive changes (`git status` should show only new files under `bio/domain/genetics/`, `bio/algorithms/genetics/`, and matching test directories)
