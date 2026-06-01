## Context

Rosalind DBRU constructs the de Bruijn graph `B_k` from a set `S` of (k+1)-mers:

- Let `S^rc` be the set of reverse complements of the elements of `S`.
- Nodes are the k-mers appearing as a prefix or suffix of some (k+1)-mer in
  `S ∪ S^rc`.
- For each (k+1)-mer `r` in `S ∪ S^rc`, add the directed edge
  `(r[1:k], r[2:k+1])` — i.e. `(prefix of length k, suffix of length k)`.
- Return the adjacency list.

The framework already provides:

- `DnaString` (validated, value class, `from`/`unsafeFrom`);
- `DnaReverseComplement.reverseComplement(dna): DnaString` — total, reuses the
  `DnaNucleotide` complement (REVC);
- the `bio.*.graph` packages with `OverlapEdge(from, to)` as a directed-edge
  precedent and `OverlapGraph` as an adjacency-list algorithm precedent;
- `GenomeAssemblyProblem` / `GenomeAssemblyProblemError` (LONG) as the precedent for
  a validated collection-of-`DnaString` bundle.

DBRU is therefore a *composition*: dedupe the inputs into a set, union with reverse
complements, split each (k+1)-mer into its prefix/suffix edge, then dedupe and sort.

## Goals / Non-Goals

**Goals:**
- A validated `DeBruijnGraphProblem(kmers)` with a first-failure-wins smart
  constructor returning `Either[DeBruijnGraphProblemError, _]`.
- A pure, total `DeBruijnGraphConstruction.construct(problem): DeBruijnGraph` that
  reuses `DnaReverseComplement` and emits a deterministic, sorted, de-duplicated
  adjacency list.
- A `DeBruijnGraph` result type rendering each edge as `(from, to)` via `format`.
- Match the canonical Rosalind sample output exactly.

**Non-Goals:**
- No de Bruijn *traversal* / Eulerian-path assembly (a later capability if needed).
- No support for mixed-length inputs — equal length is required and validated.
- No FASTA parsing: the DBRU dataset is one (k+1)-mer per line.

## Decisions

### Model the inputs as a validated collection of `DnaString`, mirroring LONG
`DeBruijnGraphProblem` is a `sealed abstract case class(kmers: Vector[DnaString])`
with a smart constructor, exactly like `GenomeAssemblyProblem`. DNA-character
validity is handled upstream by `DnaString`, so the bundle only validates the
*shape* of the collection. *Alternative considered:* accepting raw `String`s and
validating chars here — rejected; `DnaString` already owns that invariant.

### Validation cases and order
`DeBruijnGraphProblemError` carries: `EmptyKmerCollection` (case object),
`TooManyKmers(count, max)` (cap 1000), `KmerTooShort(index, length, min)` (min 2 —
a (k+1)-mer needs `k ≥ 1` to split into a prefix and suffix), `KmerTooLong(index,
length, max)` (cap 50), and `InconsistentLength(index, length, expected)` (all
(k+1)-mers must share one length). First-failure-wins order:
empty → too-many → (per-k-mer, in index order) too-short → too-long →
inconsistent-length (compared against the first k-mer's length). *Alternative
considered:* omitting `InconsistentLength` — rejected; equal length is the defining
precondition of a de Bruijn graph of fixed order, so unequal input is a real error,
not something to silently tolerate.

### Reuse `DnaReverseComplement` for `S^rc`
The reverse complements come from the existing total `reverseComplement`; no
complement logic is re-implemented. Because the inputs are validated `DnaString`s,
the call is total. *Alternative considered:* inlining a complement map — rejected as
non-DRY; REVC is the single source of truth for complementation.

### Work on a `Set[String]`, so de-duplication is structural
The (k+1)-mer values are collected into a `Set[String]`, unioned with the set of
their reverse complements; iterating that set yields `S ∪ S^rc` with duplicates
(including duplicated inputs and reverse-complement palindromes such as `CATG`)
collapsed automatically. Each (k+1)-mer `r` maps to `DeBruijnEdge(r.dropRight(1),
r.tail)` — `dropRight(1)` is the length-k prefix `r[1:k]`, `tail` is the length-k
suffix `r[2:k+1]`. Since the edge is a bijection with `r`, the edges are already
unique; they are then sorted lexicographically by `(from, to)` for deterministic,
Rosalind-matching output.

### `DeBruijnEdge` and `DeBruijnGraph`
`DeBruijnEdge(from: String, to: String)` is a plain case class (no invariant beyond
holding two strings), mirroring `OverlapEdge`. `DeBruijnGraph(edges:
Vector[DeBruijnEdge])` carries the sorted edges with
`format: String = edges.map(e => s"(${e.from}, ${e.to})").mkString("\n")`.

## Risks / Trade-offs

- [Reverse-complement palindromes could double-count] → Working on a `Set[String]`
  means an element equal to its own reverse complement (e.g. `CATG`) is added once,
  not twice; structural de-duplication removes the risk.
- [Output order matters for the sample, though Rosalind permits any order] →
  Sorting lexicographically by `(from, to)` makes the output deterministic and a
  strict refinement of the spec, so tests can assert exact equality.
- [Unequal-length inputs are rejected rather than truncated] → Intentional: a fixed
  graph order requires uniform (k+1)-mer length; tolerating mixed lengths would
  produce a meaningless graph.
