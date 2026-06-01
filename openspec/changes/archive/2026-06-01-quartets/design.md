## Context

Rosalind QRT input is a *partial character table*: a first line of `n`
space-separated taxon names, followed by character rows, each a length-`n`
string over `{0, 1, x}`. Each character is a **partial split** `S₁ | S₀` where
`S₁` is the set of taxa marked `1`, `S₀` the set marked `0`, and `x`-marked taxa
are excluded (no conclusive evidence). A **quartet** is a partial split with
exactly two taxa per side; a quartet `{a,b} | {c,d}` is *inferred* from `S₁ | S₀`
iff `{a,b} ⊆ S₁` and `{c,d} ⊆ S₀` (orientation is irrelevant — `A|B = B|A`).

So each character contributes the cross-product of the 2-combinations of `S₁`
with the 2-combinations of `S₀`. The output is the union of these over all
characters. Worked sample (taxa cat dog elephant ostrich mouse rabbit robot):

- `01xxx00` → `S₁={dog}`, `S₀={cat,rabbit,robot}` → no quartet (`|S₁| < 2`).
- `x11xx00` → `S₁={dog,elephant}`, `S₀={rabbit,robot}` → `{dog,elephant}|{rabbit,robot}`.
- `111x00x` → `S₁={cat,dog,elephant}`, `S₀={mouse,rabbit}` → 3 quartets.

Total: 4 distinct quartets, matching the published output (which lists them in
arbitrary order and orientation).

## Goals / Non-Goals

**Goals:**
- A validated `QuartetsProblem` bundling `taxa: Vector[String]` and
  `characters: Vector[String]` with structural validation.
- A canonicalised `Quartet` ADT so duplicate quartets (inferable from more than
  one character) collapse to one.
- An algorithm `Quartets.compute(problem): Vector[Quartet]` enumerating and
  deduplicating all inferred quartets, reproducing the sample.

**Non-Goals:**
- Reconstructing trees or testing quartet compatibility — only enumeration.
- Matching the sample's exact line order / side orientation — Rosalind accepts
  any order; we emit a deterministic canonical order.

## Decisions

### Placement: `bio.domain.graph` / `bio.algorithms.graph`
Quartets are a phylogenetic-tree concept; the `graph` subdomain already houses
the character-table (`CharacterTableProblem`), Newick-tree, and phylogenetic
capabilities. (The DNA-derived `GeneticCharacterTable` lives in `analysis`, but
QRT consumes a ready-made symbolic table, so it belongs with the graph-side
phylogenetics.)

### `QuartetsProblem` — structural validation, first-failure-wins
`from(taxa: Vector[String], characters: Vector[String]): Either[QuartetsProblemError, QuartetsProblem]`
enforces, in order:
1. `taxa` non-empty → else `EmptyTaxa`;
2. `taxa` distinct → else `DuplicateTaxon(name)` (first repeat);
3. `characters` non-empty → else `EmptyTable`;
4. every row length `== taxa.size` → else `InconsistentWidth(rowIndex, expected, actual)`;
5. every row character `∈ {'0','1','x'}` → else `InvalidSymbol(rowIndex, colIndex, symbol)`.
`sealed abstract case class` so `apply`/`copy` cannot bypass validation.

*No numeric cap.* The Rosalind QRT prompt specifies no bound on `n` or the
character count, so the constructor imposes only the structural contract
required for correctness — adding an arbitrary cap risks rejecting valid grader
input. (Contrast GeneticCharacterTable, whose caps are quoted from its prompt.)

### `Quartet` — canonicalised ADT for set-equality dedup
A quartet `{a,b} | {c,d}` is invariant under swapping the two taxa within a side
and swapping the two sides. To make equal quartets compare equal (so `.distinct`
deduplicates), `Quartet` stores a canonical form:
- each side's two taxa sorted lexicographically into a `(String, String)`;
- the two sides ordered so the lexicographically-smaller pair is `pairA`.

Modelled as `sealed abstract case class Quartet(pairA: (String, String), pairB: (String, String))`
with a total smart constructor `Quartet.of(w, x, y, z): Quartet` performing the
canonicalisation (the four taxa are always distinct — two from each disjoint
side). A `render: String` yields `"{a, b} {c, d}"` for output. Using a smart
constructor (rather than a plain case class) guarantees the canonical invariant
holds for every instance, so case-class equality is sound for dedup.

### Algorithm — combinations cross-product, dedup, deterministic order
```
compute(problem) =
  problem.characters.flatMap { row =>
    val ones  = taxa where row == '1'
    val zeros = taxa where row == '0'
    for { a <- ones.combinations(2); b <- zeros.combinations(2) }
      yield Quartet.of(a(0), a(1), b(0), b(1))
  }.distinct.sortBy(q => (q.pairA._1, q.pairA._2, q.pairB._1, q.pairB._2))
```
`combinations(2)` is empty when a side has `< 2` taxa, so sparse/`x`-heavy rows
contribute nothing. `.distinct` collapses quartets inferable from several
characters. The final `sortBy` gives a stable, reproducible output order.

**Complexity:** per character, `O(|S₁|² · |S₀|²)` quartets — inherent in the
problem (the output itself can be that large); enumeration is output-optimal.

## Risks / Trade-offs

- **[Output blow-up on dense tables]** A near-even split of many taxa yields
  `~C(n/2, 2)²` quartets per row. → This is intrinsic to "return all quartets";
  no mitigation beyond the output-optimal enumeration. The absence of a taxa cap
  is deliberate (see above) and matches the prompt.
- **[Canonicalisation correctness]** Dedup relies on the canonical form being a
  true normal form. → Sorting within each pair and ordering the two pairs is a
  total order on quartets; covered by a test asserting a quartet inferred from
  two different characters appears once, and by an orientation-invariance test.
