## 1. Error type

- [x] 1.1 RED: write `SupersequenceProblemErrorSpec` asserting `SequenceTooLong(length, max)` constructs and exposes its fields; confirm it fails to compile (`not found`).
- [x] 1.2 GREEN: create `bio.domain.analysis.SupersequenceProblemError` — `sealed trait` + `final case class SequenceTooLong(length: Int, max: Int)`; run the spec green.

## 2. SupersequenceProblem domain type

- [x] 2.1 RED: write `SupersequenceProblemSpec` — accepts `ATCTGAT`/`TGCATA` (Right, `s`/`t` preserved), rejects a 1001 bp `s` (`SequenceTooLong(1001, 1000)`), rejects a 1001 bp `t` with short `s` (`SequenceTooLong(1001, 1000)`), and `assertDoesNotCompile` for public `apply` and `copy`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.analysis.SupersequenceProblem` as `sealed abstract case class SupersequenceProblem(s: DnaString, t: DnaString)` with `MaxLength = 1000` and `from(s, t)` applying first-failure-wins (`s.value.length > 1000` → `SequenceTooLong`; then `t.value.length > 1000` → `SequenceTooLong`), building via `Right(new SupersequenceProblem(s, t) {})`. Run green.

## 3. Supersequence result type

- [x] 3.1 RED: write `SupersequenceResultSpec` — `value` field exposure, `format` of `ATGCATGAT` → `"ATGCATGAT"`. Confirm RED.
- [x] 3.2 GREEN: create `bio.domain.analysis.Supersequence` — `final case class Supersequence(value: String)` with `format: String = value`. Run green.

## 4. ShortestCommonSupersequence algorithm

- [x] 4.1 RED: write `ShortestCommonSupersequenceSpec` with a private `isSubsequence(sub, sup)` helper — canonical `ATCTGAT`/`TGCATA` → result length 9 and both inputs are subsequences of it; `ACGT`/`""` → `ACGT`; `ACGT`/`ACGT` → `ACGT`; `AA`/`CC` → length 4 and both subsequences. Build via `SupersequenceProblem.from`. Confirm RED.
- [x] 4.2 GREEN: create `bio.algorithms.analysis.ShortestCommonSupersequence` with `build(problem): Supersequence`. Fill an SCS-length DP `Array.ofDim[Int](m+1, n+1)` (alignment-family imperative `var`/`while`): `dp(i)(0)=i`, `dp(0)(j)=j`, match → `dp(i-1)(j-1)+1` else `1 + min(dp(i-1)(j), dp(i)(j-1))`. `@tailrec` backtrack from `(m,n)` prepending to a `List[Char]`: `i==0` → take `t(j-1)`; `j==0` → take `s(i-1)`; match → take shared char (i-1,j-1); `dp(i-1)(j) <= dp(i)(j-1)` → take `s(i-1)` (i-1); else take `t(j-1)` (j-1). Wrap `acc.mkString` in `Supersequence`. Run green.
- [x] 4.3 REFACTOR: confine the imperative DP to the table fill (public `build` pure/total); confirm the `@tailrec` backtrack and correct empty-string handling. Run full `sbt test` green.

## 5. Runner

- [x] 5.1 Create `bio.problems.SCSPProb` reading two plain DNA lines from `scsp_data.txt` (first two non-empty lines), building `DnaString.from` for each and `SupersequenceProblem.from`, running `ShortestCommonSupersequence.build`, printing `format` via `IO.println`; all errors printed (never thrown).
- [x] 5.2 Wire `SCSPProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
