## Context

MMCH ("Maximum Matchings and RNA Secondary Structures") counts the *maximum* matchings of basepair edges in the bonding graph of an RNA string `s` (length ≤ 100). The bonding graph has A-U edges between every A and every U, and C-G edges between every C and every G; the two subgraphs are vertex-disjoint. A maximum matching pairs as many bases as possible. PMCH (already in the project as `PerfectMatching`) is the special balanced case `#A = #U`, `#C = #G`; MMCH is the general case.

**Closed form.** On the complete bipartite A-U subgraph with `a` A's and `u` U's, a maximum matching has `min(a, u)` edges, and the number of such matchings is the falling factorial `P(hi, lo) = hi · (hi-1) · … · (hi-lo+1)` where `hi = max(a, u)`, `lo = min(a, u)` (choose, in order, which `lo` of the `hi` larger-side nodes are matched). The C-G subgraph is independent, so the total is the product:

  `maximumMatchings(s) = P(max(a,u), min(a,u)) · P(max(c,g), min(c,g))`.

For the sample `AUGCUUC` (`a=1, u=3, c=2, g=1`): `P(3,1) · P(2,1) = 3 · 2 = 6`.

The values overflow `Long` (at the cap, `P(50,50) = 50!` per factor), so the answer is exact `BigInt`. The framework reuses `RnaString` for the validated input.

## Goals / Non-Goals

**Goals:**
- Validated `MaximumMatchingProblem(rna, …counts…)` (length ≤ 100) via a smart constructor returning `Either`, `sealed abstract case class` to block `apply`/`copy`.
- Pure, total `MaximumMatching.count(problem): MaximumMatchings`.
- Result type with `format: String` (the exact count).
- Exact `BigInt` arithmetic, O(n) work.

**Non-Goals:**
- Enumerating the matchings — only the count is required.
- A balance requirement (the whole point of MMCH is the unbalanced case) — any RNA is valid.

## Decisions

**1. Closed-form product of falling factorials.**
`count = fallingFactorial(max(a,u), min(a,u)) * fallingFactorial(max(c,g), min(c,g))`, with `fallingFactorial(hi, lo) = (hi-lo+1 to hi).product` (and `1` when `lo = 0`). Pure functional `BigInt` arithmetic, no search, `O(n)` multiplications.

**2. Precompute the four symbol counts in the problem.**
Mirroring `PerfectMatchingProblem`, the smart constructor counts `#A`, `#U`, `#C`, `#G` in a single pass and stores them on `MaximumMatchingProblem`, so the algorithm is a pure arithmetic function of those counts. (Counts stored individually since MMCH does not assume `#A = #U`.)

**3. Exact `BigInt`, no modulo.**
Like PMCH, MMCH wants the exact integer; the result holds a `BigInt` and `format` renders its decimal string.

**4. Validation.**
`MaximumMatchingProblem.from(rna)` rejects `rna.value.length > 100` with `ExceedsMaxLength(length, 100)`; character validity (`A`,`C`,`G`,`U`) is owned upstream by `RnaString`. No balance check — every RNA within the length cap is a valid problem. The empty string is accepted (all counts 0, product `1`).

**5. Result rendering.**
`MaximumMatchings(count: BigInt)` with `format = count.toString` (mirroring `WobbleMatchings` from RNAS).

**6. Naming and placement.**
`MaximumMatchingProblem`, `MaximumMatchingProblemError`, and the `MaximumMatchings` result live in `bio.domain.nucleic` (beside the PMCH/MOTZ/RNAS types); the algorithm `MaximumMatching.count` in `bio.algorithms.nucleic`. Result (`MaximumMatchings`) and algorithm (`MaximumMatching`) names are distinct, so no import alias is needed.

## Risks / Trade-offs

- **[BigInt size]** → at the cap a factor can be `50!`; `BigInt` is exact and the O(n) multiplications are microseconds.
- **[Unbalanced inputs]** → the falling-factorial form is exactly the unbalanced generalisation; verified on the sample (`AUGCUUC` → 6) and small cases (`AUU` → 2, balanced `AAUU` → 2 matching PMCH).
- **[Empty / single-symbol inputs]** → empty → 1; a string with only A's (no U) → A-U factor `P(hi,0)=1`; covered by reasoning and scenarios.
- **[FASTA input]** → the runner concatenates the non-header lines of the FASTA record before building the `RnaString`.
