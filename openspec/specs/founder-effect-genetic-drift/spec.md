## Purpose
Define the founder-effect-genetic-drift capability: the `WrightFisherFixationProblem` validated parameter bundle (`n: Int`, `m: Int`, `recessiveCounts: Vector[Int]`, with bounds `1 ≤ n ≤ 100`, `1 ≤ m ≤ 100`, `|recessiveCounts| ≤ 100`, and `0 ≤ recessiveCounts(j) ≤ 2n`) and its six-case error ADT, plus a new method on the existing `WrightFisher` object — `fixationLogProbs(problem): Vector[Vector[Double]]` — computing per-generation per-factor `log10` fixation probabilities under the Wright-Fisher model. Serves the Rosalind "The Founder Effect and Genetic Drift" (FOUN) problem. The `WrightFisher` object now hosts three sibling methods (`atLeast` from `wright-fisher-genetic-drift`, `expectedFrequencies` from `wright-fisher-expectation`, and `fixationLogProbs` here), each with its own validated input bundle; the shared `binomialPmf` helper is reused across all three.

## Requirements

### Requirement: WrightFisherFixationProblemError is a sealed ADT of WrightFisherFixationProblem construction failures
The system SHALL provide a `sealed trait WrightFisherFixationProblemError` with cases `final case class NonPositiveN(value: Int)`, `final case class NExceedsMaximum(value: Int, max: Int)`, `final case class NonPositiveM(value: Int)`, `final case class MExceedsMaximum(value: Int, max: Int)`, `final case class TooManyRecessiveCounts(size: Int, max: Int)`, and `final case class RecessiveCountOutOfRange(index: Int, value: Int, max: Int)`. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: NonPositiveN carries the offending value
- **WHEN** `WrightFisherFixationProblemError.NonPositiveN(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: NExceedsMaximum carries the offending value and the maximum
- **WHEN** `WrightFisherFixationProblemError.NExceedsMaximum(101, 100)` is constructed
- **THEN** the value's `value` field equals `101` and `max` equals `100`

#### Scenario: NonPositiveM carries the offending value
- **WHEN** `WrightFisherFixationProblemError.NonPositiveM(0)` is constructed
- **THEN** the value's `value` field equals `0`

#### Scenario: MExceedsMaximum carries the offending value and the maximum
- **WHEN** `WrightFisherFixationProblemError.MExceedsMaximum(101, 100)` is constructed
- **THEN** the value's `value` field equals `101` and `max` equals `100`

#### Scenario: TooManyRecessiveCounts carries the offending size and the maximum
- **WHEN** `WrightFisherFixationProblemError.TooManyRecessiveCounts(101, 100)` is constructed
- **THEN** the value's `size` field equals `101` and `max` equals `100`

#### Scenario: RecessiveCountOutOfRange carries the offending index, value, and maximum (2n)
- **WHEN** `WrightFisherFixationProblemError.RecessiveCountOutOfRange(2, 9, 8)` is constructed (factor at index 2 has count 9 but max is 2*4=8)
- **THEN** the value's `index` field equals `2`, `value` equals `9`, and `max` equals `8`

### Requirement: WrightFisherFixationProblem is a validated parameter bundle for the Wright-Fisher fixation algorithm
The system SHALL provide a `sealed abstract case class WrightFisherFixationProblem(n: Int, m: Int, recessiveCounts: Vector[Int])`. Construction SHALL be possible only through `WrightFisherFixationProblem.from(n: Int, m: Int, recessiveCounts: Vector[Int]): Either[WrightFisherFixationProblemError, WrightFisherFixationProblem]` enforcing `1 <= n <= 100`, `1 <= m <= 100`, `recessiveCounts.size <= 100`, and `0 <= recessiveCounts(j) <= 2 * n` for every element. Validation SHALL apply in the order: `n` lower → `n` upper → `m` lower → `m` upper → `recessiveCounts.size` upper → first out-of-range element in `recessiveCounts` (first failure wins; element scan reports the lowest 0-indexed offending position). The synthesized `apply` and `copy` SHALL NOT be public — direct construction `WrightFisherFixationProblem(4, 3, Vector(0, 1, 2))` MUST be a compile error. The type SHALL reside in the `bio.domain.genetics` package.

#### Scenario: Rosalind sample parameters are accepted
- **WHEN** `WrightFisherFixationProblem.from(4, 3, Vector(0, 1, 2))` is called
- **THEN** the result is `Right(<WrightFisherFixationProblem with n=4, m=3, recessiveCounts=[0,1,2]>)`

#### Scenario: Minimum-bound parameters (n=1, m=1, empty array) are accepted
- **WHEN** `WrightFisherFixationProblem.from(1, 1, Vector.empty)` is called
- **THEN** the result is `Right(<WrightFisherFixationProblem with empty recessiveCounts>)`

#### Scenario: Upper-bound parameters (n=100, m=100, 100-element array) are accepted
- **WHEN** `WrightFisherFixationProblem.from(100, 100, Vector.fill(100)(0))` is called
- **THEN** the result is `Right(<WrightFisherFixationProblem at all upper bounds>)`

#### Scenario: A recessive count equal to 2n is accepted at the boundary
- **WHEN** `WrightFisherFixationProblem.from(4, 3, Vector(8))` is called (2n = 8)
- **THEN** the result is `Right(<WrightFisherFixationProblem>)`

#### Scenario: A recessive count of 0 is accepted at the boundary
- **WHEN** `WrightFisherFixationProblem.from(4, 3, Vector(0))` is called
- **THEN** the result is `Right(<WrightFisherFixationProblem>)`

#### Scenario: Non-positive n is rejected as NonPositiveN
- **WHEN** `WrightFisherFixationProblem.from(0, 3, Vector(0))` is called
- **THEN** the result is `Left(WrightFisherFixationProblemError.NonPositiveN(0))`

#### Scenario: n exceeding 100 is rejected as NExceedsMaximum
- **WHEN** `WrightFisherFixationProblem.from(101, 3, Vector(0))` is called
- **THEN** the result is `Left(WrightFisherFixationProblemError.NExceedsMaximum(101, 100))`

#### Scenario: Non-positive m is rejected as NonPositiveM
- **WHEN** `WrightFisherFixationProblem.from(4, 0, Vector(0))` is called
- **THEN** the result is `Left(WrightFisherFixationProblemError.NonPositiveM(0))`

#### Scenario: m exceeding 100 is rejected as MExceedsMaximum
- **WHEN** `WrightFisherFixationProblem.from(4, 101, Vector(0))` is called
- **THEN** the result is `Left(WrightFisherFixationProblemError.MExceedsMaximum(101, 100))`

#### Scenario: recessiveCounts larger than 100 is rejected as TooManyRecessiveCounts
- **WHEN** `WrightFisherFixationProblem.from(4, 3, Vector.fill(101)(0))` is called
- **THEN** the result is `Left(WrightFisherFixationProblemError.TooManyRecessiveCounts(101, 100))`

#### Scenario: A recessive count below 0 is rejected as RecessiveCountOutOfRange (first offender wins)
- **WHEN** `WrightFisherFixationProblem.from(4, 3, Vector(0, -1, 2))` is called
- **THEN** the result is `Left(WrightFisherFixationProblemError.RecessiveCountOutOfRange(1, -1, 8))`

#### Scenario: A recessive count above 2n is rejected as RecessiveCountOutOfRange
- **WHEN** `WrightFisherFixationProblem.from(4, 3, Vector(0, 9, 2))` is called (max = 2*4 = 8)
- **THEN** the result is `Left(WrightFisherFixationProblemError.RecessiveCountOutOfRange(1, 9, 8))`

#### Scenario: First offending element in input order wins
- **WHEN** `WrightFisherFixationProblem.from(4, 3, Vector(0, 9, -1, 99))` is called (multiple out-of-range)
- **THEN** the result is `Left(WrightFisherFixationProblemError.RecessiveCountOutOfRange(1, 9, 8))` (index 1 wins)

#### Scenario: Validation order — n lower bound first
- **WHEN** `WrightFisherFixationProblem.from(0, 0, Vector(-1))` is called (all invalid)
- **THEN** the result is `Left(WrightFisherFixationProblemError.NonPositiveN(0))`

#### Scenario: Validation order — m upper bound before recessiveCounts checks
- **WHEN** `WrightFisherFixationProblem.from(4, 101, Vector.fill(101)(-1))` is called
- **THEN** the result is `Left(WrightFisherFixationProblemError.MExceedsMaximum(101, 100))`

#### Scenario: Direct apply does not compile
- **WHEN** source code `bio.domain.genetics.WrightFisherFixationProblem(4, 3, Vector(0, 1, 2))` is compiled
- **THEN** the compiler rejects the expression

### Requirement: WrightFisher.fixationLogProbs computes per-generation log10 fixation probabilities under the Wright-Fisher model
The system SHALL provide `WrightFisher.fixationLogProbs(problem: WrightFisherFixationProblem): Vector[Vector[Double]]` returning a 2-dimensional structure of dimension `problem.m × problem.recessiveCounts.size`. The outer Vector SHALL be indexed by generation (length `problem.m`; outer index `g-1` corresponds to generation `g ∈ 1..problem.m`). The inner Vectors SHALL be indexed by factor (length `problem.recessiveCounts.size`). Each entry `result(g-1)(j)` SHALL equal `log10(P(after g Wright-Fisher generations starting from 2n − recessiveCounts(j) dominant alleles, the population is in the all-dominant absorbing state 2n))`. The algorithm SHALL be on the existing `WrightFisher` object in the `bio.algorithms.genetics` package (alongside the existing `atLeast` and `expectedFrequencies` methods). The function SHALL be total — every valid `WrightFisherFixationProblem` produces a defined `Vector[Vector[Double]]`. When a fixation probability is `0.0`, the corresponding `log10` is `Double.NegativeInfinity` (the function does not error).

#### Scenario: Rosalind sample (N=4, m=3, A=[0, 1, 2]) produces the canonical 3×3 matrix within 1e-9 per element
- **WHEN** `WrightFisher.fixationLogProbs(problem)` is called with `problem = WrightFisherFixationProblem.from(4, 3, Vector(0, 1, 2)).toOption.get`
- **THEN** the result has dimensions `3 × 3` and each element is within `1e-9` of the corresponding expected value in
  ```
  [[0.0,            -0.463935575821, -0.999509892866],
   [0.0,            -0.301424998891, -0.641668367342],
   [0.0,            -0.229066698008, -0.485798552456]]
  ```

#### Scenario: A factor with A[j] = 0 (already fixed) yields log10(1) = 0.0 at every generation
- **WHEN** `WrightFisher.fixationLogProbs(problem)` is called with `recessiveCounts = Vector(0)` and any valid `n, m`
- **THEN** every entry `result(g-1)(0)` equals `0.0` (the all-dominant state is absorbing)

#### Scenario: A factor with A[j] = 2n (all-recessive absorbing state) yields Double.NegativeInfinity at every generation
- **WHEN** `WrightFisher.fixationLogProbs(problem)` is called with `n=1, m=1, recessiveCounts = Vector(2)` (2n = 2)
- **THEN** `result(0)(0)` equals `Double.NegativeInfinity` (P(fixation) = 0 since the all-recessive state is absorbing)

#### Scenario: Analytic spot-check (N=1, m=1, A=[1]) yields exactly log10(0.25)
- **WHEN** `WrightFisher.fixationLogProbs(problem)` is called with `n=1, m=1, recessiveCounts = Vector(1)` (start: 1 dominant of 2, p = 0.5, P(both dominant next gen) = 0.25)
- **THEN** `result(0)(0)` is within `1e-9` of `Math.log10(0.25)` (≈ `-0.602060`)

#### Scenario: Output dimensions match input — m rows × |recessiveCounts| columns
- **WHEN** `WrightFisher.fixationLogProbs(problem)` is called with `n=4, m=5, recessiveCounts of length 7`
- **THEN** `result.size == 5` and every inner vector has size `7`

#### Scenario: Empty recessiveCounts vector produces m empty inner vectors
- **WHEN** `WrightFisher.fixationLogProbs(problem)` is called with `n=4, m=3, recessiveCounts = Vector.empty`
- **THEN** `result.size == 3` and every inner vector equals `Vector.empty`

#### Scenario: Output is monotonically non-decreasing in generation (per factor) — fixation probability only grows over time
- **WHEN** `WrightFisher.fixationLogProbs(problem)` is called with `n=4, m=3, recessiveCounts = Vector(1)` (any non-degenerate factor)
- **THEN** for each consecutive pair of generations `(result(g-1)(0), result(g)(0))` with `g in 0 until problem.m - 1`, `result(g)(0) <= result(g+1)(0)`
