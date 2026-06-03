## 1. Error type

- [x] 1.1 RED: write `SpectrumMatchProblemErrorSpec` asserting `EmptyProteinList` (case object), `EmptySpectrum` (case object), and `NonPositiveMass(index, value)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.protein.SpectrumMatchProblemError` — `sealed trait` + `case object EmptyProteinList`, `case object EmptySpectrum`, `final case class NonPositiveMass(index: Int, value: Double)`; run the spec green.

## 2. SpectrumMatchProblem domain type

- [x] 2.1 RED: write `SpectrumMatchProblemSpec` — accepts the canonical 4 proteins + 6-value spectrum (Right, fields preserved), rejects empty proteins (`EmptyProteinList`), rejects empty spectrum (`EmptySpectrum`), rejects spectrum with `-2.0` at index 1 (`NonPositiveMass(1, -2.0)`), and `assertDoesNotCompile` for public `apply` and `copy`. Build proteins via `ProteinString.from`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.protein.SpectrumMatchProblem` as `sealed abstract case class SpectrumMatchProblem(proteins: Vector[ProteinString], spectrum: Vector[Double])` with `from(proteins, spectrum)` applying first-failure-wins (`proteins.isEmpty` → `EmptyProteinList`; `spectrum.isEmpty` → `EmptySpectrum`; first `value <= 0` via `zipWithIndex.collectFirst` → `NonPositiveMass(index, value)`), building via `Right(new SpectrumMatchProblem(...) {})`. Run green.

## 3. SpectrumMatch result type

- [x] 3.1 RED: write `SpectrumMatchResultSpec` — `multiplicity`/`protein` field exposure, `format` of `(3, "IASWMQS")` → `"3\nIASWMQS"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.protein.SpectrumMatch` — `final case class SpectrumMatch(multiplicity: Int, protein: String)` with `format: String = s"$multiplicity\n$protein"`. Run green.

## 4. MatchSpectrum algorithm

- [x] 4.1 RED: write `MatchSpectrumSpec` — canonical sample → `(3, "IASWMQS")`; single `A` with spectrum `[71.03711]` → `(2, "A")`; proteins `A`,`AA` with spectrum `[71.03711, 142.07422]` → `(4, "AA")`. Build via `ProteinString.from` + `SpectrumMatchProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.protein.MatchSpectrum` with `bestMatch(problem): SpectrumMatch`. Private `completeSpectrum(p: ProteinString): Vector[Double]` = residue masses (`AminoAcid.fromChar(c).monoisotopicMass`) cumulative prefix sums (`scanLeft(0.0)(_ + _).tail`) ++ cumulative suffix sums (over the reversed masses). Private `maxMultiplicity(r, s): Int` = `for { a <- r; b <- s } yield round5(a - b)` then `groupBy(identity).values.map(_.size).maxOption.getOrElse(0)`, with `round5(d) = math.round(d * 1e5) / 1e5`. Compute per-protein `(maxMultiplicity(spectrum, completeSpectrum(p)), p.value)`, take `maxBy(_._1)` (first on tie), wrap in `SpectrumMatch`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — `scanLeft`/`for`/`groupBy`/`maxBy`); confirm `AminoAcid`/`ProteinString` reuse and the 5-decimal bucketing. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.PRSMProb` reading `prsm_data.txt`: line 1 is `n`; the next `n` lines are proteins; the remaining non-empty lines are the spectrum values. Parse `n` via `toIntOption`; parse proteins via `ProteinString.from` (fold into `Either`); parse spectrum via `toDoubleOption` (fold); build `SpectrumMatchProblem.from`; run `MatchSpectrum.bestMatch`; print `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `PRSMProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
