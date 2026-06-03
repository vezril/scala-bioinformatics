## 1. Error type

- [x] 1.1 RED: write `FullSpectrumProblemErrorSpec` asserting `InvalidSize(size)` and `NonPositiveMass(index, value)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.protein.FullSpectrumProblemError` — `sealed trait` + `InvalidSize(size: Int)`, `NonPositiveMass(index: Int, value: Double)`; run the spec green.

## 2. FullSpectrumProblem domain type

- [x] 2.1 RED: write `FullSpectrumProblemSpec` — accepts the 13-value canonical sample (Right, `masses` preserved), rejects size 4 (`InvalidSize(4)`), rejects size 3 (`InvalidSize(3)`), rejects `[5.0, -1.0, 2.0, 3.0, 4.0]` (`NonPositiveMass(1, -1.0)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.protein.FullSpectrumProblem` as `sealed abstract case class FullSpectrumProblem(masses: Vector[Double])` with `from(masses)` applying first-failure-wins (`size < 5 || size % 2 == 0` → `InvalidSize(size)`; first `value <= 0` via `zipWithIndex.collectFirst` → `NonPositiveMass(index, value)`), building via `Right(new FullSpectrumProblem(masses) {})`. Run green.

## 3. InferredPeptide result type

- [x] 3.1 RED: write `InferredPeptideResultSpec` — `peptide` field exposure, `format` of `KEKEP` → `"KEKEP"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.protein.InferredPeptide` — `final case class InferredPeptide(peptide: String)` with `format: String = peptide`. Run green.

## 4. InferPeptide algorithm

- [x] 4.1 RED: write `InferPeptideSpec` — canonical sample → `KEKEP`; `[90.0, 1.0, 72.03711, 1.0, 72.03711]` → `A`. Build via `FullSpectrumProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.protein.InferPeptide` with `infer(problem): InferredPeptide`. `n = (masses.size - 3) / 2`; `ions = masses.tail.sorted`; `@tailrec` walk from `ions.head`: at each step `ions.iterator.filter(_ > current).flatMap(ion => residueOf(ion - current).map(a => (ion, a.code))).nextOption()` → append code, advance to that ion, until `n` residues. `residueOf(g) = AminoAcid.all.find(a => math.abs(a.monoisotopicMass - g) < 1e-4)`. Wrap the residues (mkString) in `InferredPeptide`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — `sorted` + tailrec + iterator); confirm `AminoAcid` reuse, the residue tolerance, and `n` derivation. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.FULLProb` reading `full_data.txt` (one mass per non-empty line), parsing each via `toDoubleOption` (fold into `Either`), building `FullSpectrumProblem.from`, running `InferPeptide.infer`, printing `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `FULLProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
