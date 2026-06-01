## Context

Rosalind CUNR asks: given a positive integer `n ≤ 1000`, return `b(n)`
modulo 1,000,000, where `b(n)` is the number of distinct unrooted binary trees
on `n` labeled leaves. The combinatorial identity is the odd double factorial:

```
b(n) = (2n − 5)!! = 1 · 3 · 5 · ... · (2n − 5)   for n ≥ 3
b(n) = 1                                          for n ≤ 2  (empty product)
```

(Each new leaf, added one at a time to a tree on `k` leaves, can attach to any of
the `2k − 3` edges, yielding the product `3 · 5 · ... · (2n − 5)` of new-edge
choices, i.e. `(2n − 5)!!`.) Sample: `n = 5 → 5!! = 5·3·1 = 15`.

The existing `combinatorics` subdomain already contains the structurally
identical SSET capability (`SubsetUniverseSize` validated wrapper +
`Subsets.count` returning `2^n mod 1,000,000` by per-step modulo). CUNR reuses
that exact shape, changing only the per-step factor.

## Goals / Non-Goals

**Goals:**
- A validated value type `LeafCount` (`1 ≤ n ≤ 1000`) with a smart constructor
  and a sealed error ADT, mirroring `SubsetUniverseSize`.
- An algorithm `UnrootedBinaryTrees.count(leaves): Int` returning
  `(2n − 5)!! mod 1,000,000`, total over every valid `LeafCount`.
- A runner `CUNRProb` wired into `Main`, reproducing `5 → 15`.

**Non-Goals:**
- Enumerating the trees themselves — only the count is required.
- `BigInt` arithmetic — the per-step modulo keeps everything in `Int` range.

## Decisions

### Naming: `LeafCount` / `LeafCountError` / `UnrootedBinaryTrees`
Follow the SSET precedent (`SubsetUniverseSize` + `SubsetUniverseSizeError` +
`Subsets`). The input is the number of labeled leaves, so `LeafCount` reads
naturally. The algorithm object is `UnrootedBinaryTrees` with
`count(leaves: LeafCount): Int`.

*Alternative considered:* reusing `SubsetUniverseSize`. Rejected — the semantic
domain differs (leaf count vs. universe size); a distinct type keeps the
ubiquitous language honest, consistent with how the framework gives each problem
its own validated wrapper.

### Validation: `1 ≤ n ≤ 1000`, lower bound first
`LeafCount.from(value): Either[LeafCountError, LeafCount]` enforces
`value >= 1` (else `NonPositive(value)`) then `value <= 1000` (else
`ExceedsMaximum(value, 1000)`), first-failure-wins. `sealed abstract case class`
so synthesized `apply`/`copy` cannot bypass validation.

*Note on small `n`:* the validator admits `n = 1` and `n = 2` (Rosalind says
"positive integer"); the algorithm returns `1` for both via the empty product,
which is the correct count (a single tree).

### Algorithm: per-step-modulo product over odd factors
```
private val Modulus = 1_000_000
def count(leaves: LeafCount): Int =
  (3 to (2 * leaves.value - 5) by 2)
    .foldLeft(1) { (acc, k) => (acc * k) % Modulus }
```
The factor range `3, 5, ..., (2n − 5)` is empty for `n ≤ 3` (Scala's
`3 to upper by 2` with `upper < 3` is empty), yielding the correct `1` for
`n ∈ {1, 2, 3}`. Starting the product at `1` folds in the `1` of the double
factorial implicitly.

**`Int`-safety:** after each step `acc ∈ [0, 999_999]`. The largest factor is
`2·1000 − 5 = 1995`, so the worst intermediate is
`999_999 × 1995 = 1_994_998_005 < Int.MaxValue (2_147_483_647)`. No overflow —
the same per-step-modulo discipline SSET relies on, with headroom verified for
the larger factor.

**Complexity:** `O(n)` integer multiplies (≤ ~500 odd factors at the cap) —
trivially fast.

## Risks / Trade-offs

- **[`Int` overflow at the cap]** The factor grows to 1995 (vs. SSET's constant
  2). → Mitigation: verified `999_999 × 1995 < Int.MaxValue`; documented in the
  algorithm Scaladoc. A test exercises `n = 1000` to guard the boundary.
- **[Off-by-one in the double-factorial range]** Small `n` (1, 2, 3) must all
  yield `1`. → Mitigation: explicit edge-case scenarios for `n = 1`, `n = 2`,
  `n = 3`, plus the canonical `n = 5 → 15` and `n = 4 → 3`.
