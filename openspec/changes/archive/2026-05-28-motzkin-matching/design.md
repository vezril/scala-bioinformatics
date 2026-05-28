## Context

Rosalind problem 36 (MOTZ) asks: given an RNA string `s` (length `≤ 300`), how many **noncrossing matchings** (not necessarily perfect) of basepair edges (`A`-`U` or `C`-`G` only) does its bonding graph admit, modulo `1 000 000`? The defining feature versus CAT (spec 35) is that *some nodes may be unbonded*. The empty matching is one valid matching; matchings with one bond are valid; the full perfect matching (when it exists) is also valid.

The canonical Rosalind sample is `AUAU` → `7`:
- `{}` — empty matching
- `{(0,1)}`, `{(0,3)}`, `{(2,3)}`, `{(1,2)}` — four single-bond matchings
- `{(0,1), (2,3)}` — two sequential bonds
- `{(0,3), (1,2)}` — two nested bonds

The recurrence (Motzkin-flavoured) is `M(i, j) = M(i+1, j) + Σ_k M(i+1, k-1) · M(k+1, j)` where the new term `M(i+1, j)` corresponds to "position `i` is unbonded" and the sum is over every `k ∈ {i+1, ..., j}` such that `s(k)` is the basepair partner of `s(i)`. Compared to CAT's `dp(i, j) = Σ_k dp(i+1, k-1) · dp(k+1, j)` (perfect matchings, sum over basepair partners with `(k - i)` odd), MOTZ adds the unbonded-`i` term and drops the parity constraint.

The framework already hosts the sister `noncrossing-matching` (CAT) and `perfect-matching` (PMCH) under `bio.{algorithms,domain}.nucleic` — this spec slots in there.

## Goals / Non-Goals

**Goals:**
- Provide a validated `MotzkinMatchingProblem` ADT enforcing only the Rosalind length cap (`≤ 300`). No AU/CG balance check — partial matchings allow any input.
- Provide `bio.algorithms.nucleic.MotzkinMatching.count` returning the noncrossing-matching count modulo `1 000 000` as an `Int`.
- TDD coverage at both layers, including the canonical Rosalind sample, structural edge cases (empty, single char, all-A, single pair, mixed alphabet), and validation of the *broader* input contract (odd-length and unbalanced strings accepted, unlike CAT).

**Non-Goals:**
- Tracking *which* bonds are in each matching. We count, we don't enumerate.
- Pseudoknotted secondary structures. By construction "noncrossing" = "pseudoknot-free".
- Returning the un-modded count or using `BigInt`. Rosalind's spec is explicit: modulo `1 000 000` — fits in `Int`.
- The pure Motzkin numbers `M_n` (which count noncrossing matchings on the *complete* graph `K_n` where every pair of nodes can be bonded). Our RNA bonding graph restricts bonds to `A`-`U` and `C`-`G`, so the values we compute differ from the pure Motzkin sequence — they are the "RNA-constrained" Motzkin-flavoured counts. The capability name `motzkin-matching` references Rosalind's framing; the numbers themselves are not literal Motzkin numbers.

## Decisions

**1. Bottom-up `O(n²)`-cell interval DP, transitions `O(n)`.**

Fill `M(i)(j)` for every inclusive sub-interval `[i, j]` of `s`, in order of increasing length. Base case: `M(i)(i - 1) = 1` (empty interval) — handled via a `get(i, j): Int = if (j < i) 1 else M(i)(j)` helper. For each cell with `i ≤ j`, sum the unbonded-`i` term `get(i+1, j)` plus, for every `k ∈ {i+1, ..., j}` such that `s(k)` is the basepair partner of `s(i)`, `get(i+1, k-1) · get(k+1, j)`. All multiplications use `Long` then mod-and-cast to `Int`.

Total work: `O(n³)` arithmetic ops, `O(n²)` memory. At `n = 300` that's `27 · 10⁶` ops — milliseconds. **Alternative considered:** top-down memoised recursion (rejected: same as for CAT — bottom-up is simpler and stack-safe).

**2. The recurrence formula handles the single-character base case automatically.**

For interval `[i, i]` (length 1), `get(i+1, i) = 1` (empty interval) and the sum over `k > i = j` is empty. So `M(i)(i) = 1 + 0 = 1`. No special-case code needed. Likewise the empty-string short-circuit returns `1` directly because no DP table is needed.

**3. Validation: length cap only — no balance check.**

This is the key departure from `PerfectMatchingProblem` and `NoncrossingMatchingProblem`. Partial noncrossing matchings always exist (at minimum, the empty matching), so unbalanced AU/CG and odd-length inputs are *valid*. The smart constructor enforces only `length ≤ 300`; mirrors the simpler validation surface to the algorithm's broader domain.

**4. Drop the `k - i` parity constraint.**

CAT requires `(k - i)` to be odd so that both sub-intervals `[i+1, k-1]` and `[k+1, j]` have even length (needed for perfect sub-matchings). MOTZ allows partial sub-matchings on intervals of *any* length, so `k` ranges over every `i+1, i+2, ..., j` with no parity filter. Just the basepair-partner check.

**5. Return type: `Int`, value mod `1 000 000`.**

Same as CAT. Intermediate products use `Long` to avoid `Int` overflow.

**6. Place under `bio.{algorithms,domain}.nucleic`.**

Same family as PMCH and CAT. Co-located so readers find the trio of RNA-bonding-graph counting algorithms together.

## Risks / Trade-offs

- **[Risk]** Mis-handling of the unbonded-`i` term. → **Mitigation:** the canonical Rosalind sample (`AUAU → 7`) and the simple cases (`AU → 2`, `AAAU → 4`) directly exercise the unbonded-`i` term: in `AU` the result is `1` (empty matching) plus `1` (the single A-U bond) = `2`; in `AUAU` the `M(1, 3)` sub-problem contributes via the unbonded-`s(1)` term.
- **[Risk]** Off-by-one in the absence of the parity constraint. The CAT loop steps `k` by `2`; MOTZ steps by `1`. A copy-paste from CAT could leave the `+= 2` in place. → **Mitigation:** the `CGCG → 7` test would catch this (it requires the algorithm to sum over both `k = j` and `k = j - 2` — different parities — to reach 7).
- **[Trade-off]** Same `O(n²)` table shape as CAT but with one extra term per cell and looser iteration. Code is essentially "CAT plus one term" — duplicating the algorithm rather than factoring keeps each Scaladoc focused on its problem's specific recurrence. A future refactor could extract a shared "interval DP over RNA" skeleton if more variants land.
- **[Trade-off]** Same `(Long * Long) % Mod` cast pattern. Mirrors CAT — readers who learn one know the other.
