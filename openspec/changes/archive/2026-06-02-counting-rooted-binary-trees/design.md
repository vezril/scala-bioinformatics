## Context

The `bio.{domain,algorithms}.combinatorics` packages already host CUNR ("Counting Unrooted Binary Trees"): a validated leaf-count wrapper (`LeafCount`, `n ∈ [1,1000]`) and `UnrootedBinaryTrees.count` computing `(2n−5)!! mod 1,000,000` via a per-step-modulo product over odd factors. ROOT ("Counting Rooted Binary Trees") is the rooted sibling — `B(n) = (2n−3)!! mod 1,000,000` — and follows CUNR's structure exactly, differing only in the upper odd factor (`2n−3` vs `2n−5`).

Following the established precedent that each counting problem owns its validated `n` wrapper (INOD's `UnrootedBinaryTreeLeafCount`, CUNR's `LeafCount`), ROOT gets its own `RootedTreeLeafCount` rather than sharing CUNR's (whose scaladoc is CUNR-specific).

## Goals / Non-Goals

**Goals:**
- Validated `RootedTreeLeafCount` wrapping `n` (positive, ≤ 1000) via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage (mirroring `LeafCount`).
- Pure, total `RootedBinaryTrees.count(leaves): Int` computing `(2n−3)!! mod 1,000,000`.
- Per-step modular `Int` arithmetic (no overflow, no `BigInt`).

**Non-Goals:**
- A result type with `format` — the answer is a single integer; the algorithm returns a bare `Int`, mirroring CUNR's `UnrootedBinaryTrees.count`.
- Enumerating the trees (that is EUBT's territory); ROOT only counts.
- Sharing CUNR's `LeafCount` type (kept separate per the per-problem-wrapper precedent).

## Decisions

**1. Closed-form double factorial `(2n−3)!!`.**
A rooted binary tree on `n` labeled leaves has `n−1` internal nodes and `2n−2` edges. Building incrementally, the `k`-th leaf can attach to any of the `2(k−1)−1 = 2k−3` edges of a rooted tree on `k−1` leaves, so the count multiplies by the successive odd numbers `3, 5, …, (2n−3)`, giving `B(n) = (2n−3)!!` (with `B(1) = B(2) = 1`). Verified: `B(4) = 5!! = 15` (the sample), `B(3) = 3!! = 3`, `B(1) = B(2) = 1`.

**2. Per-step modulo with `Int` (mirrors CUNR / `Subsets.count`).**
`count = (3 to (2n−3) by 2).foldLeft(1) { (acc, k) => (acc * k) % 1_000_000 }`. For `n ≤ 2` the range `3 to (2n−3) by 2` is empty (upper bound `< 3`), so the fold returns `1` — the correct count, with the leading `1` of the double factorial folded in implicitly. After each step `acc ∈ [0, 999_999]`; the largest factor is `2·1000−3 = 1997`, so the worst intermediate is `999_999 × 1997 = 1_996_998_003 < Int.MaxValue` — no overflow, no `BigInt` needed. O(n) multiplies.

**3. Validation rules and order (first-failure-wins).**
`RootedTreeLeafCount.from(n)` checks `n >= 1` (Rosalind "positive integer"), else `NonPositive(n)`; then `n <= 1000`, else `ExceedsMaximum(n, 1000)`. Mirrors `LeafCount.from`.

**4. Naming and placement.**
`RootedTreeLeafCount` and `RootedTreeLeafCountError` live in `bio.domain.combinatorics`; the algorithm `RootedBinaryTrees.count` in `bio.algorithms.combinatorics` (alongside CUNR's `UnrootedBinaryTrees`). The algorithm returns a bare `Int`, consistent with its CUNR sibling.

## Risks / Trade-offs

- **[Duplication with CUNR]** → `RootedTreeLeafCount` closely mirrors `LeafCount`, but the per-problem-wrapper precedent (INOD, CUNR) and CUNR's CUNR-specific scaladoc make a dedicated type the cleaner, consistent choice.
- **[`Int` overflow]** → ruled out by per-step modulo (worst intermediate `< 2×10⁹`); documented.
- **[Boundary `n = 1, 2`]** → empty odd-factor range yields `1`; covered by scenarios.
- **[Modulo correctness for large `n`]** → an explicit scenario checks `n = 10 → 459425` (`17!! = 34_459_425`, `mod 1_000_000 = 459_425`).
