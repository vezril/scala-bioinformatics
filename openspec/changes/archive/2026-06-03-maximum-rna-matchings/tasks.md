## 1. MaximumMatchingProblemError ADT

- [x] 1.1 RED: write `bio.domain.nucleic.MaximumMatchingProblemErrorSpec` asserting `ExceedsMaxLength(150, 100)` carries `length == 150` / `max == 100`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.nucleic.MaximumMatchingProblemError` as a sealed trait with `ExceedsMaxLength(length, max)`. Confirm the test passes.

## 2. MaximumMatchingProblem domain type

- [x] 2.1 RED: write `bio.domain.nucleic.MaximumMatchingProblemSpec` covering: accepts length ≤ 100 with correct counts, accepts unbalanced `AUU`, accepts empty (all counts 0), rejects length 101 (`ExceedsMaxLength(101,100)`), `assertDoesNotCompile` for public `apply` and for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.nucleic.MaximumMatchingProblem` as `sealed abstract case class MaximumMatchingProblem(rna: RnaString, aCount: Int, uCount: Int, cCount: Int, gCount: Int)` with `from(rna)` enforcing `MaxLength = 100`, counting the four symbols in one pass, returning `Right(new MaximumMatchingProblem(...) {})` or `Left(ExceedsMaxLength(...))`. Confirm GREEN.

## 3. MaximumMatchings result type

- [x] 3.1 RED: write `bio.domain.nucleic.MaximumMatchingsSpec` asserting `MaximumMatchings(BigInt(6)).format == "6"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.nucleic.MaximumMatchings(count: BigInt)` with `def format: String = count.toString`. Confirm GREEN.

## 4. MaximumMatching algorithm

- [x] 4.1 RED: write `bio.algorithms.nucleic.MaximumMatchingSpec` covering: `AUGCUUC` → 6, empty → 1, `AUU` → 2, `AAUU` → 2, `AAA` → 1. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.nucleic.MaximumMatching.count(problem): MaximumMatchings` = `fallingFactorial(max(a,u), min(a,u)) * fallingFactorial(max(c,g), min(c,g))` where `fallingFactorial(hi, lo) = (hi-lo+1 to hi).map(BigInt(_)).product` (and `1` when `lo == 0`). Confirm GREEN.
- [x] 4.3 REFACTOR: tidy the `fallingFactorial` helper, keep `count` pure/total; rerun the spec to confirm still GREEN.

## 5. MMCHProb runner

- [x] 5.1 Add `bio.problems.MMCHProb` reading `src/main/scala/resources/mmch_data.txt` (FASTA: concatenate non-`>` lines), building the `RnaString` and `MaximumMatchingProblem`, running `MaximumMatching.count`, and printing `result.format` through `IO`; errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `MMCHProb.solve()` and confirm `sbt run` prints `6` for the canonical dataset.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
