## Why

Rosalind's "Genome Assembly with Perfect Coverage" (PCOV) reconstructs a circular
chromosome from a complete, error-free set of equal-length k-mer reads whose de
Bruijn graph forms exactly one simple cycle. It is the natural successor to
"Constructing a De Bruijn Graph" (DBRU): where DBRU *builds* the graph, PCOV
*traverses* it to recover the original cyclic superstring. The framework already
has the pieces it needs — a validated `DnaString`, the `bio.*.graph` package, and
the de Bruijn prefix/suffix edge idiom from DBRU — so PCOV composes these into a
single cycle-walk.

## What Changes

- Add a `PerfectCoverageProblemError` ADT capturing the ways the k-mer collection
  can be invalid (empty, a k-mer too short to split, a k-mer too long, or k-mers of
  unequal length).
- Add a validated, invariant-bearing `PerfectCoverageProblem` wrapping the
  collection of read `DnaString`s, constructed only via a smart constructor
  `from(kmers)` returning `Either`.
- Add a `CyclicSuperstring` result type holding the reconstructed circular
  chromosome with a `format` rendering it as the bare sequence (matching Rosalind).
- Add a `PerfectCoverageAssembly.assemble` algorithm: build the de Bruijn graph
  over the reads (nodes are the length-`(L-1)` prefixes/suffixes), then walk the
  unique simple cycle starting from the lexicographically smallest node, emitting
  one symbol per node to recover the minimal-length cyclic superstring.
- Add a `PCOVProb` IO runner reading `resources/pcov_data.txt` (one k-mer per line),
  assembling the chromosome, and printing it; wire it into `Main`.

## Capabilities

### New Capabilities
- `genome-assembly-with-perfect-coverage`: reconstructing the minimal-length cyclic
  superstring (circular chromosome) from a perfect-coverage set of equal-length
  k-mer reads by walking the single simple cycle of their de Bruijn graph.

### Modified Capabilities
<!-- None: this is a new capability that reuses existing types without changing their requirements. -->

## Impact

- New domain types under `bio.domain.graph` (`PerfectCoverageProblemError`,
  `PerfectCoverageProblem`, `CyclicSuperstring`).
- New algorithm `bio.algorithms.graph.PerfectCoverageAssembly`.
- New IO runner `bio.problems.PCOVProb`; one-line wiring change in `bio.Main`.
- Reads the existing `src/main/scala/resources/pcov_data.txt` dataset.
- No changes to existing types, APIs, or specs.
