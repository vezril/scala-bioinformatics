## Why

Rosalind problem 68 ("Assessing Assembly Quality with N50 and N75", ASMQ) introduces
the N-statistic family used to summarize the contiguity of a genome assembly. Unlike the
prior assembly problems (DBRU/PCOV/GASM), this is not a graph reconstruction — it is a
pure length-distribution metric over a collection of contigs. The framework needs a
capability to validate a contig collection and compute its N50 and N75.

## What Changes

- Add a validated domain bundle `ContigCollection` (with companion error ADT
  `ContigCollectionError`) holding a collection of DNA contigs, enforcing the Rosalind
  bounds: non-empty, at most 1000 contigs, each contig non-empty, combined length at
  most 50 kbp.
- Add a result type `AssemblyQuality(n50, n75)` exposing `format: String` for the
  space-separated Rosalind output.
- Add a pure algorithm `bio.algorithms.assembly.AssemblyStatistics` computing the
  N-statistic `NXX` for any percentile and the convenience `assess` producing N50/N75.
- Add an `IO` runner `bio.problems.ASMQProb` reading `asmq_data.txt` and wire it into
  `Main`.

## Capabilities

### New Capabilities
- `assessing-assembly-quality`: validate a DNA contig collection and compute its
  N-statistics (N50 and N75) — the maximum length `L` such that contigs of length at
  least `L` account for at least the target percentage of the total assembled length.

### Modified Capabilities
<!-- None: no existing requirements change. -->

## Impact

- New package `bio.domain.assembly`: `ContigCollection`, `ContigCollectionError`,
  `AssemblyQuality`.
- New package `bio.algorithms.assembly`: `AssemblyStatistics`.
- Reused: `bio.domain.nucleic.DnaString`.
- New runner: `bio.problems.ASMQProb`; `Main.scala` switches the active runner.
- New data file already present: `src/main/scala/resources/asmq_data.txt`.
