## Why

Rosalind problem SEXL ("Sex-Linked Inheritance") takes, for each of `n` recessive X-linked genes, the proportion of males exhibiting the trait, and returns the probability that a random female is a carrier. For an X-linked recessive gene a male's trait proportion equals the recessive allele frequency `q`, and under Hardy–Weinberg equilibrium a female is a carrier (heterozygous) with probability `2q(1−q)`. It adds a compact closed-form genetics computation, reusing the validated `Probability` type.

## What Changes

- Introduce a `SexLinkedProblem` domain type wrapping the male trait proportions as a `Vector[Probability]` (each already validated to `[0,1]`); it carries no extra invariant, so it is a plain wrapper.
- Introduce a `CarrierProbabilities` result type holding the per-gene female carrier probabilities (`Vector[Double]`), with a Rosalind-style `format` (space-separated, three decimals each).
- Introduce a `SexLinkedInheritance` algorithm computing `B[k] = 2·A[k]·(1 − A[k])` for each gene.
- Add a `SEXLProb` runner reading the proportions from `sexl_data.txt` and printing the carrier probabilities through `IO`.
- Reuse existing infrastructure: `bio.domain.stats.Probability`.

## Capabilities

### New Capabilities
- `sex-linked-inheritance`: Compute, for each recessive X-linked gene, the probability that a random female is a carrier from the male trait proportion, via `2q(1−q)` under Hardy–Weinberg equilibrium (Rosalind SEXL).

### Modified Capabilities
<!-- None. SEXL adds a new capability and reuses Probability without changing any existing requirement. -->

## Impact

- **New domain types** (`bio.domain.genetics`): `SexLinkedProblem`, `CarrierProbabilities` (result).
- **New algorithm** (`bio.algorithms.genetics.SexLinkedInheritance`).
- **New runner** (`bio.problems.SEXLProb`) reading `src/main/scala/resources/sexl_data.txt`.
- **Reused, unchanged**: `bio.domain.stats.Probability`.
- **Tests**: new specs under `bio.domain.genetics` and `bio.algorithms.genetics`. No existing tests change.
