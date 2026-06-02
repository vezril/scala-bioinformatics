## Why

Rosalind problem GREP ("Genome Assembly with Perfect Coverage and Repeats")
generalises the existing perfect-coverage assembly (PCOV): when a circular
chromosome contains repeats, its de Bruijn graph admits **more than one** complete
cycle, so a single read collection can be assembled into several distinct circular
strings. The framework already builds de Bruijn graphs and assembles the unique
perfect-coverage cycle; this change adds the ability to enumerate **all** complete
cycles, which is the foundation for reasoning about repeat-induced assembly
ambiguity.

## What Changes

- Add a validated `CompleteCycleProblem` domain bundle wrapping a collection of
  equal-length DNA reads (the (k+1)-mers, k ≤ 5), constructed through a smart
  constructor returning `Either[CompleteCycleProblemError, CompleteCycleProblem]`
  with first-failure-wins validation (empty, then more than 50 reads, then the first
  read shorter than 2, then the first read longer than 6, then the first
  inconsistent-length read).
- Add a `CompleteCycleProblemError` ADT with `EmptyKmerCollection`, `TooManyReads`,
  `KmerTooShort`, `KmerTooLong`, and `InconsistentLength` cases.
- Add a `CompleteCycleAssemblies` result type carrying the assembled circular
  strings, with a `format` method rendering them one per line.
- Add a pure, total `CompleteCycleAssembly.assemble` algorithm that builds the de
  Bruijn graph B_k (nodes = k-mers, edges = the (k+1)-mer reads **with
  multiplicity**) and enumerates every complete cycle (Eulerian circuit that uses
  each edge exactly as many times as its read occurs), assembling each into a
  circular string that begins with the first input read. Implemented with functional
  backtracking — no mutable state.
- Add a `GREPProb` IO runner that reads newline-separated reads from `grep_data.txt`,
  validates them, enumerates the complete cycles, and prints the assembled circular
  strings through `IO`; invalid input prints an error rather than throwing. Wire it
  into `Main`.

## Capabilities

### New Capabilities
- `genome-assembly-perfect-coverage-and-repeats`: Validates a collection of
  equal-length (k+1)-mer reads from a circular chromosome and enumerates every
  circular string assembled by a complete cycle in their de Bruijn graph B_k, each
  beginning with the first input read.

### Modified Capabilities
<!-- None — this is a purely additive capability; PCOV is unchanged. -->

## Impact

- New domain types in `bio.domain.graph`: `CompleteCycleProblem`,
  `CompleteCycleProblemError`, `CompleteCycleAssemblies`.
- New algorithm in `bio.algorithms.graph`: `CompleteCycleAssembly`.
- New IO runner `bio.problems.GREPProb`, wired into `bio.Main`.
- Reads `src/main/scala/resources/grep_data.txt` (already populated with the
  canonical sample).
- No changes to existing domain types, algorithms, or specs (PCOV and the de Bruijn
  graph builder are reused conceptually but not modified).
