## Context

Rosalind GASM gives a collection `S` of error-free reads of equal length (≤50 bp)
sampled from a circular chromosome. Because reads may come from either strand, the
true assembly is recovered by considering both each read and its reverse complement.
For some positive integer `k`, the de Bruijn graph `B_k` built on the `(k+1)`-mers of
`S ∪ S^rc` consists of exactly two directed cycles, which are reverse complements of
one another. The answer is a cyclic superstring of minimal length that contains every
read or its reverse complement.

This builds directly on PCOV (`genome-assembly-with-perfect-coverage`), which assembled
a single de Bruijn cycle from one strand. GASM reuses `CyclicSuperstring`,
`DnaReverseComplement`, and `DnaString`, and mirrors PCOV's validation bundle.

The framework conventions apply: validated ADT domain types via smart constructors
returning `Either`, `sealed abstract case class` to block `apply`/`copy` leakage,
first-failure-wins validation, pure/total algorithms (no `var`/mutable/imperative
loops), `IO` for side effects, a `format: String` on the result type, and strict TDD.

## Goals / Non-Goals

**Goals:**
- Validate an equal-length DNA read collection into `GenomeAssemblyReadsProblem`.
- Assemble the circular chromosome by finding the de Bruijn graph (over reads plus
  reverse complements) that decomposes into exactly two reverse-complement directed
  cycles, returning a `CyclicSuperstring`.
- Match the canonical Rosalind sample (a rotation/strand of `GATTACA`).
- Provide ≥2 edge cases beyond the canonical sample.

**Non-Goals:**
- Handling error-containing reads, unequal-length reads, or non-circular genomes.
- Introducing a shared abstraction over PCOV and GASM; light duplication is acceptable
  to keep each capability's invariants explicit.
- A general graph library; only the minimal cycle-detection needed for GASM.

## Decisions

### Decision 1: Mirror PCOV's validation bundle (no count cap)
`GenomeAssemblyReadsProblem` is a `sealed abstract case class` wrapping
`Vector[DnaString]`, constructed via `from(reads): Either[GenomeAssemblyReadsProblemError, _]`.
Validation order (first-failure-wins): empty collection → each read length in `[2, 50]`
(too-short before too-long) → all reads share the length of the first read.
`GenomeAssemblyReadsProblemError` cases: `EmptyReadCollection` (case object),
`ReadTooShort(index, length, min)`, `ReadTooLong(index, length, max)`,
`InconsistentLength(index, length, expected)`. No `TooManyReads` cap — Rosalind states
none. Rationale: identical invariants to PCOV; keeping a parallel structure makes the
two capabilities easy to compare and reason about.

**Alternative considered:** Reuse `PerfectCoverageProblem` directly. Rejected — the
GASM error vocabulary ("read" vs "kmer") and future divergence justify a distinct type.

### Decision 2: Search descending k-mer sizes for the two-cycle de Bruijn graph
Let `L` be the common read length and `allReads = reads ∪ reads.map(reverseComplement)`
as a `Set[String]`. For candidate `size` from `L` down to `2`, extract the distinct
`size`-mers by sliding a window over every string in `allReads`, build the de Bruijn
graph whose nodes are the `(size-1)`-mers and whose edges are `prefix -> suffix` for
each `size`-mer, and accept the first (largest) `size` whose graph is exactly two
disjoint simple directed cycles. Rationale: the largest qualifying `size` corresponds
to the minimal-length superstring, matching Rosalind's "minimal length" requirement;
descending search returns it first.

**Alternative considered:** Only test `size = L`. Rejected — the sample requires
`size = L-1 = 4` (size `5` produces dead-ends, not cycles), so a search is necessary.

### Decision 3: "Exactly two simple cycles" test via degree + walk decomposition
A de Bruijn graph here is exactly two simple directed cycles iff: every node has
in-degree = out-degree = 1 (so adjacency is a bijection `Map[String, String]`), and
walking the permutation decomposes the node set into exactly two disjoint cycles.
Implementation: build `adjacency: Map[String, String]`; reject unless it is a total
bijection on the node set (every node appears exactly once as a key and once as a
value, i.e. `keys == values`-as-sets and sizes match the distinct k-mer count with no
fan-out — guaranteed because edges come from a `Set` and each node maps to one
successor). Then enumerate cycle components by repeatedly walking from an unvisited node
until it returns to its start; accept iff exactly two components are produced and they
together cover all nodes. Rationale: pure, total, no mutation — components computed via
a tail-recursive walk accumulating visited sets.

### Decision 4: Deterministic canonical output across the two cycles
Each cycle yields a circular string by walking from its lexicographically smallest node
and emitting each node's head symbol (as in PCOV). For a cycle, the canonical linear
form is its lexicographically smallest rotation. The two cycles are reverse complements,
so both are valid Rosalind answers (each contains every read or its reverse complement).
We emit the lexicographically smallest minimal-rotation across the two cycles as the
deterministic output. For the sample this is `AATCTGT` (the reverse-complement strand of
`GATTACA`). Rationale: Rosalind accepts any rotation of either strand; a fixed lexical
choice makes the output reproducible and testable. Tests assert both the exact canonical
value and rotation/strand-equivalence to `GATTACA`.

**Alternative considered:** Emit the `GATTACA` strand specifically. Rejected — requires
arbitrarily privileging one strand; lexical minimality is simpler and deterministic.

## Risks / Trade-offs

- [Largest-size graph might not be exactly two cycles for malformed input] → The
  algorithm returns the first qualifying size; if none qualifies down to 2, it falls
  back to the smallest size (`2`) walk. Dataset guarantees a qualifying size exists, so
  this path is not exercised by valid Rosalind input; documented in code.
- [Minimal-rotation computation is O(n²) per cycle] → Read length ≤ 50 and cycles are
  small, so cost is negligible; clarity preferred over Booth's algorithm.
- [Light duplication with PCOV's problem/error types] → Accepted per Non-Goals;
  divergent vocabularies keep each capability's invariants explicit.
