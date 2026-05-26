## Context

Specs 2–8 produced scalars or single-best records. Spec 9 produces a *list of positions* — the framework's first sequence-search algorithm. Several future Rosalind problems follow the same shape (ORF detection, restriction enzymes, primer search), so the conventions chosen here (1-based, overlapping, total) will be reused.

Two design tensions surfaced:
1. Whether to wrap output positions in a new domain type or return a bare `Vector[Int]`
2. Whether to model "motif" and "text" as distinct types or both as `DnaString`

The framework's current style answers both with "use the simplest thing that works"; this design follows that lead and documents the conventions it adopts.

## Goals / Non-Goals

**Goals:**
- Provide `MotifLocations.find(text: DnaString, motif: DnaString): Vector[Int]` returning all 1-based starting positions where `motif` occurs as a contiguous substring of `text`
- Include overlapping matches (the Rosalind sample `GATATATGCATATACTT` / `ATAT` requires positions `2, 4, 10` — `2` and `4` overlap)
- Be total — every pair of `DnaString`s produces a valid result; no `Either` wrapping
- Match the Rosalind sample exactly

**Non-Goals:**
- Approximate / fuzzy matching (Hamming distance, mismatches allowed) — separate future capability
- Regex / IUPAC ambiguity codes (`N`, `R`, `Y`, etc.) — separate future capability
- Searching protein sequences for motifs — a `ProteinMotifLocations` could be added later when protein-motif problems land
- Searching against multi-record FASTA inputs — out of scope; callers compose with `FastaParser` if needed
- Performance optimization beyond linear scan (KMP, Boyer-Moore, suffix automaton) — Rosalind's "at most 1 kbp" cap makes the naïve O(n·m) algorithm fast enough; revisit when scale demands it
- A `Position` newtype enforcing the 1-based invariant in the type system

## Decisions

### Return `Vector[Int]` — no `MotifLocations` wrapper type

**Decision**: `find` returns a bare `Vector[Int]` documented as "1-based starting positions, ascending".

**Rationale**:
- The framework already returns bare collections for similar shapes (cf. `Codon.parseAll` returning `Vector[Codon]`)
- A `MotifLocations` wrapper would have no enforced invariant beyond "Ints are non-negative" — a weak return on the ceremony
- Positions are inherently positional integers; the 1-based convention is documented in one place (the function's scaladoc) rather than encoded into a wrapping type that callers would unwrap anyway

**Alternative considered**: `final case class MotifLocations(positions: Vector[Int])` wrapper. Rejected — adds wrapping noise without enforcing a new invariant.

**Alternative considered**: `Position` newtype (`sealed abstract case class Position(value: Int)`) constrained to `>= 1`. Rejected — by construction the algorithm only emits valid 1-based positions; the type wrapper would be redundant for an output the algorithm guarantees and would force callers to unwrap to use the index for substring operations.

### `text` and `motif` are both `DnaString` — distinguished by parameter name, not type

**Decision**: `find(text: DnaString, motif: DnaString)`. No type aliases, no separate `DnaMotif` type.

**Rationale**:
- Both arguments must satisfy the same invariants (alphabet ⊆ `{A, C, G, T}`, length ≤ 10000 — wait, DnaString cap is 1000; revisit below). A separate type would either re-validate (pointless) or be a transparent alias (no extra safety).
- Named parameters at the call site make the role clear: `MotifLocations.find(text = haystack, motif = needle)`.

**Trade-off**: A caller could accidentally swap the arguments. Mitigated by the parameter names; an error would just produce no matches (or fewer than expected), which a unit test typically catches.

### Total function — empty motif returns `Vector.empty` by convention

**Decision**: `find` is total over all `(DnaString, DnaString)` pairs. Edge cases:
- Empty motif → `Vector.empty` (an empty pattern has no meaningful location set)
- Motif longer than text → `Vector.empty`
- Empty text + non-empty motif → `Vector.empty`
- Empty text + empty motif → `Vector.empty`
- No matches → `Vector.empty`

**Rationale**:
- Empty-motif handling: mathematically ambiguous (every position trivially "matches"). The biological interpretation — "where does this motif occur?" — is undefined for an empty motif. Returning `Vector.empty` matches the "convention for degenerate inputs returns the empty result" pattern already used by `GcContent.of(empty DnaString) = GcContent(0.0)` and `RnaTranslation.translate(empty) = empty ProteinString`.
- All other edge cases fall out naturally from a left-to-right sliding-window scan over the range `0 to (text.length - motif.length)`. When that range is empty (text shorter than motif), zero iterations happen.

**Alternative considered**: Return `Either[MotifError, Vector[Int]]` with `EmptyMotif` and `MotifLongerThanText` errors. Rejected — these aren't errors, they're degenerate inputs with a defined zero answer. Forcing callers to handle a Left for a benign edge case is friction with no safety gain.

### Algorithm: naïve left-to-right sliding window

**Decision**: Iterate `i` from `0` to `text.length - motif.length` (inclusive), check if `text.startsWith(motif.value, i)`, collect `i + 1` for matches.

**Rationale**:
- The Rosalind cap of 1 kbp per string makes worst-case O(n·m) = O(10^6) operations — fast enough that any constant-factor optimization is invisible.
- `String.startsWith(prefix, offset)` is in the JDK standard library, runs in tight native code, and avoids the overhead of allocating substring intermediates.
- Functional style — no `var`, no mutable collections. The iterator + `filter` + `map` + `toVector` chain is idiomatic Scala.

**Alternative considered**: Knuth-Morris-Pratt (KMP) for guaranteed O(n + m). Rejected — over-engineered for the framework's current scale; revisit if a Rosalind problem brings strings into the megabase range.

**Alternative considered**: `text.value.indexOf(motif.value, fromIndex)` in a recursive accumulator. Equivalent result, more terse, but the sliding-window form is clearer about its iteration domain.

### Lives in `bio.algorithms.analysis`, not `bio.algorithms.nucleic`

**Decision**: `MotifLocations` joins `HighestGc` under `bio.algorithms.analysis`.

**Rationale**: The subdomain `analysis/` is for algorithms that *extract properties* from sequences — GC content, motif positions, future entropy / complexity / alignment scores. The `nucleic/` subdomain is for nucleic-acid *transformations* (transcription, reverse complement) that produce other nucleic-acid sequences. Motif finding produces positional metadata, not a transformed sequence; analysis is the right home.

**Alternative considered**: `bio.algorithms.nucleic.MotifLocations`. Rejected — the result type is positions, not nucleic acids; the operation reads DNA but doesn't produce DNA.

## Risks / Trade-offs

- [No `Position` newtype means callers handle bare `Int`] If a future problem composes motif positions with another framework function, the lack of a wrapper means weak type safety. → Acceptable; revisit when an actual integration pain emerges. The 1-based convention is the only invariant and it's documented.
- [Empty-motif behavior is a convention call] An alternative interpretation (every position matches) would return `Vector(1, 2, ..., text.length + 1)`. → Mitigated by explicit scaladoc and a dedicated scenario in the spec. Most users won't pass an empty motif; those who do will get a sensible empty result.
- [Naïve O(n·m) doesn't scale beyond the Rosalind cap] Multi-megabase searches would need KMP / suffix arrays. → Not a Rosalind concern; document the limit in scaladoc, swap implementation later without changing the API.
- [`String.startsWith(prefix, offset)` is a JDK API call, not a pure-Scala primitive] Pedantically, it's not "100% Scala". → Acceptable; the framework already relies on JDK string operations (`linesIterator`, `grouped`, `mkString`) throughout.
- [Two `DnaString` parameters can be swapped at the call site] Caller passes `(motif, text)` instead of `(text, motif)`. → Named parameters at the call site (`MotifLocations.find(text = ..., motif = ...)`) mitigate; the test for motif-longer-than-text returning empty catches the obvious swap case.
