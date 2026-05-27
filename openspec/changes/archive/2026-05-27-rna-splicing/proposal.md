## Why

Rosalind problem 21 ("RNA Splicing") asks us to take a DNA string `s` and a collection of intron substrings, remove each intron occurrence from `s`, transcribe the resulting (intron-free) DNA to RNA, and translate the RNA to a protein string. This is the framework's first algorithm that *composes* three existing primitives — `RnaTranscription` and `RnaTranslation` are already in the framework; the fresh step is **intron splicing** (substring removal).

Splitting the work into a reusable splice primitive (`IntronSplicing.splice`, in `bio.algorithms.nucleic`) plus a composed pipeline (`RnaSplicing.transcribeAndTranslate`, in `bio.algorithms.protein`) gives us two distinct entry points: one for callers who only need the spliced DNA, one for callers who want the full DNA → RNA → protein path. The split mirrors the existing precedent of `GcContent` (primitive) being reused by `HighestGc` (composite).

## What Changes

### New primitive: intron splicing
- Add `IntronSplicing.splice(source: DnaString, introns: Vector[DnaString]): DnaString` in `bio.algorithms.nucleic` — removes every occurrence of each intron from `source` in input order. Total — no validation at the algorithm boundary because each `DnaString` is already alphabet-validated and the function handles empty inputs trivially (empty introns are no-ops; an intron larger than the source produces no match).

### New composite: full RNA-splicing pipeline
- Add `RnaSplicingProblem` in `bio.domain.protein` as a `sealed abstract case class` validated parameter bundle wrapping `source: DnaString` and `introns: Vector[DnaString]`. The smart constructor rejects empty introns (the only meaningful cross-input invariant we can defend against — every other constraint is already enforced by the per-element types).
- Add `RnaSplicingProblemError` sealed ADT with case `EmptyIntron(index: Int)`.
- Add `RnaSplicing.transcribeAndTranslate(problem: RnaSplicingProblem): Either[TranslationError, ProteinString]` in `bio.algorithms.protein` — the full pipeline. Splice via `IntronSplicing.splice`, transcribe via `RnaTranscription.transcribe`, then translate via `RnaTranslation.translate`. `TranslationError` is the existing error type reused from `bio.domain.protein`.

### No FASTA file reader for this spec
- Callers parse FASTA via the existing `FastaFileReader.read` / `FastaParser.parse` and destructure `records[0]` → source, `records.tail.map(_.dna)` → introns themselves. No new file-ingestion surface needed.

## Capabilities

### New Capabilities
- `rna-splicing`: The `IntronSplicing.splice` primitive (DNA → DNA, removes intron substrings), the `RnaSplicingProblem` validated parameter bundle and its error ADT, and the `RnaSplicing.transcribeAndTranslate` composite algorithm running the full splice → transcribe → translate pipeline to produce a `ProteinString`.

### Modified Capabilities
<!-- none — purely additive; reuses RnaTranscription, RnaTranslation, DnaString, RnaString, ProteinString, TranslationError unchanged -->

## Impact

- New files in `bio.algorithms.nucleic`: `IntronSplicing.scala`.
- New files in `bio.domain.protein`: `RnaSplicingProblem.scala`, `RnaSplicingProblemError.scala`.
- New files in `bio.algorithms.protein`: `RnaSplicing.scala`.
- New test suites: `IntronSplicingSpec`, `RnaSplicingProblemSpec`, `RnaSplicingProblemErrorSpec`, `RnaSplicingSpec`.
- No new SBT dependencies.
- No breaking changes — purely additive. `RnaTranscription`, `RnaTranslation`, `DnaString`, `RnaString`, `ProteinString`, and `TranslationError` are all reused as-is.
