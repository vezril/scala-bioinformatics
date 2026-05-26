## Why

The current domain model represents nucleotide alphabets implicitly — as hardcoded `Set[Char]` and raw `Char` literal pattern matches. This means the compiler cannot enforce alphabet correctness, and DNA and RNA sequences share no type-level distinction. Introducing explicit nucleotide ADTs makes invalid alphabets a compile error and establishes the type boundary needed for future RNA-specific algorithms.

## What Changes

- **NEW** `DnaNucleotide` sealed ADT (`A`, `C`, `G`, `T`) with `validChars` and `fromChar` on its companion
- **NEW** `RnaNucleotide` sealed ADT (`A`, `C`, `G`, `U`) with `validChars` and `fromChar` on its companion
- **NEW** `RnaString` validated domain type (mirrors `DnaString`, uses `RnaNucleotide` alphabet)
- **NEW** `RnaNucleotideCounts` (`a`, `c`, `g`, `u`) with `format: String`
- **BREAKING** `DnaError` renamed to `SequenceError` — shared validation error type for both `DnaString` and `RnaString`
- **BREAKING** `NucleotideCounts` renamed to `DnaNucleotideCounts`
- `DnaString` updated to derive `ValidChars` from `DnaNucleotide.validChars` instead of a hardcoded literal set
- `DnaNucleotides.count` updated to pattern-match on `DnaNucleotide` values rather than raw `Char` literals

Transcription algorithms (DNA → RNA, RNA → DNA) are explicitly **out of scope** — they are a follow-on change once these domain types exist.

## Capabilities

### New Capabilities

- `dna-nucleotide`: `DnaNucleotide` sealed ADT with companion helpers; drives `DnaString` validation
- `rna-nucleotide`: `RnaNucleotide` sealed ADT with companion helpers; drives `RnaString` validation
- `rna-sequence`: `RnaString` validated sequence type and `RnaNucleotideCounts` result type

### Modified Capabilities

- `dna-nucleotides`: `DnaNucleotides.count` now dispatches on `DnaNucleotide` values; return type renamed `DnaNucleotideCounts`
- `project-setup`: `DnaError` → `SequenceError` rename affects all existing error handling; `NucleotideCounts` → `DnaNucleotideCounts` rename affects all existing count usage

## Impact

- **Breaking**: any code referencing `DnaError`, `NucleotideCounts` must be updated to `SequenceError`, `DnaNucleotideCounts`
- All existing tests updated to reflect renamed types
- No new external dependencies
- No changes to `build.sbt` or SBT structure
