## 1. CouplePopulationError ADT

- [x] 1.1 Create `src/main/scala/bio/domain/genetics/CouplePopulationError.scala` with `package bio.domain.genetics`, `sealed trait CouplePopulationError`, and cases `final case class NegativeCount(index: Int, value: Int)` and `final case class ExceedsMaxCount(index: Int, value: Int)`
- [x] 1.2 Run `sbt compile` — confirm clean compile

## 2. CouplePopulation domain type (TDD)

- [x] 2.1 Write failing tests in `src/test/scala/bio/domain/genetics/CouplePopulationSpec.scala` for `CouplePopulation.from` covering: all-zero accepted, Rosalind sample `(1, 0, 0, 1, 0, 1)` accepted with the six fields equal to those values, upper boundary `(20000, ...×6)` accepted, negative count rejected (e.g. `(1, 2, -3, 4, 5, 6)` → `Left(NegativeCount(3, -3))`), exceeds-max rejected (`(20001, 0, 0, 0, 0, 0)` → `Left(ExceedsMaxCount(1, 20001))`), short-circuit (`(-1, 20001, 0, 0, 0, 0)` → `Left(NegativeCount(1, -1))` — first failure wins). Plus invariant tests: `assertDoesNotCompile("""bio.domain.genetics.CouplePopulation(0, 0, 0, 0, 0, 0)""")` and `assertDoesNotCompile` on `.copy(homDomHomDom = 99)`
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `src/main/scala/bio/domain/genetics/CouplePopulation.scala` in `bio.domain.genetics`: `sealed abstract case class CouplePopulation(homDomHomDom: Int, homDomHet: Int, homDomHomRec: Int, hetHet: Int, hetHomRec: Int, homRecHomRec: Int)`. Companion `from(c1, c2, c3, c4, c5, c6: Int)`: pair each input with its 1-based index, walk in order, return `Left(NegativeCount(i, v))` for the first `v < 0`, then `Left(ExceedsMaxCount(i, v))` for the first `v > 20000`; otherwise `Right(new CouplePopulation(c1, c2, c3, c4, c5, c6) {})`. No `var`. Use a single `Iterator`/`Vector` pass for validation
- [x] 2.4 Run `sbt test` — confirm `CouplePopulationSpec` passes (Green)

## 3. ExpectedOffspring algorithm (TDD)

- [x] 3.1 Write failing tests in `src/test/scala/bio/algorithms/genetics/ExpectedOffspringSpec.scala` for `ExpectedOffspring.dominantPhenotype(pop)` covering: Rosalind sample `(1, 0, 0, 1, 0, 1) → 3.5`, all-zero → `0.0`, single AAxAA `(1, 0, 0, 0, 0, 0) → 2.0`, single AaxAa `(0, 0, 0, 1, 0, 0) → 1.5`, single Aaxaa `(0, 0, 0, 0, 1, 0) → 1.0`, any aaxaa contribution → `0.0` (e.g. `(0, 0, 0, 0, 0, 100) → 0.0`), upper-boundary `(20000, …×6) → 170000.0` (tolerance `±1e-6`). All tolerances `±1e-9` except the large-input case at `±1e-6`
- [x] 3.2 Run `sbt test` — confirm tests fail (Red)
- [x] 3.3 Implement `src/main/scala/bio/algorithms/genetics/ExpectedOffspring.scala` in `bio.algorithms.genetics`. Single-expression body: `def dominantPhenotype(pop: CouplePopulation): Double = 2.0 * pop.homDomHomDom + 2.0 * pop.homDomHet + 2.0 * pop.homDomHomRec + 1.5 * pop.hetHet + 1.0 * pop.hetHomRec + 0.0 * pop.homRecHomRec`. Keep the `0.0 * homRecHomRec` term for visual parallelism. Import `bio.domain.genetics.CouplePopulation`
- [x] 3.4 Run `sbt test` — confirm `ExpectedOffspringSpec` passes (Green)
- [x] 3.5 Refactor: confirm no `var`, no mutable collections, single-expression body, multipliers match the design.md math table
- [x] 3.6 Run `sbt test` — confirm all tests still pass after refactor

## 4. Final Verification

- [x] 4.1 Run `sbt clean compile` — zero errors (warnings unrelated to this change are acceptable)
- [x] 4.2 Run `sbt test` — all tests pass (count higher than 209)
- [x] 4.3 Verify the new files reside at `src/main/scala/bio/domain/genetics/CouplePopulation.scala`, `src/main/scala/bio/domain/genetics/CouplePopulationError.scala`, `src/main/scala/bio/algorithms/genetics/ExpectedOffspring.scala` with the expected `package` declarations
- [x] 4.4 Verify the formula in `ExpectedOffspring.dominantPhenotype` matches the design.md table line-by-line (2.0, 2.0, 2.0, 1.5, 1.0, 0.0 in pairing order 1..6)
