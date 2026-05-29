## 1. Domain error ADT

- [x] 1.1 Create `src/main/scala/bio/domain/analysis/FittingAlignmentProblemError.scala` with a `sealed trait FittingAlignmentProblemError` and companion `case class`es `TextTooLong(length: Int, max: Int)` and `MotifTooLong(length: Int, max: Int)`.

## 2. Input bundle (TDD)

- [x] 2.1 RED: Write `src/test/scala/bio/domain/analysis/FittingAlignmentProblemSpec.scala` covering: accepts the canonical SIMS sample; accepts two empty strings; accepts empty text / non-empty motif; accepts non-empty text / empty motif; accepts text at 10000 and motif at 1000; rejects 10001-nt text as `TextTooLong(10001, 10000)`; rejects 1001-nt motif as `MotifTooLong(1001, 1000)`; first-failure-wins reports `TextTooLong` when both exceed; `assertDoesNotCompile` for companion `apply` and for `copy`.
- [x] 2.2 GREEN: Create `src/main/scala/bio/domain/analysis/FittingAlignmentProblem.scala` as a `sealed abstract case class FittingAlignmentProblem(text: DnaString, motif: DnaString)` with a `from` smart constructor enforcing the caps (text 10000, then motif 1000) first-failure-wins.
- [x] 2.3 Run `sbt "testOnly bio.domain.analysis.FittingAlignmentProblemSpec"` and confirm green.

## 3. Output ADT (TDD)

- [x] 3.1 RED: Add an output-ADT construction/equality test (named fields, value-equality) — may live in the algorithm spec file or its own spec.
- [x] 3.2 GREEN: Create `src/main/scala/bio/domain/analysis/FittingAlignment.scala` as a plain `final case class FittingAlignment(score: Int, augmentedText: String, augmentedMotif: String)`.

## 4. Algorithm (TDD)

- [x] 4.1 RED: Write `src/test/scala/bio/algorithms/analysis/FittingAlignmentSpec.scala` covering: canonical Rosalind SIMS sample (`score == 5` + the five structural invariants); clean motif occurrence `TTGATTACATT` / `GATTACA` → `FittingAlignment(7, "GATTACA", "GATTACA")`; identical `ACGT`/`ACGT` → `FittingAlignment(4, "ACGT", "ACGT")`; empty motif → `FittingAlignment(0, "", "")`; empty text + motif `ACG` → `FittingAlignment(-3, "---", "ACG")`; motif-fully-consumed invariant; contiguous-substring invariant.
- [x] 4.2 GREEN: Create `src/main/scala/bio/algorithms/analysis/FittingAlignment.scala` with `align(problem: FittingAlignmentProblem): FittingAlignment` implementing the fitting-alignment DP (boundary `dp(i)(0)=0`, `dp(0)(j)=-j`; recurrence with +1/-1 mismatch score), last-column argmax (smallest `i`), and traceback (tie-break diagonal > up > left, border forced-left, stop at `j == 0`) emitting augmented strings via reversed `StringBuilder`s. Short-circuit empty motif to `FittingAlignment(0, "", "")`.
- [x] 4.3 Run `sbt "testOnly bio.algorithms.analysis.FittingAlignmentSpec"` and confirm green; correct any tie-break/boundary issue revealed by the sample.

## 5. Problem runner + wiring

- [x] 5.1 Create `src/main/scala/bio/problems/SIMSProb.scala` (mirrors `LOCAProb`) that builds the two `DnaString`s, calls `FittingAlignment.align`, and prints score then the two augmented strings; handle the `Either` from `FittingAlignmentProblem.from`.
- [x] 5.2 Wire `SIMSProb.solve()` into `bio/Main.scala` following the existing commented-runner pattern.

## 6. Verify

- [x] 6.1 Run the full suite `sbt test` and confirm zero regressions across all suites.
