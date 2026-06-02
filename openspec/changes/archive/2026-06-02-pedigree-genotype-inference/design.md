## Context

MEND ("Inferring Genotype from a Pedigree") gives a rooted binary pedigree as a Newick tree: leaves are known ancestor genotypes (`AA`, `Aa`, `aa`), internal nodes are unlabelled binary nodes (each the offspring of its two children/parents), and the root is the individual whose genotype distribution is sought.

The project already provides everything needed to consume this input:
- `bio.parsing.NewickParser.parse(s): Either[NewickParseError, NewickTree]` (used by NWCK/CTBL) and `bio.domain.graph.NewickTree(label: Option[String], children: Vector[NewickTree])`.
- `bio.domain.genetics.Genotype` ADT: `HomozygousDominant` (AA), `Heterozygous` (Aa), `HomozygousRecessive` (aa).

MEND is a Mendelian-genetics computation, so it lives in `bio.{domain,algorithms}.genetics` alongside mendelian-inheritance, expected-offspring, and independent-alleles.

## Goals / Non-Goals

**Goals:**
- A `Pedigree` ADT (`KnownAncestor(Genotype)` leaf, `Offspring(p1, p2)` internal) and a validated `PedigreeProblem` built from a `NewickTree`.
- Pure, total `InferGenotype.infer(problem): GenotypeProbabilities` computing the root's `(AA, Aa, aa)` distribution.
- Result type with `format: String` rendering three space-separated 3-decimal probabilities (sample `0.156 0.5 0.344`).
- Reuse the Newick parser and the `Genotype` ADT; functional recursion, no `var`/`while`/mutable collections.

**Non-Goals:**
- Parsing Newick (delegated to `NewickParser`); the problem is built from an already-parsed `NewickTree`.
- Branch lengths or labelled internal nodes (a pedigree's internal nodes are unlabelled binary crosses).
- A general allele model — exactly two alleles, `A` dominant and `a` recessive.

## Decisions

**1. Validated `Pedigree` ADT from the `NewickTree`.**
`PedigreeProblem.from(tree)` recursively converts the parsed `NewickTree` into a `Pedigree`: a node with no children becomes `KnownAncestor(g)` where the label maps `"AA" → HomozygousDominant`, `"Aa" → Heterozygous`, `"aa" → HomozygousRecessive` (any other label → `InvalidGenotype(label)`); a node with exactly two children becomes `Offspring(convert(c0), convert(c1))`; any other arity → `NotBinary(count)`. First failure wins. A root leaf (e.g. `Aa;`) is a valid degenerate pedigree (the individual's genotype is already known).

**2. Bottom-up distribution via allele-transmission probabilities.**
Each node carries a distribution `(pAA, pAa, paa)`. A leaf is a point mass (`HomozygousDominant → (1,0,0)`, etc.). For an `Offspring(p1, p2)`, the probability a parent transmits the `A` allele is `tA = pAA + pAa/2` (expected over its distribution). Because the two parents transmit independently, the offspring distribution is `P(AA) = tA₁·tA₂`, `P(aa) = (1−tA₁)(1−tA₂)`, `P(Aa) = 1 − P(AA) − P(aa)`. This is exactly the convolution of the 3×3 Mendelian cross table but in closed form, so no 6-case Punnett table is needed. Verified on the sample: root `= (0.15625, 0.5, 0.34375) → 0.156 0.5 0.344`.

**3. Pure recursion over the `Pedigree`.**
`infer` is a recursion returning `(Double, Double, Double)` for each node and wrapping the root's triple in `GenotypeProbabilities`. Pedigree trees are small, so plain (non-tail) recursion is safe. No `var`/`while`/mutable state.

**4. Result formatting.**
`GenotypeProbabilities.format = f"$homozygousDominant%.3f $heterozygous%.3f $homozygousRecessive%.3f"`. Rosalind grades numerically within 0.001, so `0.5 → "0.500"` is accepted (the sample shows `0.5`).

**5. Naming and placement.**
`Pedigree`, `PedigreeProblem`, `PedigreeProblemError`, and the `GenotypeProbabilities` result live in `bio.domain.genetics`; the algorithm `InferGenotype.infer` in `bio.algorithms.genetics`. The `Genotype` ADT is reused unchanged. The runner `MENDProb` parses the Newick string via `NewickParser.parse` then `PedigreeProblem.from`, mirroring `NWCKProb`.

## Risks / Trade-offs

- **[Empty `mend_data.txt`]** → the data file is currently empty; it will be populated with the canonical sample Newick so the runner produces output.
- **[Formatting `0.5` vs `0.500`]** → `%.3f` yields `0.500`; accepted by Rosalind's 0.001 tolerance (consistent with EVAL/RSTR formatting).
- **[Malformed pedigree]** → non-binary internal nodes and unknown genotype labels are rejected with `NotBinary` / `InvalidGenotype`; covered by scenarios.
- **[Degenerate root leaf]** → `Aa;` yields `(0,1,0)`; covered by a scenario.
- **[Deep pedigrees]** → recursion depth is bounded by tree height; Rosalind pedigrees are small, so no stack concern.
