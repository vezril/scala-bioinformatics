## Context

The genetics subdomain already houses two Mendelian-style algorithms:
- `MendelianInheritance.probabilityOfDominantPhenotype(pop: Population): Probability` (spec 5) — closed-form probability that a random offspring shows the dominant phenotype
- `ExpectedOffspring.dominantPhenotype(pop: CouplePopulation): Double` (spec 11) — expected count of dominant-phenotype offspring across a population of couples

Spec 14 asks a *different* question with a *different* shape of answer: the binomial tail probability that at least `N` organisms in generation `k` of a specific family tree carry the genotype Aa Bb. The combinatorial structure of the family tree is what makes it interesting; the math reduces cleanly to a binomial distribution.

## The math

**Setup**: Tom (Aa Bb) in gen 0. Each organism in gen `i` has two children in gen `i+1`, with the other parent always being Aa Bb. Gen `k` therefore contains `2^k` organisms.

**Key independence claim**: For any organism with diploid genotype `Xx Yy` (where `Xx` is any of AA/Aa/aa and `Yy` is any of BB/Bb/bb) crossed with `Aa Bb`, the probability the offspring is `Aa Bb` is exactly `1/4`.

Proof sketch (by enumeration over the 9 parent genotypes; the math is symmetric in A and B):
- For factor A: `P(Aa offspring | parent Xx × Aa)` equals `1/2` for every `Xx ∈ {AA, Aa, aa}`
  - AA × Aa → 1/2 AA, 1/2 Aa → P(Aa) = 1/2
  - Aa × Aa → 1/4 AA, 1/2 Aa, 1/4 aa → P(Aa) = 1/2
  - aa × Aa → 1/2 Aa, 1/2 aa → P(Aa) = 1/2
- By Mendel's second law, factor A and factor B are independent
- `P(Aa Bb offspring) = P(Aa) × P(Bb) = 1/2 × 1/2 = 1/4`

**Consequence**: Every organism in gen `k` (for `k >= 1`) is independently Aa Bb with probability `p = 1/4`. Therefore `X = #{Aa Bb organisms in gen k} ~ Binomial(2^k, 1/4)`, and

```
P(X >= N) = sum_{j=N}^{2^k} C(2^k, j) * (1/4)^j * (3/4)^(2^k - j)
```

### Sample trace

`(k=2, N=1)`:
- `total = 2^2 = 4`, `p = 1/4`, `q = 3/4`
- `P(X = 0) = q^4 = 81/256 ≈ 0.3164`
- `P(X >= 1) = 1 - 81/256 = 175/256 ≈ 0.6836`
- Rosalind expects `0.684` — match within `±0.001`. ✓

### PMF recurrence (avoids computing factorials)

For a Binomial(n, p) distribution:
```
P(X = j+1) / P(X = j) = C(n, j+1) / C(n, j) × p/q = (n - j) / (j + 1) × p/q
```

So starting from `P(X = 0) = q^n`, each subsequent term is the previous times `(n - j) / (j + 1) × p/q`. This is what the algorithm uses — an incremental scan over `j = 0..n-1` accumulating Doubles.

## Goals / Non-Goals

**Goals:**
- Provide `IndependentAlleles.probability(problem: IndependentAllelesProblem): Probability` computing the binomial tail probability `P(X >= atLeast)` for `X ~ Binomial(2^generations, 1/4)`
- Match the Rosalind sample `(k=2, N=1)` → `≈ 0.684` within `±0.001`
- Model the parameter bundle as a validated `IndependentAllelesProblem` with smart-constructor validation
- Return the framework's existing `Probability` type (validated `[0, 1]` Double) — the result is mathematically guaranteed in range
- Be total once the input is a valid `IndependentAllelesProblem`

**Non-Goals:**
- A `BigInt`-backed exact-rational probability. The Rosalind tolerance is `0.001`; Double precision is sufficient for `2^k ≤ 128`.
- Closed-form / log-space computation for very large `k`. The PMF recurrence is O(2^k) and trivially fast for Rosalind's bounds. For `k > ~30` the algorithm becomes impractical (memory / time), but those inputs are out of scope.
- A separate `Generation` or `OrganismCount` newtype. The framework precedent uses bare `Int` for generation indices and counts (cf. `RabbitProblem.months`, `MortalRabbitProblem.lifespan`).
- Modeling the family tree explicitly. The mathematical reduction makes the tree implicit; explicit tree simulation would be O(2^k) memory for the tree structure alone — same big-O but with massive constants and no information gain.
- Generalizing to other p-values or non-Aa-Bb mates. The 1/4 probability is hard-coded; if a future spec needs configurability, refactor then.

## Decisions

### Parameter type: `IndependentAllelesProblem(generations: Int, atLeast: Int)` with smart constructor

**Decision**: A `sealed abstract case class` bundling the two parameters. Smart-constructor validation enforces `generations >= 1`, `atLeast >= 1`, `atLeast <= 2^generations`. Exposes `populationSize: Long = 1L << generations` for callers.

**Rationale**:
- Matches the framework's pattern (`RabbitProblem`, `MortalRabbitProblem`, `CouplePopulation`) — multi-field inputs get a validated bundle
- The cross-input constraint `atLeast <= 2^generations` is naturally a smart-constructor concern; can't be expressed in either field alone
- The `populationSize` accessor saves callers from computing `1L << generations` themselves

**Alternative considered**: Two raw `Int` parameters on the algorithm method (`probability(generations: Int, atLeast: Int)`). Rejected — the cross-input constraint would have to live in the algorithm, which would either return `Either[Error, Probability]` (different from existing genetics algorithms) or fail at runtime.

### Three error cases for three independent failure modes

**Decision**: `IndependentAllelesProblemError` ADT with `NonPositiveGenerations(value)`, `NonPositiveAtLeast(value)`, and `AtLeastExceedsPopulation(atLeast, generations)`.

**Rationale**: Each case captures a distinct invariant violation with the information needed to diagnose it. `AtLeastExceedsPopulation` carries both inputs so the caller can compute the population size or render a useful message without re-deriving anything.

**Trade-off**: Three error cases vs. one generic `InvalidInput`. The explicit cases are slightly more code but vastly more useful diagnostically. Matches the framework's existing pattern (`CouplePopulationError` distinguishes `NegativeCount` from `ExceedsMaxCount`).

### Validation order: generations → atLeast → cross-constraint

**Decision**: `from` checks `generations >= 1` first, then `atLeast >= 1`, then `atLeast <= 2^generations`. First failure wins.

**Rationale**: Validates "simpler" invariants before the compound one. If `generations < 1`, computing `2^generations` is undefined (or negative bit-shift); we catch that first to ensure the cross-constraint check has well-defined inputs.

### Algorithm: incremental PMF scan, no factorial computation

**Decision**: Compute `P(X = 0) = q^total = (0.75)^total`, then use the ratio `P(X = j+1) / P(X = j) = (total - j) / (j + 1) × p/q` to walk the PMF. Sum the tail from index `atLeast` to `total`.

**Implementation**: `scanLeft` over `0 until total` starting from `pmf0 = math.pow(q, total)`. The resulting `Vector[Double]` has `total + 1` entries, one per possible value of X. `pmfs.drop(atLeast).sum` is the answer.

**Rationale**:
- Avoids computing `C(n, j)` via factorials, which would overflow for `n > 20` even in `Long`
- One Double per PMF entry; numerically stable for `total ≤ 128` (Rosalind's bound)
- Pure FP — no `var`, no mutable collections; `scanLeft` is the idiomatic Scala 2.13 sequential-state combinator

**Alternative considered**: `BigInt` binomial coefficient + `BigDecimal` arithmetic. Rejected — overkill for the precision Rosalind requires (`0.001`); Double is more than accurate enough.

**Alternative considered**: Compute `1 - P(X < atLeast)` (lower-tail subtraction). Rejected — for `atLeast` close to `total`, the lower-tail sum is close to 1.0 and subtracting yields catastrophic cancellation. The upper-tail sum is numerically stable for both small and large `atLeast`.

### `1L << generations` not `math.pow(2, generations).toLong`

**Decision**: Use the bit-shift operator for `2^generations`.

**Rationale**: Exact integer result; no floating-point coercion. `math.pow(2.0, 30.0).toLong` is `1073741824` exactly, but the integer bit-shift is more obviously correct and faster.

**Trade-off**: `1L << generations` is undefined for `generations >= 64` in Java (Long is 64-bit). The smart constructor's `generations >= 1` lower bound has no upper limit, so a pathological caller could trigger nonsense. → Documented in scaladoc; for Rosalind's `k ≤ 7`, the issue doesn't arise.

### `Int` for the PMF index, `Long` only for the constraint check

**Decision**: Cast `populationSize` to `Int` inside the algorithm (`val total = problem.populationSize.toInt`). The constraint check `atLeast.toLong > populationSize` uses `Long` to avoid overflow.

**Rationale**: For Rosalind's `k ≤ 7`, `populationSize ≤ 128` fits trivially in `Int`. `Vector.drop` and the range `(0 until total)` both want `Int`. Using `Long` consistently would require `(0L until total)` and `scanLeft` over `Range[Long]`, which compiles but is awkward.

**Trade-off**: If a caller passes `generations = 31`, `populationSize.toInt` would overflow to a negative number, breaking the algorithm. → Acceptable; the smart constructor's documented expectation is small `generations` per Rosalind. If a future spec needs larger, we'd switch to `Long` throughout (or use a log-space algorithm).

### Returns `Probability`, not `Double`

**Decision**: `IndependentAlleles.probability(problem): Probability` (the framework's validated `[0, 1]` type).

**Rationale**:
- The result is mathematically guaranteed in `[0, 1]` (binomial tail probability)
- Matches the precedent set by `MendelianInheritance.probabilityOfDominantPhenotype`, which also returns `Probability` via `unsafeFrom`
- `unsafeFrom` is the appropriate construction here — math guarantees range, no runtime validation needed

### Hard-coded `p = 1/4`

**Decision**: `private val P: Double = 0.25` inside `IndependentAlleles`.

**Rationale**:
- The 1/4 probability comes from the biology (the offspring is Aa Bb with probability 1/4 *given* the Aa Bb mate). It is not configurable.
- Future spec variants (different mate genotypes, different factors) would warrant a new algorithm/method, not parameterizing this one.

## Risks / Trade-offs

- [`1 << generations` overflows for `generations >= 31` (Int) or `>= 64` (Long)] No upper-bound validation. → Documented; Rosalind caps at 7 so this is theoretical for now. If exercised, the algorithm silently misbehaves.
- [Double precision could accumulate small errors over 128 PMF entries] At `total = 128` the smallest PMF entry is `(1/4)^128 ≈ 10^-77`, far below Double's smallest normal. Such terms underflow to denormals or zero — not a problem for the tail sum since they contribute negligibly. The largest terms (around `j = total*p ≈ 32`) dominate; precision there is excellent. → Acceptable for Rosalind's 0.001 tolerance; tests use `±1e-3` for the Rosalind sample and `±1e-9` for exact-rational comparisons.
- [Algorithm builds a Vector of `total + 1` Doubles] Memory: ~1 KB for `total = 128`. Trivially small. → Fine.
- [`Probability.unsafeFrom` skips validation] If a bug ever caused `pmfs.drop(atLeast).sum` to exceed 1.0 (numerical drift), `unsafeFrom` would silently wrap a bad value. → Mitigation: the math is rigorous; the recurrence preserves the property `sum(pmf) = 1` modulo floating-point error of order `1e-15`; tail sums are at most `1.0 - 0` and bounded by 1.0 within FP precision.
- [Three Mendelian-style algorithms with distinct parameter types could clutter the genetics subdomain] Each has a clear purpose: `MendelianInheritance` (single-offspring probability), `ExpectedOffspring` (expected count across couples), `IndependentAlleles` (binomial-tail across generations). → Acceptable specialization; bundling them would muddy the API.
