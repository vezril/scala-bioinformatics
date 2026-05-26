## 1. IndependentAllelesProblemError ADT

- [x] 1.1 Create `src/main/scala/bio/domain/genetics/IndependentAllelesProblemError.scala` with `package bio.domain.genetics`, `sealed trait IndependentAllelesProblemError`, and cases `final case class NonPositiveGenerations(value: Int)`, `final case class NonPositiveAtLeast(value: Int)`, and `final case class AtLeastExceedsPopulation(atLeast: Int, generations: Int)`
- [x] 1.2 Run `sbt compile` — confirm clean compile

## 2. IndependentAllelesProblem domain type (TDD)

- [x] 2.1 Write failing tests in `src/test/scala/bio/domain/genetics/IndependentAllelesProblemSpec.scala` for `IndependentAllelesProblem.from` covering: Rosalind sample `(2, 1)` accepted with `populationSize == 4` and both fields exposed, boundary `(3, 8)` (atLeast equals 2^3) accepted, zero generations → `Left(NonPositiveGenerations(0))`, negative generations → `Left(NonPositiveGenerations(-1))`, zero atLeast → `Left(NonPositiveAtLeast(0))`, negative atLeast → `Left(NonPositiveAtLeast(-3))`, atLeast > 2^generations → `Left(AtLeastExceedsPopulation(5, 2))`, validation order (when both generations and atLeast are invalid, generations wins). Plus invariant tests: `assertDoesNotCompile("""bio.domain.genetics.IndependentAllelesProblem(2, 1)""")` and `.copy(generations = 99)` rejection
- [x] 2.2 Run `sbt test` — confirm tests fail (Red)
- [x] 2.3 Implement `src/main/scala/bio/domain/genetics/IndependentAllelesProblem.scala`: `sealed abstract case class IndependentAllelesProblem(generations: Int, atLeast: Int) { def populationSize: Long = 1L << generations }`. Companion `from(generations, atLeast)`: validate `generations >= 1` first, then `atLeast >= 1`, then `atLeast.toLong <= (1L << generations)` (use `.toLong` to compare against the Long population size). Return the appropriate `Left` on the first failure; otherwise `Right(new IndependentAllelesProblem(generations, atLeast) {})`
- [x] 2.4 Run `sbt test` — confirm `IndependentAllelesProblemSpec` passes (Green)

## 3. IndependentAlleles algorithm (TDD)

- [x] 3.1 Write failing tests in `src/test/scala/bio/algorithms/genetics/IndependentAllelesSpec.scala` for `IndependentAlleles.probability` covering: Rosalind sample `(2, 1)` → `≈ 0.684` (`±1e-3`), `(1, 1)` → `7/16` (`±1e-9`), `(1, 2)` → `1/16` (`±1e-9`), `(2, 4)` → `1/256` (`±1e-9`), `(7, 1)` → `1 - (0.75)^128` (`±1e-12`), `(7, 128)` → positive and equals `(0.25)^128` (`±1e-80`), and a "result is `Probability`, value in `[0, 1]`" sanity check. Use a `prob(k, n)` helper that calls `IndependentAllelesProblem.from(k, n).getOrElse(sys.error(...))`
- [x] 3.2 Run `sbt test` — confirm tests fail (Red) because `IndependentAlleles` does not yet exist
- [x] 3.3 Implement `src/main/scala/bio/algorithms/genetics/IndependentAlleles.scala` in `bio.algorithms.genetics`. Use a private `P = 0.25`, `Q = 0.75`, `Ratio = P / Q`. Algorithm:
  - `val total = problem.populationSize.toInt` (safe for Rosalind k ≤ 7)
  - `val n = problem.atLeast`
  - `val pmf0 = math.pow(Q, total.toDouble)`
  - `val pmfs: Vector[Double] = (0 until total).scanLeft(pmf0) { (pmfJ, j) => pmfJ * (total - j).toDouble / (j + 1).toDouble * Ratio }`
  - `Probability.unsafeFrom(pmfs.drop(n).sum)`
  Imports: `bio.domain.genetics.IndependentAllelesProblem`, `bio.domain.stats.Probability`. No `var`, no mutable collections
- [x] 3.4 Run `sbt test` — confirm `IndependentAllelesSpec` passes (Green)
- [x] 3.5 Refactor: confirm no `var`, single `scanLeft` over `0 until total`, recurrence uses the closed-form ratio `(total - j) / (j + 1) × P/Q`, result wrapped via `Probability.unsafeFrom`
- [x] 3.6 Run `sbt test` — confirm all tests still pass after refactor

## 4. Final Verification

- [x] 4.1 Run `sbt clean compile` — zero errors (warnings unrelated to this change are acceptable)
- [x] 4.2 Run `sbt test` — all tests pass (count higher than 254)
- [x] 4.3 Verify the new files reside at `src/main/scala/bio/domain/genetics/IndependentAllelesProblem.scala`, `src/main/scala/bio/domain/genetics/IndependentAllelesProblemError.scala`, `src/main/scala/bio/algorithms/genetics/IndependentAlleles.scala` with the expected `package` declarations
- [x] 4.4 Verify the algorithm returns `Probability` (not bare `Double`) and that `populationSize` accessor returns `Long`
