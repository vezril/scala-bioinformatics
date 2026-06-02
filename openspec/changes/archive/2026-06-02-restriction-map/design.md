## Context

The `bio.{domain,algorithms}.combinatorics` packages already host set/combination reconstruction problems (combination sums, k-mer composition, set operations) following the framework conventions: `sealed abstract case class` problems with smart constructors, plain result types with `format`, pure-FP algorithms. PDPL ("Creating a Restriction Map") fits here — it reconstructs a set of integer positions from the multiset of its pairwise differences (the Turnpike / Partial Digest problem). It does not touch any sequence type, so it depends only on the standard library.

## Goals / Non-Goals

**Goals:**
- Validated `RestrictionMapProblem` wrapping the distance multiset `L: Vector[Int]`, via a smart constructor returning `Either`, using `sealed abstract case class` to block `apply`/`copy` leakage.
- Pure, total `RestrictionMapConstruction.solve(problem): Option[RestrictionMap]` — Turnpike backtracking, returning `None` when `L` is unrealisable.
- Result type with `format: String` rendering the positions space-separated, ascending (Rosalind sample: `0 2 4 7 10`).
- Functional implementation (recursion + immutable multiset), no `var`/`while`/mutable collections.

**Non-Goals:**
- Returning *all* solutions or a canonical one — Rosalind accepts any `X` with `ΔX = L`. `X` and its reflection `width − X` are both valid; the algorithm returns whichever its deterministic search finds first.
- Validating realisability up front — feasibility is decided by the search (`None` if it fails).
- Any DNA/sequence modelling — `L` and `X` are integer multisets/sets.

## Decisions

**1. Turnpike backtracking (Skiena's PLACE).**
The largest distance `width = max(L)` is the span between the outermost points, so `X` starts as `{0, width}` with one `width` removed from `L`. Recursively, let `y = max(remaining L)`; the next point is either `y` or `width − y`. For a candidate `p`, compute the distances from `p` to every already-placed point; if all are present in the remaining multiset, remove them and recurse, else backtrack. When the multiset is exhausted, `X` is a solution. Verified on the sample (`2 2 3 3 4 5 6 7 8 10` → `{0,3,6,8,10}`, the reflection of Rosalind's `0 2 4 7 10`; both have `ΔX = L`).

**2. Multiset as an immutable `Map[Int, Int]` (value → count).**
`max` is `keys.max`; removing a distance decrements its count (dropping the key at zero), so `isEmpty` means fully consumed. Removing a candidate's distance list folds `removeAll` over the list, decrementing one occurrence at a time and failing (`None`) the moment a required distance is unavailable — this correctly respects multiplicities (e.g. two equal distances need count ≥ 2). All immutable; backtracking is plain `Option` `orElse`.

**3. Pure-FP recursion (no imperative DP).**
PDPL is not an alignment-family algorithm, so the framework's pure-FP rule applies. The search is expressed as mutually-recursive total functions (`place`, `tryPoint`, `removeAll`) threading immutable `Map`/`Set` values; backtracking uses `Option.orElse`. No `var`, `while`, or mutable collection.

**4. `Option` result for unrealisable inputs.**
Although Rosalind guarantees a solution, a valid-shaped multiset need not be realisable (e.g. `{1,1,1}` — three mutually unit-distant points are impossible on a line). `solve` returns `Option[RestrictionMap]` (`None` when no placement succeeds); the runner prints a clear message rather than throwing.

**5. Validation rules and order (first-failure-wins).**
`RestrictionMapProblem.from(distances)` checks, in order: the size must be triangular — `∃ n ≥ 1 : n(n-1)/2 = |L|`, else `InvalidSize(size)`; then each element must be positive (index order), else `NonPositiveDistance(index, value)`. The empty multiset (`size 0`, `n = 1`) is accepted and yields the trivial map `{0}`. The triangular check derives `n` from `size` via `n = round((1 + √(1 + 8·size)) / 2)` and confirms `n(n-1)/2 = size`.

**6. Naming and placement.**
`RestrictionMapProblem`, `RestrictionMapProblemError`, and the `RestrictionMap` result live in `bio.domain.combinatorics`; the algorithm `RestrictionMapConstruction.solve` in `bio.algorithms.combinatorics` (mirroring the `*Construction` naming used for `TrieConstruction`/`DeBruijnGraphConstruction`). Result and algorithm names are distinct, so no `=> Result` alias is needed. The capability is named `restriction-map` (distinct from the DNA-palindrome `restriction-sites` capability).

## Risks / Trade-offs

- **[Backtracking is worst-case exponential]** → For Rosalind PDPL sizes the Turnpike search is fast in practice (heavy pruning: every candidate must match existing distances). Acceptable; no input cap beyond the triangular-size invariant.
- **[Output differs from the sample]** → `{0,3,6,8,10}` vs Rosalind's `0 2 4 7 10` (reflections). Both satisfy `ΔX = L`; tests assert the defining property (`ΔX(result) == sorted L`) rather than byte-equality, plus a deterministic regression check.
- **[Unrealisable valid-size input]** → returns `None`; covered by an explicit scenario (`{1,1,1}`).
- **[Empty / single-distance inputs]** → `size 0 → {0}`; `size 1 (e.g. {5}) → {0,5}`; both covered as boundary scenarios.
- **[Large integer distances]** → positions/distances are `Int`; Rosalind PDPL values fit comfortably within `Int` range.
