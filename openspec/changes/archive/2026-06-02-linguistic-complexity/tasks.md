## 1. LinguisticComplexityProblem domain type

(No error type: `DnaString` already enforces the 100 kbp bound, so the problem carries no extra invariant — a plain wrapper, like `SpectralConvolutionProblem`.)

- [x] 1.1 RED: write `LinguisticComplexityProblemSpec` — constructing from `ATTTGGATT` exposes that `DnaString` via `dna`; constructing from an empty `DnaString` exposes the empty string. Confirm RED.
- [x] 1.2 GREEN: create `bio.domain.analysis.LinguisticComplexityProblem` as `final case class LinguisticComplexityProblem(dna: DnaString)`. Run green.

## 2. LinguisticComplexity result type

- [x] 2.1 RED: write `LinguisticComplexityResultSpec` — `value` field exposure, `format` of `0.875` → `"0.875"`, `format` of `0.4` → `"0.400"`. Confirm RED.
- [x] 2.2 GREEN: create `bio.domain.analysis.LinguisticComplexity` result — `final case class LinguisticComplexity(value: Double)` with `format: String = f"$value%.3f"`. Run green.

## 3. LinguisticComplexityAnalysis algorithm

- [x] 3.1 RED: write `LinguisticComplexityAnalysisSpec` — canonical `ATTTGGATT` → within 0.001 of `0.875`; `A` → `1.0`; `AAAA` → within 0.001 of `0.4`; plus a brute-force cross-check: for a few small strings, the computed value equals `bruteDistinct(s).toDouble / m(4, n)` where `bruteDistinct` is the size of the set of all non-empty substrings. Build inputs via `DnaString.from` + `LinguisticComplexityProblem.from`. Confirm RED.
- [x] 3.2 GREEN: create `bio.algorithms.analysis.LinguisticComplexityAnalysis` with `compute(problem): LinguisticComplexity`. For `n = 0` return `LinguisticComplexity(0.0)`. Else `sub = n*(n+1)/2 - sumLcp` (Long), `m = maxSubstrings(4, n)`, value `sub.toDouble / m.toDouble`. Implement (pure FP):
  - `suffixArray(s): Vector[Int]` by prefix doubling — ranks from char codes, each round `sortBy((rank(i), rank(i+k)))` and recompute ranks via `foldLeft`, doubling `k` until ranks all distinct;
  - `rankOf(sa, n): Vector[Int]` = inverse permutation of `sa`;
  - `sumLcp(s, sa, rank): Long` by Kasai — `foldLeft` over `0 until n` threading `(h, sum)`, extending the match from `h` with a `@tailrec` counter, carrying `max(h-1, 0)`;
  - `maxSubstrings(a, n): Long = Σ_{k=1}^{n} min(aPowCapped(a, k, n-k+1), n-k+1)` with a capped tail-recursive power (stop once it exceeds the cap) to avoid overflow.
  Run green.
- [x] 3.3 REFACTOR: review for `var`/`while`/mutable collections (none — immutable `Vector`, folds, tailrec) and confirm `Long` arithmetic + overflow-safe powers; extract `suffixArray`/`rankOf`/`sumLcp`/`maxSubstrings` helpers. Run full `sbt test` green.

## 4. Runner

- [x] 4.1 Create `bio.problems.LINGProb` reading the DNA string from `ling_data.txt` (single non-empty line), build `DnaString.from` and `LinguisticComplexityProblem.from`, run `LinguisticComplexityAnalysis.compute`, print `format` via `IO.println`; all errors printed (never thrown).
- [x] 4.2 Wire `LINGProb.solve()` as the active runner in `bio.Main` (comment the prior active line), and run the full `sbt test` suite to confirm everything is green.
