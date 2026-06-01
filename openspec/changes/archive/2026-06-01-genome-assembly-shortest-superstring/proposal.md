## Why

Rosalind problem 62 ("Genome Assembly as Shortest Superstring", LONG) asks us to
reconstruct a single linear chromosome from a collection of overlapping DNA reads
by computing the shortest superstring that contains every read. This extends the
framework's existing overlap machinery (`OverlapGraph`, `OverlapLength`) from
*detecting* suffix/prefix overlaps to *assembling* reads into one contiguous
sequence, adding our first fragment-assembly capability.

## What Changes

- Add a `GenomeAssemblyProblemError` ADT enumerating the structural ways a read
  collection can be invalid (empty, too many reads, an over-long read).
- Add a validated `GenomeAssemblyProblem` bundle (`sealed abstract case class`)
  wrapping the reads, constructed only via a smart constructor `from(reads)` that
  enforces the Rosalind caps (≥1 read, ≤50 reads, each ≤1000 bp) with
  first-failure-wins ordering.
- Add a `ShortestSuperstring` result type that renders the assembled chromosome
  via `format`.
- Add a `GenomeAssembly.assemble(problem): Option[ShortestSuperstring]` algorithm
  that chains the reads through their >half-length overlaps (the uniqueness
  guarantee) and merges them into the shortest superstring; `None` when no unique
  assembly exists.
- Add a `LONGProb` IO runner that reads `long_data.txt` (FASTA), builds the
  problem, runs the assembly, and prints the superstring; wire it into `Main`.

## Capabilities

### New Capabilities
- `genome-assembly-shortest-superstring`: validating a collection of DNA reads and
  assembling them into the shortest superstring via more-than-half-length overlaps.

### Modified Capabilities
<!-- none — this is a purely additive capability -->

## Impact

- New domain types under `bio.domain.graph`: `GenomeAssemblyProblemError`,
  `GenomeAssemblyProblem`, `ShortestSuperstring`.
- New algorithm under `bio.algorithms.graph`: `GenomeAssembly`.
- New runner `bio.problems.LONGProb`; one line changed in `bio.Main`.
- Reuses existing `DnaString` (validated reads) and `FastaFileReader`/`FastaParser`
  for input. No existing behavior changes.
