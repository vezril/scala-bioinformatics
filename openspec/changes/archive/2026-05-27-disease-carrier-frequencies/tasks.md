## 1. DiseaseCarriers.frequencies algorithm (TDD)

- [x] 1.1 Write `DiseaseCarriersSpec` (package `bio.algorithms.genetics`) covering all 8 scenarios from the spec: Rosalind sample [0.1, 0.25, 0.5] → [0.532, 0.75, 0.914] within 0.001 abs error; A=0.0 → B=0.0 exact; A=1.0 → B=1.0 exact; A=0.25 → B=0.75 within 1e-12 (the exact `q=1/2` case); empty input → empty output; result length equals input length (using length=7); monotonic non-decreasing for a sorted ascending input [0.1, 0.25, 0.5, 0.9]; every output is in [0, 1]. Run, observe red
- [x] 1.2 Implement `bio.algorithms.genetics.DiseaseCarriers.frequencies(homozygousRecessive: Vector[Probability]): Vector[Probability]` per design.md: `homozygousRecessive.map { p => val a = p.value; Probability.unsafeFrom(2.0 * Math.sqrt(a) - a) }`; run all tests green
- [x] 1.3 Refactor pass: scaladoc explaining the Hardy-Weinberg derivation (`B = 2q − A` where `q = √A`), the `unsafeFrom` justification (`2√a − a` maps `[0, 1] → [0, 1]` — monotonic, endpoints 0 and 1), and the bare-vector input/output choice

## 2. Whole-suite verification

- [x] 2.1 Run `sbt test` and confirm all existing suites plus the new tests pass with no warnings
- [x] 2.2 Verify only additive changes (`git status` should show only one new file under `bio/algorithms/genetics/` and one matching test file)
