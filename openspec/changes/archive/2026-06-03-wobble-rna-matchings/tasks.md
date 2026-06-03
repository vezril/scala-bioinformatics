## 1. WobbleMatchingProblemError ADT

- [x] 1.1 RED: write a test in `bio.domain.nucleic.WobbleMatchingProblemErrorSpec` asserting `WobbleMatchingProblemError.SequenceTooLong(250, 200)` carries `length == 250` and `max == 200`; confirm it fails to compile ("not found").
- [x] 1.2 GREEN: create `bio.domain.nucleic.WobbleMatchingProblemError` as a sealed trait with `SequenceTooLong(length: Int, max: Int)`. Confirm the test passes.

## 2. WobbleMatchingProblem domain type

- [x] 2.1 RED: write `bio.domain.nucleic.WobbleMatchingProblemSpec` covering: accepts length ≤ 200, accepts empty, rejects length 201 with `SequenceTooLong(201, 200)`, `assertDoesNotCompile` for public `apply`, `assertDoesNotCompile` for `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.nucleic.WobbleMatchingProblem` as a `sealed abstract case class WobbleMatchingProblem(rna: RnaString)` with `MaxLength = 200` and `from(rna): Either[WobbleMatchingProblemError, WobbleMatchingProblem]` returning `Right(new WobbleMatchingProblem(rna) {})` or `Left(SequenceTooLong(...))`. Confirm GREEN.

## 3. WobbleMatchings result type

- [x] 3.1 RED: write `bio.domain.nucleic.WobbleMatchingsSpec` asserting `WobbleMatchings(BigInt("284850219977421")).format == "284850219977421"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.nucleic.WobbleMatchings(count: BigInt)` with `def format: String = count.toString`. Confirm GREEN.

## 4. WobbleMatching algorithm

- [x] 4.1 RED: write `bio.algorithms.nucleic.WobbleMatchingSpec` covering the canonical sample → `BigInt("284850219977421")`, empty → 1, `AAAA` → 1, `GU` → 1, `GAAAU` → 2. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.nucleic.WobbleMatching.count(problem): WobbleMatchings` as a `BigInt` interval DP (imperative fill, pure signature): `get(i,j)=BigInt(1)` for `j<i`; `M(i)(j) = get(i+1,j) + Σ_{k=i+4..j, pair(s_i,s_k)} get(i+1,k-1)*get(k+1,j)`; `pair` admits A-U/U-A, C-G/G-C, U-G/G-U; answer `M(0)(n-1)` (and `1` for empty). Confirm GREEN.
- [x] 4.3 REFACTOR: confine `var`/`while` to the table fill, keep the public signature pure/total, tidy `pair`/`get` helpers; rerun the spec to confirm still GREEN.

## 5. RNASProb runner

- [x] 5.1 Add `bio.problems.RNASProb` reading `src/main/scala/resources/rnas_data.txt`, parsing the RNA via `RnaString.from`, building the `WobbleMatchingProblem`, running `WobbleMatching.count`, and printing `result.format` through `IO`; errors print a message rather than throw.
- [x] 5.2 Point `Main.scala` at `RNASProb.solve()` and confirm `sbt run` prints `284850219977421` for the canonical dataset.

## 6. Full suite

- [x] 6.1 Run `sbt test` and confirm the whole suite is green.
