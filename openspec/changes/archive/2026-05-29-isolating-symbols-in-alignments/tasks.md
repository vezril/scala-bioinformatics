## 1. Domain error ADT

- [x] 1.1 Create `src/main/scala/bio/domain/analysis/IsolatedSymbolsProblemError.scala` with a `sealed trait IsolatedSymbolsProblemError` and companion `case class`es `LeftTooLong(length: Int, max: Int)` and `RightTooLong(length: Int, max: Int)`.

## 2. Input bundle (TDD)

- [x] 2.1 RED: Write `src/test/scala/bio/domain/analysis/IsolatedSymbolsProblemSpec.scala` covering: accepts the canonical OSYM sample; accepts two empty strings; accepts empty left / non-empty right; accepts non-empty left / empty right; accepts both at 1000; rejects 1001-bp left as `LeftTooLong(1001, 1000)`; rejects 1001-bp right as `RightTooLong(1001, 1000)`; first-failure-wins reports `LeftTooLong` when both exceed; `assertDoesNotCompile` for companion `apply` and for `copy`.
- [x] 2.2 GREEN: Create `src/main/scala/bio/domain/analysis/IsolatedSymbolsProblem.scala` as a `sealed abstract case class IsolatedSymbolsProblem(left: DnaString, right: DnaString)` with a `from` smart constructor enforcing the caps (left 1000, then right 1000) first-failure-wins.
- [x] 2.3 Run `sbt "testOnly bio.domain.analysis.IsolatedSymbolsProblemSpec"` and confirm green.

## 3. Output ADT (TDD)

- [x] 3.1 RED: Add an output-ADT construction/equality test (named fields, `matrixSum` is a `Long`, value-equality) — may live in the algorithm spec file.
- [x] 3.2 GREEN: Create `src/main/scala/bio/domain/analysis/IsolatedSymbols.scala` as a plain `final case class IsolatedSymbols(globalScore: Int, matrixSum: Long)`.

## 4. Algorithm (TDD)

- [x] 4.1 RED: Write `src/test/scala/bio/algorithms/analysis/IsolatedSymbolsSpec.scala` covering: canonical OSYM sample `ATAGATA` / `ACAGGTA` → `IsolatedSymbols(3, -139L)`; identical single chars `A`/`A` → `(1, 1L)`; mismatched single chars `A`/`C` → `(-1, -1L)`; identical strings `GATTACA` → `globalScore == 7`; two empty → `(0, 0L)`; empty left + `GATTACA` → `(-7, 0L)`; empty right → `(-7, 0L)`; symmetry of `globalScore` and `matrixSum` under argument swap.
- [x] 4.2 GREEN: Create `src/main/scala/bio/algorithms/analysis/IsolatedSymbols.scala` with `compute(problem): IsolatedSymbols` implementing the forward DP `f`, the backward DP `b` (unit mismatch score, gap -1), `globalScore = f(m)(n)`, and `matrixSum = Σ f(j)(k) + sc(left(j),right(k)) + b(j+1)(k+1)` over `j ∈ [0,m), k ∈ [0,n)` accumulated in a `Long` without materialising `M`.
- [x] 4.3 Run `sbt "testOnly bio.algorithms.analysis.IsolatedSymbolsSpec"` and confirm green.

## 5. Problem runner + wiring

- [x] 5.1 Create `src/main/scala/bio/problems/OSYMProb.scala` (mirrors `SIMSProb`) that builds the two `DnaString`s, calls `IsolatedSymbols.compute`, and prints `globalScore` then `matrixSum`; handle the `Either` from `IsolatedSymbolsProblem.from`.
- [x] 5.2 Wire `OSYMProb.solve()` into `bio/Main.scala` following the existing commented-runner pattern.

## 6. Verify

- [x] 6.1 Run the full suite `sbt test` and confirm zero regressions across all suites.
