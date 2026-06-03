## Context

MGAP gives two DNA strings `s`, `t` (≤ 5000 bp each in FASTA) and asks for the maximum number of gap symbols across all *maximum-score* alignments under any scoring with `m>0, d<0, g<0`.

The alignment problems already live in `bio.{domain,algorithms}.analysis` (overlap, fitting, semiglobal, …), wrapping two `DnaString`s and validating per-field length. FASTA two-record reading is handled by `bio.parsing.FastaFileReader.read(path): IO[Either[FastaError, List[FastaRecord]]]` with `FastaRecord(id, dna)`. The framework permits imperative DP (`var`/`while`/`Array`) for alignment-family algorithms, keeping the function signature pure and total.

## Goals / Non-Goals

**Goals:**
- Validated `MaxGapProblem(s, t)` (each ≤ 5000 bp) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `MaximizeGapSymbols.maxGaps(problem): MaxGapSymbols`.
- Result type with `format: String` (the integer count).
- Scale to 5000×5000 in O(n·m) time and O(min(n,m)) space.

**Non-Goals:**
- Producing an actual alignment — only the gap count is required.
- Supporting general gap models — the result is independent of the specific `m, d, g` values (given the sign constraints).

## Decisions

**1. The gap count is `|s| + |t| − 2·LCS(s,t)`.**
With `m>0, d<0, g<0`, a maximum-score alignment aligns a longest common subsequence as matches; among optimal alignments, the one maximising gaps replaces every non-matched position with gap symbols rather than mismatches. Each of the `LCS` matched columns consumes one symbol from each string; the remaining `|s| − LCS` symbols of `s` and `|t| − LCS` symbols of `t` are each opposite a gap, giving `(|s| − LCS) + (|t| − LCS) = |s| + |t| − 2·LCS` gap symbols. Verified on the sample: `|s|=6, |t|=7, LCS("AACGTA","ACACCTA") = 5` (e.g. `AACTA`) → `6 + 7 − 10 = 3`.

**2. LCS length by a rolling-row dynamic program (alignment-family imperative DP).**
`lcs(i,j) = lcs(i−1,j−1) + 1` on a match, else `max(lcs(i−1,j), lcs(i,j−1))`. Only the previous and current rows are needed, so the DP keeps two `Array[Int]` of length `min(|s|,|t|) + 1` (swapping the shorter string in for the inner dimension) — O(n·m) time, O(min) space, no 25M-cell matrix. This uses the alignment-family `var`/`while`/`Array` exception; the public `maxGaps` signature stays pure and total.

**3. Validation rules and order (first-failure-wins).**
`MaxGapProblem.from(s, t)` checks `s.value.length <= 5000` then `t.value.length <= 5000`, each failing with `SequenceTooLong(length, 5000)` for the first offending sequence. (The 5000 cap is below `DnaString`'s own 100000 cap, so it is a genuine, reachable check.) Empty sequences are accepted.

**4. Naming and placement.**
`MaxGapProblem`, `MaxGapProblemError`, and the `MaxGapSymbols` result live in `bio.domain.analysis`; the algorithm `MaximizeGapSymbols.maxGaps` in `bio.algorithms.analysis`. Result (`MaxGapSymbols`) and algorithm (`MaximizeGapSymbols`) names are distinct, so no alias is needed.

## Risks / Trade-offs

- **[Memory/time at 5000×5000]** → O(n·m) = 25M cell updates is fast; the rolling two-row layout keeps memory to ~O(5000) ints, avoiding a 100 MB full matrix.
- **[Parameter independence]** → the `|s|+|t|−2·LCS` identity holds for all `m>0, d<0, g<0`; documented and verified on the sample.
- **[Empty / identical / disjoint inputs]** → identical strings give `0` gaps; an empty string gives `|other|`; fully disjoint strings give `|s|+|t|`; all covered by scenarios.
- **[Imperative DP]** → confined to the private LCS helper, consistent with the project's other alignment algorithms; the public signature is pure and total.
