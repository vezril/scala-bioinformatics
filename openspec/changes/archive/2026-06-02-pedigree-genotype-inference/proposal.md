## Why

Rosalind problem MEND ("Inferring Genotype from a Pedigree") takes a rooted binary pedigree tree (in Newick format, leaves labelled with known ancestor genotypes AA/Aa/aa) and computes the probability distribution over the root individual's genotype. It extends the project's Mendelian-genetics family (mendelian-inheritance, expected-offspring, independent-alleles) with a recursive bottom-up cross over a pedigree, reusing the existing Newick parser and `Genotype` ADT.

## What Changes

- Introduce a `Pedigree` domain ADT — `KnownAncestor(genotype)` (a leaf) and `Offspring(parent1, parent2)` (an internal node) — modelling a rooted binary pedigree.
- Introduce a validated `PedigreeProblem` domain type built from a parsed `NewickTree`, validating that internal nodes are binary and leaves carry a valid genotype label.
- Introduce a `PedigreeProblemError` ADT for the new invariants (invalid genotype label, non-binary node).
- Introduce a `GenotypeProbabilities` result type holding the root's `(AA, Aa, aa)` probabilities, with a Rosalind-style `format` (three space-separated 3-decimal values).
- Introduce an `InferGenotype` algorithm computing each node's genotype distribution bottom-up via allele-transmission probabilities (offspring `P(AA) = tA₁·tA₂`, `P(aa) = (1−tA₁)(1−tA₂)`, `P(Aa)` the remainder).
- Add a `MENDProb` runner reading the Newick pedigree from `mend_data.txt` and printing the root distribution through `IO`.
- Reuse existing infrastructure: `bio.parsing.NewickParser`, `bio.domain.graph.NewickTree`, `bio.domain.genetics.Genotype`.

## Capabilities

### New Capabilities
- `pedigree-genotype-inference`: Compute the genotype probability distribution `(AA, Aa, aa)` for the root individual of a Newick pedigree tree with known ancestor genotypes (Rosalind MEND).

### Modified Capabilities
<!-- None. MEND adds a new capability and reuses NewickParser / Genotype without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.genetics`): `Pedigree`, `PedigreeProblem`, `PedigreeProblemError`, `GenotypeProbabilities` (result).
- **New algorithm** (`bio.algorithms.genetics.InferGenotype`).
- **New runner** (`bio.problems.MENDProb`) reading `src/main/scala/resources/mend_data.txt`.
- **Reused, unchanged**: `bio.parsing.NewickParser`, `bio.domain.graph.NewickTree`, `bio.domain.genetics.Genotype`.
- **Tests**: new specs under `bio.domain.genetics` and `bio.algorithms.genetics`. No existing tests change.
