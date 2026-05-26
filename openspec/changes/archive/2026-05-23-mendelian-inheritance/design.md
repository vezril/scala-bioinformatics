## Context

The first four changes built a sequence-processing framework: validated DNA/RNA strings, sealed nucleotide ADTs, pure algorithms dispatching via `fromChar` / `toChar`. Spec 5 breaks that pattern. It is a closed-form probability calculation over a typed population. There are no sequences, no characters, no alphabets — just counts of three genotype categories and a piece of high-school-level Mendelian probability.

Current state of relevant code: nothing exists. This change introduces an entirely new domain.

## The math

For a population of `k` homozygous-dominant, `m` heterozygous, and `n` homozygous-recessive individuals (total `N = k + m + n`), the probability that a uniformly random pair of distinct organisms produces offspring with a recessive phenotype (aa) is:

```
P(aa) = [ m(m-1)/4 + m*n + n(n-1) ] / (N * (N-1))
```

Derivation: enumerate ordered parent pairs `(p1, p2)` of distinct individuals. For each typed pair, multiply the pair selection probability by the conditional probability that the offspring is `aa`:

| Pair (ordered)   | P(select pair)         | P(offspring aa) |
|------------------|------------------------|-----------------|
| (AA, *) or (*, AA) | any                  | 0               |
| (Aa, Aa)         | m(m-1) / N(N-1)        | 1/4             |
| (Aa, aa) or (aa, Aa) | 2*m*n / N(N-1)     | 1/2             |
| (aa, aa)         | n(n-1) / N(N-1)        | 1               |

Sum yields the formula above. The probability of a dominant phenotype is `1 - P(aa)`.

Verification with Rosalind sample `(k=2, m=2, n=2)`, `N=6`:
- Numerator: `2*1/4 + 2*2 + 2*1 = 0.5 + 4 + 2 = 6.5`
- Denominator: `6 * 5 = 30`
- `P(aa) = 6.5/30 ≈ 0.21667`
- `P(dominant) ≈ 0.78333` ✓

## Goals / Non-Goals

**Goals:**
- Model the domain with ADTs: `Genotype` (sealed trait), `Population` (case class with smart constructor), `Probability` (value class with smart constructor)
- Implement `MendelianInheritance.probabilityOfDominantPhenotype` as a pure, total function using the closed-form formula
- Validate inputs at construction (non-negative counts, total ≥ 2, probability in [0, 1])

**Non-Goals:**
- Monte Carlo simulation — Rosalind suggests it as a sanity check, but we use analytically-derivable test cases instead
- Multiple-allele or multi-locus inheritance — single Mendelian locus only
- Lazy evaluation, distribution types beyond `Probability` — keep the surface minimal
- Sharing `Probability` with future statistical work — colocate here for now; extract if reused

## Decisions

### One capability, four types

**Decision**: Bundle `Genotype`, `Population`, `Probability`, and `MendelianInheritance` under a single capability spec (`mendelian-inheritance`).

**Rationale**: They are tightly coupled — `Probability` is the algorithm's output type, `Population` is the input type, `Genotype` is referenced in the docs and tests. Splitting into four capabilities would be premature granularity. The framework precedent in `rna-sequence` already bundles `RnaString` + `RnaNucleotideCounts` for the same reason.

**Reversibility**: If `Probability` or `Genotype` end up reused by future capabilities (sequence alignment scores, multi-allele genetics), they can be extracted into their own capability. The rename/relocation is small.

### `Genotype` as a sealed ADT, not strings or enums

**Decision**: `sealed trait Genotype` with three case objects: `HomozygousDominant`, `Heterozygous`, `HomozygousRecessive`.

**Rationale**: Same logic as `DnaNucleotide` — exhaustive pattern matching enforced by the compiler, no string typos, self-documenting. Future operations (e.g., a `Punnett.cross(g1, g2)` function) get compile-time exhaustiveness.

**Alternative considered**: A short-form ADT (`AA`, `Aa`, `aa`). Rejected — symbolic names are ambiguous in cross-references. The descriptive names match how the Rosalind problem describes the genotypes.

### `Population` as a case class with smart constructor

**Decision**: `final case class Population private (homozygousDominant: Int, heterozygous: Int, homozygousRecessive: Int)` with `from(k: Int, m: Int, n: Int): Either[PopulationError, Population]`.

**Rationale**: Counts have invariants (non-negative; total ≥ 2 for the algorithm to be defined). The smart constructor centralizes validation. `total: Int` is exposed as a derived value.

**Validation rules:**
- Each count ≥ 0 → otherwise `PopulationError.NegativeCount`
- `total >= 2` → otherwise `PopulationError.InsufficientPopulation` (needed because `total * (total - 1)` would be 0 or negative)

Rosalind's "three positive integers" guarantee is **stronger** than this — but the smart constructor accepts zero counts to keep the function defined for educational/test purposes (e.g., `Population(2, 0, 0)` = all-AA population).

### `Probability` as a value class with `[0, 1]` invariant

**Decision**: `final class Probability private (val value: Double) extends AnyVal` with `from(d: Double): Either[ProbabilityError, Probability]` (rejects NaN, infinity, and out-of-range values) and `private[bio] unsafeFrom`.

**Rationale**: A probability is a domain concept with a meaningful invariant. Making it a value class adds no runtime overhead. The smart constructor catches programmer errors (e.g., accidentally returning `0.78333 * 100`). `unsafeFrom` is for the algorithm itself, where the math guarantees the result is in range — re-validating would be wasteful and obscure the invariant.

### Per-type errors, not a unified `DomainError`

**Decision**: `PopulationError` and `ProbabilityError` are separate sealed ADTs, distinct from `SequenceError`.

**Rationale**: The error cases are semantically different. `SequenceError` is about parsing strings; population/probability errors are about numeric validity. Forcing them into one ADT would either over-generalize (`InvalidValue(reason: String)` — loses precision) or add irrelevant cases to `SequenceError`. The cost of two extra small ADTs is negligible compared to the clarity gain.

### Algorithm uses `Double` arithmetic with `unsafeFrom` at the boundary

**Decision**: The algorithm computes `1 - pAa` in `Double` and wraps with `Probability.unsafeFrom`.

**Rationale**: `Population.from` guarantees `total >= 2`, so the denominator `N*(N-1)` is at least 2 and never zero. The numerator is non-negative (sum of non-negative terms). `pAa` is therefore in `[0, 1]`, and `1 - pAa` is also in `[0, 1]`. Wrapping via `unsafeFrom` is safe and documents the invariant.

**Edge precision**: For the boundary case where `pAa = 1.0` (e.g., `n=N`, all-recessive), the algorithm returns `Probability(0.0)`. For `pAa = 0.0` (e.g., `k=N`, all-dominant), the algorithm returns `Probability(1.0)`. No floating-point underflow concerns for typical Rosalind input sizes.

## Risks / Trade-offs

- [Floating-point comparison in tests] Equality on `Double` is fragile. → Tests use `should be within tolerance` (ScalaTest's `Tolerance` matcher with `+- 1e-5`) so `0.78333...` matches the Rosalind expected output.
- [`Population.from(0, 0, 2)` is accepted, but Rosalind says "positive integers"] The smart constructor is more permissive than the problem statement. → Acceptable: the function generalizes naturally. Callers consuming Rosalind input should enforce their own ≥1 contract if needed.
- [`Probability` may collide with future statistics work] If a generic probability type is introduced later, this one becomes ambiguous. → Mitigate by keeping the smart constructor strict (`[0, 1]`, no NaN/infinity) so the eventual extraction is mechanical.
- [No simulation cross-check in production code] We rely on analytically-derivable test cases (all-AA = 1.0, all-aa = 0.0, all-Aa = 0.75, etc.) instead of Monte Carlo. → Sufficient for correctness; simulation can be added in a future change if needed.
