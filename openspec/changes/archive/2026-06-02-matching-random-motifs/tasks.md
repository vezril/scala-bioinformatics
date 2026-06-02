## 1. Error type

- [x] 1.1 RED: write `RandomMotifProblemErrorSpec` asserting `MotifTooLong(length, max)`, `NonPositiveTrials(trials)`, and `TooManyTrials(trials, max)` construct and expose their fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.analysis.RandomMotifProblemError` — `sealed trait` + `final case class MotifTooLong(length: Int, max: Int)`, `final case class NonPositiveTrials(trials: Int)`, `final case class TooManyTrials(trials: Int, max: Int)`; run the spec green.

## 2. RandomMotifProblem domain type

- [x] 2.1 RED: write `RandomMotifProblemSpec` — accepts motif `ATAGCCGA`/trials 90000/GC 0.6 (Right, fields preserved), accepts 10 bp motif + trials 100000, accepts empty motif, rejects 11 bp motif (`MotifTooLong(11, 10)`), rejects trials 0 (`NonPositiveTrials(0)`), rejects trials 100001 (`TooManyTrials(100001, 100000)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.analysis.RandomMotifProblem` as `sealed abstract case class RandomMotifProblem(motif: DnaString, trials: Int, gcContent: Probability)` with `MaxMotifLength = 10`, `MaxTrials = 100000`, and `from(motif, trials, gcContent): Either[RandomMotifProblemError, RandomMotifProblem]` applying first-failure-wins (motif length > 10 → `MotifTooLong`; trials < 1 → `NonPositiveTrials`; trials > 100000 → `TooManyTrials`), building via `Right(new RandomMotifProblem(...) {})`. Run green.

## 3. RandomMotifMatch result type

- [x] 3.1 RED: write `RandomMotifMatchResultSpec` — `probability` field exposure (`0.25`), `format` of `0.5` → `"0.500"`, `format` of `0.1234` → `"0.123"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.analysis.RandomMotifMatch` — `final case class RandomMotifMatch(probability: Double)` with `format: String = f"${probability}%.3f"`. Run green.

## 4. MatchingRandomMotifs algorithm

- [x] 4.1 RED: write `MatchingRandomMotifsAlgoSpec` with scenarios — canonical sample (90000, 0.6, `ATAGCCGA`) → probability within `0.001` of `0.689`; (1, 0.5, `G`) → `0.25`; (1, 0.5, `AT`) → `0.0625`; (5, 0.0, `G`) → `0.0`; empty motif → `1.0`. Build inputs with `DnaString.from`, `Probability.from`, `RandomMotifProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.analysis.MatchingRandomMotifs` with `probability(problem: RandomMotifProblem): RandomMotifMatch`. Compute per-symbol probability by dispatching on the `DnaNucleotide` ADT (`G`/`C` → `x/2`, `A`/`T` → `(1-x)/2`) over `problem.motif.value` (lifted via `DnaNucleotide.fromChar`), take the product `p`, then `1.0 - math.pow(1.0 - p, problem.trials)`; wrap in `RandomMotifMatch`. Run green.
- [x] 4.3 REFACTOR: review for `var`/`while`/mutable collections (none — pure FP) and raw `Char` literals (none — dispatch on the ADT); extract a small private per-symbol helper; confirm `Probability`/`DnaNucleotide` reuse. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.RSTRProb` reading `rstr_data.txt` (first line `N x`, second line the motif). Parse: split first line on whitespace → `toIntOption`/`toDoubleOption`; validate the double via `Probability.from`; build `DnaString.from` and `RandomMotifProblem.from`; run `MatchingRandomMotifs.probability`; print `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `RSTRProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
