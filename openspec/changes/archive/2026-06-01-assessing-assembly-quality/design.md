## Context

Rosalind ASMQ gives a collection of DNA contigs (at most 1000 strings, combined length
≤ 50 kbp) and asks for N50 and N75. The N-statistic `NXX` is the maximum positive
integer `L` such that the contigs of length ≥ `L` total at least `XX%` of the summed
contig length. For the sample `GATTACA` (7), `TACTACTAC` (9), `ATTGAT` (6), `GAAGA` (5),
total = 27: contigs ≥ 7 total 16 bp (> 50% of 27) while contigs ≥ 8 total 9 bp, so
N50 = 7; contigs ≥ 6 total 22 bp (> 75%) while contigs ≥ 7 total 16 bp, so N75 = 6.

This is the first assembly-*quality* capability; the existing assembly problems
(DBRU/PCOV/GASM) live in `bio.{domain,algorithms}.graph` because they are graph
reconstructions. ASMQ is a pure length statistic, so it gets its own
`bio.{domain,algorithms}.assembly` package.

Framework conventions apply: validated ADT domain types via smart constructors returning
`Either`, `sealed abstract case class` to block `apply`/`copy` leakage, first-failure-wins
validation, pure/total algorithms (no `var`/mutable/imperative loops), `IO` for side
effects, a `format: String` on the result type, and strict TDD.

## Goals / Non-Goals

**Goals:**
- Validate a DNA contig collection into `ContigCollection` enforcing Rosalind bounds.
- Compute `NXX` for any percentile and expose `assess` producing N50/N75 as
  `AssemblyQuality` with `format`.
- Match the canonical Rosalind sample (`7 6`).
- Provide ≥2 edge cases beyond the canonical sample.

**Non-Goals:**
- Other assembly metrics (L50, NG50, auN) — out of scope for this problem.
- Reusing the graph-assembly bundles; ASMQ has unrelated invariants (variable-length
  contigs, count + total-length caps) and warrants its own types.

## Decisions

### Decision 1: New `assembly` package with a `ContigCollection` bundle
`ContigCollection` is a `sealed abstract case class` wrapping `Vector[DnaString]`,
constructed via `from(contigs): Either[ContigCollectionError, ContigCollection]`.
Validation order (first-failure-wins): empty collection → more than 1000 contigs →
first empty contig (length 0) → combined length over 50 000. `ContigCollectionError`
cases: `EmptyContigCollection` (case object), `TooManyContigs(count, max)`,
`EmptyContig(index)`, `ExceedsTotalLength(total, max)`. Rationale: contigs are
*variable* length (the whole point of N-statistics), so there is no per-contig length
cap or inconsistent-length check; instead the Rosalind caps are the contig count (1000)
and the combined length (50 kbp). Empty contigs are rejected because `NXX` is defined
over positive integer lengths and a zero-length contig is not a meaningful contig.

**Alternative considered:** Place ASMQ under `graph` next to the other assembly
problems. Rejected — ASMQ has no graph; co-locating by problem-family (assembly quality)
is clearer than by historical accident.

### Decision 2: N-statistic via descending-length cumulative scan
`nStatistic(contigs, percentile)`: take the contig lengths, sort them descending,
accumulate a running total, and return the length of the first contig at which the
running total reaches at least `percentile%` of the grand total. Because the included
set grows monotonically as `L` decreases, the first length whose cumulative crosses the
threshold is exactly the maximum `L` satisfying the definition. Comparison is done with
integer cross-multiplication (`cumulative * 100 >= percentile * total`) to avoid floating
point. `assess` calls this with 50 and 75. Rationale: this is the standard, total, and
allocation-light computation; on a non-empty collection of positive-length contigs the
final cumulative equals the total, so a crossing always exists.

**Alternative considered:** Compute a fractional threshold with `Double`. Rejected —
integer cross-multiplication is exact and avoids rounding ambiguity at the boundary.

### Decision 3: `percentile` as a plain `Int` parameter, not a domain type
`nStatistic` takes `percentile: Int` (intended range 1..99) as an algorithm parameter
rather than a wrapped ADT. The only call sites are the fixed values 50 and 75 inside
`assess`. Rationale: the percentile is a computation input, not a validated domain
entity; wrapping it would add ceremony without protecting any invariant the problem
relies on.

## Risks / Trade-offs

- [Overflow in `cumulative * 100`] → Combined length ≤ 50 000, so `cumulative * 100`
  ≤ 5 000 000, well within `Int`; computation uses `Long` for headroom and clarity.
- [`nStatistic` called with an out-of-range percentile] → The public surface only
  invokes 50 and 75 via `assess`; `nStatistic` documents the 1..99 precondition. No
  defensive validation is added to keep the function total and simple.
- [Light duplication of the validated-bundle pattern] → Accepted; the contig invariants
  differ enough from the graph bundles that a shared abstraction would obscure them.
