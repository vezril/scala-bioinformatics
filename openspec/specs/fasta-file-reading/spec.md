## Purpose
Define the FASTA file-reading capability: a Cats Effect IO entry point (`FastaFileReader.read`) that loads a FASTA file from a `java.nio.file.Path`, reads its contents on the blocking pool as UTF-8, and delegates parsing to the pure `FastaParser`. Failures are unified through a sealed `FastaError` ADT (`IoFailure(cause: Throwable)`, `Parse(error: FastaParseError)`), keeping the file-reading vocabulary distinct from the pure-parser vocabulary while reusing all parsing logic from the `fasta-parsing` capability.

## Requirements

### Requirement: FastaError is a sealed ADT of FASTA file-reading failures
The system SHALL provide a `sealed trait FastaError` with cases `final case class IoFailure(cause: Throwable)` (any underlying I/O or decode exception from reading the file) and `final case class Parse(error: bio.parsing.FastaParseError)` (the file was read successfully but its content failed to parse). The type SHALL reside in the `bio.parsing` package.

#### Scenario: IoFailure carries the underlying throwable
- **WHEN** `FastaError.IoFailure(new java.nio.file.NoSuchFileException("missing.fa"))` is constructed
- **THEN** the value's `cause` field is a `NoSuchFileException` with message `"missing.fa"`

#### Scenario: Parse wraps a FastaParseError
- **WHEN** `FastaError.Parse(FastaParseError.MissingHeader)` is constructed
- **THEN** the value's `error` field equals `FastaParseError.MissingHeader`

### Requirement: FastaFileReader.read loads a FASTA file via Cats Effect IO
The system SHALL provide `FastaFileReader.read(path: java.nio.file.Path): cats.effect.IO[Either[FastaError, List[bio.parsing.FastaRecord]]]`. The function SHALL read the file's full contents as UTF-8 on the Cats Effect blocking pool, then delegate parsing to `FastaParser.parse`. The function SHALL reside in the `bio.parsing` package.

#### Scenario: Reading a well-formed FASTA file returns the parsed records
- **WHEN** `FastaFileReader.read(path)` is called on a path whose contents are `">Rosalind_0001\nACGTACGT"`
- **THEN** the `IO` evaluates to `Right(List(FastaRecord("Rosalind_0001", <DnaString of "ACGTACGT">)))`

#### Scenario: Reading the Rosalind sample input returns three records in order
- **WHEN** `FastaFileReader.read(path)` is called on a path whose contents are the canonical Rosalind sample (three records `Rosalind_6404`, `Rosalind_5959`, `Rosalind_0808` with their respective DNA sequences)
- **THEN** the `IO` evaluates to `Right` containing three `FastaRecord`s with ids `Rosalind_6404`, `Rosalind_5959`, `Rosalind_0808` in that order

#### Scenario: Reading an empty file returns an empty record list
- **WHEN** `FastaFileReader.read(path)` is called on an empty file
- **THEN** the `IO` evaluates to `Right(List.empty)`

#### Scenario: Reading a file that does not exist returns IoFailure
- **WHEN** `FastaFileReader.read(path)` is called on a path that does not exist on disk
- **THEN** the `IO` evaluates to `Left(FastaError.IoFailure(cause))` where `cause` is a `java.nio.file.NoSuchFileException` (or another `IOException` from the JDK)

#### Scenario: Reading a file with invalid FASTA content returns Parse
- **WHEN** `FastaFileReader.read(path)` is called on a path whose contents are `"ACGT\n>Rosalind_0001\nACGT"` (sequence before header)
- **THEN** the `IO` evaluates to `Left(FastaError.Parse(FastaParseError.MissingHeader))`

#### Scenario: Reading a file with invalid DNA characters returns Parse(InvalidDna)
- **WHEN** `FastaFileReader.read(path)` is called on a path whose contents are `">Rosalind_0001\nACGTX"`
- **THEN** the `IO` evaluates to `Left(FastaError.Parse(FastaParseError.InvalidDna("Rosalind_0001", SequenceError.InvalidCharacter('X'))))`

#### Scenario: The returned IO is referentially transparent — describing the action does not perform it
- **WHEN** `val program = FastaFileReader.read(path)` is created but never run (no `unsafeRunSync` / `IOApp` execution)
- **THEN** the file is not read (no side effect occurs)
