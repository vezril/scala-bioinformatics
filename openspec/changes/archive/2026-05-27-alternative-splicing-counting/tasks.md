## 1. CombinationSumProblemError ADT (TDD)

- [x] 1.1 Write `CombinationSumProblemErrorSpec` (package `bio.domain.combinatorics`) covering field-exposure for all 4 cases: `NegativeN(-1)`, `NExceedsMaximum(2001, 2000)`, `NegativeM(-3)`, `MExceedsN(5, 3)`; run, observe red
- [x] 1.2 Implement `bio.domain.combinatorics.CombinationSumProblemError` sealed trait with `NegativeN(value: Int)`, `NExceedsMaximum(value: Int, max: Int)`, `NegativeM(value: Int)`, `MExceedsN(m: Int, n: Int)` cases; run all tests green
- [x] 1.3 Refactor pass: scaladoc describing each error case and the "lower bound is 0, not 1" framing

## 2. CombinationSumProblem bundle (TDD)

- [x] 2.1 Write `CombinationSumProblemSpec` covering all 12 scenarios from the spec: accept Rosalind sample (6,3); accept minimum bounds (0,0); accept upper bound (2000,2000); accept equal m=n at mid-range (5,5); reject negative n (-1,0) as NegativeN; reject n>2000 (2001,0) as NExceedsMaximum; reject negative m (10,-1) as NegativeM; reject m>n (3,5) as MExceedsN; validation order n-lower-first (-1,-1); validation order n-upper before m-checks (2001,-1); validation order m-lower before cross-constraint (3,-5); `assertDoesNotCompile` for direct `CombinationSumProblem(6, 3)`; run, observe red
- [x] 2.2 Implement `bio.domain.combinatorics.CombinationSumProblem` as `sealed abstract case class` with `from` smart constructor enforcing `n >= 0 → n <= 2000 → m >= 0 → m <= n` in that order; run all tests green
- [x] 2.3 Refactor pass: scaladoc describing each validation rule, the validation order, the "0 is valid" lower bound, and the `sealed abstract case class` pattern

## 3. Combinations.sumFrom algorithm (TDD)

- [x] 3.1 Write `CombinationsSpec` (package `bio.algorithms.combinatorics`) covering all 7 scenarios from the spec: Rosalind sample (6,3) → 42; (6,6) → 1; (6,0) → 64; (0,0) → 1; (10,5) → 638; upper bound (2000,0) → 29376; upper bound (2000,2000) → 1. Run, observe red
- [x] 3.2 Implement `bio.algorithms.combinatorics.Combinations.sumFrom(problem: CombinationSumProblem): Int` per design.md: build row `n` of Pascal's triangle modulo `1_000_000` via `foldLeft` over `1 to n` (each step: `Vector(1) ++ inner-pairwise-sums ++ Vector(1)`), then sum from index `m` to `n` accumulating modulo. Helper `buildModRow(n, modulus)` private; run all tests green
- [x] 3.3 Refactor pass: scaladoc describing the row-by-row Pascal idiom, the O(n²) trade-off, the `Int`-safety analysis (worst intermediate = 999_999 + 999_999 well within `Int.MaxValue`), and the "modular inverse doesn't apply because 10^6 isn't prime" decision

## 4. Whole-suite verification

- [x] 4.1 Run `sbt test` and confirm all existing suites plus the new tests pass with no warnings
- [x] 4.2 Verify only additive changes (`git status` should show only new files under `bio/domain/combinatorics/`, `bio/algorithms/combinatorics/`, and matching test directories)
