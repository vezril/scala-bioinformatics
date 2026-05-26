## Purpose
Define the FASTA-format parsing capability: a labeled-DNA record type (`FastaRecord`), a sealed parser-error ADT (`FastaParseError`), and a pure parser (`FastaParser.parse`) that turns FASTA-format strings into a list of validated records. Parsing is structural (no file I/O) and serves as the entry point for every downstream algorithm that operates on collections of labeled sequences.

## Requirements

### Requirement: FastaRecord pairs an id with a validated DnaString
The system SHALL provide a `final case class FastaRecord(id: String, dna: DnaString)` representing one labeled DNA sequence parsed from FASTA format. `dna` is a validated `DnaString` (see `project-setup` capability). The case class has a public constructor — the parser is the canonical validation point for `id`.

#### Scenario: FastaRecord exposes id and dna fields
- **WHEN** `FastaRecord("Rosalind_0001", DnaString.from("ACGT").toOption.get)` is constructed
- **THEN** `record.id == "Rosalind_0001"` and `record.dna.value == "ACGT"`

### Requirement: FastaParseError is a sealed ADT of parser failures
The system SHALL provide a `sealed trait FastaParseError` with at least the following cases: `MissingHeader` (sequence content appeared before any `>` header line), `EmptyId` (a `>` header line had no id after `>`), and `InvalidDna(id: String, cause: SequenceError)` (the assembled DNA sequence for record `id` failed `DnaString.from` validation).

#### Scenario: All cases extend FastaParseError
- **WHEN** the `FastaParseError` sealed trait is inspected
- **THEN** `MissingHeader`, `EmptyId`, and `InvalidDna(_, _)` are all subtypes

### Requirement: FastaParser.parse handles a single valid record
The system SHALL provide `FastaParser.parse(input: String): Either[FastaParseError, List[FastaRecord]]`. For input consisting of a single header line followed by one or more sequence lines (all valid DNA), `parse` SHALL return `Right` with a singleton list containing the record.

#### Scenario: Single-record input
- **WHEN** `parse(">Rosalind_0001\nACGTACGT")` is called
- **THEN** the result is `Right(List(FastaRecord("Rosalind_0001", <DnaString of "ACGTACGT">)))`

#### Scenario: Multi-line sequence is concatenated
- **WHEN** `parse(">Rosalind_0001\nACGT\nACGT")` is called
- **THEN** the result is `Right(List(FastaRecord("Rosalind_0001", <DnaString of "ACGTACGT">)))`

### Requirement: FastaParser.parse handles multiple records (Rosalind sample)
The system SHALL parse multiple records separated by `>` header lines. Records are returned in input order.

#### Scenario: Rosalind sample parses to three records in order
- **WHEN** `parse` is called with the canonical Rosalind sample input (`>Rosalind_6404`, `>Rosalind_5959`, `>Rosalind_0808` each followed by their respective DNA sequences)
- **THEN** the result is `Right` containing three `FastaRecord`s with ids `Rosalind_6404`, `Rosalind_5959`, `Rosalind_0808` in that order

#### Scenario: Trailing whitespace on header is trimmed
- **WHEN** `parse(">Rosalind_0808 \nACGT")` is called (note trailing space on header)
- **THEN** the resulting record has `id == "Rosalind_0808"` (whitespace stripped)

### Requirement: FastaParser.parse handles empty input
The system SHALL return `Right(List.empty)` for empty or whitespace-only input — an empty FASTA is structurally valid.

#### Scenario: Empty string parses to empty list
- **WHEN** `parse("")` is called
- **THEN** the result is `Right(List.empty)`

#### Scenario: Whitespace-only input parses to empty list
- **WHEN** `parse("   \n\n  ")` is called
- **THEN** the result is `Right(List.empty)`

### Requirement: FastaParser.parse rejects sequence content before any header
The system SHALL return `Left(FastaParseError.MissingHeader)` when the input contains non-empty non-`>` lines before the first header line.

#### Scenario: Sequence before header is rejected
- **WHEN** `parse("ACGT\n>Rosalind_0001\nACGT")` is called
- **THEN** the result is `Left(FastaParseError.MissingHeader)`

### Requirement: FastaParser.parse rejects empty id headers
The system SHALL return `Left(FastaParseError.EmptyId)` when a `>` line has no id (e.g., `>` alone, or `>` followed only by whitespace).

#### Scenario: Bare > header is rejected
- **WHEN** `parse(">\nACGT")` is called
- **THEN** the result is `Left(FastaParseError.EmptyId)`

#### Scenario: > followed by whitespace only is rejected
- **WHEN** `parse(">   \nACGT")` is called
- **THEN** the result is `Left(FastaParseError.EmptyId)`

### Requirement: FastaParser.parse rejects invalid DNA characters
The system SHALL return `Left(FastaParseError.InvalidDna(id, cause))` when the concatenated sequence for record `id` fails `DnaString.from` validation. The `cause` field SHALL contain the underlying `SequenceError`.

#### Scenario: Sequence with invalid character is rejected with the offending id
- **WHEN** `parse(">Rosalind_0001\nACGTX")` is called
- **THEN** the result is `Left(FastaParseError.InvalidDna("Rosalind_0001", SequenceError.InvalidCharacter('X')))`

### Requirement: FastaParser.parse handles a header with empty sequence
The system SHALL accept a header followed by no sequence lines and produce a `FastaRecord` whose `dna` is the empty `DnaString`.

#### Scenario: Header with no sequence yields empty DnaString record
- **WHEN** `parse(">Rosalind_0001")` is called
- **THEN** the result is `Right(List(FastaRecord("Rosalind_0001", <empty DnaString>)))`
