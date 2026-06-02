## Context

The project already assembles the *unique* perfect-coverage cyclic superstring in
`bio.algorithms.graph.PerfectCoverageAssembly` (Rosalind PCOV): it de-duplicates
reads into a `Set`, treats the de Bruijn graph as a node bijection
(`Map[String, String]`), and walks the single simple cycle.

Rosalind GREP ("Genome Assembly with Perfect Coverage and Repeats") removes the
"simple cycle" guarantee. With repeats, a k-mer node can have out-degree > 1 and a
(k+1)-mer read can occur multiple times, so the de Bruijn graph B_k is an Eulerian
*multigraph* admitting several **complete cycles** — Eulerian circuits that traverse
each edge exactly as many times as its read occurs. Each complete cycle assembles to
a circular string; GREP asks for **all** of them, each beginning with the first input
(k+1)-mer.

Constraints: reads are (k+1)-mers with k ≤ 5 (so read length 2..6); the chromosome
length is ≤ 50, and since a circular assembly's length equals the number of edges,
there are at most 50 reads.

## Goals / Non-Goals

**Goals:**
- A validated `CompleteCycleProblem` bundle (equal-length reads, length 2..6, at most
  50, non-empty), constructed only via a smart constructor with first-failure-wins
  validation.
- A pure, total `CompleteCycleAssembly.assemble` enumerating every complete cycle and
  assembling each into a circular string beginning with the first input read.
- A `CompleteCycleAssemblies` result type with a `format` rendering one string per
  line.
- A `GREPProb` IO runner reading `grep_data.txt`, printing the strings or a
  descriptive error.

**Non-Goals:**
- No read-error correction, no reverse-complement strand handling (reads are stated to
  come from the same strand).
- No change to PCOV or the existing de Bruijn graph builder.
- No imperative DP — this is graph enumeration, implemented with functional
  recursion/backtracking (the alignment-family `var`/`while` exception does not apply
  here).

## Decisions

### Decision: Model edges as `(from, to)` with multiplicity; preserve repeats

For a read `r` of length `L = k + 1`, the de Bruijn edge is
`from = r.dropRight(1)` (length k) `-> to = r.tail` (length k). Two reads produce the
**same** edge iff they are the **same (k+1)-mer** (because `r = from ++ to.last`), so
an edge `(from, to)` uniquely identifies its read. Repeats are preserved by counting:
the available edges are a multiset `Map[(from, to), Int]` keyed by edge with its read
multiplicity. This is exactly the "same number of times as it appears in the reads"
clause of the complete-cycle definition. (Contrast PCOV, which de-duplicates into a
`Set` and would collapse repeats.)

**Alternative considered**: keeping a `List` of edge instances. Rejected — a multiset
keyed by `(from, to)` makes "try each distinct continuation once" natural and avoids
generating duplicate orderings of identical edges.

### Decision: Enumerate complete cycles by functional backtracking

Fix the first edge to the first input read `e1` (so every result begins with it);
let `start = e1.from`. Recursively extend a partial path from the current node,
consuming one available edge at a time:

```
extend(node, remaining): List[List[Edge]] =
  if (remaining is empty)
    if (node == start) List(Nil) else Nil        // closed circuit vs dead end
  else
    distinct edges e with e.from == node and remaining(e) > 0, flatMap { e =>
      extend(e.to, remaining decremented at e).map(e :: _)
    }
```

The full set of circuits is `extend(e1.to, allEdges − e1).map(e1 :: _)`. Backtracking
naturally discards partial paths that dead-end before consuming all edges, and the
`node == start` guard enforces closure. Pure, immutable, no mutable state.

### Decision: Assemble each circuit; the circular string begins with the first read

For a circuit `e1, e2, …, eE` (E = number of reads), build the linear superstring
`e1.read ++ e2.to.last ++ … ++ eE.to.last` (length `k + E`) and take the first `E`
characters — the trailing `k` characters wrap around to the start of a circular
string of length `E`. Because `e1` is the first input read, every assembled string
begins with it.

### Decision: De-duplicate and sort the assembled strings

Distinct circuits can, in pathological repeat structures, assemble to the same
circular string, so the results are de-duplicated. The surviving strings are sorted
lexicographically for deterministic output. Rosalind accepts the strings **in any
order**, so tests compare them as a set; the canonical sample yields exactly six
distinct strings.

### Decision: Domain & result types follow the graph-package conventions

- `CompleteCycleProblemError`: `sealed trait` with `EmptyKmerCollection` (case
  object), `TooManyReads(count, max)`, `KmerTooShort(index, length, min)`,
  `KmerTooLong(index, length, max)`, `InconsistentLength(index, length, expected)`.
- `CompleteCycleProblem`: `sealed abstract case class CompleteCycleProblem(kmers: Vector[DnaString])`;
  `MinLength = 2`, `MaxLength = 6`, `MaxReads = 50`; `from` validates empty →
  too-many → first bad read (too-short → too-long → inconsistent vs `kmers.head`
  length); constructed via anonymous subclass. No public `apply`/`copy`.
- `CompleteCycleAssemblies`: `final case class CompleteCycleAssemblies(strings: Vector[String])`
  with `def format: String = strings.mkString("\n")`.

### Decision: Runner reads newline-separated reads like PCOV

`GREPProb.solve(): IO[Unit]` reads `grep_data.txt`, folds the lines into
`Either[String, Vector[DnaString]]`, validates into `CompleteCycleProblem`, runs
`assemble`, and prints `result.format`. On any read/validation error it prints a
descriptive message rather than throwing — mirroring `PCOVProb`.

## Risks / Trade-offs

- **[Combinatorial blow-up]** → Eulerian-circuit enumeration is worst-case
  exponential, but the GREP constraints (≤ 50 edges, k ≤ 5) keep the canonical and
  test inputs tiny (the sample has 17 edges and 6 results). No pruning beyond
  backtracking is needed.
- **[Multiple co-optimal outputs / ordering]** → Rosalind accepts any order, so tests
  assert the result **set** (`theSameElementsAs`) plus that every string begins with
  the first input read; the algorithm sorts for a stable rendering.
- **[Repeat collapse]** → using a `Set` (as PCOV does) would silently drop repeated
  edges and lose complete cycles; the multiset keying is the explicit guard against
  this, covered by a repeat-bearing test.
