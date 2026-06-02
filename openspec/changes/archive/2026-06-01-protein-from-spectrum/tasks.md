## 1. Monoisotopic masses on AminoAcid

- [x] 1.1 RED: add tests to `AminoAcidSpec` (create if absent) asserting `AminoAcid.W.monoisotopicMass == 186.07931`, `AminoAcid.closestByMass(128.0586) == AminoAcid.Q` (not `K`), and `AminoAcid.closestByMass(113.08406) == AminoAcid.L` (isobaric I/L tie → first in `all`); confirm it fails to compile
- [x] 1.2 GREEN: extend `bio/domain/protein/AminoAcid.scala` — add abstract `def monoisotopicMass: Double` to the trait, the standard Rosalind monoisotopic mass to each of the 20 case objects, and companion `closestByMass(target: Double): AminoAcid = all.minBy(aa => math.abs(aa.monoisotopicMass - target))`; confirm tests pass

## 2. Error ADT

- [x] 2.1 RED: write `PrefixSpectrumErrorSpec` asserting `EmptySpectrum`, `TooManyWeights(count, max)`, and `NonPositiveWeight(index, value)` are `PrefixSpectrumError` subtypes; confirm it fails to compile
- [x] 2.2 GREEN: create `bio/domain/protein/PrefixSpectrumError.scala` — `sealed trait` with `case object EmptySpectrum`, `final case class TooManyWeights(count: Int, max: Int)`, `final case class NonPositiveWeight(index: Int, value: Double)`; confirm test passes

## 3. Validated prefix-spectrum bundle

- [x] 3.1 RED: write `PrefixSpectrumSpec` covering: accepts canonical sample (preserves 5 weights in order); accepts single positive weight; rejects empty list with `EmptySpectrum`; rejects 101 weights with `TooManyWeights(101, 100)`; rejects `[10.0, 0.0, 20.0]` with `NonPositiveWeight(1, 0.0)`; reports `EmptySpectrum` first when empty; `assertDoesNotCompile` for public `apply(...)` and `.copy(...)`; confirm it fails to compile
- [x] 3.2 GREEN: create `bio/domain/protein/PrefixSpectrum.scala` — `sealed abstract case class PrefixSpectrum(weights: Vector[Double])`; `MaxWeights = 100`; `from(weights)` first-failure-wins empty → too-many → first non-positive (via `zipWithIndex.collectFirst`); constructed via `Right(new PrefixSpectrum(weights) {})`; confirm tests pass

## 4. Result type

- [x] 4.1 RED: write `InferredProteinSpec` asserting `format` on an `InferredProtein` wrapping `ProteinString` `WMQS` returns exactly `WMQS`; confirm it fails to compile
- [x] 4.2 GREEN: create `bio/domain/protein/InferredProtein.scala` — `final case class InferredProtein(protein: ProteinString)` with `def format: String = protein.value`; confirm test passes

## 5. Inference algorithm

- [x] 5.1 RED: write `InferProteinFromSpectrumSpec` (in `bio.algorithms.protein`) asserting: canonical sample → `WMQS`; an `n`-weight spectrum → protein of length `n − 1`; a single-weight spectrum → empty protein; confirm it fails to compile
- [x] 5.2 GREEN: create `bio/algorithms/protein/InferProteinFromSpectrum.scala` — `infer(problem: PrefixSpectrum): InferredProtein` using `weights.sliding(2).collect { case Vector(a, b) => AminoAcid.closestByMass(b - a) }.toVector` then `ProteinString.fromAminoAcids`; pure/total, no `var`/`while`; confirm tests pass
- [x] 5.3 REFACTOR: review `infer` for clarity (pure FP, total); rerun spec to confirm still green

## 6. IO runner and Main wiring

- [x] 6.1 GREEN: create `bio/problems/SPECProb.scala` — `solve(): IO[Unit]` reads `spec_data.txt`, splits on `\R`, trims, drops blanks, parses each line via `toDoubleOption` (parse failure → descriptive `Left`), `foldRight`s into `Either[String, List[Double]]`, validates into `PrefixSpectrum`, runs `infer`, prints `result.format`; on parse/validation error prints a descriptive message rather than throwing (mirror `GREPProb`)
- [x] 6.2 GREEN: wire `SPECProb.solve()` into `bio/Main.scala`; run `sbt run` and confirm it prints `WMQS`

## 7. Verification

- [x] 7.1 Run full `sbt test` and confirm the whole suite is green with the new specs included
