## Context

The framework's genetics subdomain (after the subdomain reorganization) contains:
- `bio.domain.genetics`: `Genotype` (3-case ADT), `Population` (k/m/n counts of *individuals* with smart constructor), `PopulationError`
- `bio.algorithms.genetics`: `MendelianInheritance` (probability of a single dominant offspring given a population)

Spec 11 needs a *different* population shape — counts of *couples* grouped by genotype *pairing* — and an algorithm returning an *expected count* rather than a probability. These don't fit on the existing `Population` type without overloading it past clarity, so we introduce a parallel domain type and a parallel algorithm.

## The math

Each pairing produces offspring with a known per-offspring dominant-phenotype probability under standard Mendelian assumptions:

| # | Pairing | P(dominant offspring) | Expected per couple (× 2) |
|---|---------|----------------------|---------------------------|
| 1 | AA × AA | 1.0                  | 2.0                       |
| 2 | AA × Aa | 1.0                  | 2.0                       |
| 3 | AA × aa | 1.0                  | 2.0                       |
| 4 | Aa × Aa | 0.75                 | 1.5                       |
| 5 | Aa × aa | 0.5                  | 1.0                       |
| 6 | aa × aa | 0.0                  | 0.0                       |

Total expected dominant offspring across a population:

```
E = 2·c1 + 2·c2 + 2·c3 + 1.5·c4 + 1.0·c5 + 0.0·c6
```

Sanity check on Rosalind sample `1 0 0 1 0 1`: `2·1 + 1.5·1 + 0·1 = 3.5` ✓

## Goals / Non-Goals

**Goals:**
- Model the population shape (6 counts) as a typed ADT (`CouplePopulation`) with input validation at construction time
- Validate each count is in `[0, 20000]` (Rosalind's stated cap is 20000 per count)
- Match the Rosalind sample exactly (`1 0 0 1 0 1` → `3.5`)
- Keep the algorithm total — once the input is a valid `CouplePopulation`, computation cannot fail
- Use Double arithmetic (Rosalind's output `3.5` is a real number, not a probability)

**Non-Goals:**
- A `GenotypePairing` ADT enumerating the 6 pairings. Tempting but adds friction for a single use site; revisit when a second algorithm needs the same enumeration.
- Punnett-square simulation. The pairing-specific probabilities are well-known constants; tabulating them by hand is correct and clearer than recomputing.
- Modeling "expected offspring of any phenotype" — Rosalind only asks for dominant. The "× 2" factor (two offspring per couple) is fixed; if a future spec generalizes it, we'd introduce a parameter.
- A `ExpectedOffspring` wrapper type around the `Double` result. Bare `Double` matches the bare-`Int` precedent set by `HammingDistance`.

## Decisions

### `CouplePopulation` is `sealed abstract case class` per project convention

**Decision**: `sealed abstract case class CouplePopulation(homDomHomDom: Int, homDomHet: Int, homDomHomRec: Int, hetHet: Int, hetHomRec: Int, homRecHomRec: Int)` with companion `from(c1, c2, c3, c4, c5, c6: Int): Either[CouplePopulationError, CouplePopulation]`.

**Rationale**: Same pattern as `Population`, `GcContent`, `Probability`, `RabbitProblem`. `apply` and `copy` synthesis is locked down; the only construction path validates the invariants.

**Trade-off**: Six positional `Int` parameters could be swapped by an unaware caller. Mitigation: named parameters in `from(c1 = ..., c2 = ..., c3 = ...)` at call sites (the Rosalind input order is positional anyway). The Rosalind problem statement enumerates the ordering 1–6 explicitly, so documenting "c1 through c6 in Rosalind order" is unambiguous.

### Field names are descriptive (`homDomHet`), not indexed (`c2`)

**Decision**: The case-class fields use descriptive names (`homDomHomDom`, `homDomHet`, `homDomHomRec`, `hetHet`, `hetHomRec`, `homRecHomRec`). The smart constructor parameters use `c1..c6` to match the Rosalind input vector.

**Rationale**:
- Field names read at the call site of `population.hetHet` rather than `population._4`
- Smart constructor parameters preserve the Rosalind ordering — callers pass values in the same order they appear in the problem input
- Trade-off: a tiny duplication between constructor parameter names and stored field names. Worth it for legibility on both ends.

**Alternative considered**: Use `c1..c6` everywhere. Rejected — at call sites of computed values (`pop.c4 + pop.c5`), the indices are opaque and easy to misread.

### `CouplePopulationError` carries 1-based index + offending value

**Decision**: `sealed trait CouplePopulationError` with cases `NegativeCount(index: Int, value: Int)` and `ExceedsMaxCount(index: Int, value: Int)`. `index` is 1-based and matches the Rosalind input position; `value` is the offending count.

**Rationale**:
- 1-based matches the Rosalind problem statement (which numbers the inputs 1..6) and the spec scenarios
- Index + value is the minimum information needed for an actionable error message ("c3 was -5; must be in [0, 20000]")

**Alternative considered**: `OutOfBounds(index, value)` lumping both error cases. Rejected — losing the distinction between "negative" and "too large" reduces diagnostic value at no real cost.

### Validation short-circuits on the first invalid count

**Decision**: `from` walks the inputs in order (c1, c2, ..., c6) and returns the first failure encountered. Subsequent invalid counts (if any) are not reported.

**Rationale**:
- Matches the framework's existing pattern (`Population.from` reports `NegativeCount` once, doesn't enumerate which field)
- Multi-error reporting (`Validated[NEL[Error], T]`) is overkill for an interactive CLI use case; the user fixes one error, recompiles, sees the next
- Simpler implementation, predictable order

**Alternative considered**: Accumulate all errors using `cats.data.Validated`. Rejected — adds a dependency-style import path and complexity for marginal benefit at this scale.

### Result is `Double`, not `BigDecimal` or `Probability`

**Decision**: `ExpectedOffspring.dominantPhenotype(pop): Double` returns a bare `Double`.

**Rationale**:
- Expected count is a real number, not a probability — the value can exceed 1.0 (e.g., 3.5 in the sample; 170000.0 at max). `Probability` (which is `[0, 1]`) would be wrong.
- The math is six multiplications + five additions. Double precision is more than enough for inputs ≤ 20000 (max expected value 6 × 2.0 × 20000 = 240000, well within Double's exact-integer range).
- Bare `Double` matches `HammingDistance` returning bare `Int` — both are output values without invariants worth wrapping.

**Alternative considered**: `BigDecimal` for exact arithmetic. Rejected — the multipliers `2.0` and `1.5` are exactly representable in Double; `1.0` and `0.0` trivially so. No precision loss; no need for arbitrary-precision overhead.

### Algorithm is a single-expression sum

**Decision**:
```scala
def dominantPhenotype(pop: CouplePopulation): Double =
  2.0 * pop.homDomHomDom +
  2.0 * pop.homDomHet +
  2.0 * pop.homDomHomRec +
  1.5 * pop.hetHet +
  1.0 * pop.hetHomRec +
  0.0 * pop.homRecHomRec
```

**Rationale**: One expression, no helpers, no folds, no abstractions. The 6 multipliers correspond directly to the table in the math section; a reader can verify the algorithm by checking the table. Adding a `multipliers: Vector[Double]` and a fold would add a layer of indirection that obscures the formula.

The `0.0 * pop.homRecHomRec` line is kept (rather than dropped) for *parallelism* — it makes the formula visibly cover all 6 pairings. A future reader is less likely to wonder whether the aa×aa case was forgotten.

### Lives in `bio.algorithms.genetics`, joining `MendelianInheritance`

**Decision**: `ExpectedOffspring` is in `bio.algorithms.genetics` alongside `MendelianInheritance`. `CouplePopulation` and `CouplePopulationError` are in `bio.domain.genetics`.

**Rationale**: Both algorithms operate on Mendelian-genetics populations; both produce expected-value scalars from population counts. Co-locating keeps the genetics subdomain coherent.

## Risks / Trade-offs

- [Six positional Int args in `from` are easy to mis-order] Mitigated by named parameters at call sites and by the validation rejecting `< 0` or `> 20000` (which catches gross errors). A truly mis-ordered but in-bounds input would silently produce a wrong answer — same risk as `Population.from(k, m, n)`. → Documented; caller responsibility; the Rosalind input ordering is fixed.
- [Floating-point output] `Double` rounding could theoretically produce e.g. `3.4999999...` instead of `3.5`. → Mitigation: tests compare with `±1e-9` tolerance on the Rosalind sample; for inputs ≤ 20000, the multipliers `2.0`, `1.5`, `1.0`, `0.0` and the products all stay within Double's exact-integer range up to 2^53.
- [Six descriptive field names increase verbosity] → Acceptable; the field names match the biology and read naturally at call sites.
- [No `GenotypePairing` ADT to enumerate the 6 pairings] If a future algorithm needs to enumerate, abstract, or pattern-match on the pairings, we'd reach for one. → Defer until that need arises.
- [Cap is hard-coded at 20000] If a future problem needs a higher cap, we'd change the constant. → One-line change; not a concern.
