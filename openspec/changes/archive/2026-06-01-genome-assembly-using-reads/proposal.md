## Why

Rosalind problem 67 ("Genome Assembly Using Reads", GASM) extends perfect-coverage
assembly to the realistic case where reads come from both strands of a circular
chromosome. Unlike PCOV, the de Bruijn graph over the reads and their reverse
complements is not a single cycle; instead, for the correct k-mer size it consists
of exactly two directed cycles that are reverse complements of one another. The
framework needs a capability to recover the cyclic chromosome from such a read set.

## What Changes

- Add a validated domain bundle `GenomeAssemblyReadsProblem` (with companion error
  ADT `GenomeAssemblyReadsProblemError`) holding equal-length DNA reads, mirroring
  the PCOV validation rules (non-empty, each read length 2..50, all reads the same
  length, no read-count cap).
- Add a pure algorithm `bio.algorithms.graph.GenomeAssemblyReads.assemble` that
  augments the reads with their reverse complements, searches descending k-mer sizes
  for the de Bruijn graph that decomposes into exactly two reverse-complement
  directed cycles, and emits a deterministic cyclic superstring containing every read
  or its reverse complement.
- Reuse the existing `CyclicSuperstring` result type (introduced for PCOV) for output.
- Add an `IO` runner `bio.problems.GASMProb` reading `gasm_data.txt` and wire it into
  `Main`.

## Capabilities

### New Capabilities
- `genome-assembly-using-reads`: validate an equal-length DNA read collection and
  assemble the circular chromosome by finding the de Bruijn graph (over reads plus
  reverse complements) that forms exactly two reverse-complement cycles, returning a
  cyclic superstring containing every read or its reverse complement.

### Modified Capabilities
<!-- None: CyclicSuperstring is reused as-is; no existing requirements change. -->

## Impact

- New domain types: `bio.domain.graph.GenomeAssemblyReadsProblem`,
  `bio.domain.graph.GenomeAssemblyReadsProblemError`.
- New algorithm: `bio.algorithms.graph.GenomeAssemblyReads`.
- Reused: `bio.domain.graph.CyclicSuperstring`,
  `bio.algorithms.nucleic.DnaReverseComplement`, `bio.domain.nucleic.DnaString`.
- New runner: `bio.problems.GASMProb`; `Main.scala` switches the active runner.
- New data file already present: `src/main/scala/resources/gasm_data.txt`.
