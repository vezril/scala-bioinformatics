## 1. Error type

- [x] 1.1 RED: write `ExpectedRestrictionSitesProblemErrorSpec` asserting `MotifTooLong(length, max)`, `OddMotifLength(length)`, `NonPositiveLength(length)`, `LengthTooLarge(length, max)`, and `TooManyGcContents(size, max)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.analysis.ExpectedRestrictionSitesProblemError` — `sealed trait` + `MotifTooLong(length: Int, max: Int)`, `OddMotifLength(length: Int)`, `NonPositiveLength(length: Int)`, `LengthTooLarge(length: Int, max: Int)`, `TooManyGcContents(size: Int, max: Int)`; run the spec green.

## 2. ExpectedRestrictionSitesProblem domain type

- [x] 2.1 RED: write `ExpectedRestrictionSitesProblemSpec` — accepts (`AG`, 10, [0.25,0.5,0.75]) (Right, fields preserved), accepts upper bounds (10 bp motif, length 1000000, 20 GC-contents), accepts empty motif, rejects 12 bp motif (`MotifTooLong(12,10)`), rejects odd motif `AGT` (`OddMotifLength(3)`), rejects length 0 (`NonPositiveLength(0)`), rejects length 1000001 (`LengthTooLarge(1000001,1000000)`), rejects 21 GC-contents (`TooManyGcContents(21,20)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.analysis.ExpectedRestrictionSitesProblem` as `sealed abstract case class ExpectedRestrictionSitesProblem(motif: DnaString, length: Int, gcContents: Vector[Probability])` with `MaxMotifLength = 10`, `MaxLength = 1000000`, `MaxGcContents = 20`, and `from(...)` applying first-failure-wins (motif length > 10 → `MotifTooLong`; motif length odd → `OddMotifLength`; length < 1 → `NonPositiveLength`; length > 1000000 → `LengthTooLarge`; gcContents.size > 20 → `TooManyGcContents`), building via `Right(new ExpectedRestrictionSitesProblem(...) {})`. Run green.

## 3. ExpectedRestrictionSites result type

- [x] 3.1 RED: write `ExpectedRestrictionSitesResultSpec` — `expectations` field exposure, `format` of `[0.421875, 0.5625]` → `"0.422 0.563"`, empty result → `""`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.analysis.ExpectedRestrictionSites` result — `final case class ExpectedRestrictionSites(expectations: Vector[Double])` with `format: String = expectations.map(e => f"$e%.3f").mkString(" ")`. Run green.

## 4. ExpectedRestrictionSites algorithm

- [x] 4.1 RED: write `ExpectedRestrictionSitesAlgoSpec` with scenarios — canonical (10, `AG`, [0.25,0.5,0.75]) → each within 0.001 of [0.422,0.563,0.422]; (1, `AG`, [0.5]) → [0.0]; (10, `AT`, [0.5]) → within 0.001 of 0.5625; (10, `GG`, [0.0]) → [0.0]; empty GC-contents → empty result. Build inputs with `DnaString.from`, `Probability.from`, `ExpectedRestrictionSitesProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.analysis.ExpectedRestrictionSites`, aliasing the domain result via `import bio.domain.analysis.{ExpectedRestrictionSites => Result}`. Implement `expectedCounts(problem): Result`: `positions = math.max(0, problem.length - problem.motif.value.length + 1)`; for each `gc` in `problem.gcContents`, `p = ∏ symbolProbability(nucleotide, gc.value)` over `problem.motif.value` (lifted via `DnaNucleotide.fromChar`), expected = `positions.toDouble * p`; wrap the vector in `Result`. Per-symbol helper dispatches on the `DnaNucleotide` ADT (`G`/`C` → `x/2`, `A`/`T` → `(1-x)/2`). Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — pure FP) and raw `Char` literals (none — ADT dispatch); confirm `Probability`/`DnaNucleotide` reuse and the order-preserving `map` over `gcContents`. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.EVALProb` reading `eval_data.txt` (line 1: length `n`; line 2: motif `s`; line 3: whitespace-separated GC-contents). Parse: `n` via `toIntOption`; motif via `DnaString.from`; each GC token via `toDoubleOption` then `Probability.from` (fold into `Either`); build `ExpectedRestrictionSitesProblem.from`; run `ExpectedRestrictionSites.expectedCounts`; print `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `EVALProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
