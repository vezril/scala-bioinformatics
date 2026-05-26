## Why

The framework has validated DNA and RNA domain types but no way to move between them. RNA transcription — replacing every T in a DNA string with U to produce the corresponding RNA string — is the canonical first inter-molecule operation and the next Rosalind problem in the curriculum. Now that `DnaString`, `RnaString`, `DnaNucleotide`, and `RnaNucleotide` exist as distinct types, the transcription algorithm can be expressed with full compile-time safety.

## What Changes

- **NEW** `RnaTranscription` algorithm object in `bio.algorithms` with a pure function `transcribe(dna: DnaString): RnaString`
- The transcription replaces every `DnaNucleotide.T` with `RnaNucleotide.U`; all other bases (A, C, G) map to their RNA equivalents

No existing domain types or algorithms are modified.

## Capabilities

### New Capabilities

- `rna-transcription`: Pure transcription function mapping a `DnaString` to its corresponding `RnaString` by replacing T→U; dispatches on `DnaNucleotide` values via `DnaNucleotide.fromChar`

### Modified Capabilities

## Impact

- New file: `src/main/scala/bio/algorithms/RnaTranscription.scala`
- New test file: `src/test/scala/bio/algorithms/RnaTranscriptionSpec.scala`
- No changes to `build.sbt` or existing source files
- No new dependencies
