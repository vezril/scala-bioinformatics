## Context

Rosalind PCOV reconstructs a circular chromosome from a perfect-coverage set `S`
of equal-length reads (the (k+1)-mers of an unknown circular DNA string):

- Build the de Bruijn graph `B_k`: nodes are the length-`(L-1)` k-mers; each read
  `r` of length `L` contributes a directed edge `(r[1:L-1], r[2:L]) =
  (prefix of length L-1, suffix of length L-1)`.
- The dataset guarantees `B_k` is exactly **one simple cycle**: every node has
  in-degree = out-degree = 1.
- Walking that cycle and emitting one symbol per node recovers the original
  circular string, whose length equals the number of nodes.

The framework already provides:

- `DnaString` (validated value class, `from`/`unsafeFrom`);
- the `bio.*.graph` packages, with DBRU (`DeBruijnGraphConstruction`,
  `DeBruijnEdge`) as the immediate precedent for splitting a read into its
  prefix/suffix k-mers;
- `GenomeAssemblyProblem` / `GenomeAssemblyProblemError` (LONG) and
  `DeBruijnGraphProblem` / `DeBruijnGraphProblemError` (DBRU) as precedents for a
  validated collection-of-`DnaString` bundle.

PCOV differs from DBRU in two ways: it uses only `S` (no reverse complements), and
it **traverses** the graph rather than emitting its adjacency list.

## Goals / Non-Goals

**Goals:**
- A validated `PerfectCoverageProblem(kmers)` with a first-failure-wins smart
  constructor returning `Either[PerfectCoverageProblemError, _]`.
- A pure, total `PerfectCoverageAssembly.assemble(problem): CyclicSuperstring`
  that walks the single de Bruijn cycle deterministically.
- A `CyclicSuperstring` result type rendering the bare sequence via `format`.
- Reconstruct the canonical Rosalind sample chromosome (up to rotation).

**Non-Goals:**
- No general Eulerian-path / multi-contig assembly: the input is guaranteed to be
  a single simple cycle, and the algorithm relies on that.
- No reverse-complement handling (PCOV reads all derive from one strand).
- No support for mixed-length inputs — equal length is required and validated.
- No FASTA parsing: the PCOV dataset is one read per line.

## Decisions

### Model the inputs as a validated collection of `DnaString`, mirroring DBRU
`PerfectCoverageProblem` is a `sealed abstract case class(kmers: Vector[DnaString])`
with a smart constructor, exactly like `DeBruijnGraphProblem`. DNA-character
validity is owned upstream by `DnaString`, so the bundle validates only the
*shape* of the collection. *Alternative considered:* raw `String`s validated here
— rejected; `DnaString` already owns that invariant.

### Validation cases and order
`PerfectCoverageProblemError` carries: `EmptyKmerCollection` (case object),
`KmerTooShort(index, length, min)` (min 2 — a read needs `L ≥ 2` to split into a
length-`(L-1)` prefix and suffix), `KmerTooLong(index, length, max)` (cap 50, the
Rosalind `k ≤ 50` bound), and `InconsistentLength(index, length, expected)` (all
reads must share one length). First-failure-wins order: empty → (per-k-mer, in
index order) too-short → too-long → inconsistent-length (compared against the
first read's length). *Alternative considered:* a `TooManyKmers` cap as in DBRU —
omitted because Rosalind states no read-count limit for PCOV, so imposing one
would be an ungrounded invariant. *Alternative considered:* validating the
single-cycle precondition in the bundle — rejected; that property requires graph
analysis and is guaranteed by the dataset, so it belongs to the algorithm's
documented precondition, not the shape validator.

### Build the de Bruijn adjacency as a `Map[String, String]`
Each distinct read `r` yields a node pair `(r.dropRight(1) -> r.tail)` — the
length-`(L-1)` prefix maps to the length-`(L-1)` suffix. Collecting reads into a
`Set[String]` first makes duplicate reads collapse structurally (the dataset may
repeat reads). Because the graph is a single simple cycle, each prefix is unique,
so a `Map[String, String]` is a faithful, total adjacency (a bijection on nodes).
*Alternative considered:* reusing `DeBruijnEdge` plus a sort — unnecessary here;
the cycle walk needs O(1) successor lookup, which a `Map` gives directly.

### Walk the cycle from the lexicographically smallest node, emitting one symbol per node
The set of nodes is the set of map keys (= set of values, since it is a cycle).
Starting from the lexicographically smallest node guarantees a **deterministic**
output. Following successors for `n` steps (where `n` is the node count) and
emitting the first symbol of each visited node yields a string of length `n` — the
minimal-length cyclic superstring. *Why first-symbol-per-node:* consecutive nodes
overlap in `L-2` symbols, so each step contributes exactly one new symbol;
collecting first symbols around the full cycle reproduces the circular string.
*Alternative considered:* starting from the first input read's prefix — rejected;
it is not deterministic under input reordering. The deterministic start makes the
output a fixed rotation of the chromosome; Rosalind accepts any rotation, so the
sample `GATTACA` and our `ACAGATT` denote the same necklace.

### `CyclicSuperstring`
`CyclicSuperstring(value: String)` is a plain case class (no invariant beyond
holding the reconstructed sequence) with `format: String = value` — Rosalind's
PCOV output is the bare sequence with no parentheses.

## Risks / Trade-offs

- [Output rotation differs from the literal Rosalind sample] → The result is a
  circular string; Rosalind accepts any rotation. Choosing the lexicographically
  smallest start node makes output deterministic and testable, and the sample is
  verified to be a rotation of it.
- [Algorithm assumes a single simple cycle] → This is the dataset's stated
  guarantee. The walk terminates after `n` node-visits regardless, so a
  non-conforming input cannot loop forever; it would simply yield a partial
  result. Documented as a precondition rather than re-validated.
- [Duplicate reads] → Collecting reads into a `Set[String]` before building the
  adjacency collapses duplicates structurally, so they cannot distort the cycle.
