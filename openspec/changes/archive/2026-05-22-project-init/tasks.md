## 1. SBT Project Scaffolding

- [x] 1.1 Create `build.sbt` with Scala 2.13.18, `cats-effect 3.5.x`, `scalatest` dependencies
- [x] 1.2 Create `project/build.properties` pinning the SBT version
- [x] 1.3 Create `project/plugins.sbt` (empty or with any required plugins)
- [x] 1.4 Create directory structure: `src/main/scala/` and `src/test/scala/`
- [x] 1.5 Verify `sbt compile` succeeds on the empty project

## 2. Hello World Entry Point (TDD)

- [x] 2.1 Write failing test: `MainSpec` asserts `Main.run` returns `IO[ExitCode.Success]`
- [x] 2.2 Run `sbt test` — confirm it fails (Red)
- [x] 2.3 Implement `Main` extending `IOApp` with `IO.println("Hello, Bioinformatics!")` returning `IO(ExitCode.Success)`
- [x] 2.4 Run `sbt test` — confirm test passes (Green)
- [x] 2.5 Run `sbt run` — confirm "Hello, Bioinformatics!" is printed to stdout

## 3. DNA Domain ADTs

- [x] 3.1 Write failing tests for `DnaString` smart constructor: valid string, invalid character, empty string, string exceeding 1000 chars
- [x] 3.2 Run `sbt test` — confirm tests fail (Red)
- [x] 3.3 Implement `DnaString` sealed trait / case class with `DnaError` ADT and `DnaString.from(s: String): Either[DnaError, DnaString]` smart constructor
- [x] 3.4 Run `sbt test` — confirm `DnaString` tests pass (Green)
- [x] 3.5 Write failing tests for `NucleotideCounts` ADT: field existence, `format` method producing `"A C G T"` space-separated output
- [x] 3.6 Run `sbt test` — confirm tests fail (Red)
- [x] 3.7 Implement `NucleotideCounts` case class with `format: String` method
- [x] 3.8 Run `sbt test` — confirm `NucleotideCounts` tests pass (Green)

## 4. DNA Nucleotide Counting (TDD)

- [x] 4.1 Write failing tests for `DnaNucleotides.count`: Rosalind sample, single character, empty string, all-T string
- [x] 4.2 Run `sbt test` — confirm tests fail (Red)
- [x] 4.3 Implement `DnaNucleotides.count(dna: DnaString): NucleotideCounts` as a pure function using `foldLeft` or equivalent functional approach
- [x] 4.4 Run `sbt test` — confirm all nucleotide counting tests pass (Green)
- [x] 4.5 Refactor: ensure no mutable state, no imperative loops; review for idiomatic Scala style (Refactor)
- [x] 4.6 Run `sbt test` — confirm all tests still pass after refactor

## 5. README

- [x] 5.1 Write `README.md` with prerequisites (JDK 11+, SBT), `sbt compile`, `sbt run`, and `sbt test` instructions
- [x] 5.2 Include a brief description of the project and its purpose
- [x] 5.3 Include example output for the DNA nucleotide counting feature

## 6. Final Verification

- [x] 6.1 Run `sbt compile` — zero errors
- [x] 6.2 Run `sbt test` — all tests pass, no skipped
- [x] 6.3 Run `sbt run` — "Hello, Bioinformatics!" printed, exits 0
- [x] 6.4 Review all source files: no bare `println`, no `var`, no mutable collections
