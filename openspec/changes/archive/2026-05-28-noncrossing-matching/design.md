## Context

Rosalind problem 35 (CAT) asks: given a balanced RNA string `s` (length `≤ 300`, `#A == #U`, `#C == #G`), how many *noncrossing* perfect matchings of basepair edges (`A`-`U` or `C`-`G` only) does its bonding graph admit, modulo `1 000 000`? "Noncrossing" means: if positions `i < j` are bonded and positions `k < l` are bonded with `i < k`, then we must have either `k < l < j` (the pair `(k,l)` is *nested inside* `(i,j)`) or `j < k` (the pair `(k,l)` comes *after* `(i,j)`) — never `i < k < j < l` (crossing).

The canonical Rosalind sample is `AUAU` → `2`:
- `(A_0, U_1)` & `(A_2, U_3)` — two sequential pairs
- `(A_0, U_3)` & `(A_2, U_1)`? No — `U_1` is between `A_0` and `U_3`, crossing. So only:
- `(A_0, U_3)` & `(U_1, ?)`? `U_1` would need an `A` partner *inside* `[A_0, U_3]` — that's `A_2`. So `(A_0, U_3)` & `(A_2, U_1)` — but wait, `U_1 < A_2 < U_3`, and inside the outer pair `(0, 3)` we have `(1, 2)` (using positions): not crossing because `1 < 2 < 3`. Yes, this is the second valid matching.

The math comes from the recurrence `m(i, j) = Σ_{k} m(i+1, k-1) · m(k+1, j)` summed over every `k > i` such that `s(k)` is the basepair partner of `s(i)` and the gap `[i+1, k-1]` has even length. When the alphabet is unconstrained and the gap can hold *any* matching this collapses to the standard Catalan recurrence `c_n = Σ c_{k-1} · c_{n-k}`.

The framework already hosts `bio.{algorithms,domain}.nucleic` with closely related `perfect-matching` (PMCH, spec 34) — this spec slots in there.

## Goals / Non-Goals

**Goals:**
- Provide a validated `NoncrossingMatchingProblem` ADT enforcing the Rosalind input contract (length `≤ 300`, balanced AU, balanced CG) so the algorithm can assume well-formed input.
- Provide `bio.algorithms.nucleic.NoncrossingMatching.count` returning the noncrossing-matching count modulo `1 000 000` as an `Int`.
- TDD coverage at both layers, including the canonical Rosalind sample, three edge cases (empty input, single pair, fully-nested), Catalan equivalence (`AUAU → 2`, `AUAUAU → 5`), mixed-alphabet (`AUCG`, `CGCG`), and a modulo-exercising case.

**Non-Goals:**
- Returning the un-modded count or using `BigInt`. Rosalind's spec is explicit: modulo `1 000 000` — a small integer fits in `Int`.
- The "all matchings" (`PMCH`, spec 34) variant. That's a different algorithm with a closed-form answer; this one needs DP.
- Pseudoknot-aware matchings. By construction "noncrossing" = "pseudoknot-free"; pseudoknotted secondary structures are out of scope for this spec (and for Rosalind CAT).
- Generic Catalan-number computation. The algorithm specialises to the basepair-constraint case.
- FASTA-aware file ingestion. Rosalind's input is FASTA-wrapped; a problem runner can compose `FastaFileReader` with the algorithm.

## Decisions

**1. Bottom-up `O(n²)`-cell interval DP, transitions `O(n)`.**

Fill `dp(i)(j)` for every inclusive sub-interval `[i, j]` of `s`, in order of increasing length. For each cell with `j - i + 1` even, sum over every `k > i` such that `s(k)` is the basepair partner of `s(i)` and `k - i` is odd (so `[i+1, k-1]` has even length):
```
dp(i)(j) = Σ_{k matchable} (dp(i+1)(k-1) * dp(k+1)(j)) mod M
```
Base cases: `j < i` ⇒ empty interval ⇒ `1`. Odd-length intervals ⇒ `0`.

Total work: `O(n³)` arithmetic ops, `O(n²)` memory. At `n = 300` that's `27 · 10⁶` ops and `90 000` cells — milliseconds. **Alternative considered:** top-down memoised recursion (rejected: bottom-up is simpler to reason about, avoids JVM stack-depth concerns at `n = 300`, and the cell-fill order is mechanical).

**2. Return type: `Int`, value mod `1 000 000`.**

Rosalind specifies the modulus. The mod result fits trivially in `Int`. Intermediate products (`dp[a] * dp[b]`) could overflow `Int` (each operand is `< 10⁶`, product up to `~10¹²`), so internally the multiply must use `Long` then mod-and-cast back. **Alternative considered:** `Long` throughout (rejected: leaks "this is a mod-1M number" into the type — `Int` matches the framework's per-spec convention of "use the smallest type the result fits in").

**3. Precompute `auCount` and `cgCount` in the smart constructor.**

Mirrors `PerfectMatchingProblem`'s pattern (spec 34). The single-pass count in the validator does double duty: it both enforces balance and exposes the counts on the constructed value (for any downstream caller that wants them).

**4. Empty input accepted, returns `1`.**

The empty bonding graph has exactly one perfect matching — the empty matching. Mathematically the base case `dp(i)(i-1) = 1` falls out naturally. Mirrors `PerfectMatching`'s acceptance of empty input.

**5. Validation order: length cap → AU balance → CG balance.**

Same cascade as `PerfectMatchingProblem`. First-failure-wins. Lower-cost check first.

**6. Place under `bio.{algorithms,domain}.nucleic`.**

Same family as `PerfectMatching`. Strongly related — same input shape, different counting question. Co-locating helps readers find both.

## Risks / Trade-offs

- **[Risk]** Off-by-one in the DP indexing. With `j < i` as the empty-interval sentinel and odd-length intervals returning `0` immediately, the cell-fill loop must skip odd lengths. → **Mitigation:** the DP loop iterates `length` in `0, 2, 4, ..., n` so odd cells stay at their initialised value of `0`. Tests cover `AUAU → 2` and `AUAUAU → 5` to lock in the recurrence.
- **[Risk]** Intermediate-product overflow. `dp(a) * dp(b)` where both are `< 10⁶` exceeds `Int.MaxValue` (`~2.1 · 10⁹`). → **Mitigation:** do the multiply as `Long`, take the mod, cast back to `Int`. Tested by `"AU" * 14 → 674440` which forces the DP through values that overflow `Int` if multiplied raw.
- **[Trade-off]** No early-exit on impossible intervals (e.g. an interval with unbalanced AU/CG counts). The DP would naturally return `0` for such intervals via the recurrence, but a pre-check could prune them. At `n ≤ 300` the work is microseconds either way, so we keep the algorithm simple.
- **[Trade-off]** Bottom-up loop with a mutable `Array[Array[Int]]` rather than a fold over an immutable structure. Same pattern as `FailureArray` (KMP) — the algorithm is intrinsically array-shaped and the public surface (`count(problem): Int`) is pure.
