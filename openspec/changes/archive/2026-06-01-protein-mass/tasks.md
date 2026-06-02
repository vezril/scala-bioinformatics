## 1. AminoAcid.fromChar

- [x] 1.1 RED: add tests to `AminoAcidSpec` asserting `AminoAcid.fromChar('W') == Some(AminoAcid.W)` and `AminoAcid.fromChar('B') == None`; confirm it fails to compile
- [x] 1.2 GREEN: extend `bio/domain/protein/AminoAcid.scala` — add a private `byCode: Map[Char, AminoAcid] = all.map(aa => aa.code -> aa).toMap` and companion `def fromChar(c: Char): Option[AminoAcid] = byCode.get(c)`; confirm tests pass

## 2. Error ADT

- [x] 2.1 RED: write `ProteinMassProblemErrorSpec` asserting `ProteinTooLong(length, max)` is a `ProteinMassProblemError` subtype; confirm it fails to compile
- [x] 2.2 GREEN: create `bio/domain/protein/ProteinMassProblemError.scala` — `sealed trait` with `final case class ProteinTooLong(length: Int, max: Int)`; confirm test passes

## 3. Validated problem bundle

- [x] 3.1 RED: write `ProteinMassProblemSpec` covering: accepts `SKADYEK` (preserves protein); accepts empty protein; accepts a 1000-aa protein; rejects a 1001-aa protein with `ProteinTooLong(1001, 1000)`; `assertDoesNotCompile` for public `apply(...)` and `.copy(...)`; confirm it fails to compile
- [x] 3.2 GREEN: create `bio/domain/protein/ProteinMassProblem.scala` — `sealed abstract case class ProteinMassProblem(protein: ProteinString)`; `MaxLength = 1000`; `from(protein)` rejects length > 1000 with `ProteinTooLong`; empty accepted; constructed via `Right(new ProteinMassProblem(protein) {})`; confirm tests pass

## 4. Result type

- [x] 4.1 RED: write `ProteinMassSpec` (in `bio.domain.protein`) asserting `format` on `ProteinMass(821.39192)` returns exactly `821.392`; confirm it fails to compile
- [x] 4.2 GREEN: create `bio/domain/protein/ProteinMass.scala` — `final case class ProteinMass(mass: Double)` with `def format: String = f"$mass%.3f"`; confirm test passes

## 5. Mass-calculation algorithm

- [x] 5.1 RED: write `ProteinMassAlgoSpec` (in `bio.algorithms.protein`) asserting: canonical `SKADYEK` mass within `1e-3` of `821.39192`; single residue `W` within `1e-5` of `186.07931`; empty protein mass `0`; confirm it fails to compile
- [x] 5.2 GREEN: create `bio/algorithms/protein/ProteinMass.scala` (alias domain result as `Result`) — `calculate(problem: ProteinMassProblem): Result` summing `problem.protein.value.iterator.map(c => AminoAcid.fromChar(c).fold(0.0)(_.monoisotopicMass)).sum`; pure/total, no `var`/`while`; confirm tests pass
- [x] 5.3 REFACTOR: review `calculate` for clarity (pure FP, total); rerun spec to confirm still green

## 6. IO runner and Main wiring

- [x] 6.1 GREEN: create `bio/problems/PRTMProb.scala` — `solve(): IO[Unit]` reads `prtm_data.txt`, takes the first non-blank line, builds a `ProteinString` (char error → descriptive `Left`), validates into `ProteinMassProblem` (length error → `Left`), runs `calculate`, prints `result.format`; on any error prints a descriptive message rather than throwing (mirror `SPECProb`)
- [x] 6.2 GREEN: wire `PRTMProb.solve()` into `bio/Main.scala`; run `sbt run` and confirm it prints `821.392`

## 7. Verification

- [x] 7.1 Run full `sbt test` and confirm the whole suite is green with the new specs included
