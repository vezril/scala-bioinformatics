## Context

The framework's subdomain organization (established by the spec-15-era reorg) sorts code into biology subdomains (`nucleic`, `protein`, `genetics`, `analysis`) plus a small set of math primitives (`stats` for `Probability`, `recurrence` for Fibonacci-style problems). Permutations are a *pure-math* concept dressed up by Rosalind as "gene orders". They don't fit `genetics` (no biology), don't fit `recurrence` (no time-step iteration), and don't fit `stats` (no probability/expectation). A new `combinatorics/` subdomain is the right home — small now, growing as future enumeration problems (combinations, subsets, partitions, Catalan numbers) land.

## The math

A permutation of length `n` is an ordering of `{1, 2, ..., n}`. There are exactly `n!` such orderings. For `n ≤ 7`:

| n | n! |
|--:|---:|
| 1 | 1 |
| 2 | 2 |
| 3 | 6 |
| 4 | 24 |
| 5 | 120 |
| 6 | 720 |
| 7 | 5040 |

Rosalind caps `n ≤ 7` (so 5040 permutations is the upper bound). The framework matches this cap because the output grows factorially.

## Goals / Non-Goals

**Goals:**
- Provide `Permutations.enumerate(length: PermutationLength): Vector[Vector[Int]]` returning all permutations of `{1, ..., length.value}`
- Be total once the input is a valid `PermutationLength`
- Match the Rosalind sample `n=3` → 6 permutations, each containing `{1, 2, 3}` exactly once
- Enforce the `n ≤ 7` cap at construction time (Rosalind constraint)
- Establish `bio.domain.combinatorics` and `bio.algorithms.combinatorics` as the new home for enumeration-style primitives

**Non-Goals:**
- A `Permutation` wrapper type around `Vector[Int]`. The algorithm's output is structurally guaranteed to contain each value exactly once; wrapping would add ceremony without enforcing a new invariant.
- A `count(length): Int` factorial method. The caller can compute `enumerate(length).size` if needed; for `n ≤ 7` the cost is constant (≤ 5040 small Vectors).
- Streaming / lazy enumeration. Even at `n=7`, the result is 5040 small vectors — trivially fits in memory.
- Lexicographic / non-lexicographic ordering control. Rosalind says "in any order"; we return Scala's `Iterable.permutations` order (lexicographic) and document it.
- Permutations of arbitrary types (e.g., a `String`, a `Vector[A]`). The Rosalind problem is over `{1, ..., n}`. Generalizing now would obscure intent; revisit if a future spec needs it.

## Decisions

### `PermutationLength` is a `sealed abstract case class`

**Decision**: `sealed abstract case class PermutationLength(value: Int)` with smart constructor `from(value: Int): Either[PermutationLengthError, PermutationLength]` enforcing `1 <= value <= 7`.

**Rationale**: Matches the framework's pattern (`Population`, `RabbitProblem`, `IndependentAllelesProblem`, `MortalRabbitProblem`, etc.) — single-Int validated parameter. Locks the `apply` and `copy` to keep the invariant intact.

**Alternative considered**: Raw `Int` argument on `Permutations.enumerate`. Rejected — the algorithm would need to validate at every call, and the validity guarantee wouldn't travel with the value.

### Upper-bound cap at construction time, not in the algorithm

**Decision**: `PermutationLength.from` rejects `value > 7` with `ExceedsMaximum(value, max=7)`.

**Rationale**:
- Rosalind explicitly caps `n ≤ 7`; matching the cap in the type system makes downstream code safe by construction
- The factorial growth means `n = 12` produces 479M permutations — well beyond what fits in a reasonable `Vector`. The cap saves callers from accidentally OOM-ing.
- The cap is documented (Rosalind constraint, factorial blow-up). If a future spec needs a higher cap, raise it explicitly.

**Trade-off**: Hard-coded cap at 7. → Acceptable; the constraint is biology-problem-driven, not arbitrary. A `MaxLength` constant in the companion makes the value explicit.

### Two error cases: `NonPositive`, `ExceedsMaximum`

**Decision**: `sealed trait PermutationLengthError` with `final case class NonPositive(value: Int)` and `final case class ExceedsMaximum(value: Int, max: Int)`. The `ExceedsMaximum` case carries `max` so the caller can render a diagnostic without re-deriving the bound.

**Rationale**: Matches the framework's "explicit error cases > lumped 'OutOfBounds'" pattern from `CouplePopulationError`. Two genuinely distinct failure modes warrant two distinct cases.

**Alternative considered**: Lump into `OutOfRange(value, min, max)`. Rejected — `min` is always `1`, so it'd be a tautological field; the cases are semantically distinct (zero/negative input vs. too-large input) and should be matchable separately.

### Algorithm: use Scala stdlib's `Iterable.permutations`

**Decision**: `(1 to length.value).toVector.permutations.toVector`.

**Rationale**:
- `permutations` is part of the Scala standard library — tested, idiomatic, O(n!) with constant overhead per permutation
- Returns lexicographic order (documented behavior)
- No custom recursion to maintain
- Single line of code; the smart constructor + the stdlib do all the work

**Alternative considered**: Hand-rolled recursive permutation generator (e.g., Heap's algorithm). Rejected — reinventing a stdlib primitive adds maintenance burden with no functional gain.

**Alternative considered**: Use `Iterator` to avoid materializing 5040 vectors at once. Rejected — Rosalind's `n ≤ 7` bound means the materialized result is at most 5040 small vectors (~40 KB); lazy enumeration adds API complexity for no real benefit.

### Return type: `Vector[Vector[Int]]`, no wrapper

**Decision**: `def enumerate(length: PermutationLength): Vector[Vector[Int]]`.

**Rationale**:
- Each permutation is a `Vector[Int]` of length `length.value` containing every value in `1..length.value` exactly once. The algorithm guarantees this by construction.
- Wrapping each permutation in `Permutation(value: Vector[Int])` would force callers to unwrap before using the values (`.map(_.value)`)
- Matches the framework's "no wrapper when no further invariant is meaningful" precedent (`MotifLocations.find` returns `Vector[Int]`)

### `Permutations.enumerate`, not `Permutations.all` or `Permutations.generate`

**Decision**: Method name `enumerate`.

**Rationale**:
- "Enumerate" precisely describes "list all elements of a finite set"
- Matches the Rosalind problem title "Enumerating Gene Orders"
- Avoids the ambiguity of `all` (all what?) or `generate` (a generator? a side-effect?)

### New subdomain `combinatorics/`

**Decision**: Create `bio.domain.combinatorics/` and `bio.algorithms.combinatorics/` as new subdomain pair.

**Rationale**:
- Permutations don't belong in any existing subdomain — `genetics` is biology; `recurrence` is time-step; `stats` is probability; `analysis` is sequence-property extraction
- Future spec problems will need siblings here: combinations, subsets, partitions, Catalan numbers, integer compositions
- Starting with one file in each new directory is acceptable per the framework's precedent (`stats/` and `recurrence/` also started small)

**Alternative considered**: Place under `bio.algorithms.genetics.GeneOrders` matching the Rosalind problem title. Rejected — frames a pure-math primitive as a biology concern, reducing reusability.

**Alternative considered**: Place under `bio.algorithms` directly (no subdomain). Rejected — breaks the established pattern that every algorithm lives in a subdomain.

## Risks / Trade-offs

- [Hard-coded cap of 7] If a future Rosalind problem needs `n = 10`, the cap must change. → One-line edit in `PermutationLength.MaxLength`. If many specs need different caps, parameterize.
- [Materializing all 5040 vectors at `n=7`] ~40 KB allocation. → Trivially small.
- [No `Permutation` wrapper] If a future caller wants to enforce permutation-ness via the type system, they'd have to add the wrapper later. → Acceptable; YAGNI for now; the algorithm's output is provably correct.
- [`Iterable.permutations` order is implementation-defined] Scala 2.13 documents lexicographic order, but a future Scala version could change. → Rosalind says "any order", so tests check membership (`should contain`) rather than positional equality.
- [Two empty subdirectories (`combinatorics/`) start with one file each] Less aesthetically tidy than full directories. → Acceptable; matches the pattern set by `stats/` and `recurrence/`.
