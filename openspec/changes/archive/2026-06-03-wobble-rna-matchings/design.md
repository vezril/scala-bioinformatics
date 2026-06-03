## Context

RNAS ("Wobble Bonding and RNA Secondary Structures") counts every valid noncrossing matching in the bonding graph of an RNA string `s` (≤ 200 bp). Edges connect `A`–`U`, `C`–`G`, and `U`–`G` (wobble); an edge may join positions `j < k` only when `k ≥ j+4`; partial matchings (including the empty matching) all count. The counts grow past `Long` (the sample alone is `284 850 219 977 421`), so the answer is exact `BigInt`.

The project already solves the un-wobbled, no-separation variant in `bio.algorithms.nucleic.MotzkinMatching` (MOTZ) with a bottom-up interval DP over an `Array`. RNAS reuses that shape and `bio.domain.nucleic.RnaString`.

## Goals / Non-Goals

**Goals:**
- Validated `WobbleMatchingProblem(rna)` (≤ 200 bp) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `WobbleMatching.count(problem): WobbleMatchings`.
- Result type with `format: String` (the exact count).
- Exact `BigInt` arithmetic (no modulo), O(n³) time / O(n²) space.

**Non-Goals:**
- Enumerating the matchings — only the count is required.
- Modular reduction (unlike MOTZ/CAT) — RNAS wants the full integer.

## Decisions

**1. Interval DP, mirroring MOTZ with wobble + separation.**
Let `M(i)(j)` be the number of valid noncrossing matchings on the inclusive substring `s[i..j]`, with `M(i)(j) = 1` for `j < i`. Then
`M(i)(j) = M(i+1)(j) + Σ_{k=i+4}^{j} [pair(s_i, s_k)] · M(i+1)(k-1) · M(k+1)(j)`,
where the first term leaves `i` unbonded and the sum bonds `i` to each admissible `k`. Two changes from MOTZ: `pair` additionally accepts `U`–`G`/`G`–`U` (wobble), and `k` starts at `i+4` (not `i+1`) to enforce the minimum separation `k ≥ j+4`. The answer is `M(0)(n-1)` (and `1` for the empty string). Filled bottom-up by increasing interval length — `O(n³)` arithmetic, `O(n²)` memory; at `n = 200` that is ~8·10⁶ `BigInt` operations, fast.

**2. Exact `BigInt`, no modulo.**
The table is `Array.fill(n, n)(BigInt(0))`; `get(i, j) = if (j < i) BigInt(1) else M(i)(j)`. Sums and products are `BigInt`, so the full value (well beyond `Long`) is preserved. (MOTZ reduces mod 1 000 000; RNAS must not.)

**3. Imperative interval-DP fill (the established counting-DP style).**
The table fill uses `var`/`while` over the `Array`, exactly as `MotzkinMatching`/`NoncrossingMatching` do for these O(n³) RNA counting DPs; the public `count` signature is pure and total.

**4. Validation.**
`WobbleMatchingProblem.from(rna)` rejects `rna.value.length > 200` with `SequenceTooLong(length, 200)`; character validity (`A`,`C`,`G`,`U`) is owned upstream by `RnaString`. The empty string is accepted (count `1`).

**5. Naming and placement.**
`WobbleMatchingProblem`, `WobbleMatchingProblemError`, and the `WobbleMatchings` result live in `bio.domain.nucleic`; the algorithm `WobbleMatching.count` in `bio.algorithms.nucleic` (alongside MOTZ/CAT/PMCH). Result (`WobbleMatchings`) and algorithm (`WobbleMatching`) names are distinct, so no alias is needed.

## Risks / Trade-offs

- **[BigInt cost]** → at `n ≤ 200` the O(n³) `BigInt` DP is comfortably fast; exactness is required by the problem.
- **[Correctness of wobble + separation]** → verified on the sample (length-64 input → `284850219977421`) and small hand cases (`GAAAU` → 2 via a single wobble pair at distance 4; `GU` → 1 since the pair is too close).
- **[Empty / unpaired inputs]** → empty string and pair-free strings (e.g. `AAAA`) count `1` (the empty matching); covered by scenarios.
- **[Imperative DP]** → confined to the table fill, consistent with the MOTZ sibling; the public signature is pure.
