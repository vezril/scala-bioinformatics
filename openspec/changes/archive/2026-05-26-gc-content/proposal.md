## Why

Spec 7 of the project brief introduces three things at once: a domain metric (GC content), an input format (FASTA, the lingua franca of bioinformatics file formats), and the framework's first algorithm that operates on a *collection* of records rather than a single sequence. Beyond satisfying the Rosalind problem, this change establishes the FASTA parser as a reusable foundation for every future Rosalind problem that begins with "Given: multiple DNA strings in FASTA format."

## What Changes

- **NEW** `FastaRecord` case class in `bio.domain` representing a labeled DNA sequence (`id: String`, `dna: DnaString`)
- **NEW** `FastaParseError` sealed ADT for parser failures: `MissingHeader`, `EmptyId`, `InvalidDna(id, cause)`
- **NEW** `FastaParser` in a new `bio.parsing` package — pure function `parse(input: String): Either[FastaParseError, List[FastaRecord]]`
- **NEW** `GcContent` sealed abstract case class in `bio.domain` — value in `[0.0, 100.0]` representing a percentage
- **NEW** `GcContentError` sealed ADT — `OutOfRange(value)`, `NotFinite`
- **NEW** `GcContent.of(dna: DnaString): GcContent` smart factory on the companion — pure, total (empty `DnaString` returns `GcContent(0.0)` by convention)
- **NEW** `HighestGc.find(records: List[FastaRecord]): Option[(FastaRecord, GcContent)]` in `bio.algorithms` — returns `None` for empty input; ties resolved to the first record encountered

The change also introduces `bio.parsing` as a new top-level package alongside `bio.domain` and `bio.algorithms`. Parsing is structural (string → records), distinct from biological algorithms.

## Capabilities

### New Capabilities

- `fasta-parsing`: Domain type `FastaRecord` (id + dna pair), error ADT `FastaParseError`, and pure parser `FastaParser.parse` accepting a string in FASTA format and returning a list of labeled records. Handles multi-line sequences, trailing whitespace, and empty input.
- `gc-content`: Domain type `GcContent` (validated percentage `[0, 100]`), error ADT `GcContentError`, smart factory `GcContent.of(dna: DnaString): GcContent`, and algorithm `HighestGc.find(records: List[FastaRecord]): Option[(FastaRecord, GcContent)]` to identify the record with the highest GC content.

### Modified Capabilities

## Impact

- New files in `bio.domain`: `FastaRecord.scala`, `FastaParseError.scala`, `GcContent.scala`, `GcContentError.scala`
- New file in new package `bio.parsing`: `FastaParser.scala`
- New file in `bio.algorithms`: `HighestGc.scala`
- New test files mirroring each
- No changes to existing code, no new dependencies
- All existing 113 tests continue passing
