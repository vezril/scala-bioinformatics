## Context

Specs 2–9 produced either scalars from a single input (counts, percentages, transcribed strings, proteins) or positional metadata from a haystack/needle pair (motif locations). Spec 10 is the first algorithm whose two inputs are *symmetric* — the Hamming distance `d(a, b) == d(b, a)`, neither is a "subject" of the other. It is also the framework's first case where two `DnaString` inputs are subject to a *cross-input precondition* (equal length) that cannot be expressed in either type alone.

Current state after spec 9: `bio.algorithms.analysis` contains `HighestGc` and `MotifLocations`. `bio.domain.analysis` contains `GcContent` and `GcContentError`. The pattern for new analysis algorithms is established.

## Goals / Non-Goals

**Goals:**
- Provide `HammingDistance.between(a: DnaString, b: DnaString): Either[HammingError, Int]` computing the number of differing positions
- Model the length-mismatch precondition as an explicit `HammingError.LengthMismatch(lengthA, lengthB)` error
- Be total — no exceptions; every input pair yields either a `Right(Int)` or a `Left(error)`
- Return distance 0 for two empty `DnaString`s (well-defined boundary case, not a convention)
- Match the Rosalind sample exactly (`GAGCCTACTAACGGGAT` vs `CATCGTAATGACGGCCT` → 7)

**Non-Goals:**
- Weighted / position-specific scoring (substitution matrices, PAM/BLOSUM) — those belong to alignment algorithms
- Edit distance (insertions/deletions allowed) — separate future algorithm
- Hamming distance over `RnaString` or `ProteinString` — separate future overloads when problems demand them
- Hamming distance over arbitrary string types — confined to `DnaString` for now, mirroring the framework's typed-domain philosophy
- A `Distance` wrapper type — bare `Int` matches the bare `Vector[Int]` precedent set by `MotifLocations.find`

## Decisions

### Use `Either[HammingError, Int]`, not `Option[Int]` or convention

**Decision**: Length-mismatched inputs return `Left(HammingError.LengthMismatch(lengthA, lengthB))`. The error carries both lengths for actionable diagnostics.

**Rationale**:
- Unlike empty motif (which has a defensible empty-result convention), unequal-length Hamming distance is mathematically undefined — there is no "natural" zero
- Returning `Option[Int]` would hide *why* the result is absent; `Either` with a structured error matches the framework's pattern (`Population.from` returns `Either[PopulationError, ...]`, `GcContent.from` returns `Either[GcContentError, ...]`)
- The error carrying both lengths lets the caller log a useful diagnostic without re-computing them

**Alternative considered**: Return `Option[Int]`. Rejected — silently drops the diagnostic information about *which* lengths mismatched.

**Alternative considered**: Truncate to the shorter of the two and compute distance over that prefix. Rejected — silently changes the semantics; a caller expecting Hamming distance gets something else; bug-prone.

### Error type lives in `bio.domain.analysis`, not co-located with the algorithm

**Decision**: `HammingError` is in `bio.domain.analysis`, alongside `GcContent`/`GcContentError`. The algorithm `HammingDistance` is in `bio.algorithms.analysis`.

**Rationale**: This mirrors the existing pattern — `GcContentError` is in `bio.domain.analysis` (the data side), while `GcContent.of` and `HighestGc.find` are in `bio.algorithms.analysis` (the algorithm side). Errors are values; values live in `domain`. Keep the convention.

**Alternative considered**: Co-locate `HammingError` with `HammingDistance` in `bio.algorithms.analysis`. Rejected — would diverge from the established `domain/algorithms` split.

### Algorithm: zipped comparison with character inequality

**Decision**: `a.value.lazyZip(b.value).count { case (x, y) => x != y }`. After verifying length parity, count positions where characters differ.

**Rationale**:
- `lazyZip` (Scala 2.13) avoids allocating an intermediate tuple sequence — counts in a single pass
- Compares chars directly rather than dispatching through `DnaNucleotide.fromChar`. Both inputs are validated `DnaString`s already; their underlying chars are guaranteed to be in `{A, C, G, T}`. Direct `Char` inequality is correct and 4× faster.
- Pure FP — no `var`, no mutable collections

**Alternative considered**: Dispatch through `DnaNucleotide.fromChar` and compare ADT values. Rejected — adds two `fromChar` calls per position with no semantic gain; the chars are already known-valid.

**Alternative considered**: `(a.value zip b.value).count(...)` (eager `zip`). Functionally equivalent for our small inputs; `lazyZip` is the idiomatic 2.13 choice and slightly more efficient.

### Method name: `between(a, b)`, not `compute(a, b)` or `apply(a, b)`

**Decision**: `HammingDistance.between(a: DnaString, b: DnaString): Either[HammingError, Int]`.

**Rationale**:
- "The Hamming distance *between* a and b" reads naturally
- Avoids `apply` (which suggests a smart-constructor for a `HammingDistance` type that doesn't exist here)
- Avoids generic verbs like `compute` or `calculate` that don't communicate symmetry

### Empty/empty case is `Right(0)` — not an error

**Decision**: `between(empty, empty) == Right(0)`. Both empty strings have the same length (0) and no differing positions; the answer is mathematically 0, not an error.

**Rationale**:
- Length parity check passes (both lengths are 0)
- The count of differing positions in zero positions is zero
- No special-case needed; the algorithm naturally falls out

### No `Distance` newtype around the `Int` result

**Decision**: Return bare `Int` inside the `Right`, not a `final case class Distance(value: Int)` wrapper.

**Rationale**: Consistent with `MotifLocations.find` returning bare `Vector[Int]`. Hamming distance is a non-negative integer with no further invariant the type system can usefully enforce beyond `Int >= 0`. Adding a wrapper would force callers to unwrap before doing arithmetic on the value.

**Trade-off**: A future algorithm that returns a Levenshtein / edit distance would have the same shape and be indistinguishable at the type level. Acceptable — the function name is the documentation.

## Risks / Trade-offs

- [Direct char inequality skips ADT dispatch] If `DnaString` validation is ever relaxed to accept lowercase or extended alphabets, a `'A' != 'a'` comparison could over-count. → Mitigation: the framework's `DnaString.from` validates uppercase-only; if that changes, this algorithm's test suite catches it via the Rosalind sample (which uses uppercase only and would not regress unless the *result* differs).
- [Bare `Int` return loses semantic context if mixed with other distance algorithms] Future Levenshtein / edit distance would have the same return type. → Acceptable for now; revisit if multiple distance algorithms emerge and need disambiguation in compound APIs.
- [`LengthMismatch` carries lengths but not which side is which by name] Field names are `lengthA` and `lengthB`. Callers pass `a` and `b`; the error preserves that order. → Documented in scaladoc.
- [Length comparison happens before any character comparison] If a caller has slightly mismatched lengths but otherwise mostly aligned sequences, they may expect a "partial" distance. → Documented as out of scope; that's edit distance, not Hamming.
- [No support for `RnaString` or `ProteinString`] If a problem needs Hamming distance over RNA or protein, that's a separate overload. → Acceptable; add when needed. Premature generalization would obscure the simple two-DNA case.
