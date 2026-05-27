## 1. WrightFisherProblemError ADT (TDD)

- [x] 1.1 Write `WrightFisherProblemErrorSpec` (package `bio.domain.genetics`) covering field-exposure for all 8 cases: `NonPositiveN(0)`, `NExceedsMaximum(8, 7)`, `NonPositiveM(0)`, `MExceedsTotalAlleles(9, 8)`, `NonPositiveG(0)`, `GExceedsMaximum(7, 6)`, `NonPositiveK(0)`, `KExceedsTotalAlleles(9, 8)`; run, observe red
- [x] 1.2 Implement `bio.domain.genetics.WrightFisherProblemError` sealed trait with the 8 final case classes described above; run all tests green
- [x] 1.3 Refactor pass: scaladoc describing each case, the "value + max" payload pattern, and why `MExceedsTotalAlleles` / `KExceedsTotalAlleles` use that wording rather than generic `ExceedsMaximum`

## 2. WrightFisherProblem bundle (TDD)

- [x] 2.1 Write `WrightFisherProblemSpec` covering all 17 scenarios from the spec: 4 accept scenarios (Rosalind sample (4,6,2,1); minimum (1,1,1,1); upper bound (7,14,6,14); m at 2n boundary (4,8,1,1)); 8 reject scenarios (one per error case); 4 validation-order scenarios (n-lower-first; n-upper before m; m-upper before g/k; g-upper before k); 1 compile-error scenario for direct apply; run, observe red
- [x] 2.2 Implement `bio.domain.genetics.WrightFisherProblem` as `sealed abstract case class` with `from(n, m, g, k)` smart constructor enforcing `1 <= n <= 7 → 1 <= m <= 2n → 1 <= g <= 6 → 1 <= k <= 2n` in that order; run all tests green
- [x] 2.3 Refactor pass: scaladoc describing each rule, the 4-stage validation order (with explanation that `n` must come first because `m` and `k` cross-constraints depend on it), and the `sealed abstract case class` pattern

## 3. WrightFisher.atLeast algorithm (TDD)

- [x] 3.1 Write `WrightFisherSpec` (package `bio.algorithms.genetics`) covering all 6 scenarios from the spec: Rosalind sample (4,6,2,1) → 0.7717925 within 0.001; absorbing state (4,8,1,1) → exactly 0.0; analytic spot-check (2,3,1,1) → 1 - 0.75^4 within 1e-9; monotonic-in-k property (compare (4,6,2,1) vs (4,6,2,2)); upper bound (7,7,6,7) → 0.5385700 within 0.001; output is always a valid Probability in [0,1] (sampling several problems). Run, observe red
- [x] 3.2 Implement `bio.algorithms.genetics.WrightFisher.atLeast(problem)` per design.md: build transition matrix `T[i][j] = binomialPmf(2n, i/(2n))[j]` via a private helper `binomialPmf(n, p): Vector[Double]` that uses the multiplicative recurrence (with special-cases for `p == 0.0` and `p == 1.0` to avoid `0/0`); start with a one-hot distribution at `m`; apply T `g` times via `foldLeft` over `(1 to problem.g)` doing `Vector.tabulate(states) { j => Σ_i dist(i) * T(i)(j) }`; sum the result over indices `[0, 2n - k]`; wrap in `Probability.unsafeFrom`; run all tests green
- [x] 3.3 Refactor pass: scaladoc describing the Markov-chain modeling, the transition-kernel construction, the absorbing-states behavior at `p=0`/`p=1`, and the `Probability.unsafeFrom` justification (sub-distribution sums are always in [0,1])

## 4. Whole-suite verification

- [x] 4.1 Run `sbt test` and confirm all existing suites plus the new tests pass with no warnings
- [x] 4.2 Verify only additive changes (`git status` should show only new files under `bio/domain/genetics/`, `bio/algorithms/genetics/`, and matching test directories)
