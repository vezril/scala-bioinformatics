## 1. Error type

- [x] 1.1 RED: write `SpectrumGraphProblemErrorSpec` asserting `TooManyMasses(size, max)` and `NonPositiveMass(index, value)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.protein.SpectrumGraphProblemError` — `sealed trait` + `TooManyMasses(size: Int, max: Int)`, `NonPositiveMass(index: Int, value: Double)`; run the spec green.

## 2. SpectrumGraphProblem domain type

- [x] 2.1 RED: write `SpectrumGraphProblemSpec` — accepts the 9-value canonical sample (Right, `masses` preserved), rejects 101 masses (`TooManyMasses(101, 100)`), rejects `[10.0, -1.0]` (`NonPositiveMass(1, -1.0)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.protein.SpectrumGraphProblem` as `sealed abstract case class SpectrumGraphProblem(masses: Vector[Double])` with `MaxMasses = 100` and `from(masses)` applying first-failure-wins (`size > 100` → `TooManyMasses`; first `value <= 0` via `zipWithIndex.collectFirst` → `NonPositiveMass`), building via `Right(new SpectrumGraphProblem(masses) {})`. Run green.

## 3. SpectrumGraphPeptide result type

- [x] 3.1 RED: write `SpectrumGraphPeptideResultSpec` — `peptide` field exposure, `format` of `WMSPG` → `"WMSPG"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.protein.SpectrumGraphPeptide` — `final case class SpectrumGraphPeptide(peptide: String)` with `format: String = peptide`. Run green.

## 4. SpectrumGraph algorithm

- [x] 4.1 RED: write `SpectrumGraphSpec` — canonical sample → `WMSPG`; `[10.0, 81.03711]` → `A`; `[10.0, 20.0]` → `""`. Build via `SpectrumGraphProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.protein.SpectrumGraph` with `longestPeptide(problem): SpectrumGraphPeptide`. `sorted = masses.sorted`; build `best: Map[Int, (Int, List[Char])]` by `foldLeft` over indices `n-1 .. 0`: for node `i`, candidates `for { j <- i+1 until n; code <- residueOf(sorted(j) - sorted(i)).map(_.code) } yield (1 + best(j)._1, code :: best(j)._2)`, take `maxByOption(_._1)` else `(0, Nil)`. Answer = `(0 until n).iterator.map(best).maxByOption(_._1).map(_._2.mkString).getOrElse("")`. `residueOf(g) = AminoAcid.all.find(a => math.abs(a.monoisotopicMass - g) < 1e-4)`. Wrap in `SpectrumGraphPeptide`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — sort + fold + immutable `Map`); confirm `AminoAcid` reuse, residue tolerance, and deterministic start-node ordering. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.SGRAProb` reading `sgra_data.txt` (one mass per non-empty line), parsing each via `toDoubleOption` (fold into `Either`), building `SpectrumGraphProblem.from`, running `SpectrumGraph.longestPeptide`, printing `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `SGRAProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
