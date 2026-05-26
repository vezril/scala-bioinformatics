## Purpose
Establish the baseline SBT project on Scala 2.13.18 with Cats Effect 3, including an `IOApp` entry point, a passing ScalaTest suite, README documentation, and the initial `DnaString` validated domain type.

## Requirements
### Requirement: SBT project compiles and runs
The project SHALL be buildable with `sbt compile` and runnable with `sbt run` using Scala 2.13.18 and Cats Effect 3.x. The build SHALL NOT require any manual environment setup beyond having JDK 11+ and SBT installed.

#### Scenario: Fresh compile succeeds
- **WHEN** a developer runs `sbt compile` in the project root for the first time
- **THEN** all sources compile without errors or warnings that block the build

#### Scenario: Application entry point runs
- **WHEN** a developer runs `sbt run`
- **THEN** the application prints a greeting (e.g., "Hello, Bioinformatics!") to stdout and exits with code 0

#### Scenario: Missing JDK produces clear error
- **WHEN** SBT is invoked without a JDK on the PATH
- **THEN** SBT itself surfaces the missing-JDK error (framework behavior, not application behavior)

### Requirement: Hello World uses IOApp
The main entry point SHALL extend `cats.effect.IOApp` and return `IO[ExitCode]`. No direct use of `scala.App` or `def main` with side effects is permitted.

#### Scenario: Entry point is an IOApp
- **WHEN** the source of the main object is inspected
- **THEN** it extends `cats.effect.IOApp` and its `run` method returns `IO[ExitCode]`

#### Scenario: Output is produced inside IO
- **WHEN** the program runs
- **THEN** all console output is performed via `IO.println` or equivalent `IO`-wrapped effect, not bare `println`

### Requirement: Tests pass with sbt test
The project SHALL include at least one ScalaTest suite that passes when `sbt test` is run.

#### Scenario: Test suite passes
- **WHEN** a developer runs `sbt test`
- **THEN** all tests pass and SBT exits with code 0

#### Scenario: No test means zero failures (empty suite edge case)
- **WHEN** `sbt test` is run before any tests have been written
- **THEN** SBT reports 0 tests run, 0 failures (not a build error)

### Requirement: README describes how to run and test
The project root SHALL contain a `README.md` that documents, at minimum: prerequisites, how to compile (`sbt compile`), how to run (`sbt run`), and how to run tests (`sbt test`).

#### Scenario: README is present
- **WHEN** the project root is listed
- **THEN** `README.md` exists

#### Scenario: README contains run instructions
- **WHEN** `README.md` is read
- **THEN** it contains instructions for both `sbt run` and `sbt test`

### Requirement: DNA string is a validated domain type
The system SHALL model a DNA string as `DnaString` with a smart constructor `DnaString.from(s: String): Either[SequenceError, DnaString]` (error type renamed from `DnaError` to `SequenceError`). The valid alphabet SHALL be derived from `DnaNucleotide.validChars` rather than a hardcoded literal set.

#### Scenario: Valid DNA string is accepted
- **WHEN** a string containing only 'A', 'C', 'G', 'T' characters is passed to the smart constructor
- **THEN** the result is `Right(DnaString(...))`

#### Scenario: String with invalid character is rejected
- **WHEN** a string containing any character outside {A, C, G, T} is passed to the smart constructor
- **THEN** the result is `Left(SequenceError.InvalidCharacter(...))`

#### Scenario: Empty string is valid
- **WHEN** an empty string is passed to the smart constructor
- **THEN** the result is `Right(DnaString(""))`

#### Scenario: String exceeds 1000 characters
- **WHEN** a string longer than 1000 characters is passed to the smart constructor
- **THEN** the result is `Left(SequenceError.ExceedsMaxLength(...))`

#### Scenario: String of exactly 1000 characters is accepted
- **WHEN** a DNA string of exactly 1000 valid characters is passed to the smart constructor
- **THEN** the result is `Right(DnaString(...))`

#### Scenario: RNA-specific U character is rejected by DnaString
- **WHEN** `DnaString.from("ACGU")` is called
- **THEN** the result is `Left(SequenceError.InvalidCharacter('U'))`
