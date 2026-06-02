## 1. SexLinkedProblem domain type

(No error type: each proportion is validated by `Probability`, so the problem carries no extra invariant — a plain wrapper, like `SpectralConvolutionProblem`.)

- [x] 1.1 RED: write `SexLinkedProblemSpec` — constructing from proportions `0.1, 0.5, 0.8` exposes those `Probability` values via `maleProportions`; constructing from no proportions yields an empty `maleProportions`. Build inputs via `Probability.from`. Confirm RED.
- [x] 1.2 GREEN: create `bio.domain.genetics.SexLinkedProblem` as `final case class SexLinkedProblem(maleProportions: Vector[Probability])`. Run green.

## 2. CarrierProbabilities result type

- [x] 2.1 RED: write `CarrierProbabilitiesResultSpec` — `values` field exposure, `format` of `[0.18, 0.5, 0.32]` → `"0.180 0.500 0.320"`, empty → `""`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.genetics.CarrierProbabilities` — `final case class CarrierProbabilities(values: Vector[Double])` with `format: String = values.map(v => f"$v%.3f").mkString(" ")`. Run green.

## 3. SexLinkedInheritance algorithm

- [x] 3.1 RED: write `SexLinkedInheritanceSpec` — canonical `[0.1, 0.5, 0.8]` → within 0.001 of `[0.18, 0.5, 0.32]`; `[0.0, 1.0]` → `[0.0, 0.0]`; `[0.5]` → within 0.001 of `0.5`; empty → empty. Build inputs via `Probability.from` + `SexLinkedProblem`. Confirm RED.
- [x] 3.2 GREEN: create `bio.algorithms.genetics.SexLinkedInheritance` with `carrierProbabilities(problem: SexLinkedProblem): CarrierProbabilities = CarrierProbabilities(problem.maleProportions.map { p => val q = p.value; 2.0 * q * (1.0 - q) })`. Run green.
- [x] 3.3 REFACTOR: review for `var`/`while`/mutable collections (none — single `map`); confirm `Probability` reuse and order preservation. Run full `sbt test` green.

## 4. Runner

- [x] 4.1 Create `bio.problems.SEXLProb` reading `sexl_data.txt` (single whitespace-separated line of proportions). Parse: `split("\\s+")` + `toDoubleOption` then `Probability.from` (fold into `Either`); build `SexLinkedProblem`; run `SexLinkedInheritance.carrierProbabilities`; print `format` via `IO.println`; all errors printed (never thrown).
- [x] 4.2 Wire `SEXLProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
