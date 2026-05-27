## 1. SubsetUniverseSizeError ADT (TDD)

- [x] 1.1 Write `SubsetUniverseSizeErrorSpec` (package `bio.domain.combinatorics`) covering all 3 scenarios: `NonPositive(0)` field exposure, `NonPositive(-5)` field exposure, `ExceedsMaximum(1001, 1000)` field exposure (both `value` and `max`); run, observe red
- [x] 1.2 Implement `bio.domain.combinatorics.SubsetUniverseSizeError` sealed trait with `NonPositive(value: Int)` and `ExceedsMaximum(value: Int, max: Int)` cases; run all tests green
- [x] 1.3 Refactor pass: scaladoc describing both error cases

## 2. SubsetUniverseSize value type (TDD)

- [x] 2.1 Write `SubsetUniverseSizeSpec` covering all 8 scenarios from the spec: accept n=1 (minimum), accept n=3 (Rosalind sample), accept n=1000 (upper bound), reject n=0 as NonPositive, reject n=-5 as NonPositive, reject n=1001 as ExceedsMaximum, validation order (lower bound before upper bound), `assertDoesNotCompile` for direct `SubsetUniverseSize(3)`; run, observe red
- [x] 2.2 Implement `bio.domain.combinatorics.SubsetUniverseSize` as `sealed abstract case class SubsetUniverseSize(value: Int)` with `from` smart constructor enforcing `1 <= value <= 1000` (lower bound first); run all tests green
- [x] 2.3 Refactor pass: scaladoc describing both validation rules, validation order, and the `sealed abstract case class` pattern

## 3. Subsets.count algorithm (TDD)

- [x] 3.1 Write `SubsetsSpec` (package `bio.algorithms.combinatorics`) covering all 6 scenarios: n=3 → 8 (Rosalind sample); n=1 → 2; n=10 → 1024; n=19 → 524288 (last value before modulo wraps); n=20 → 48576 (first wrap, = 2^20 mod 1_000_000); n=1000 → 69376 (verified upper bound). Run, observe red
- [x] 3.2 Implement `bio.algorithms.combinatorics.Subsets.count(size: SubsetUniverseSize): Int` as `(0 until size.value).foldLeft(1)((acc, _) => (acc * 2) % 1_000_000)`; run all tests green
- [x] 3.3 Refactor pass: scaladoc describing the per-step-modulo idiom (mirrors `PartialPermutations.count`), the `Int`-safety analysis (worst intermediate = 999_999 * 2 well within `Int.MaxValue`), and the modulus constant

## 4. Whole-suite verification

- [x] 4.1 Run `sbt test` and confirm all existing suites plus the new tests pass with no warnings
- [x] 4.2 Verify only additive changes (`git status` should show only new files under `bio/domain/combinatorics/`, `bio/algorithms/combinatorics/`, and matching test directories)
