## Why

Rosalind's "k-Mer Composition" (KMER) asks for the frequency of every possible
length-`k` DNA word inside a sequence, reported in the lexicographic order of
those words. This is a foundational primitive for assembly and sequence-comparison
work, and the framework already has the pieces it needs: a validated `DnaString`,
FASTA parsing, and the just-completed `EnumerateKmers` (LEXF) that produces the
ordered k-mers. KMER composes those into a single counting pass.

## What Changes

- Add a `KmerCompositionProblemError` ADT capturing the ways the `k` parameter can
  be invalid (non-positive, exceeds the cap).
- Add a validated, invariant-bearing `KmerCompositionProblem` pairing a `DnaString`
  with a word length `k`, constructed only via a smart constructor `from(dna, k)`
  returning `Either`.
- Add a `KmerComposition` result type holding the per-k-mer counts as an ordered
  `Vector[Int]`, with a `format` that renders them space-separated (Rosalind style).
- Add a `ComposeKmers.compose` algorithm that slides a length-`k` window over the
  DNA string, tallies occurrences, and emits one count per k-mer in the
  lexicographic order produced by reusing `EnumerateKmers` over the DNA alphabet.
- Add a `KMERProb` IO runner reading `resources/kmer_data.txt` (FASTA), composing
  the 4-mer composition, and printing the result; wire it into `Main`.

## Capabilities

### New Capabilities
- `k-mer-composition`: counting every length-`k` DNA word in a sequence and
  reporting the counts in lexicographic k-mer order.

### Modified Capabilities
<!-- None: this is a new capability that reuses existing types without changing their requirements. -->

## Impact

- New domain types under `bio.domain.combinatorics`
  (`KmerCompositionProblemError`, `KmerCompositionProblem`, `KmerComposition`).
- New algorithm `bio.algorithms.combinatorics.ComposeKmers`, reusing
  `EnumerateKmers` / `KmerEnumerationProblem` from the LEXF capability.
- New IO runner `bio.problems.KMERProb`; one-line wiring change in `bio.Main`.
- Reads the existing `src/main/scala/resources/kmer_data.txt` dataset.
- No changes to existing types, APIs, or specs.
