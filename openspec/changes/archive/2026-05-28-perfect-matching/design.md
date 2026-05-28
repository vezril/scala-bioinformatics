## Context

Rosalind problem 34 (PMCH) gives an RNA string `s` (length `≤ 80`, balanced — `#A == #U` and `#C == #G`) and asks for the number of perfect matchings of the *basepair edges* in its bonding graph. The bonding graph has one node per nucleotide and edges only between A-U pairs and between C-G pairs (the "basepair edges" — distinct from the cyclic "adjacency edges" that connect consecutive positions). A perfect matching of basepair edges picks `#A` A-U edges (one per A, one per U) and `#C` C-G edges (one per C, one per G), independently.

Because the A-U sub-problem and the C-G sub-problem decouple (they share no nodes), the count factors:

> `perfectMatchings(s) = (#A)! · (#C)!`

The canonical Rosalind sample `AGCUAGUCAU` has `#A = #U = 3` and `#C = #G = 2`, giving `3! · 2! = 6 · 2 = 12`.

The framework already hosts `bio.{algorithms,domain}.nucleic` with RNA primitives — this slots in there.

## Goals / Non-Goals

**Goals:**
- Provide a validated `PerfectMatchingProblem` ADT enforcing the Rosalind input contract (length cap and the two balance conditions) so the algorithm can assume well-formed input.
- Provide `bio.algorithms.nucleic.PerfectMatching.count` returning a `BigInt` (the closed-form `auCount! · cgCount!`).
- TDD coverage at both layers, including the canonical Rosalind sample, the empty-input edge case (1 matching), single-pair cases, mixed cases, the 40-pair upper boundary (`40!`), and three validation-failure cases.

**Non-Goals:**
- General graph-matching algorithms. This spec exploits the bipartite-decoupled structure to land at a closed form — no need for a Hopcroft–Karp / Hungarian / blossom implementation.
- The Catalan / Motzkin number variants that show up in later Rosalind problems (CAT, MOTZ). Those are separate features.
- FASTA-aware file ingestion. The Rosalind input *is* FASTA-wrapped, but the framework already has `FastaFileReader` — runners can compose. The algorithm sees a raw `RnaString`.
- Modular arithmetic. Rosalind expects the exact integer, no modulo. Hence `BigInt`.

## Decisions

**1. Precompute `auCount` and `cgCount` in the smart constructor.**

The validation already iterates the RNA once to count `A`/`C`/`G`/`U`. Storing the two precomputed counts on the constructed value means the algorithm doesn't iterate again — and any downstream code that wants the counts (a problem runner, a future caller) doesn't either. Mirrors `GeneticCharacterTableProblem.length`'s "compute-once" pattern.

**2. Return type: `BigInt`, not `Long`.**

At the 80-character upper bound the worst case is `40! · 0! = 40! ≈ 8.16 × 10^47` — `Long` overflows at `~9.2 × 10^18`. Rosalind expects the exact value; no modulo is given. `BigInt` is the only correct choice. **Alternative considered:** `Long` with a guard (rejected: the guard would have to refuse legal inputs, which the framework's "validated wrapper → algorithm returns the answer" convention forbids).

**3. Validation order: length cap → AU balance → CG balance.**

Lower-cost check first (length is a single read), then the AU balance, then the CG balance. First-failure-wins. Errors name the offending counts so callers can diagnose the imbalance without re-counting.

**4. Empty input is *not* an error — it yields `BigInt(1)`.**

The bonding graph of the empty RNA string is empty; the *empty matching* is its only matching, and it is vacuously perfect. The closed form gives `0! · 0! = 1 · 1 = 1`. Allowing empty input keeps the algorithm total and matches the mathematics. Rosalind never gives empty input, but the framework's wrappers prefer total functions where the math makes sense.

**5. Place under `bio.{algorithms,domain}.nucleic`.**

Same family as `CountNucleotides`, `DnaReverseComplement`, `RnaTranscription` — all "operate on an RNA/DNA sequence, return some output". The algorithm happens to compute factorials, but the *operation* is "given an RNA string, count its perfect matchings". Closest fit. **Alternative considered:** `bio.algorithms.combinatorics` (rejected: the input type is `RnaString`, and the existing `Combinations` and `Permutations` there take pure integer inputs — splitting domain ADT and algorithm across two subdomains breaks the framework's "same place" pattern).

**6. `factorial(n: Int): BigInt` as a small private helper.**

Implemented as `(BigInt(1) to BigInt(n)).foldLeft(BigInt(1))(_ * _)` — three lines, no external dependency. **Alternative considered:** memoize across calls (rejected: not needed at `n ≤ 40`; each factorial computes in microseconds and the algorithm is called once per Rosalind dataset).

## Risks / Trade-offs

- **[Risk]** Returning `BigInt` to a Rosalind runner whose `IO.println` formats it via `.toString` — the default `BigInt.toString` is base-10 with no separators, exactly what Rosalind expects. → No mitigation needed; this is the standard behavior.
- **[Risk]** A caller who computes `factorial(auCount)` themselves *for some other reason* might be tempted to reach into `PerfectMatching` for the helper. → **Mitigation:** the helper is `private` to the algorithm object. If reuse becomes needed later, promote it to a utility object.
- **[Trade-off]** No memoization of factorials. At `n ≤ 40` the cost is microseconds; introducing a memo table would complicate the algorithm with no observable benefit. If a future spec wants high-volume factorial use, factor out a `MathUtils.factorial` then.
- **[Trade-off]** Empty input is accepted (returns `1`) rather than rejected. Rosalind never produces it, but it falls out cleanly from the closed form. If a future caller wants "I have a real RNA, never empty", they can add their own check.
