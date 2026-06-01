## Context

Rosalind LONG gives ≤50 DNA reads (each ≤1 kbp) drawn from the same strand of one
linear chromosome, **guaranteed** to admit a unique reconstruction by gluing pairs
of reads that overlap by *more than half their length*. We must return a shortest
superstring containing every read.

The general shortest-common-superstring problem is NP-hard, but the
more-than-half-length overlap guarantee collapses it to a tractable, deterministic
problem: each read has at most one successor (a read whose prefix it overlaps by
>half) and at most one predecessor, so the reads form a single chain (a Hamiltonian
path through a sparse overlap graph). Walking that chain and merging on the overlap
yields the unique shortest superstring.

The framework already models suffix/prefix overlaps in `bio.*.graph`
(`OverlapGraph`, `OverlapLength`, `OverlapEdge`) and validates DNA via `DnaString`
and FASTA input via `FastaFileReader`/`FastaParser`. This change reuses those and
adds the assembly layer alongside them.

## Goals / Non-Goals

**Goals:**
- A validated `GenomeAssemblyProblem` bundle with first-failure-wins construction.
- A pure, total-where-possible `GenomeAssembly.assemble` returning
  `Option[ShortestSuperstring]` (`None` only when no unique assembly exists).
- Exploit the >half-length guarantee for a deterministic O(n²·L) chain walk — no
  brute-force superstring search.
- Match the canonical sample output `ATTAGACCTGCCGGAATAC`.

**Non-Goals:**
- Solving the general (NP-hard) shortest-common-superstring problem.
- Handling reverse-complement strands or sequencing errors (explicitly out of scope
  per the problem statement).
- Streaming/large-genome assembly; the Rosalind caps (≤50 reads, ≤1 kbp) keep an
  O(n²) pairwise approach comfortable.

## Decisions

**1. Overlap rule: maximum k with suffix(a, k) == prefix(b, k) and k > min(|a|,|b|)/2.**
The problem defines a glue when reads overlap by *more than half their length*. For
an ordered pair `(a, b)` we take the largest `k` (k ≤ min length) where `a`'s
length-`k` suffix equals `b`'s length-`k` prefix, and accept the edge only if
`k * 2 > min(|a|, |b|)`. The >half threshold is what makes the successor of each
read unique, so we keep only the maximal qualifying overlap per pair.

*Alternative considered:* threshold against `|a|` only. Rejected — using
`min(|a|,|b|)` is symmetric and robust to the "approximately equal length" reads.

**2. Build a successor map, find the unique start, walk the chain.**
For each read compute its unique successor (the read it overlaps into by >half).
The start read is the one that is no read's successor; walk successors, at each step
appending only the non-overlapping tail of the next read. This is O(n²) pairs ×
O(L) per overlap check. Returns `None` if no unique start exists, if a read has more
than one successor/predecessor, or if the walk fails to consume all reads (e.g. a
cycle or split) — defensive against malformed input even though Rosalind guarantees
a clean chain.

*Alternative considered:* classic greedy "repeatedly merge the max-overlap pair".
Rejected — equivalent here but harder to reason about for the uniqueness/`None`
cases; the chain walk makes the guarantee explicit.

**3. Result type `ShortestSuperstring(value)` with `format`.**
Mirrors `ConsistentCharacterTable`/result-type convention: a thin wrapper whose
`format: String` returns the assembled sequence for the runner to print.

**4. Domain placement in `bio.*.graph`.**
Genome assembly is a Hamiltonian path over the overlap graph and sits naturally
beside the existing overlap machinery. Reads are reused as validated `DnaString`s,
so no new alphabet/sequence type is introduced.

**5. `GenomeAssemblyProblem` validation order: empty → too-many → over-long.**
`from(reads)` rejects an empty collection (`EmptyReadCollection`), then >50 reads
(`TooManyReads(count, max)`), then the first read exceeding 1000 bp
(`ReadTooLong(index, length, max)`), reporting the earliest failure. `sealed
abstract case class` prevents `apply`/`copy` from bypassing validation.

## Risks / Trade-offs

- **A degenerate input (cycle, branch, or no valid chain) →** `assemble` returns
  `None` rather than throwing or looping; the runner prints a no-solution message.
- **Two reads could tie for an overlap under a weaker rule →** the strict
  >half-length threshold plus taking the maximal `k` per pair preserves the
  problem's uniqueness guarantee, so ties do not arise for valid inputs.
- **O(n²·L) pairwise scan →** acceptable within the Rosalind caps; a prefix-index
  rewrite is possible later behind the same `assemble` signature if needed.
